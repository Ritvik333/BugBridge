import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { IoArrowBackCircle } from "react-icons/io5";

const ResetPasswordPage = () => {
    const [newPassword, setNewPassword] = useState("");
    const [confirmPassword, setConfirmPassword] = useState("");
    const [error, setError] = useState("");
    const [loading, setLoading] = useState(false);
    const navigate = useNavigate();

    const handleResetPassword = (event) => {
        event.preventDefault();
        setLoading(true);
        setError("");

        if (newPassword !== confirmPassword) {
            setError("Passwords do not match!");
            setLoading(false);
            return;
        }

        setTimeout(() => {
            alert("Password reset successful! Redirecting to login...");
            navigate("/");
        }, 2000);
    };

    return (
        <div className="min-h-screen flex items-center justify-center bg-[#e5e5e5] px-4">
            <div className="bg-white p-8 shadow-lg rounded-xl max-w-4xl w-full flex flex-col md:flex-row">
            <div className="md:w-1/2 flex items-center justify-center bg-gradient-to-r from-[#2D3E50] to-[#A3D8F4] text-white p-8 rounded-t-xl md:rounded-l-xl md:rounded-tr-none">
                    <h2 className="text-2xl md:text-3xl font-bold text-center">Secure Your Account</h2>
                </div>
                <div className="md:w-1/2 p-8">
                    <div className="flex items-center gap-2 mb-6">
                        <IoArrowBackCircle className="text-3xl text-gray-700 cursor-pointer" onClick={() => navigate("/")} />
                        <h1 className="text-center md:text-left text-[#2D3E50] text-4xl font-boldd">Reset Password</h1>
                    </div>
                    {error && <p className="text-red-500 text-sm mb-4">{error}</p>}
                    <form onSubmit={handleResetPassword} className="space-y-4">
                        <div>
                            <label className="block text-gray-700 text-sm font-medium">New Password</label>
                            <input type="password" value={newPassword} onChange={(e) => setNewPassword(e.target.value)}
                                className="w-full p-2 border rounded-lg focus:ring focus:ring-blue-300" required />
                        </div>
                        <div>
                            <label className="block text-gray-700 text-sm font-medium">Confirm Password</label>
                            <input type="password" value={confirmPassword} onChange={(e) => setConfirmPassword(e.target.value)}
                                className="w-full p-2 border rounded-lg focus:ring focus:ring-blue-300" required />
                        </div>
                        <button type="submit" disabled={loading}
                            className={`w-full py-2 rounded-lg text-white ${loading ? 'bg-gray-400' : 'bg-[#2D3E50] hover:bg-[#1A2B37]'}`}>
                            {loading ? "Processing..." : "Done"}
                        </button>
                    </form>
                </div>
            </div>
        </div>
    );
};

export default ResetPasswordPage;
