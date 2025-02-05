import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { IoArrowBackCircle } from "react-icons/io5";
import { signup } from "../services/auth";

const SignupPage = () => {
    const [username, setUsername] = useState("");
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [confirmPassword, setConfirmPassword] = useState("");
    const navigate = useNavigate();
const [error, setError] = useState("");
    const [loading, setLoading] = useState(false);

    const handleSubmit = async (event) => {
            event.preventDefault();
            setLoading(true);
            setError("");
            // Simple validation to check if passwords match
        if (password !== confirmPassword) {
            alert("Passwords do not match.");
            return;
        }
            const credentials = {username, email, password };
            
            try {
                const response = await signup(credentials);
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
            <div className="register-container">
                <div style={{display:"flex", alignItems:"center",gap:"10px", justifyContent:"start",width:"100%"}}>
                                    <IoArrowBackCircle className="back-arrow" onClick={()=>{navigate("/")}}/>
                                    <h1>Sign Up</h1>
                                    </div>
            <form onSubmit={handleSubmit}>
                <div className="inputbox">
                    <ion-icon name="person-outline"></ion-icon>
                    <input
                        type="text"
                        id="username"
                        name="username"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                        required
                    />
                    <label htmlFor="username">Name</label>
                </div>
                <div className="inputbox">
                    <ion-icon name="mail-outline"></ion-icon>
                    <input
                        type="email"
                        id="email"
                        name="email"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        required
                    />
                    <label htmlFor="email">Email</label>
                </div>
                <div className="inputbox">
                    <ion-icon name="lock-closed-outline"></ion-icon>
                    <input
                        type="password"
                        id="password"
                        name="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        required
                    />
                    <label htmlFor="password">Password</label>
                </div>
                <div className="inputbox">
                    <ion-icon name="lock-closed-outline"></ion-icon>
                    <input
                        type="password"
                        id="passwordcon"
                        name="passwordcon"
                        value={confirmPassword}
                        onChange={(e) => setConfirmPassword(e.target.value)}
                        required
                    />
                    <label htmlFor="passwordcon">Confirm Password</label>
                </div>
                <button id="submit" type="submit">
                    Sign Up
                </button>
                <div className="register">
                    <p style={{color:"black"}}>
                        Already have an account? <Link to="/login" style={{color:"black"}}>Log In</Link>
                    </p>
                </div>
            </form>
        </div>
        </div>
    );
};

export default SignupPage;
