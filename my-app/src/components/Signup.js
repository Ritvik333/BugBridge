import { useState } from "react";
import { Link } from "react-router-dom";
import "../styles/style.css";

const Signup = () => {
    const [user, setUser] = useState({ username: "", email: "", password: "", confirmPassword: "" });

    const handleChange = (e) => {
        setUser({ ...user, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (user.password !== user.confirmPassword) {
            alert("Passwords do not match");
            return;
        }

        console.log(user);
        // Call API for signup
    };

    return (
        <section>
            <form onSubmit={handleSubmit}>
                <h1>Sign Up</h1>
                <div className="inputbox">
                    <input type="text" name="username" placeholder="Username" required onChange={handleChange} />
                </div>
                <div className="inputbox">
                    <input type="email" name="email" placeholder="Email" required onChange={handleChange} />
                </div>
                <div className="inputbox">
                    <input type="password" name="password" placeholder="Password" required onChange={handleChange} />
                </div>
                <div className="inputbox">
                    <input type="password" name="confirmPassword" placeholder="Confirm Password" required onChange={handleChange} />
                </div>
                <button type="submit">Sign Up</button>
                <div className="register">
                    <p>Already have an account? <Link to="/login">Log In</Link></p>
                </div>
            </form>
        </section>
    );
};

export default Signup;
