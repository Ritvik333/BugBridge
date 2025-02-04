import React, { useState } from "react";
import { login } from "../services/auth";
import { IoArrowBackCircle } from "react-icons/io5";
import { Link, useNavigate } from "react-router-dom";

const LoginPage = () => {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [rememberMe, setRememberMe] = useState(false);
    const [error, setError] = useState("");
    const [loading, setLoading] = useState(false);
    const navigate = useNavigate();
    const handleSubmit = async (event) => {
        event.preventDefault();
        setLoading(true);
        setError("");
        
        const credentials = { email, password };
        
        try {
            const response = await login(credentials);
            localStorage.setItem('authToken', response.token);
            if (rememberMe) {
                localStorage.setItem('rememberMe', email);
            } else {
                localStorage.removeItem('rememberMe');
            }
            // window.location.href = "/dashboard";
        } catch (err) {
            setError("Invalid credentials. Please try again.");
            console.error(err);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="login-wrapper">
            <div className="login-container">
            <div style={{display:"flex", alignItems:"center", gap:"10px", marginBottom:"20px", justifyContent:"start",width:"100%"}}>
                    <IoArrowBackCircle className="back-arrow" onClick={()=>{navigate("/")}}/>
                    <h1>Login</h1>
                    </div>
                <form onSubmit={handleSubmit}>
                    {error && (
                        <div className="dialog-row">
                            <label className="text-center redText">{error}</label>
                        </div>
                    )}
                    <div className="inputbox">
                        <ion-icon name="email-outline"></ion-icon>
                        <input
                            type="text"
                            name="username"
                            id="username"
                            required
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                        />
                        <label htmlFor="username">Username</label>
                    </div>
                    <div className="inputbox">
                        <ion-icon name="lock-closed-outline"></ion-icon>
                        <input
                            type="password"
                            name="password"
                            id="password"
                            required
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                        />
                        <label htmlFor="password">Password</label>
                    </div>
                    <div className="options-row" style={{display:"flex", justifyContent:"space-between", gap:"30px", marginBottom:"10px"}}>
                        <label style={{display: "flex"
,
    alignItems: "center",
    gap: "4px"}}>
                            <input 
                                type="checkbox" 
                                checked={rememberMe} 
                                onChange={() => setRememberMe(!rememberMe)} 
                            />
                            Remember Me
                        </label>
                        <p><a href="#">Forgot Password?</a></p>
                    </div>
                    <button type="submit" disabled={loading}>
                        {loading ? "Logging in..." : "Log in"}
                    </button>
                    <div className="register">
                        <p style={{color:"black"}}>
                            Don't have an account? <Link to="/register" style={{color:"black"}}>Sign Up!</Link>
                        </p>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default LoginPage;
