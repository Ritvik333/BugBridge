import { useLocation } from "react-router-dom";
import { useState, useEffect, useRef } from "react";
import MonacoEditor from "@monaco-editor/react";
import { runCode } from "../services/auth";

export default function BugDetails() {
  const location = useLocation();
  const bug = location.state;

  const [selectedLanguage, setSelectedLanguage] = useState("python");
  const [output, setOutput] = useState("");
  const [code, setCode] = useState("");
  const [saveStatus, setSaveStatus] = useState("Saved"); // "Saving..." | "Saved"

  const originalCodeRef = useRef(bug.code);
  const saveTimeoutRef = useRef(null); // To debounce saving

  useEffect(() => {
    const savedCode = localStorage.getItem(`bug_${bug.id}_code`);
    setCode(savedCode || bug.code);
  }, [bug.id, bug.code]);

  const handleCodeChange = (newCode) => {
    setCode(newCode);
    setSaveStatus("Saving..."); // Show buffering icon

    // Clear any previous timeout to debounce saving
    if (saveTimeoutRef.current) {
      clearTimeout(saveTimeoutRef.current);
    }

    // Wait 800ms before saving to avoid excessive localStorage writes
    saveTimeoutRef.current = setTimeout(() => {
      localStorage.setItem(`bug_${bug.id}_code`, newCode);
      setSaveStatus("Saved"); // Show "Draft saved"
    }, 800);
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
    setSaveStatus("Saved"); // Reset means it's back to the original
  };

  return (
    <div className="min-h-screen bg-gray-100 flex">
      {/* Left: Bug Description */}
      <div className="w-1/2 p-6 bg-white shadow-lg">
        <h2 className="text-xl font-bold">{bug.title}</h2>
        <p className="text-gray-600 mt-2">Severity: {bug.severity} | Status: {bug.status}</p>
        <p className="mt-4">{bug.description}</p>
      </div>

      {/* Right: Code Editor */}
      <div className="w-1/2 p-6">
        <div className="flex items-center justify-between">
          <h2 className="text-lg font-semibold">Code</h2>

          <div className="flex items-center space-x-2">
            <select 
              value={selectedLanguage} 
              onChange={(e) => setSelectedLanguage(e.target.value)} 
              className="p-1 border rounded-md"
            >
              <option value="javascript">JavaScript</option>
              <option value="python">Python</option>
              <option value="java">Java</option>
            </select>

            <button 
              onClick={handleRunCode} 
              className="p-2 bg-blue-500 text-white rounded-md hover:bg-blue-600"
            >
              Run
            </button>

            </div>
            </div>
        <MonacoEditor
          height="400px"
          language={selectedLanguage}
          theme="vs-dark"
          value={code}
          onChange={handleCodeChange}
          options={{
            minimap: { enabled: false },
            automaticLayout: true,
            fontSize: 14,
            lineNumbers: "on",
          }}
        />

        {/* Reset Button & Save Status */}
        <div className="flex items-center space-x-4 mt-2">
        <button 
            onClick={handleResetCode} 
            className="p-2 bg-red-500 text-white rounded-md hover:bg-red-600"
        >
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
        </div>


        {/* Output Section */}
        <h2 className="text-lg font-semibold mt-4">Output</h2>
        <div className="mt-2 p-4 bg-gray-800 text-white rounded-md">
          <pre>{output}</pre>
        </div>
      </div>
    </div>
  );
}
