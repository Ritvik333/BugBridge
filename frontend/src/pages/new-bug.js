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

  const handleSubmit = (e) => {
    e.preventDefault()
    // Here you would typically send the data to your backend
    const newBug = {
      id: Date.now(),
      title,
      description,
      severity,
      language,
      codeSnippet,
      status: "open",
      creator: "Current User",
      creationDate: new Date().toISOString().split("T")[0],
    }

    // In a real application, you would dispatch an action or call an API here
    console.log("New bug:", newBug)

    // Simulate adding the new bug to the list
    const existingBugs = JSON.parse(localStorage.getItem("bugs") || "[]")
    localStorage.setItem("bugs", JSON.stringify([newBug, ...existingBugs]))

    // Redirect back to the dashboard
    navigate("/dashboard")
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



// "use client"

// import { useState } from "react"
// import { useNavigate } from "react-router-dom"

// function NewBugPage() {
//   const navigate = useNavigate()
//   const [title, setTitle] = useState("")
//   const [description, setDescription] = useState("")
//   const [severity, setSeverity] = useState("")
//   const [language, setLanguage] = useState("")
//   const [codeSnippet, setCodeSnippet] = useState("")
//   const [file, setFile] = useState(null)

//   const handleSubmit = (e) => {
//     e.preventDefault()
//     // Here you would typically send the data to your backend
//     const newBug = {
//       id: Date.now(),
//       title,
//       description,
//       severity,
//       language,
//       codeSnippet,
//       status: "open",
//       creator: "Current User",
//       creationDate: new Date().toISOString().split("T")[0],
//     }

//     // In a real application, you would dispatch an action or call an API here
//     console.log("New bug:", newBug)

//     // Simulate adding the new bug to the list
//     const existingBugs = JSON.parse(localStorage.getItem("bugs") || "[]")
//     localStorage.setItem("bugs", JSON.stringify([newBug, ...existingBugs]))

//     // Redirect back to the bug board
//     navigate("/bug-board")
//   }

//   return (
//     <div className="min-h-screen bg-gray-100 py-12 px-4 sm:px-6 lg:px-8">
//       <div className="max-w-4xl mx-auto bg-white rounded-lg shadow-md overflow-hidden">
//         <div className="px-4 py-5 sm:p-6">
//           <h1 className="text-2xl font-medium leading-6 text-gray-900 mb-4">Submit a New Bug</h1>
//           <form onSubmit={handleSubmit} className="space-y-6">
//             <div>
//               <label htmlFor="title" className="block text-sm font-medium text-gray-700">
//                 Title
//               </label>
//               <input
//                 type="text"
//                 id="title"
//                 value={title}
//                 onChange={(e) => setTitle(e.target.value)}
//                 required
//                 className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 sm:text-sm p-3"
//               />
//             </div>
//             <div>
//               <label htmlFor="description" className="block text-sm font-medium text-gray-700">
//                 Description
//               </label>
//               <textarea
//                 id="description"
//                 value={description}
//                 onChange={(e) => setDescription(e.target.value)}
//                 required
//                 rows={4}
//                 className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 sm:text-sm p-3"
//               />
//             </div>
//             <div className="grid grid-cols-2 gap-4">
//               <div>
//                 <label htmlFor="severity" className="block text-sm font-medium text-gray-700">
//                   Severity
//                 </label>
//                 <select
//                   id="severity"
//                   value={severity}
//                   onChange={(e) => setSeverity(e.target.value)}
//                   required
//                   className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 sm:text-sm"
//                 >
//                   <option value="">Select severity</option>
//                   <option value="low">Low</option>
//                   <option value="medium">Medium</option>
//                   <option value="high">High</option>
//                   <option value="critical">Critical</option>
//                 </select>
//               </div>
//               <div>
//                 <label htmlFor="language" className="block text-sm font-medium text-gray-700">
//                   Language
//                 </label>
//                 <select
//                   id="language"
//                   value={language}
//                   onChange={(e) => setLanguage(e.target.value)}
//                   required
//                   className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 sm:text-sm"
//                 >
//                   <option value="">Select language</option>
//                   <option value="javascript">JavaScript</option>
//                   <option value="python">Python</option>
//                   <option value="java">Java</option>
//                   <option value="csharp">C#</option>
//                   <option value="other">Other</option>
//                 </select>
//               </div>
//             </div>
//             <div>
//               <label htmlFor="codeSnippet" className="block text-sm font-medium text-gray-700">
//                 Code Snippet
//               </label>
//               <textarea
//                 id="codeSnippet"
//                 value={codeSnippet}
//                 onChange={(e) => setCodeSnippet(e.target.value)}
//                 rows={6}
//                 className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 sm:text-sm font-mono p-3"
//                 placeholder="Paste your code snippet here..."
//               />
//             </div>
//             <div>
//               <label htmlFor="file" className="block text-sm font-medium text-gray-700">
//                 Attachment (optional)
//               </label>
//               <input
//                 type="file"
//                 id="file"
//                 onChange={(e) => setFile(e.target.files[0])}
//                 className="mt-1 block w-full text-sm text-gray-500 file:mr-4 file:py-2 file:px-4 file:rounded-md file:border-0 file:text-sm file:font-semibold file:bg-blue-50 file:text-blue-700 hover:file:bg-blue-100"
//               />
//             </div>
//             <div className="flex justify-end space-x-4">
//               <button
//                 type="button"
//                 onClick={() => navigate("/bug-board")}
//                 className="px-4 py-2 border border-gray-300 rounded-md shadow-sm text-sm font-medium text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
//               >
//                 Cancel
//               </button>
//               <button
//                 type="submit"
//                 className="px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
//               >
//                 Submit Bug
//               </button>
//             </div>
//           </form>
//         </div>
//       </div>
//     </div>
//   )
// }

// export default NewBugPage

