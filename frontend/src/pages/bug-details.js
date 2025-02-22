import { useLocation, useNavigate } from "react-router-dom";
import { useState, useEffect, useRef } from "react";
import MonacoEditor from "@monaco-editor/react";
import { runCode } from "../services/auth";
import jsBeautify from "js-beautify";

export default function BugDetails() {
  const location = useLocation();
  const navigate = useNavigate();
  const bug = location.state;

  const [selectedLanguage, setSelectedLanguage] = useState(bug.language || "python");
  const [output, setOutput] = useState("");
  const [code, setCode] = useState("");
  const [bugDescription, setBugDescription] = useState(bug.description);
  const [savedDescription, setSavedDescription] = useState(bug.description);
  const [additionalNotes, setAdditionalNotes] = useState("");
  const [lastSaved, setLastSaved] = useState("");
  const [showNotes, setShowNotes] = useState(true);
  const [isSaving, setIsSaving] = useState(false);

  const originalCodeRef = useRef(bug.code);
  const saveTimeoutRef = useRef(null);

  // Fetch the code from the file using bug.codeFilePath
  useEffect(() => {
    const savedCode = localStorage.getItem(`bug_${bug.id}_code`);
    const savedBugDescription = localStorage.getItem(`bug_${bug.id}_description`);
    const savedNotes = localStorage.getItem(`bug_${bug.id}_notes`);
    const savedTimestamp = localStorage.getItem(`bug_${bug.id}_lastSaved`);

    setCode(savedCode || bug.code);
    setBugDescription(savedBugDescription || bug.description);
    setSavedDescription(savedBugDescription || bug.description);
    setAdditionalNotes(savedNotes || "");
    setLastSaved(savedTimestamp || "Not saved yet");
  }, [bug.id, bug.code, bug.description]);

  const saveTimestamp = () => {
    const timestamp = new Date().toLocaleString();
    localStorage.setItem(`bug_${bug.id}_lastSaved`, timestamp);
    setLastSaved(timestamp);
  };

  const handleCodeChange = (newCode) => {
    const formattedCode = jsBeautify(newCode, { indent_size: 2 }); // Auto-Format
    setCode(formattedCode);
    setIsSaving(true);

    // Debounce the save operation
    if (saveTimeoutRef.current) clearTimeout(saveTimeoutRef.current);
    saveTimeoutRef.current = setTimeout(() => {
      localStorage.setItem(`bug_${bug.id}_code`, formattedCode);
      setIsSaving(false);
      saveTimestamp();
    }, 800);
  };

  const handleDescriptionChange = (e) => {
    setBugDescription(e.target.value);
  };

  const handleNotesChange = (e) => {
    setAdditionalNotes(e.target.value);
    localStorage.setItem(`bug_${bug.id}_notes`, e.target.value);
    saveTimestamp();
  };

  const saveChanges = () => {
    localStorage.setItem(`bug_${bug.id}_description`, bugDescription);
    setSavedDescription(bugDescription);
    saveTimestamp();
    alert("Bug description saved!");
  };

  const discardChanges = () => {
    setBugDescription(savedDescription);
    alert("Last saved version restored!");
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

  console.log("cdoe:"+code);

  return (
    <div className="min-h-screen bg-gray-100 flex">
      {/* Left: Bug Description & Additional Notes (50% Width) */}
      <div className="w-1/2 flex flex-col p-6 bg-white shadow-lg">
        {/* Breadcrumb Navigation */}
        <div className="flex items-center space-x-2 mb-4">
          <button 
            className="text-blue-500 hover:underline text-lg font-semibold"
            onClick={() => navigate("/dashboard")}
          >
            Bug Board
          </button>
          <span className="text-gray-500">{'>'}</span>
          <span className="text-lg font-semibold">Bug Description</span>
        </div>

        {/* Editable Bug Description (Fixed Height with Scroll) */}
        <h2 className="text-xl font-bold">Bug Description</h2>
        <textarea
          className="w-full p-3 mt-2 border rounded-md h-96 overflow-y-auto focus:outline-none focus:ring-2 focus:ring-blue-400"
          placeholder="Edit bug description..."
          value={bugDescription}
          onChange={handleDescriptionChange}
        ></textarea>

        {/* Save & Discard Buttons */}
        <div className="mt-2 flex space-x-4">
          <button className="p-2 bg-green-500 text-white rounded-md hover:bg-green-600" onClick={saveChanges}>
            Save Changes
          </button>
          <button className="p-2 bg-gray-500 text-white rounded-md hover:bg-gray-600" onClick={discardChanges}>
            Discard Changes
          </button>
        </div>
        <p className="text-sm text-gray-500 mt-2">Last saved: {lastSaved}</p>

        {/* Toggle for Additional Notes (Auto-Saved) */}
        <h2 className="text-lg font-semibold cursor-pointer mt-6" onClick={() => setShowNotes(!showNotes)}>
          Additional Notes {showNotes ? "▼" : "▲"}
        </h2>
        {showNotes && (
          <textarea
            className="w-full p-3 mt-2 border rounded-md h-56 overflow-y-auto focus:outline-none focus:ring-2 focus:ring-blue-400"
            placeholder="Add your notes here..."
            value={additionalNotes}
            onChange={handleNotesChange}
          ></textarea>
        )}
      </div>

      {/* Right: Code Editor (50% Width) */}
      <div className="w-1/2 p-6 bg-white shadow-lg flex flex-col">
        <div className="flex items-center justify-between mb-2">
          <h2 className="text-lg font-semibold">Code</h2>
          <div className="flex items-center space-x-2">
            <select value={selectedLanguage} onChange={(e) => setSelectedLanguage(e.target.value)} className="p-1 border rounded-md">
              <option value="javascript">JavaScript</option>
              <option value="python">Python</option>
              <option value="java">Java</option>
            </select>
            <button className="p-2 bg-blue-500 text-white rounded-md hover:bg-blue-600" onClick={handleRunCode}>Run</button>
            <button className="p-2 bg-gray-500 text-white rounded-md hover:bg-gray-600" onClick={handleCopyCode}>Copy</button>
          </div>
        </div>

        {/* Code Editor (Auto-Formats Code on Change) */}
        <MonacoEditor height="500px" language={selectedLanguage} theme="vs-dark" value={code} onChange={handleCodeChange} />

        <div className="mt-2 flex space-x-4 items-center">
          <button className="p-2 bg-red-500 text-white rounded-md hover:bg-red-600" onClick={handleResetCode}>
            Reset
          </button>
          <p className="text-sm text-gray-500">
            {isSaving ? "Saving Draft..." : "✔ Draft Saved"}
          </p>
        </div>

        <h2 className="text-lg font-semibold mt-4">Output</h2>
        <div className="mt-2 p-4 bg-gray-800 text-white rounded-md">
          <pre>{output}</pre>
        </div>
      </div>
    </div>
  );
}
