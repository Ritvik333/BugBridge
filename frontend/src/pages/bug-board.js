import { useState, useEffect, useRef } from "react";
<<<<<<< HEAD
import { Bell, Menu } from "lucide-react";
import { logout } from "../services/auth";
import { fetchBugs, fetchUsers, createBug, updateBug, deleteBug } from "../services/auth"; // Import API functions
import { useNavigate } from "react-router-dom";
=======
import { logout,fetchBugs, fetchUsers } from "../services/auth";
import { Link, useNavigate } from "react-router-dom"
import { Bell, Plus, Menu, AlertCircle, CheckCircle, Clock } from "lucide-react"


>>>>>>> 527a87d2b0e20dfee3e4ca12a02da3afa1e5a7f3

export default function BugBoardPage() {
  const [notifications, setNotifications] = useState([]);
  const [menuOpen, setMenuOpen] = useState(false);
  const [filterSeverity, setFilterSeverity] = useState("");
  const [filterStatus, setFilterStatus] = useState("");
  const [filterCreator, setFilterCreator] = useState("");
  const [sortOption, setSortOption] = useState("created_at");
  const [bugs, setBugs] = useState([]);
  const [users, setUsers] = useState([]);
  

  const navigate = useNavigate();
  const menuRef = useRef(null);

<<<<<<< HEAD
  useEffect(() => {
    const loadBugs = async () => {
      try {
        const data = await fetchBugs({ filterSeverity, filterStatus, filterCreator, sortOption });
        setBugs(data);
      } catch (error) {
        console.error("Error fetching bugs:", error);
      }
    };
    loadBugs();
  }, [filterSeverity, filterStatus, filterCreator, sortOption]);

  useEffect(() => {
    const loadUsers = async () => {
      try {
        const data = await fetchUsers();
        setUsers(Array.isArray(data) ? data : []);
      } catch (error) {
        console.error("Error fetching users:", error);
      }
    };
    loadUsers();
  }, []);

  const handleLogout = () => {
    logout();
    navigate("/");
  };

  const filteredBugs = bugs
    .filter((bug) => !filterSeverity || bug.severity === filterSeverity)
    .filter((bug) => !filterStatus || bug.status === filterStatus)
    .filter((bug) => !filterCreator || bug.creator.id === parseInt(filterCreator))
    .sort((a, b) => (sortOption === "priority" ? a.priority - b.priority : new Date(a.creationDate) - new Date(b.creationDate)));
=======

useEffect(() => {
  fetchBugsList();
}, [filterSeverity, filterStatus, filterCreator, sortOption]);


  useEffect(() => {
    const fetchUsersList = async () => {
      try {
        const usersData = await fetchUsers();
        setUsers(Array.isArray(usersData) ? usersData : []);
      } catch (error) {
        console.error("Error fetching users:", error);
      }
    };

    fetchUsersList();
  }, []);


  const loadBugsFromStorage = () => {
    try {
      const storedBugs = JSON.parse(localStorage.getItem("bugs") || "[]");
      console.log("Loaded bugs from storage:", storedBugs);
      setBugs(storedBugs);
    } catch (error) {
      console.error("Error loading bugs from storage:", error);
      setBugs([]);
    }
  };

// Modified fetchBugsList to include fallback:
const fetchBugsList = async () => {
  try {
    // First try to fetch from API
    const filters = { filterSeverity, filterStatus, filterCreator, sortOption };
    const data = await fetchBugs(filters);
    if (data && data.length > 0) {
      setBugs(data);
    } else {
      // If no data from API, load from localStorage
      loadBugsFromStorage();
    }
  } catch (error) {
    console.error("Error fetching bugs:", error);
    // Fallback to localStorage if API fails
    loadBugsFromStorage();
  }
};

  const handleLogout = () => {
    logout(); // Clear auth data
    navigate("/"); // Redirect to login page
  };

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
    .filter((bug) => {
      if (!filterCreator) return true;
      if (!bug.creator) return false;
      return String(bug.creator).toLowerCase().includes(filterCreator.toLowerCase());
    })
        // .filter((bug) => !filterCreator || bug.creator.toLowerCase().includes(filterCreator.toLowerCase()))
    .sort((a, b) =>
      sortOption === "language"
        ? a.language.localeCompare(b.language)
        : new Date(b.creationDate) - new Date(a.creationDate),
    )


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
>>>>>>> 527a87d2b0e20dfee3e4ca12a02da3afa1e5a7f3

  return (
    <div className="min-h-screen bg-gray-100 flex flex-col">
      {/* Navigation Bar */}
      <nav className="h-14 bg-white shadow-sm flex justify-between items-center px-4">
        <h1 className="text-lg font-semibold">Bug Board</h1>
        <div className="flex space-x-4">
<<<<<<< HEAD
          <button className="p-2 rounded-md text-gray-600 hover:bg-gray-100" onClick={() => setNotifications([...notifications, "New Bug Reported!"])}>
=======
          <button
            className="p-2 rounded-md text-gray-600 hover:bg-gray-100"
            onClick={() => setNotifications([...notifications, "New Bug Reported!"])}
          >
>>>>>>> 527a87d2b0e20dfee3e4ca12a02da3afa1e5a7f3
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
                <p onClick={handleLogout} className="p-2 hover:bg-gray-100 cursor-pointer">Log Out</p>
              </div>
            )}
          </div>
        </div>
      </nav>
<<<<<<< HEAD
      <div className="flex-1 p-4">
        <div className="flex gap-4 mb-4">
          <select onChange={(e) => setFilterSeverity(e.target.value)} className="p-2 border rounded hover:border-gray-400">
=======

      {/* Main Content */}
      <div className="flex-1 p-4">
        <div className="flex gap-4 mb-4">
          <select
            onChange={(e) => setFilterSeverity(e.target.value)}
            className="p-2 border rounded hover:border-gray-400"
          >
>>>>>>> 527a87d2b0e20dfee3e4ca12a02da3afa1e5a7f3
            <option value="">All Severities</option>
            <option value="low">Low</option>
            <option value="medium">Medium</option>
            <option value="high">High</option>
            <option value="critical">Critical</option>
          </select>
<<<<<<< HEAD
          <select onChange={(e) => setFilterStatus(e.target.value)} className="p-2 border rounded hover:border-gray-400">
=======
          <select
            onChange={(e) => setFilterStatus(e.target.value)}
            className="p-2 border rounded hover:border-gray-400"
          >
>>>>>>> 527a87d2b0e20dfee3e4ca12a02da3afa1e5a7f3
            <option value="">All Statuses</option>
            <option value="open">Open</option>
            <option value="in Progress">In Progress</option>
            <option value="Resolved">Resolved</option>
          </select>
<<<<<<< HEAD
          <select onChange={(e) => setFilterCreator(e.target.value)} className="p-2 border rounded hover:border-gray-400">
            <option value="">All Creators</option>
            {users.map((user) => (
              <option key={user.id} value={user.id}>
                {user.username}
              </option>
            ))}
          </select>
=======
          <input
            type="text"
            placeholder="Filter by Creator"
            onChange={(e) => setFilterCreator(e.target.value)}
            className="p-2 border rounded hover:border-gray-400"
          />
>>>>>>> 527a87d2b0e20dfee3e4ca12a02da3afa1e5a7f3
          <select onChange={(e) => setSortOption(e.target.value)} className="p-2 border rounded hover:border-gray-400">
            <option value="creationDate">Sort by Creation Date</option>
            <option value="language">Sort by Language</option>
          </select>
        </div>
        <div className="bg-white p-4 rounded shadow">
          <h2 className="font-semibold mb-2">Detected Bugs</h2>
          <div className="space-y-2">
<<<<<<< HEAD
            {filteredBugs.map((bug) => (
              <div key={bug.id} className="p-3 border rounded cursor-pointer hover:bg-gray-100" onClick={() => navigate(`/bug/${bug.id}`, { state: bug })}>
                <h3 className="font-medium">{bug.title}</h3>
                <p className="text-sm text-gray-500">Severity: {bug.severity} | Status: {bug.status}</p>
              </div>
            ))}
=======
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
                      <div onClick={() => navigate(`/bug-details/${bug.id}`, { state: bug })} className="text-blue-500 hover:underline">
                        {bug.title}
                      </div>
                    </h3>
                    <p className="text-sm text-gray-500">
                       <span className={getSeverityColor(bug.severity)}>{bug.severity}</span> | {" "}
                       <span className={getStatusColor(bug.status)}>{bug.status}</span> | {" "}
                      {bug.language}
                    </p>
                  </div>
                  <div className="flex items-center space-x-2">{getStatusIcon(bug.status)}</div>
                </div>
              ))
            )}
>>>>>>> 527a87d2b0e20dfee3e4ca12a02da3afa1e5a7f3
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
  );
}