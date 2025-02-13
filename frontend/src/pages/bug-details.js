import { useLocation } from "react-router-dom";
import { useState, useEffect } from "react";
import MonacoEditor from "@monaco-editor/react";
import { runCode } from "../services/auth";

export default function BugDetails() {
  const location = useLocation();
  const bug = location.state;

  const [selectedLanguage, setSelectedLanguage] = useState("python");
  const [output, setOutput] = useState("");
  const [code, setCode] = useState("");

  // Load code from localStorage when the component mounts
  useEffect(() => {
    const savedCode = localStorage.getItem(`bug_${bug.id}_code`);
    setCode(savedCode || bug.code); // Use saved code if available, else use bug default
  }, [bug.id, bug.code]);

  // Save code to localStorage whenever it changes
  const handleCodeChange = (newCode) => {
    setCode(newCode);
    localStorage.setItem(`bug_${bug.id}_code`, newCode);
  };

  // Function to run the code
  const handleRunCode = async () => {
    try {
      const result = await runCode(code, selectedLanguage);
      setOutput(result || "No output");
    } catch (error) {
      setOutput(`Error: ${error}`);
    }
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
            {/* Language Selection */}
            <select 
              value={selectedLanguage} 
              onChange={(e) => setSelectedLanguage(e.target.value)} 
              className="p-1 border rounded-md"
            >
              <option value="javascript">JavaScript</option>
              <option value="python">Python</option>
              <option value="java">Java</option>
            </select>

            {/* Run Button */}
            <button 
              onClick={handleRunCode} 
              className="p-2 bg-blue-500 text-white rounded-md hover:bg-blue-600"
            >
              Run
            </button>
          </div>
        </div>

        <div className="mt-2"></div>
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
        <div className="mt-2"></div>
        <h2 className="text-lg font-semibold">Output</h2>
        <div className="mt-2 p-4 bg-gray-800 text-white rounded-md">
          <pre className="mt-2">{output}</pre>
        </div>
      </div>
    </div>
  );
}
