import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import LoginPage from "../pages/login";
import SignupPage from "../pages/register";
import ForgotPasswordPage from "../pages/forgot-password";
import ResetPasswordPage from "../pages/reset-password";
import ProtectedRoute from "../pages/ProtectedRoute";
import BugBoardPage from "../pages/bug-board";


const AppRoutes = () => {
  return (
      <Routes>
        {/* <Route path="/" element={<HomePage />} /> */}
        <Route path="/"  element={<LoginPage />} />
        <Route path="/register" element={<SignupPage />} />
        <Route path="/forgot-password" element={<ForgotPasswordPage />} />
        <Route path="/reset-password" element={<ResetPasswordPage />} />
        <Route path="/dashboard" element={<ProtectedRoute><BugBoardPage /></ProtectedRoute>} />
        
      </Routes>
  );
};

export default AppRoutes;
