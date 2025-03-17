import { useState, useRef, useEffect } from "react";
import { Bell, Menu } from 'lucide-react';
import { useNavigate } from "react-router-dom";
import { logout } from "../services/auth";

const Navbar = () => {
  const [menuOpen, setMenuOpen] = useState(false);
  const [notificationsOpen, setNotificationsOpen] = useState(false);
  const [notifications, setNotifications] = useState([]);
  
  const menuRef = useRef(null);
  const notificationRef = useRef(null);
  const navigate = useNavigate();

  // Load notifications from localStorage on component mount
  useEffect(() => {
    const initialNotifications = [
      { id: 1, message: "New bug reported: Login form validation issue", read: false, time: "5 min ago" },
      { id: 2, message: "Bug #42 status changed to 'In Progress'", read: false, time: "1 hour ago" },
      { id: 3, message: "John assigned you a critical bug", read: true, time: "Yesterday" },
      { id: 4, message: "Weekly bug report is ready", read: true, time: "2 days ago" }
    ];
    
    // Try to get read status from localStorage
    const savedReadStatus = JSON.parse(localStorage.getItem('notificationReadStatus') || '{}');
    
    // Apply saved read status to notifications
    const notificationsWithSavedStatus = initialNotifications.map(notification => ({
      ...notification,
      read: savedReadStatus[notification.id] || notification.read
    }));
    
    setNotifications(notificationsWithSavedStatus);
  }, []);

  // Click outside handler
  useEffect(() => {
    function handleClickOutside(event) {
      if (menuRef.current && !menuRef.current.contains(event.target)) {
        setMenuOpen(false);
      }
      if (notificationRef.current && !notificationRef.current.contains(event.target)) {
        setNotificationsOpen(false);
      }
    }

    document.addEventListener("mousedown", handleClickOutside);
    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, []);

  const markNotificationAsRead = (id) => {
    // Update notifications state
    setNotifications(notifications.map(notification => 
      notification.id === id ? { ...notification, read: true } : notification
    ));
    
    // Update localStorage
    const savedReadStatus = JSON.parse(localStorage.getItem('notificationReadStatus') || '{}');
    savedReadStatus[id] = true;
    localStorage.setItem('notificationReadStatus', JSON.stringify(savedReadStatus));
  };

  const markAllNotificationsAsRead = () => {
    // Update notifications state
    setNotifications(notifications.map(notification => ({ ...notification, read: true })));
    
    // Update localStorage
    const savedReadStatus = {};
    notifications.forEach(notification => {
      savedReadStatus[notification.id] = true;
    });
    localStorage.setItem('notificationReadStatus', JSON.stringify(savedReadStatus));
  };

  const unreadCount = notifications.filter(notification => !notification.read).length;

  return (
      <nav className="h-16 w-full bg-white shadow-md fixed top-0 left-0 z-20 flex justify-between items-center px-8">
        <h1 className="text-xl font-semibold hover:text-blue-600 transition duration-200" onClick={() => navigate("/dashboard")}>Bug Board</h1>
        <div className="flex space-x-6">
          <button
            className="p-2 rounded-md text-gray-600 hover:bg-gray-100 transition relative"
            onClick={() => setNotificationsOpen(!notificationsOpen)}
          >
            <Bell className="h-6 w-6" />
            {unreadCount > 0 && (
              <span className="absolute top-0 right-0 h-4 w-4 bg-red-500 rounded-full flex items-center justify-center text-white text-xs">
                {unreadCount}
              </span>
            )}
          </button>
          
          {notificationsOpen && (
            <div className="absolute right-0 mt-2 w-80 bg-white border rounded-md shadow-lg overflow-hidden z-30">
              <div className="p-3 border-b flex justify-between items-center">
                <h3 className="font-medium">Notifications</h3>
                {unreadCount > 0 && (
                  <button 
                    onClick={(e) => {
                      e.stopPropagation();
                      markAllNotificationsAsRead();
                    }}
                    className="text-xs text-blue-600 hover:text-blue-800"
                  >
                    Mark all as read
                  </button>
                )}
              </div>
              <div className="max-h-96 overflow-y-auto">
                {notifications.length === 0 ? (
                  <div className="p-4 text-center text-gray-500">No notifications</div>
                ) : (
                  notifications.map(notification => (
                    <div 
                      key={notification.id} 
                      className={`p-3 border-b hover:bg-gray-50 ${notification.read ? '' : 'bg-blue-50'}`}
                      onClick={() => markNotificationAsRead(notification.id)}
                    >
                      <div className="flex items-start">
                        {!notification.read && (
                          <span className="h-2 w-2 mt-1.5 mr-2 bg-blue-500 rounded-full flex-shrink-0"></span>
                        )}
                        <div className={`flex-1 ${notification.read ? 'ml-4' : ''}`}>
                          <p className="text-sm">{notification.message}</p>
                          <p className="text-xs text-gray-500 mt-1">{notification.time}</p>
                        </div>
                      </div>
                    </div>
                  ))
                )}
              </div>
              <div className="p-2 border-t text-center">
                <button className="text-sm text-blue-600 hover:text-blue-800">
                  View all notifications
                </button>
              </div>
            </div>
          )}
        </div>
        
        {/* User menu */}
        <div className="relative" ref={menuRef}>
          <button 
            className="p-2 rounded-md text-gray-600 hover:bg-gray-100 transition" 
            onClick={() => setMenuOpen(!menuOpen)}
          >
            <Menu className="h-6 w-6" />
          </button>
          
          {menuOpen && (
            <div style={{ zIndex: "30" }} className="absolute right-0 mt-2 w-56 bg-white border rounded-md shadow-lg p-3">
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
    </nav>
  );
};

export default Navbar;