import React from "react";
import { useNavigate } from "react-router-dom";
import { logout } from "../services/auth";

const Dashboard = () => {
    const navigate = useNavigate();

    const handleLogout = () => {
        logout(); // Clear auth data
        navigate("/"); // Redirect to login page
    };

    return (
        <div className="flex flex-col items-center justify-center h-screen bg-gray-100">
            <h2 className="text-2xl font-bold mb-4">Welcome to Dashboard</h2>
            <button 
                className="px-6 py-2 bg-red-500 text-white rounded-lg hover:bg-red-600"
                onClick={handleLogout}
            >
                Logout
            </button>
        </div>
    );
};

export default Dashboard;
