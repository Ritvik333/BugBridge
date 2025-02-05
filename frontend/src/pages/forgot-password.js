import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { IoArrowBackCircle, IoSend } from "react-icons/io5";

const ForgotPasswordPage = () => {
    const [email, setEmail] = useState("");
    const [code, setCode] = useState("");
    const [error, setError] = useState("");
    const [loading, setLoading] = useState(false);
    const [otpSent, setOtpSent] = useState(false);
    const navigate = useNavigate();

    const handleSendOtp = async () => {
        if (!email) {
            setError("Please enter an email to receive OTP.");
            return;
        }
        setLoading(true);
        setError("");

        try {
            // Simulate API Call (Replace with actual API call)
            await new Promise((resolve) => setTimeout(resolve, 2000));
            setOtpSent(true);
            alert("OTP sent successfully!");
        } catch (err) {
            setError("Failed to send OTP. Please try again.");
        } finally {
            setLoading(false);
        }
    };

    const handleSubmit = async (event) => {
        event.preventDefault();
        setLoading(true);
        setError("");

        try {
            // Simulate API Call (Replace with actual API call)
            await new Promise((resolve) => setTimeout(resolve, 2000));
            alert("Code submitted successfully!"); // Replace with further navigation or API response handling
            navigate("/reset-password");
        } catch (err) {
            setError("Invalid email or code. Please try again.");
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="min-h-screen flex items-center justify-center bg-blue-100 px-4">
            <div className="bg-white p-8 shadow-lg rounded-xl max-w-4xl w-full flex flex-col md:flex-row">
                <div className="md:w-1/2 flex items-center justify-center bg-blue-500 text-white p-8 rounded-t-xl md:rounded-l-xl md:rounded-tr-none">
                    <h2 className="text-2xl md:text-3xl font-bold text-center">Recover Your Account</h2>
                </div>
                <div className="md:w-1/2 p-8">
                    <div className="flex items-center gap-2 mb-6">
                        <IoArrowBackCircle className="text-3xl text-gray-700 cursor-pointer" onClick={() => navigate("/")} />
                        <h1 className="text-2xl font-semibold">Forgot Password</h1>
                    </div>
                    {error && <p className="text-red-500 text-sm mb-4">{error}</p>}
                    <form onSubmit={handleSubmit} className="space-y-4">
                        <div>
                            <label className="block text-gray-700 text-sm font-medium">Enter Registered Email</label>
                            <div className="flex items-center border rounded-lg p-2">
                                <input type="email" value={email} onChange={(e) => setEmail(e.target.value)}
                                    className="w-full outline-none" required />
                                <button type="button" onClick={handleSendOtp} className="ml-2 text-blue-500 hover:text-blue-700">
                                    <IoSend className="text-2xl" />
                                </button>
                            </div>
                        </div>
                        <div>
                            <label className="block text-gray-700 text-sm font-medium">Enter OTP</label>
                            <input type="text" value={code} onChange={(e) => setCode(e.target.value)}
                                className="w-full p-2 border rounded-lg focus:ring focus:ring-blue-300" required />
                        </div>
                        <button type="submit" disabled={loading}
                            className={`w-full py-2 rounded-lg text-white ${loading ? 'bg-gray-400' : 'bg-blue-500 hover:bg-blue-600'}`}> 
                            {loading ? "Verifying..." : "Submit"}
                        </button>
                    </form>
                </div>
            </div>
        </div>
    );
};

export default ForgotPasswordPage;
