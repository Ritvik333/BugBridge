import { useState, useEffect, useRef } from "react";
import MonacoEditor from "@monaco-editor/react";
import {runCode, submitCode, saveDraft, deleteComment, fetchComments, fetchDraftCodeFile} from "../services/auth";
import jsBeautify from "js-beautify";
import { Copy, Play, RotateCw, Save, Upload, CheckCircle, XCircle } from "lucide-react";
import { fetchCodeFile } from "../services/auth";


export default function CodeEditor({ bug, draftCodeFilePath,rememberMeId }) {
    const [selectedLanguage, setSelectedLanguage] = useState(bug.language || "python");
    const originalCodeRef = useRef("");
    const [code, setCode] = useState("");
    const [output, setOutput] = useState("");
    const [saveStatus, setSaveStatus] = useState("Saved");
    const [isSaving, setIsSaving] = useState(false);
    const [showSubmitModal, setShowSubmitModal] = useState(false);
    const [description, setDescription] = useState("");
    const [selectedFile, setSelectedFile] = useState(null);

    const saveTimeoutRef = useRef(null);
    const debounceTimerRef = useRef(null);
    const periodicSyncIntervalRef = useRef(null);
    const handleCloseSubmitModal = () => setShowSubmitModal(false);
    useEffect(() => {
        if (!bug || !bug.codeFilePath) {
            console.warn("No code snippet found for this bug.");
            return;
        }
        console.log(bug)

        async function fetchFile(userId, username, language, filename) {
            try {
                // Try to fetch the draft code file
                let fetchedCode = await fetchDraftCodeFile(userId, username, bug.id, filename);

                // If fetchDraftCodeFile doesn't throw, it succeeded.
                console.log("Draft file fetched successfully:", fetchedCode);
                return fetchedCode;
            } catch (error) {
                console.error("Failed to fetch draft file, trying fallback:", error);
                // Call the fallback endpoint if the first one fails.
                let codeFile = await fetchCodeFile(userId, username, language, filename);
                console.log("Fallback file fetched successfully:", codeFile);
                return codeFile;
            }
        }
        const loadCode = async () => {
            try {
                const userId = bug.creator?.id;
                const username = bug.creator?.username;
                const language = bug.language;
                const filepath = bug.codeFilePath;
                const filename =filepath.split(/[/\\]/).pop();
                console.log(filepath)
                // Fetch the code file
                let fetchedCode = await fetchFile(userId, username, language, filename);

                console.log(fetchedCode)
                if(fetchedCode)
                    setCode(fetchedCode || "");  // Set default if empty
                originalCodeRef.current = fetchedCode || "";

            } catch (error) {
                console.error("Error fetching code file:", error);
            }
        };

        loadCode();


    }, [bug]);


    useEffect(() => {


        const loadCodeSnippet = async () => {
            if (!bug || !bug.codeFilePath) {
                console.warn("No code snippet found for this bug.");
                return;
            }
            async function fetchFile(userId, username, language, filename) {
                try {
                    // Try to fetch the draft code file
                    let fetchedCode = await fetchDraftCodeFile(userId, username, bug.id, filename);

                    // If fetchDraftCodeFile doesn't throw, it succeeded.
                    console.log("Draft file fetched successfully:", fetchedCode);
                    return fetchedCode;
                } catch (error) {
                    console.error("Failed to fetch draft file, trying fallback:", error);
                    // Call the fallback endpoint if the first one fails.
                    let codeFile = await fetchCodeFile(userId, username, language, filename);
                    console.log("Fallback file fetched successfully:", codeFile);
                    return codeFile;
                }
            }
            try {
                const userId = bug.creator?.id;
                const username = bug.creator?.username;
                const language = bug.language;
                const filename = bug.codeFilePath.split(/[/\\]/).pop();

                let fetchedCode = await fetchFile(userId, username, language, filename);
                console.log(fetchedCode)
                if(fetchedCode)
                    setCode(fetchedCode || "");  // Set default if empty
                originalCodeRef.current = fetchedCode || "";

            } catch (error) {
                console.error("Error fetching code snippet:", error);
            }
        };

        loadCodeSnippet();
    }, [bug]);  // Run this effect whenever `bug` changes

    useEffect(() => {
        const savedCode = localStorage.getItem(`bug_${bug.id}_code`);
        if (savedCode) {
            setCode(savedCode);
        }
    }, [bug.id]);
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

    const debouncedSaveToDB = (codeToSave) => {
        clearTimeout(debounceTimerRef.current);
        debounceTimerRef.current = setTimeout(async () => {
            try {
                const userId = localStorage.getItem("rememberMe");
                await saveDraft({ userId, bugId: bug.id, username: bug.creator.username, code: codeToSave });
                setSaveStatus("Saved");
            } catch (error) {
                console.error("Error saving draft to DB:", error);
                setSaveStatus("Error saving draft");
            }
        }, 3000);
    };

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


    const handleCopyCode = () => {
        navigator.clipboard.writeText(code);
        alert("Code copied to clipboard!");
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
    };
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


    return (
        <div className="w-1/2 p-6 bg-white shadow-lg flex flex-col">
            <div className="flex items-center justify-between mb-2">
                <h2 className="text-lg font-semibold">Code</h2>
                <div className="flex items-center space-x-2">
                    <select value={selectedLanguage} onChange={(e) => setSelectedLanguage(e.target.value)} className="p-1 border rounded-md">
                        <option value="javascript">JavaScript</option>
                        <option value="python">Python</option>
                        <option value="java">Java</option>
                    </select>
                    <button className="p-2 bg-blue-500 text-white rounded-md hover:bg-blue-600" onClick={handleRunCode}><Play /></button>
                    <button className="p-2 bg-green-500 text-white rounded-md hover:bg-green-600" onClick={handleSubmitCode}><Upload /></button>
                    <button className="p-2 bg-gray-500 text-white rounded-md hover:bg-gray-600" onClick={handleCopyCode}><Copy /></button>
                </div>
            </div>

            <MonacoEditor height="500px" language={selectedLanguage} theme="vs-dark" value={code} onChange={handleCodeChange} />

            <div className="mt-2 flex justify-between items-center">
                <button className="p-2 bg-red-500 text-white rounded-md hover:bg-red-600" onClick={handleResetCode}><RotateCw /></button>
                <p className="text-sm text-gray-500">{isSaving ? "Saving Draft..." : saveStatus}</p>
                <button onClick={handleSaveDraft} className="p-2 bg-green-500 text-white rounded-md hover:bg-green-600"><Save /></button>
            </div>

            <h2 className="text-lg font-semibold mt-4">Output</h2>
            <div className="mt-2 p-4 bg-gray-800 text-white rounded-md"><pre>{output}</pre></div>
        </div>
    );
}
