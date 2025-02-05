import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import LoginPage from "../pages/login";
import SignupPage from "../pages/register";
import HomePage from "../pages/home";
import ForgotPasswordPage from "../pages/forgot-password";
import ResetPasswordPage from "../pages/reset-password";


const AppRoutes = () => {
  return (
      <Routes>
        {/* <Route path="/" element={<HomePage />} /> */}
        <Route path="/"  element={<LoginPage />} />
        <Route path="/register" element={<SignupPage />} />
        <Route path="/forgot-password" element={<ForgotPasswordPage />} />
        <Route path="/reset-password" element={<ResetPasswordPage />} />
        
      </Routes>
  );
};

export default AppRoutes;
