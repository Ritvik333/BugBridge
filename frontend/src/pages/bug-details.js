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

  // Store the ORIGINAL bug.code permanently
  const originalCodeRef = useRef(bug.code);

  useEffect(() => {
    const savedCode = localStorage.getItem(`bug_${bug.id}_code`);

    if (savedCode) {
      setCode(savedCode);
    } else {
      setCode(bug.code);
    }
  }, [bug.id, bug.code]);

  const handleCodeChange = (newCode) => {
    setCode(newCode);
    localStorage.setItem(`bug_${bug.id}_code`, newCode);
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
    setCode(originalCodeRef.current); // Reset code to original bug.code
    localStorage.removeItem(`bug_${bug.id}_code`); // Clear saved changes in local storage
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

            <button 
              onClick={handleResetCode} 
              className="p-2 bg-red-500 text-white rounded-md hover:bg-red-600"
            >
              Reset
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

        {/* Output Section */}
        <h2 className="text-lg font-semibold mt-4">Output</h2>
        <div className="mt-2 p-4 bg-gray-800 text-white rounded-md">
          <pre>{output}</pre>
        </div>
      </div>
    </div>
  );
}
