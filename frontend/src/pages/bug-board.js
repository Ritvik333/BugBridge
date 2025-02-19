"use client"

import { useState, useEffect, useRef } from "react"
import { Link, useNavigate } from "react-router-dom"
import { Bell, Plus, Menu, AlertCircle, CheckCircle, Clock } from "lucide-react"

function BugBoardPage() {
  const navigate = useNavigate()
  const [notifications, setNotifications] = useState([])
  const [menuOpen, setMenuOpen] = useState(false)
  const [filterSeverity, setFilterSeverity] = useState("")
  const [filterStatus, setFilterStatus] = useState("")
  const [filterCreator, setFilterCreator] = useState("")
  const [sortOption, setSortOption] = useState("creationDate")
  const [bugs, setBugs] = useState([])

  useEffect(() => {
    // Load bugs from localStorage
    const storedBugs = JSON.parse(localStorage.getItem("bugs") || "[]")
    setBugs(storedBugs)
  }, [])

  const menuRef = useRef(null)

  useEffect(() => {
    const handleClickOutside = (event) => {
      if (menuRef.current && !menuRef.current.contains(event.target)) {
        setMenuOpen(false)
      }
    }

    document.addEventListener("mousedown", handleClickOutside)
    return () => {
      document.removeEventListener("mousedown", handleClickOutside)
    }
  }, [])

  const filteredBugs = bugs
    .filter((bug) => !filterSeverity || bug.severity === filterSeverity)
    .filter((bug) => !filterStatus || bug.status === filterStatus)
    .filter((bug) => !filterCreator || bug.creator.toLowerCase().includes(filterCreator.toLowerCase()))
    .sort((a, b) =>
      sortOption === "language"
        ? a.language.localeCompare(b.language)
        : new Date(b.creationDate) - new Date(a.creationDate),
    )

  // const getInitials = (name) => {
  //   return name
  //     .split(" ")
  //     .map((n) => n[0])
  //     .join("")
  //     .toUpperCase()
  // }

  const getSeverityColor = (severity) => {
    switch (severity) {
      case "low":
        return "text-green-600"
      case "medium":
        return "text-yellow-600"
      case "high":
        return "text-orange-600"
      case "critical":
        return "text-red-600"
      default:
        return "text-gray-600"
    }
  }

  const getStatusColor = (status) => {
    switch (status) {
      case "resolved":
        return "text-green-600"
      case "in progress":
        return "text-orange-600"
      case "open":
        return "text-red-600"
      default:
        return "text-gray-600"
    }
  }

  const getStatusIcon = (status) => {
    switch (status) {
      case "open":
        return <AlertCircle className="h-5 w-5 text-red-500" />
      case "in progress":
        return <Clock className="h-5 w-5 text-yellow-500" />
      case "resolved":
        return <CheckCircle className="h-5 w-5 text-green-500" />
      default:
        return null
    }
  }

  return (
    <div className="min-h-screen bg-gray-100 flex flex-col">
      {/* Navigation Bar */}
      <nav className="h-14 bg-white shadow-sm flex justify-between items-center px-4">
        <h1 className="text-lg font-semibold">Bug Board</h1>
        <div className="flex space-x-4">
          <button
            className="p-2 rounded-md text-gray-600 hover:bg-gray-100"
            onClick={() => setNotifications([...notifications, "New Bug Reported!"])}
          >
            <Bell className="h-5 w-5" />
          </button>
          <div className="relative" ref={menuRef}>
            <button className="p-2 rounded-md text-gray-600 hover:bg-gray-100" onClick={() => setMenuOpen(!menuOpen)}>
              <Menu className="h-5 w-5" />
            </button>
            {menuOpen && (
              <div className="absolute right-0 mt-2 w-48 bg-white border rounded-md shadow-lg p-2">
                <p className="p-2 hover:bg-gray-100 cursor-pointer">My Account</p>
                <p className="p-2 hover:bg-gray-100 cursor-pointer">Settings</p>
                <p className="p-2 hover:bg-gray-100 cursor-pointer">Log Out</p>
              </div>
            )}
          </div>
        </div>
      </nav>

      {/* Main Content */}
      <div className="flex-1 p-4">
        <div className="flex gap-4 mb-4">
          <select
            onChange={(e) => setFilterSeverity(e.target.value)}
            className="p-2 border rounded hover:border-gray-400"
          >
            <option value="">All Severities</option>
            <option value="low">Low</option>
            <option value="medium">Medium</option>
            <option value="high">High</option>
            <option value="critical">Critical</option>
          </select>
          <select
            onChange={(e) => setFilterStatus(e.target.value)}
            className="p-2 border rounded hover:border-gray-400"
          >
            <option value="">All Statuses</option>
            <option value="open">Open</option>
            <option value="in progress">In Progress</option>
            <option value="resolved">Resolved</option>
          </select>
          <input
            type="text"
            placeholder="Filter by Creator"
            onChange={(e) => setFilterCreator(e.target.value)}
            className="p-2 border rounded hover:border-gray-400"
          />
          <select onChange={(e) => setSortOption(e.target.value)} className="p-2 border rounded hover:border-gray-400">
            <option value="creationDate">Sort by Creation Date</option>
            <option value="language">Sort by Language</option>
          </select>
        </div>
        <div className="bg-white p-4 rounded shadow">
          <h2 className="font-semibold mb-2">Detected Bugs</h2>
          <div className="space-y-2">
            {filteredBugs.length === 0 ? (
              <p className="text-gray-500">No bugs found. Add a new bug to get started!</p>
            ) : (
              filteredBugs.map((bug) => (
                <div
                  key={bug.id}
                  className="p-3 border rounded cursor-pointer hover:bg-gray-50 transition duration-150 ease-in-out flex justify-between items-center"
                >
                  <div>
                    <h3 className="font-medium">
                      <Link to={`/bug-details/${bug.id}`} className="text-blue-500 hover:underline">
                        {bug.title}
                      </Link>
                    </h3>
                    <p className="text-sm text-gray-500">
                       <span className={getSeverityColor(bug.severity)}>{bug.severity}</span> | {" "}
                       <span className={getStatusColor(bug.status)}>{bug.status}</span> |  Language:{" "}
                      {bug.language}
                    </p>
                  </div>
                  <div className="flex items-center space-x-2">{getStatusIcon(bug.status)}</div>
                </div>
              ))
            )}
          </div>
        </div>
        <button
          onClick={() => navigate("/new-bug")}
          className="fixed bottom-8 right-8 w-16 h-16 bg-blue-600 text-white rounded-full shadow-lg hover:bg-blue-700 flex items-center justify-center focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 transition duration-150 ease-in-out"
        >
          <Plus className="h-8 w-8" />
        </button>
      </div>
    </div>
  )
}

export default BugBoardPage



// import { useState, useEffect, useRef } from "react";
// import { Bell, Menu } from "lucide-react";
// import { logout,fetchBugs } from "../services/auth";
// import { useNavigate } from "react-router-dom";


// export default function BugBoardPage() {
//   const [notifications, setNotifications] = useState([]);
//   const [menuOpen, setMenuOpen] = useState(false);
//   const [filterSeverity, setFilterSeverity] = useState("");
//   const [filterStatus, setFilterStatus] = useState("");
//   const [filterCreator, setFilterCreator] = useState("");
//   const [sortOption, setSortOption] = useState("created_at");
//   const [bugs, setBugs] = useState([]);
//   const [users, setUsers] = useState([]);

//   const navigate = useNavigate();
//   const menuRef = useRef(null); // Reference for the dropdown menu

// // eslint-disable-next-line react-hooks/exhaustive-deps
// useEffect(() => {
//   fetchBugsList();
// }, [filterSeverity, filterStatus, filterCreator, sortOption]);


//   useEffect(() => {
//     fetch("http://localhost:8080/api/users")
//         .then(res => res.json())
//         .then(data => setUsers(Array.isArray(data) ? data : []))
//         .catch(error => console.error("Error fetching users:", error));
// }, []);



//   const fetchBugsList = async () => {
//     try {
//       const filters = { filterSeverity, filterStatus, filterCreator, sortOption };
//       const data = await fetchBugs(filters); // Call fetchBugs from auth.js
//       setBugs(data);
//     } catch (error) {
//       console.error("Error fetching bugs:", error);
//     }
//   };

//   const handleLogout = () => {
//     logout(); // Clear auth data
//     navigate("/"); // Redirect to login page
//   };

//   useEffect(() => {
//     const handleClickOutside = (event) => {
//       if (menuRef.current && !menuRef.current.contains(event.target)) {
//         setMenuOpen(false);
//       }
//     };
    
//     document.addEventListener("mousedown", handleClickOutside);
//     return () => {
//       document.removeEventListener("mousedown", handleClickOutside);
//     };
//   }, []);

//   const filteredBugs = bugs
//   .filter((bug) => !filterSeverity || bug.severity === filterSeverity)
//   .filter((bug) => !filterStatus || bug.status === filterStatus)
//   .filter((bug) => !filterCreator || bug.creator.id === parseInt(filterCreator))
//   .sort((a, b) => (sortOption === "priority" ? a.priority - b.priority : new Date(a.creationDate) - new Date(b.creationDate)));

//   return (
//     <div className="min-h-screen bg-gray-100 flex flex-col">
//       <nav className="h-14 bg-white shadow-sm flex justify-between items-center px-4">
//         <h1 className="text-lg font-semibold">Bug Board</h1>
//         <div className="flex space-x-4">
//           <button className="p-2 rounded-md text-gray-600 hover:bg-gray-100" onClick={() => setNotifications([...notifications, "New Bug Reported!"])} >
//             <Bell className="h-5 w-5" />
//           </button>
//           <div className="relative" ref={menuRef}>
//             <button className="p-2 rounded-md text-gray-600 hover:bg-gray-100" onClick={() => setMenuOpen(!menuOpen)}>
//               <Menu className="h-5 w-5" />
//             </button>
//             {menuOpen && (
//               <div className="absolute right-0 mt-2 w-48 bg-white border rounded-md shadow-lg p-2">
//                 <p className="p-2 hover:bg-gray-100 cursor-pointer">My Account</p>
//                 <p className="p-2 hover:bg-gray-100 cursor-pointer">Settings</p>
//                 <button className="w-full text-left p-2 hover:bg-gray-100 cursor-pointer" onClick={handleLogout}>Log Out</button>
//               </div>
//             )}
//           </div>
//         </div>
//       </nav>
//        {/* Main Content */}
//        <div className="flex-1 p-4">
//         <div className="flex gap-4 mb-4">

//           <select onChange={(e) => setFilterSeverity(e.target.value)} className="p-2 border rounded hover:border-gray-400">
//             <option value="">All Severities</option>
//             <option value="low">Low</option>
//             <option value="medium">Medium</option>
//             <option value="high">High</option>
//             <option value="critical">Critical</option>
//           </select>

//           <select onChange={(e) => setFilterStatus(e.target.value)} className="p-2 border rounded hover:border-gray-400">
//             <option value="">All Statuses</option>
//             <option value="open">Open</option>
//             <option value="in progress">In Progress</option>
//             <option value="resolved">Resolved</option>
//           </select>

//           <select onChange={(e) => setFilterCreator(e.target.value)} className="p-2 border rounded hover:border-gray-400">
//             <option value="">All Creators</option>
//             {users.length > 0 ? (
//               users.map((user) => (
//                 <option key={user.id} value={user.id}>
//                   {user.username}
//                 </option>
//               ))
//             ) : (
//               <option disabled>Loading users...</option>
//             )}
//           </select>

//           {/* <select onChange={(e) => setFilterCreator(e.target.value)} className="p-2 border rounded hover:border-gray-400">
//           <option value="">All Creators</option>
//           {users.map(user => (
//             <option key={user.id} value={user.id}>{user.name}</option>
//             ))}
//           {/* {users.map(user => (
//             <option key={user.id} value={user.id}>{user.name}</option>
//           ))} 
//           </select> */}

//           <select onChange={(e) => setSortOption(e.target.value)} className="p-2 border rounded hover:border-gray-400">
//             <option value="created_at">Sort by Creation Date</option>
//             <option value="priority">Sort by Priority</option>
//           </select>
//         </div>

//         <div className="bg-white p-4 rounded shadow">
//           <h2 className="font-semibold mb-2">Detected Bugs</h2>
//           <div className="space-y-2">
//           {filteredBugs.map((bug) => (
//               <div key={bug.id} className="p-3 border rounded cursor-pointer hover:bg-gray-100" onClick={() => navigate(`/bug/${bug.id}`, { state: bug })}>
//                 <h3 className="font-medium">{bug.title}</h3>
//                 <p className="text-sm text-gray-500">Severity: {bug.severity} | Status: {bug.status}</p>
//               </div>
//             ))}
//           </div>
//         </div>
//       </div>
//     </div>
//   );
// }

//future functionality for create, update, delete bugs
  // const createBug = async (bugData) => {
  //   try {
  //     const response = await fetch("http://localhost:8080/api/bugs", {
  //       method: "POST",
  //       headers: { "Content-Type": "application/json" },
  //       body: JSON.stringify({ ...bugData, creator_id: bugData.creator }),
  //     });
  
  //     if (!response.ok) {
  //       throw new Error("Failed to create bug");
  //     }
  
  //     fetchBugs(); // Refresh bugs after adding
  //   } catch (error) {
  //     console.error("Error creating bug:", error);
  //   }
  // };
  
  // const updateBug = async (bugId, updatedData) => {
  //   try {
  //     const response = await fetch(`http://localhost:8080/api/bugs/${bugId}`, {
  //       method: "PUT",
  //       headers: { "Content-Type": "application/json" },
  //       body: JSON.stringify(updatedData),
  //     });
  
  //     if (!response.ok) {
  //       throw new Error("Failed to update bug");
  //     }
  
  //     fetchBugs(); // Refresh bugs after update
  //   } catch (error) {
  //     console.error("Error updating bug:", error);
  //   }
  // };
  
  // const deleteBug = async (bugId) => {
  //   try {
  //     const response = await fetch(`http://localhost:8080/api/bugs/${bugId}`, {
  //       method: "DELETE",
  //     });
  
  //     if (!response.ok) {
  //       throw new Error("Failed to delete bug");
  //     }
  
  //     fetchBugs(); // Refresh bugs after deletion
  //   } catch (error) {
  //     console.error("Error deleting bug:", error);
  //   }
  // };