import { useState } from "react";

const ForgotPassword = () => {
    const [email, setEmail] = useState("");
    const [message, setMessage] = useState("");

    const handleSubmit = async (e) => {
        e.preventDefault();
        // Call API for password reset request
        setMessage("If this email exists, a password reset link has been sent.");
    };

    return (
        <section>
            <form onSubmit={handleSubmit}>
                <h1>Forgot Password</h1>
                <div className="inputbox">
                    <input type="email" placeholder="Enter your email" value={email} onChange={(e) => setEmail(e.target.value)} required />
                </div>
                <button type="submit">Request Reset</button>
                {message && <p>{message}</p>}
            </form>
        </section>
    );
};

export default ForgotPassword;
