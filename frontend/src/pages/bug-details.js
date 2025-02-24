import { useLocation, useNavigate } from "react-router-dom";
import { useState, useEffect, useRef } from "react";
import MonacoEditor from "@monaco-editor/react";

import { runCode, fetchCodeFile, updateBug, fetchComments, addComment, saveDraft } from "../services/auth";

import jsBeautify from "js-beautify";

export default function BugDetails({ currentUser }) {
  const location = useLocation();
  const navigate = useNavigate();
  const bug = location.state;
  const rememberMeId = parseInt(localStorage.getItem("rememberMe"), 10);
  const isCreator = bug && bug.creator && bug.creator.id === rememberMeId;

  // Only one declaration for selectedLanguage
  const [selectedLanguage, setSelectedLanguage] = useState(bug.language || "python");
  const [output, setOutput] = useState("");
  const [code, setCode] = useState(""); // Initially empty
  const [bugDescription, setBugDescription] = useState(bug.description);
  const [savedDescription, setSavedDescription] = useState(bug.description);
  const [isEditing, setIsEditing] = useState(false);
  const [isSaving, setIsSaving] = useState(false);
  const [saveStatus, setSaveStatus] = useState("Saved"); // "Saving..." | "Saved"




  // States for the comment section
  const [comments, setComments] = useState([]);
  const [newComment, setNewComment] = useState("");
  const commentsContainerRef = useRef(null);


  const originalCodeRef = useRef(""); // To store the original code
  const saveTimeoutRef = useRef(null);

  useEffect(() => {
    // Retrieve saved bug description outside loadCode
    const savedBugDescription = localStorage.getItem(`bug_${bug.id}_description`);

    const loadCode = async () => {
      try {
        const userId = bug.creator.id;
        const username = bug.creator.username; // Extract username
        const language = bug.language;
        const filepath = bug.codeFilePath;
        const filename = filepath.split("/").pop(); // Extract filename from path
  
        const fetchedCode = await fetchCodeFile(userId, username, language, filename );
      
  
        setCode(fetchedCode || ""); // Set fetched code
        originalCodeRef.current = fetchedCode || ""; // Store original code
  
        // Check if there's a saved draft in localStorage
        const savedCode = localStorage.getItem(`bug_${bug.id}_code`);
        if (savedCode) {
          setCode(savedCode); // Load saved draft
        }
      } catch (error) {
        console.error("Error fetching code file:", error);
      } 
    };
  
    loadCode();
    setBugDescription(savedBugDescription || bug.description);
    setSavedDescription(savedBugDescription || bug.description);
  }, [bug]);

  // Fetch comments for this bug when component mounts
  useEffect(() => {
    const getComments = async () => {
      try {
        const commentsData = await fetchComments(bug.id);
        setComments(commentsData);
      } catch (error) {
        console.error("Error fetching comments:", error);
      }
    };
    getComments();
  }, [bug.id]);

  const handleCodeChange = (newCode) => {
    // Beautify the new code and update state
    const formattedCode = jsBeautify(newCode, { indent_size: 2 });
    setCode(formattedCode);
    setIsSaving(true);
    console.log(isSaving)


    // Clear any previous timeout to debounce saving
    if (saveTimeoutRef.current) {
      clearTimeout(saveTimeoutRef.current);
    }

    // Save to localStorage after 800ms debounce
    saveTimeoutRef.current = setTimeout(() => {
      localStorage.setItem(`bug_${bug.id}_code`, formattedCode);
      setIsSaving(false);
    }, 800);
  };

  const handleDescriptionChange = (e) => {
    setBugDescription(e.target.value);
  };

  const saveChanges = async () => {
    if (!isCreator) return;
    try {
      // Create an updated bug object.
      const updatedBug = {
        ...bug, // existing bug details
        description: bugDescription, // updated description from textarea
      };
  
      // Call the API to update the bug
      await updateBug(updatedBug);
  
      // Update state to reflect the saved changes and exit editing mode.
      setSavedDescription(bugDescription);
      setIsEditing(false);
    } catch (error) {
      console.error("Error updating bug description:", error);
    }
  };

  const discardChanges = () => {
    setBugDescription(savedDescription);
    setIsEditing(false);
  };

  const handleRunCode = async () => {
    try {
      const result = await runCode(code, selectedLanguage);
      setOutput(result || "No output");
    } catch (error) {
      console.error("Error running code:", error);
      setOutput(`Unexpected Error: ${error.message || error}`);
    }
  };

  const handleResetCode = () => {
    setCode(originalCodeRef.current);
    localStorage.removeItem(`bug_${bug.id}_code`);
    alert("Code reset to original state.");
  };

  const handleCopyCode = () => {
    navigator.clipboard.writeText(code);
    alert("Code copied to clipboard!");
  };

  // Handler for adding comments using API
  const handleAddComment = async () => {
    if (newComment.trim() === "") return;
    try {
      const commentData = {
        bugId: bug.id,
        userId: localStorage.getItem("rememberMe"), 
        text: newComment,
      };
      // Call the API to add the comment
      await addComment(commentData);
      // After adding the comment, fetch the updated comments
const updatedComments = await fetchComments(bug.id);

// Update comments state to include the new comment
setComments(updatedComments);
setNewComment(""); // Reset the input field

// Scroll to the latest comment after updating state
setTimeout(() => {
  commentsContainerRef.current?.scrollTo({ top: commentsContainerRef.current.scrollHeight, behavior: "smooth" });
}, 100);


      // // Update comments state to include the new comment
      // setComments([...comments, savedComment]);
      // setNewComment("");
    } catch (error) {
      console.error("Error adding comment:", error);
    }
  };
  const handleSaveDraft = async () => {
    try {
      const userId=localStorage.getItem("rememberMe")
      const bugId=bug.id;
      const username=bug.creator.username
      console.log(username)
      // Call the saveDraft API function and pass the necessary parameters
      const result = await saveDraft({userId, bugId, username,code});
      console.log("Draft saved successfully:", result);
      setSaveStatus("Saved"); // Update save status UI
    } catch (error) {
      console.error("Error saving draft:", error);
      setSaveStatus("Error saving draft");
    }
  };

  return (
    <div className="min-h-screen bg-gray-100 flex">
      {/* Left: Bug Description & Comments */}
       <div className="w-1/2 flex flex-col p-6 bg-white shadow-lg h-screen">
      
         {/* Breadcrumb Navigation */}
        <div className="flex items-center space-x-2 mb-4">
          <button className="text-blue-500 hover:underline text-lg font-semibold" onClick={() => navigate("/dashboard")}>
            ⬅ Bug Board
          </button>
        </div>

        {/* Bug Title & Edit Button */}
        <div className="flex justify-between items-center">
          <h2 className="text-xl font-bold">{bug.title}</h2>
          {(isCreator && !isEditing )&&(
            <button 
              className="p-1 px-3 bg-blue-500 text-white rounded-md hover:bg-blue-600" s
              onClick={async () => {
                if (isEditing) {
                  await saveChanges();
                } else {
                  setIsEditing(true);
                }
              }}
            >
              { "Edit"}
            </button>
          )}
        </div>

        {/* Severity & Status */}
        <p className="text-gray-600 mt-2">
          <strong>Severity:</strong> {bug.severity} | <strong>Status:</strong> {bug.status}
        </p>

        {/* Editable Bug Description */}
        <textarea 
  className="w-full p-3 mt-2 border rounded-md h-[420px] overflow-y-auto focus:outline-none bg-white resize-none"
  placeholder="Edit bug description..."
  value={bugDescription}
  onChange={handleDescriptionChange}
  readOnly={!isEditing || !isCreator}
/>


        {/* Save & Discard Buttons */}
        {isEditing && isCreator && (
          <div className="mt-2 flex space-x-4">
            <button className="p-2 bg-green-500 text-white rounded-md hover:bg-green-600" onClick={saveChanges}>
              Save Changes
            </button>
            <button className="p-2 bg-gray-500 text-white rounded-md hover:bg-gray-600" onClick={discardChanges}>
              Discard Changes
            </button>
          </div>
        )}

        {/* Comment Section */}
        <div className="mt-6">
          <h3 className="text-xl font-bold mb-2">Comments</h3>
          <div ref={commentsContainerRef} className={`transition-all border p-2 rounded-md overflow-y-auto flex-grow  h-48`}>
          {comments?.length === 0 ? (
            <p className="text-gray-500">No comments yet.</p>
  ) : (
    comments?.map((comment) => (
      <div key={comment?.id} className="mb-3 flex gap-2">
        <div className="bg-black rounded-full w-[30px] text-white flex justify-center items-center font-bold">{comment?.user?.username[0].toUpperCase()}</div>
        <div><p className="text-xs font-light text-gray-500">
          {comment?.user?.username}
        </p>
        <p className="text-sm text-gray-800">
          {comment?.text}
        </p></div>
      </div>
    ))
  )}
</div>

<div className="mt-2 flex space-x-2 sticky bottom-0 bg-white p-2">

  <input 
    type="text" 
    className="flex-grow p-2 border rounded-md focus:outline-none" 
    placeholder="Add a comment..." 
    value={newComment} 
    onChange={(e) => setNewComment(e.target.value)} 
    onKeyDown={(e) => {
      if (e.key === "Enter") {
        e.preventDefault();
        handleAddComment();
      }
    }}
  />
  <button className="p-2 bg-blue-500 text-white rounded-md hover:bg-blue-600" onClick={handleAddComment}>
    Send
  </button>
</div>

        </div>
      </div>

      {/* Right: Code Editor */}
      <div className="w-1/2 p-6 bg-white shadow-lg flex flex-col">
        <div className="flex items-center justify-between mb-2">
          <h2 className="text-lg font-semibold">Code</h2>
          <div className="flex items-center space-x-2">
            {bug.language && (
              <select 
                value={selectedLanguage} 
                onChange={(e) => setSelectedLanguage(e.target.value)} 
                className="p-1 border rounded-md"
              >
                <option value="javascript">JavaScript</option>
                <option value="python">Python</option>
                <option value="java">Java</option>
              </select>
            )}
            <button className="p-2 bg-blue-500 text-white rounded-md hover:bg-blue-600" onClick={handleRunCode}>
              Run
            </button>
            <button className="p-2 bg-gray-500 text-white rounded-md hover:bg-gray-600" onClick={handleCopyCode}>
              Copy
            </button>
          </div>
        </div>

        <MonacoEditor height="500px" language={selectedLanguage} theme="vs-dark" value={code} onChange={handleCodeChange} />

        <div className="mt-2 flex space-x-4 items-center">
          <button className="p-2 bg-red-500 text-white rounded-md hover:bg-red-600" onClick={handleResetCode}>
            Reset
          </button>

          {/* Save Status Indicator */}
          <div className="text-sm text-gray-500 flex items-center">
            {saveStatus === "Saving..." ? (
              <>
                <div className="animate-spin h-4 w-4 border-t-2 border-gray-500 rounded-full mr-2"></div>
                Saving...
              </>
            ) : (
              <>
                ✔ <span className="ml-1">Draft saved</span>
              </>
            )}
          </div>
          <button
          onClick={handleSaveDraft}
          className="p-2 bg-green-500 text-white rounded-md hover:bg-green-600">Save Draft
        </button>
        </div>

        <h2 className="text-lg font-semibold mt-4">Output</h2>
        <div className="mt-2 p-4 bg-gray-800 text-white rounded-md">
          <pre>{output}</pre>
        </div>
      </div>
    </div>
  );
}
