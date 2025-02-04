import React, { useState } from "react";
import {Link, Redirect, useNavigate} from "react-router-dom";
import { IoArrowBackCircle } from "react-icons/io5";
import { signup } from "../services/auth";
import {Box, Modal, Typography} from "@mui/material";

const style = {
    position: 'absolute',
    top: '50%',
    left: '50%',
    transform: 'translate(-50%, -50%)',
    width: 400,
    bgcolor: 'background.paper',
    border: '2px solid #000',
    boxShadow: 24,
    p: 4,
};


const SignupPage = () => {
    const [open, setOpen] = useState(false);
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
                console.log(response);
                handleOpen();
                setTimeout(()=>{
                    handleClose();
                },3000)
                // window.location.href = "/dashboard";
            } catch (err) {
                setError("Invalid credentials. Please try again.");
                console.error(err);
            } finally {
                setLoading(false);
            }
        };
    const handleOpen = () => setOpen(true);
    const handleClose = () => {
        setOpen(false);
        navigate("/login");
    }
    return (
        <div className="login-wrapper">
            <Modal
                open={open}
                onClose={handleClose}
                aria-labelledby="modal-modal-title"
                aria-describedby="modal-modal-description"
            >
                <Box sx={style}>
                    <Typography id="modal-modal-description" sx={{ mt: 2 }}>
                        User Successfully registered!
                    </Typography>
                </Box>
            </Modal>
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
