import { useLocation } from "react-router-dom";
import { useState, useEffect, useRef } from "react";
import MonacoEditor from "@monaco-editor/react";
import { runCode, fetchCodeFile,saveDraft } from "../services/auth";

export default function BugDetails() {
  const location = useLocation();
  const bug = location.state;
  const [output, setOutput] = useState("");
  const [code, setCode] = useState(""); // Initially empty
  const [saveStatus, setSaveStatus] = useState("Saved"); // "Saving..." | "Saved"
  const [loading, setLoading] = useState(true); // Loading while fetching the code
  const originalCodeRef = useRef(""); // To store the original code
  const saveTimeoutRef = useRef(null); // To debounce saving
  const [selectedLanguage, setSelectedLanguage] = useState(bug.language);

  // Fetch the code from the file using bug.codeFilePath
  useEffect(() => {
    const loadCode = async () => {
      try {
        console.log(bug)
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
      } finally {
        setLoading(false); // Stop loading indicator
      }
    };
  
    loadCode();
  }, [bug.codeFilePath]);
  
  

  const handleCodeChange = (newCode) => {
    setCode(newCode);
    setSaveStatus("Saving...");

    // Clear any previous timeout to debounce saving
    if (saveTimeoutRef.current) {
      clearTimeout(saveTimeoutRef.current);
    }

    // Save to localStorage after 800ms debounce
    saveTimeoutRef.current = setTimeout(() => {
      localStorage.setItem(`bug_${bug.id}_code`, newCode);
      setSaveStatus("Saved"); // Set status as "Draft saved"
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
    localStorage.setItem(`bug_${bug.id}_code`, originalCodeRef.current);
    setSaveStatus("Saved"); // Reset means it's back to the original
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

            <button 
              onClick={handleRunCode} 
              className="p-2 bg-blue-500 text-white rounded-md hover:bg-blue-600"
            >
              Run
            </button>
          </div>
        </div>
        
        {loading ? (
          <p>Loading code...</p> // Show loading message while fetching
        ) : (
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
        )}

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
          <button
          onClick={handleSaveDraft}
          className="p-2 bg-green-500 text-white rounded-md hover:bg-green-600">Save Draft
        </button>
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
