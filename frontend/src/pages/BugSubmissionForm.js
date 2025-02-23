"use client"

import { useState } from "react"

const BugSubmissionForm = ({ onSubmit }) => {
  const [title, setTitle] = useState("")
  const [description, setDescription] = useState("")
  const [severity, setSeverity] = useState("")
  const [priority, setPriority] = useState("")
  const [file, setFile] = useState(null)

  const handleSubmit = (e) => {
    e.preventDefault()
    if (title && description && severity && priority) {
      onSubmit({ title, description, severity, priority, file })
      setTitle("")
      setDescription("")
      setSeverity("")
      setPriority("")
      setFile(null)
    }
  }

  return (
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
          className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 sm:text-sm"
          placeholder="Enter bug title"
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
          rows="4"
          className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 sm:text-sm"
          placeholder="Describe the bug"
        ></textarea>
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
          <label htmlFor="priority" className="block text-sm font-medium text-gray-700">
            Priority
          </label>
          <select
            id="priority"
            value={priority}
            onChange={(e) => setPriority(e.target.value)}
            required
            className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 sm:text-sm"
          >
            <option value="">Select priority</option>
            <option value="1">1 (Highest)</option>
            <option value="2">2</option>
            <option value="3">3</option>
            <option value="4">4 (Lowest)</option>
          </select>
        </div>
      </div>
      <div>
        <label htmlFor="file" className="block text-sm font-medium text-gray-700">
          Attachment (optional)
        </label>
        <input
          type="file"
          id="file"
          onChange={(e) => setFile(e.target.files[0])}
          className="mt-1 block w-full text-sm text-gray-500
            file:mr-4 file:py-2 file:px-4
            file:rounded-md file:border-0
            file:text-sm file:font-semibold
            file:bg-blue-50 file:text-blue-700
            hover:file:bg-blue-100
            focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2"
        />
      </div>
      <button
        type="submit"
        className="w-full flex justify-center py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 transition duration-150 ease-in-out"
      >
        Submit Bug
      </button>
    </form>
  )
}

export default BugSubmissionForm




// "use client"

// import { useState } from "react"

// const BugSubmissionForm = ({ onSubmit }) => {
//   const [title, setTitle] = useState("")
//   const [description, setDescription] = useState("")
//   const [severity, setSeverity] = useState("")
//   const [file, setFile] = useState(null)

//   const handleSubmit = (e) => {
//     e.preventDefault()
//     if (title && description && severity) {
//       onSubmit({ title, description, severity, file })
//       setTitle("")
//       setDescription("")
//       setSeverity("")
//       setFile(null)
//     }
//   }

//   return (
//     <form onSubmit={handleSubmit} className="space-y-4">
//       <div>
//         <label htmlFor="title" className="block text-sm font-medium text-gray-700">
//           Title
//         </label>
//         <input
//           type="text"
//           id="title"
//           value={title}
//           onChange={(e) => setTitle(e.target.value)}
//           required
//           className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-300 focus:ring focus:ring-blue-200 focus:ring-opacity-50"
//         />
//       </div>
//       <div>
//         <label htmlFor="description" className="block text-sm font-medium text-gray-700">
//           Description
//         </label>
//         <textarea
//           id="description"
//           value={description}
//           onChange={(e) => setDescription(e.target.value)}
//           required
//           rows="3"
//           className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-300 focus:ring focus:ring-blue-200 focus:ring-opacity-50"
//         ></textarea>
//       </div>
//       <div>
//         <label htmlFor="severity" className="block text-sm font-medium text-gray-700">
//           Severity
//         </label>
//         <select
//           id="severity"
//           value={severity}
//           onChange={(e) => setSeverity(e.target.value)}
//           required
//           className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-300 focus:ring focus:ring-blue-200 focus:ring-opacity-50"
//         >
//           <option value="">Select severity</option>
//           <option value="low">Low</option>
//           <option value="medium">Medium</option>
//           <option value="high">High</option>
//           <option value="critical">Critical</option>
//         </select>
//       </div>
//       <div>
//         <label htmlFor="file" className="block text-sm font-medium text-gray-700">
//           Attachment (optional)
//         </label>
//         <input
//           type="file"
//           id="file"
//           onChange={(e) => setFile(e.target.files[0])}
//           className="mt-1 block w-full text-sm text-gray-500
//             file:mr-4 file:py-2 file:px-4
//             file:rounded-md file:border-0
//             file:text-sm file:font-semibold
//             file:bg-blue-50 file:text-blue-700
//             hover:file:bg-blue-100"
//         />
//       </div>
//       <button
//         type="submit"
//         className="w-full flex justify-center py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
//       >
//         Submit Bug
//       </button>
//     </form>
//   )
// }

// export default BugSubmissionForm

