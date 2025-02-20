import { useState, useEffect, useRef } from "react";
import { Bell, Menu } from "lucide-react";
import { logout,fetchBugs, fetchUsers } from "../services/auth";
import { useNavigate } from "react-router-dom";


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
  const menuRef = useRef(null); // Reference for the dropdown menu

// eslint-disable-next-line react-hooks/exhaustive-deps
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


  const fetchBugsList = async () => {
    try {
      const filters = { filterSeverity, filterStatus, filterCreator, sortOption };
      const data = await fetchBugs(filters); // Call fetchBugs from auth.js
      setBugs(data);
    } catch (error) {
      console.error("Error fetching bugs:", error);
    }
  };

  const handleLogout = () => {
    logout(); // Clear auth data
    navigate("/"); // Redirect to login page
  };

  useEffect(() => {
    const handleClickOutside = (event) => {
      if (menuRef.current && !menuRef.current.contains(event.target)) {
        setMenuOpen(false);
      }
    };
    
    document.addEventListener("mousedown", handleClickOutside);
    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, []);

  const filteredBugs = bugs
  .filter((bug) => !filterSeverity || bug.severity === filterSeverity)
  .filter((bug) => !filterStatus || bug.status === filterStatus)
  .filter((bug) => !filterCreator || bug.creator.id === parseInt(filterCreator))
  .sort((a, b) => (sortOption === "priority" ? a.priority - b.priority : new Date(a.creationDate) - new Date(b.creationDate)));

  return (
    <div className="min-h-screen bg-gray-100 flex flex-col">
      <nav className="h-14 bg-white shadow-sm flex justify-between items-center px-4">
        <h1 className="text-lg font-semibold">Bug Board</h1>
        <div className="flex space-x-4">
          <button className="p-2 rounded-md text-gray-600 hover:bg-gray-100" onClick={() => setNotifications([...notifications, "New Bug Reported!"])} >
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
                <button className="w-full text-left p-2 hover:bg-gray-100 cursor-pointer" onClick={handleLogout}>Log Out</button>
              </div>
            )}
          </div>
        </div>
      </nav>
       {/* Main Content */}
       <div className="flex-1 p-4">
        <div className="flex gap-4 mb-4">

          <select onChange={(e) => setFilterSeverity(e.target.value)} className="p-2 border rounded hover:border-gray-400">
            <option value="">All Severities</option>
            <option value="low">Low</option>
            <option value="medium">Medium</option>
            <option value="high">High</option>
            <option value="critical">Critical</option>
          </select>

          <select onChange={(e) => setFilterStatus(e.target.value)} className="p-2 border rounded hover:border-gray-400">
            <option value="">All Statuses</option>
            <option value="open">Open</option>
            <option value="In Progress">In Progress</option>
            <option value="Resolved">Resolved</option>
          </select>

          <select onChange={(e) => setFilterCreator(e.target.value)} className="p-2 border rounded hover:border-gray-400">
            <option value="">All Creators</option>
            {users.length > 0 ? (
              users.map((user) => (
                <option key={user.id} value={user.id}>
                  {user.username}
                </option>
              ))
            ) : (
              <option disabled>Loading users...</option>
            )}
          </select>

          {/* <select onChange={(e) => setFilterCreator(e.target.value)} className="p-2 border rounded hover:border-gray-400">
          <option value="">All Creators</option>
          {users.map(user => (
            <option key={user.id} value={user.id}>{user.name}</option>
            ))}
          {/* {users.map(user => (
            <option key={user.id} value={user.id}>{user.name}</option>
          ))} 
          </select> */}

          <select onChange={(e) => setSortOption(e.target.value)} className="p-2 border rounded hover:border-gray-400">
            <option value="created_at">Sort by Creation Date</option>
            <option value="priority">Sort by Priority</option>
          </select>
        </div>

        <div className="bg-white p-4 rounded shadow">
          <h2 className="font-semibold mb-2">Detected Bugs</h2>
          <div className="space-y-2">
          {filteredBugs.map((bug) => (
              <div key={bug.id} className="p-3 border rounded cursor-pointer hover:bg-gray-100" onClick={() => navigate(`/bug/${bug.id}`, { state: bug })}>
                <h3 className="font-medium">{bug.title}</h3>
                <p className="text-sm text-gray-500">Severity: {bug.severity} | Status: {bug.status}</p>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}
