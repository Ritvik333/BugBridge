import { useState, useRef } from "react";
import { Bell, Menu } from "lucide-react";
import { useNavigate } from "react-router-dom";
import { logout } from "../services/auth";

const ProfileNavbar = () => {
  const [menuOpen, setMenuOpen] = useState(false);
  const [notifications, setNotifications] = useState([]);
  const menuRef = useRef(null);
  const navigate = useNavigate();

  return (
      <nav className="h-16 w-full bg-white shadow-md fixed top-0 left-0 z-20 flex justify-between items-center px-8">
        <h1 className="text-xl font-semibold hover:text-blue-600 transition duration-200" onClick={() => navigate("/dashboard")}>Bug Board</h1>
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
                <div style={{ zIndex: "10" }} className="absolute right-0 mt-2 w-56 bg-white border rounded-md shadow-lg p-3">
                  <p className="p-3 hover:bg-gray-100 cursor-pointer" onClick={() => navigate("/dashboard")}>Dashboard</p>
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

export default ProfileNavbar;
