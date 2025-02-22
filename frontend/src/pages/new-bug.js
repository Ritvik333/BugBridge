"use client"

import { useState } from "react"
import { useNavigate } from "react-router-dom"

function NewBugPage() {
  const navigate = useNavigate()
  const [title, setTitle] = useState("")
  const [description, setDescription] = useState("")
  const [severity, setSeverity] = useState("")
  const [language, setLanguage] = useState("")
  const [codeSnippet, setCodeSnippet] = useState("")
  const [file, setFile] = useState(null)

  const handleSubmit = async (e) => {
    e.preventDefault()
    
    try {
      // Create FormData object
      const formData = new FormData()
      formData.append('title', title)
      formData.append('description', description)
      formData.append('language', language)
      formData.append('severity', severity)
      formData.append('status', 'open') // or you can make this configurable
      formData.append('codeFilePath', codeSnippet) // or handle actual file if needed
      formData.append('creatorId', '1') // replace with actual user ID if available
  
      // Send to API
      const response = await fetch('http://localhost:8080/api/bugs', {
        method: 'POST',
        body: formData
        // Don't set Content-Type header - browser will set it automatically with boundary
      });
  
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
  
      // Also save to localStorage for local state
      const newBug = {
        id: Date.now().toString(),
        title,
        description,
        severity,
        language,
        codeSnippet,
        status: "open",
        creator: "1",
        creationDate: new Date().toISOString(),
      }
  
      // Get existing bugs from localStorage
      let existingBugs = []
      try {
        const stored = localStorage.getItem("bugs")
        if (stored) {
          const parsed = JSON.parse(stored)
          existingBugs = Array.isArray(parsed) ? parsed : []
        }
      } catch (error) {
        console.error("Error reading existing bugs:", error)
      }
  
      // Update localStorage
      const updatedBugs = [newBug, ...existingBugs]
      localStorage.setItem("bugs", JSON.stringify(updatedBugs))
      
      console.log("Bug saved successfully to API and localStorage")
  
      // Clear form
      setTitle("")
      setDescription("")
      setSeverity("")
      setLanguage("")
      setCodeSnippet("")
      setFile(null)
  
      // Navigate back to dashboard
      navigate("/dashboard")
    } catch (error) {
      console.error("Error saving bug:", error)
      alert("Failed to save bug. Please try again.")
    }
  }

  return (
    <div className="min-h-screen bg-gray-100 py-12 px-4 sm:px-6 lg:px-8">
      <div className="max-w-4xl mx-auto bg-white rounded-lg shadow-md overflow-hidden">
        <div className="px-4 py-5 sm:p-6">
          <h1 className="text-2xl font-medium leading-6 text-gray-900 mb-4">Submit a New Bug</h1>
          <form onSubmit={handleSubmit} className="space-y-6">
            <div>
              <label htmlFor="title" className="block text-sm font-medium text-gray-700">
                Title
              </label>
              <input
                type="text"
                id="title"
                value={title}
                onChange={(e) => setTitle(e.target.value)}
                required
                className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 sm:text-sm p-3"
              />
            </div>
            <div>
              <label htmlFor="description" className="block text-sm font-medium text-gray-700">
                Description
              </label>
              <textarea
                id="description"
                value={description}
                onChange={(e) => setDescription(e.target.value)}
                required
                rows={4}
                className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 sm:text-sm p-3"
              />
            </div>
            <div className="grid grid-cols-2 gap-4">
              <div>
                <label htmlFor="severity" className="block text-sm font-medium text-gray-700">
                  Severity
                </label>
                <select
                  id="severity"
                  value={severity}
                  onChange={(e) => setSeverity(e.target.value)}
                  required
                  className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 sm:text-sm"
                >
                  <option value="">Select severity</option>
                  <option value="low">Low</option>
                  <option value="medium">Medium</option>
                  <option value="high">High</option>
                  <option value="critical">Critical</option>
                </select>
              </div>
              <div>
                <label htmlFor="language" className="block text-sm font-medium text-gray-700">
                  Language
                </label>
                <select
                  id="language"
                  value={language}
                  onChange={(e) => setLanguage(e.target.value)}
                  required
                  className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 sm:text-sm"
                >
                  <option value="">Select language</option>
                  <option value="javascript">JavaScript</option>
                  <option value="python">Python</option>
                  <option value="java">Java</option>
                  <option value="csharp">C#</option>
                  <option value="other">Other</option>
                </select>
              </div>
            </div>
            <div>
              <label htmlFor="codeSnippet" className="block text-sm font-medium text-gray-700">
                Code Snippet
              </label>
              <textarea
                id="codeSnippet"
                value={codeSnippet}
                onChange={(e) => setCodeSnippet(e.target.value)}
                rows={6}
                className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 sm:text-sm font-mono p-3"
                placeholder="Paste your code snippet here..."
              />
            </div>
            <div>
              <label htmlFor="file" className="block text-sm font-medium text-gray-700">
                Attachment (optional)
              </label>
              <input
                type="file"
                id="file"
                onChange={(e) => setFile(e.target.files[0])}
                className="mt-1 block w-full text-sm text-gray-500 file:mr-4 file:py-2 file:px-4 file:rounded-md file:border-0 file:text-sm file:font-semibold file:bg-blue-50 file:text-blue-700 hover:file:bg-blue-100"
              />
            </div>
            <div className="flex justify-end space-x-4">
              <button
                type="button"
                onClick={() => navigate("/dashboard")}
                className="px-4 py-2 border border-gray-300 rounded-md shadow-sm text-sm font-medium text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
              >
                Cancel
              </button>
              <button
                type="submit"
                className="px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
              >
                Submit Bug
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  )
}

export default NewBugPage

export const storageKeys = {
  BUGS: 'bugs',
  AUTH: 'auth'
};

export const storage = {
  getBugs: () => {
    try {
      return JSON.parse(localStorage.getItem(storageKeys.BUGS) || '[]');
    } catch (error) {
      console.error('Error reading bugs from storage:', error);
      return [];
    }
  },

  saveBugs: (bugs) => {
    try {
      localStorage.setItem(storageKeys.BUGS, JSON.stringify(bugs));
      return true;
    } catch (error) {
      console.error('Error saving bugs to storage:', error);
      return false;
    }
  },

  addBug: (bug) => {
    try {
      const bugs = storage.getBugs();
      bugs.unshift(bug); // Add to beginning of array
      return storage.saveBugs(bugs);
    } catch (error) {
      console.error('Error adding bug to storage:', error);
      return false;
    }
  }
};