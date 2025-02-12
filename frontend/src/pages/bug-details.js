import { useLocation } from "react-router-dom";
import { useState } from "react";
import MonacoEditor from "@monaco-editor/react";
import apiClient from "../utils/apiClient";
import { runCode } from "../services/auth";
export default function BugDetails() {
  const location = useLocation();
  const bug = location.state;

  const [selectedLanguage, setSelectedLanguage] = useState("python");
  const [output, setOutput] = useState("");
  const [code, setCode] = useState(bug.code);

  // Function to run the code
  const runCode = async () => {
    try {
      const response = await apiClient.post("/api/run", { 
        code, 
        language: selectedLanguage 
      });
      setOutput(response.data.output || "No output");
    } catch (error) {
      setOutput(`Error: ${error.response ? error.response.data : error.message}`);
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
        <h2 className="text-lg font-semibold">Code</h2>
        <MonacoEditor
          height="400px"
          language={selectedLanguage}
          theme="vs-dark"
          value={code}
          onChange={(newCode) => setCode(newCode)}
          options={{
            minimap: { enabled: false },
            automaticLayout: true,
            fontSize: 14,
            lineNumbers: "on",
          }}
        />

        {/* Language Selection */}
        <select 
          value={selectedLanguage} 
          onChange={(e) => setSelectedLanguage(e.target.value)} 
          className="mt-4 p-2 border rounded-md"
        >
          <option value="javascript">JavaScript</option>
          <option value="python">Python</option>
          <option value="java">Java</option>
        </select>

        {/* Run Button */}
        <button 
          onClick={runCode} 
          className="ml-2 p-2 bg-blue-500 text-white rounded-md hover:bg-blue-600"
        >
          Run
        </button>

        {/* Output Section */}
        <div className="mt-4 p-4 bg-gray-800 text-white rounded-md">
          <h3 className="text-lg font-semibold">Output</h3>
          <pre className="mt-2">{output}</pre>
        </div>
      </div>
    </div>
  );
}
