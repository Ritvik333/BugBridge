
import { useState, useEffect, useRef } from "react";

import "../styles/SubmitModal.css";
import CodeEditor from "../components/CodeEditor";
import { useNavigate, useLocation } from "react-router-dom";
// import HorizontalNav from "../components/HorizontalNav";
import SolutionPage from "./SolutionPage";
import SubmissionsPage from "./SubmissionsPage";




import {  updateBug, fetchComments, addComment, deleteComment } from "../services/auth";

import jsBeautify from "js-beautify";
import {  Trash } from "lucide-react";

export default function BugDetails({ currentUser }) {
    const navigate = useNavigate();
    const location = useLocation();
    const [activeTab, setActiveTab] = useState("description");
    const draft = location.state;
    const bug = draft.bug;
    const draftCodeFilePath = draft.codeFilePath;
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
    // const periodicSyncIntervalRef = useRef(null);
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
        if (!bug) return;
        // Load saved description from localStorage or use bug.description
        const savedBugDescription = localStorage.getItem(`bug_${bug.id}_description`);
        setBugDescription(savedBugDescription || bug.description);
        setSavedDescription(savedBugDescription || bug.description);
    }, [bug]);

    useEffect(() => {
        const getComments = async () => {
            try {
                const commentsData = await fetchComments(bug.id);
                setComments(commentsData);
                console.log(commentsData)
            } catch (error) {
                console.error("Error fetching comments:", error);
            }
        };
        getComments();
    }, [bug.id]);

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

    return (
        <div className="min-h-screen bg-gray-100 flex">
            {/* Left: Dynamic Content */}
            <div className="w-1/2 flex flex-col p-6 bg-white shadow-lg h-screen overflow-auto">
                {/* Back Button - Lifted Up */}
                <button
                    onClick={() => navigate("/dashboard")}
                    className="text-blue-600 font-semibold flex items-center mb-1 -mt-2"
                >
                    ← Bug Board
                </button>

                {/* Horizontal Navigation Bar - Slightly Separated */}
                {/*<div className="mb-4">*/}
                {/*    <HorizontalNav navigate={navigate} />*/}
                {/*</div>*/}

                {/* Dynamic Content Based on URL */}
                {location.pathname.includes("solution") ? (
                    <SolutionPage />
                ) : location.pathname.includes("submissions") ? (
                    <SubmissionsPage />
                ) : (
                    <>
                        {/* Bug Title & Edit Button - Added Space */}
                        <div className="flex justify-between items-center mt-2">
                            <h2 className="text-xl font-bold">{bug.title}</h2>
                            {(isCreator && !isEditing) && (
                                <button
                                    className="p-1 px-3 bg-blue-500 text-white rounded-md hover:bg-blue-600 mt-2"
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
                            <div className="transition-all border p-2 rounded-md overflow-y-auto h-48">
                                {comments?.length === 0 ? (
                                    <p className="text-gray-500">No comments yet.</p>
                                ) : (
                                    comments.map((comment) => (
                                        <div key={comment?.id} className="mb-3 flex justify-between">
                                            <div className="flex gap-2">
                                                <div className="bg-black rounded-full w-[30px] h-[30px] text-white flex justify-center items-center font-bold">
                                                    {comment?.user?.username[0].toUpperCase()}
                                                </div>
                                                <div>
                                                    <p className="text-xs font-light text-gray-500">{comment?.user?.username}</p>
                                                    <p className="text-sm text-gray-800">{comment?.text}</p>
                                                </div>
                                            </div>
                                            {(isCreator || localStorage.getItem("rememberMe") == comment.user.id) && (
                                                <div onClick={() => handleDeleteComment(comment.id)} className="flex items-center space-x-2 cursor-pointer">
                                                    <Trash className="h-4 w-4 text-red-500" />
                                                </div>
                                            )}
                                        </div>
                                    ))
                                )}
                            </div>

                            {/* Add Comment Section */}
                            <div className="mt-2 flex space-x-2 bg-white p-2">
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
                    </>
                )}
            </div>

            {/* Right: Code Editor (Fixed) */}
            <CodeEditor bug={bug} draftCodeFilePath={draftCodeFilePath} rememberMeId={localStorage.getItem("rememberMe")} />
        </div>
    );
}




