import { useState } from "react";
import { Link } from "react-router-dom";
import "../styles/style.css";

const Login = () => {
    const [credentials, setCredentials] = useState({ username: "", password: "" });

    const handleChange = (e) => {
        setCredentials({ ...credentials, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        console.log(credentials);
        // Handle login logic here
    };

    return (
        <section>
            <form onSubmit={handleSubmit}>
                <h1>Login</h1>
                <div className="inputbox">
                    <input type="text" name="username" placeholder="Username" required onChange={handleChange} />
                </div>
                <div className="inputbox">
                    <input type="password" name="password" placeholder="Password" required onChange={handleChange} />
                </div>
                <button type="submit">Log in</button>
                <div className="register">
                    <p><Link to="/forgot-password">Forgot Password?</Link></p>
                    <p>Don't have an account? <Link to="/signup">Register</Link></p>
                </div>
            </form>
        </section>
    );
};

export default Login;
