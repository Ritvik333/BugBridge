import { useLocation, useNavigate } from "react-router-dom";
import { useState, useEffect, useRef } from "react";
import MonacoEditor from "@monaco-editor/react";
import "../styles/SubmitModal.css";

import { runCode, submitCode, fetchCodeFile, updateBug, fetchComments, addComment, saveDraft, deleteComment } from "../services/auth";

import jsBeautify from "js-beautify";
import {  Trash } from "lucide-react";

export default function BugDetails({ currentUser }) {
    const location = useLocation();
    const navigate = useNavigate();
    const bug = location.state;
    const rememberMeId = parseInt(localStorage.getItem("rememberMe"), 10);
    const isCreator = bug && bug.creator && bug.creator.id === rememberMeId;
    const [selectedLanguage, setSelectedLanguage] = useState(bug.language || "python");
    const [output, setOutput] = useState("");
    const [code, setCode] = useState("");
    const [bugDescription, setBugDescription] = useState(bug.description);
    const [savedDescription, setSavedDescription] = useState(bug.description);
    const [saveStatus, setSaveStatus] = useState("Saved"); // "Saving..." | "Saved"
    const [isEditing, setIsEditing] = useState(false);
    const [isSaving, setIsSaving] = useState(false);
    const [descriptionMinimized, setDescriptionMinimized] = useState(false);
    const [comments, setComments] = useState([]);
    const [newComment, setNewComment] = useState("");
    const [loading, setLoading] = useState(true);
    const commentsContainerRef = useRef(null);
    const originalCodeRef = useRef("");
    const [isModalOpen, setIsModalOpen] = useState(false);
    const saveTimeoutRef = useRef(null);

    // Debounce Timer Ref
    const debounceTimerRef = useRef(null);
    // Periodic Sync Interval Ref
    const periodicSyncIntervalRef = useRef(null);
    const [showSubmitModal, setShowSubmitModal] = useState(false);
  const [description, setDescription] = useState("");
  const [selectedFile, setSelectedFile] = useState(null);

  // Handle opening and closing of the modal
  const handleOpenSubmitModal = () => setShowSubmitModal(true);
  const handleCloseSubmitModal = () => setShowSubmitModal(false);

  // Handle file selection
  const handleFileChange = (event) => {
    setSelectedFile(event.target.files[0]);
  };


    useEffect(() => {
        const savedBugDescription = localStorage.getItem(`bug_${bug.id}_description`);

        const loadCode = async () => {
            try {
                console.log(bug)
                const userId = bug.creator.id;
                const username = bug.creator.username; // Extract username
                const language = bug.language;
                const filepath = bug.codeFilePath;
                if (!filepath) {
                  console.error("codeFilePath is missing!", bug);
                  return;
                }
                const filename = filepath.split(/[/\\]/).pop(); // Extract filename from path
                const fetchedCode = await fetchCodeFile(userId, username, language, filename);

                setCode(fetchedCode || "");
                originalCodeRef.current = fetchedCode || "";

                const savedCode = localStorage.getItem(`bug_${bug.id}_code`);
                if (savedCode) {
                    setCode(savedCode);
                }
            } catch (error) {
                console.error("Error fetching code file:", error);
            } finally {
                setLoading(false);
            }
        };

        loadCode();
        setBugDescription(savedBugDescription || bug.description);
        setSavedDescription(savedBugDescription || bug.description);
    }, [bug]);

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

    // Debounced Save to DB Function
    const debouncedSaveToDB = (codeToSave) => {
        if (debounceTimerRef.current) {
            clearTimeout(debounceTimerRef.current);
        }

        debounceTimerRef.current = setTimeout(async () => {
            try {
                const userId = localStorage.getItem("rememberMe");
                const bugId = bug.id;
                const username = bug.creator.username;
                await saveDraft({ userId, bugId, username, code: codeToSave });
                setSaveStatus("Saved");
            } catch (error) {
                console.error("Error saving draft to DB:", error);
                setSaveStatus("Error saving draft");
            }
        }, 3000); // 2-second debounce delay
    };

    // Start Periodic Sync
    useEffect(() => {
        periodicSyncIntervalRef.current = setInterval(() => {
            const codeToSave = localStorage.getItem(`bug_${bug.id}_code`);
            if (codeToSave) {
                // Save to DB using the same function as debounced save
                console.log("periodic called")
                debouncedSaveToDB(codeToSave);
            }
        }, 30000);  // Every 30 seconds

        // Cleanup interval on unmount
        return () => {
            clearInterval(periodicSyncIntervalRef.current);
            if (debounceTimerRef.current) {
                clearTimeout(debounceTimerRef.current);
            }
        };
    }, [bug.id, bug.creator?.username]);

    const handleCodeChange = (newCode) => {
      // Beautify the new code and update state
      const formattedCode = jsBeautify(newCode, { indent_size: 2 });
      setCode(formattedCode);
      setIsSaving(true);
  
      // Immediately save to localStorage
      localStorage.setItem(`bug_${bug.id}_code`, formattedCode);
  
      // Clear any previous timeout to debounce saving
      if (saveTimeoutRef.current) {
        clearTimeout(saveTimeoutRef.current);
      }
  
      // Save to localStorage after 800ms debounce
      saveTimeoutRef.current = setTimeout(() => {
        setIsSaving(false);
      }, 800);
  
      // Trigger the debounced save
      debouncedSaveToDB(formattedCode);
      setSaveStatus("Saving Draft..."); // Update save status UI
    };
  

    const handleDescriptionChange = (e) => {
      const newDescription = e.target.value;
      setBugDescription(newDescription);
      setIsSaving(true);  
  
      // Save to localStorage
      localStorage.setItem(`bug_${bug.id}_description`, newDescription);
    };

    const saveChanges = async () => {
        if (!isCreator) return;
        try {
            const updatedBug = {
                ...bug,
                description: bugDescription,
            };

            await updateBug(updatedBug);

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

  //   const handleSubmitCode = async () => {
  //     try{
  //       const userId = localStorage.getItem("rememberMe");
  //       const bugId = bug.id;
  //       const desc = "no desc for now";

  //       const response = await submitCode({userId,bugId,desc,code});
  //       console.log(response);

  //     } catch(error){
  //       console.log("error submitting solution: ", error);
  //     }
      
  // };
  const handleSubmitCode = async () => {
    try {
      const userId = localStorage.getItem("rememberMe");
      const bugId = bug.id;
      const desc = description;

      const formData = new FormData();
      formData.append("userId", userId);
      formData.append("bugId", bugId);
      formData.append("desc", description);

      if (selectedFile) {
        formData.append("file", selectedFile); // Send file if selected
      } else {
        formData.append("code", code); // Send code from the editor
      }

      await submitCode({userId,bugId,desc,code});
      alert("Code submitted successfully!");
      handleCloseSubmitModal();
    } catch (error) {
      console.error("Error submitting code:", error);
      alert("Failed to submit code.");
    }
  };

    const handleResetCode = () => {
      setCode(originalCodeRef.current);
      localStorage.removeItem(`bug_${bug.id}_code`);
      setIsModalOpen(false); // Close modal after reset
  };
    

  const handleCopyCode = () => {
    navigator.clipboard.writeText(code);
    alert("Code copied to clipboard!");
  };
  const handleDeleteComment = async (commentId) => {
    try {
      // Call the API to delete the comment
      await deleteComment(commentId);
      // After deleting the comment, fetch the updated comments
      const updatedComments = await fetchComments(bug.id);
      // Update comments state to reflect the deletion
      setComments(updatedComments);
    } catch (error) {
      console.error("Error deleting comment:", error);
    }
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

            setTimeout(() => {
                commentsContainerRef.current?.scrollTo({ top: commentsContainerRef.current.scrollHeight, behavior: "smooth" });
            }, 100);
        } catch (error) {
            console.error("Error adding comment:", error);
        }
    };

    const handleSaveDraft = async () => {
        try {
            setSaveStatus("Saving Draft..."); // Update save status UI
            const userId = localStorage.getItem("rememberMe");
            const bugId = bug.id;
            const username = bug.creator.username;
            await saveDraft({ userId, bugId, username, code });
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
                    {(isCreator && !isEditing) && (
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
                            {"Edit"}
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
                <div key={comment?.id} style={{ justifyContent: 'space-between' }} className="mb-3 flex gap-2 ">

                  <div className="mb-3 flex gap-2 ">
                    <div className="bg-black rounded-full w-[30px] text-white flex justify-center items-center font-bold">{comment?.user?.username[0].toUpperCase()}</div>
                    <div>
                      <p className="text-xs font-light text-gray-500">
                      {comment?.user?.username}
                    </p>
                      <p className="text-sm text-gray-800">
                        {comment?.text}
                      </p>
                      </div>

                  </div>
                  {isCreator && <div onClick={()=>handleDeleteComment(comment.id)} style={{cursor:"pointer"}} className="flex items-center space-x-2">
                    <Trash className="h-4 w-4 text-red-500" />
                  </div>}

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
                        {/* <button className="p-2 bg-green-500 text-white rounded-md hover:bg-green-600" onClick={handleSubmitCode}>
                            Submit
                        </button> */}
                        <div>
      <button className="p-2 bg-green-500 text-white rounded-md hover:bg-green-600"onClick={handleOpenSubmitModal}>Submit</button>

      {showSubmitModal && (
        <div className="modal-overlay">
          <div className="modal">
            <h2>Submit Code</h2>
            <label>Description:</label>
            <textarea
              value={description}
              onChange={(e) => setDescription(e.target.value)}
              placeholder="Enter a brief description..."
            />
            
            <label>Upload File (Optional):</label>
            <input type="file" onChange={handleFileChange} />

            <h4>OR</h4>

            <label>Code Preview:</label>
            <MonacoEditor
              theme="vs-dark"
              height="250px"
              defaultLanguage="javascript" // Change based on your language
              value={code}
              options={{
                readOnly: true,
                minimap: { enabled: false }, // Hide minimap
                scrollbar: { vertical: "hidden" },
                lineNumbers: "on",
                automaticLayout: true,
              }}
            />

            <button onClick={handleSubmitCode}>Submit</button>
            <button onClick={handleCloseSubmitModal}>Cancel</button>
          </div>
        </div>
      )}
    </div>
                        <button className="p-2 bg-gray-500 text-white rounded-md hover:bg-gray-600" onClick={handleCopyCode}>
                            Copy
                        </button>
                    </div>
                </div>

                <MonacoEditor height="500px" language={selectedLanguage} theme="vs-dark" value={code} onChange={handleCodeChange} />

                <div className="mt-2 flex justify-between items-center">
                <div>
            <button className="p-2 bg-red-500 text-white rounded-md hover:bg-red-600" onClick={() => setIsModalOpen(true)}>Reset Code</button>

            {isModalOpen && (
                <div className="modal-overlay">
                    <div className="modal">
                        <p>Are you sure you want to reset the code?</p>
                        <button onClick={handleResetCode}>Yes</button>
                        <button onClick={() => setIsModalOpen(false)}>No</button>
                    </div>
                </div>
            )}
        </div>
                    <p className="text-sm text-gray-500">{isSaving ? "Saving Draft..." : saveStatus}</p>
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
