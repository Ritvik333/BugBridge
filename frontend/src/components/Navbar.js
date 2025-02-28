import { useState, useRef } from "react";
import { Bell, Menu } from "lucide-react";
import { useNavigate } from "react-router-dom";
import { logout } from "../services/auth";

const Navbar = () => {
  const [menuOpen, setMenuOpen] = useState(false);
  const [notifications, setNotifications] = useState([]);
  const menuRef = useRef(null);
  const navigate = useNavigate();

  return (
    <nav className="h-16 w-full bg-white shadow-sm flex justify-between items-center px-8"> {/* Increased width using w-full & px-8 */}
      <h1 className="text-xl font-semibold">Bug Board</h1> 
      <div className="flex space-x-6"> 
        <button
          className="p-2 rounded-md text-gray-600 hover:bg-gray-100 transition"
          onClick={() => setNotifications([...notifications, "New Bug Reported!"])}
        >
          <Bell className="h-6 w-6" /> 
        </button>
        <div className="relative" ref={menuRef}>
          <button className="p-2 rounded-md text-gray-600 hover:bg-gray-100 transition" onClick={() => setMenuOpen(!menuOpen)}>
            <Menu className="h-6 w-6" /> 
          </button>
          {menuOpen && (
            <div style={{ zIndex: "1" }} className="absolute right-0 mt-2 w-56 bg-white border rounded-md shadow-lg p-3"> 
              <p className="p-3 hover:bg-gray-100 cursor-pointer">My Account</p>
              <p className="p-3 hover:bg-gray-100 cursor-pointer">Settings</p>
              <p
                onClick={() => {
                  logout();
                  navigate("/");
                }}
                className="p-3 hover:bg-gray-100 cursor-pointer"
              >
                Log Out
              </p>
            </div>
          )}
        </div>
      </div>
    </nav>
  );
};

export default Navbar;
