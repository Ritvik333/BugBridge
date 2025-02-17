import { Routes, Route } from "react-router-dom";
import LoginPage from "../pages/login";
import SignupPage from "../pages/register";
import ForgotPasswordPage from "../pages/forgot-password";
import ResetPasswordPage from "../pages/reset-password";
import ProtectedRoute from "../pages/ProtectedRoute";
import BugBoardPage from "../pages/bug-board";
import BugDetails from "../pages/bug-details"; // Add BugDetails import

const AppRoutes = () => {
  return (
    <Routes>
      <Route path="/" element={<LoginPage />} />
      <Route path="/register" element={<SignupPage />} />
      <Route path="/forgot-password" element={<ForgotPasswordPage />} />
      <Route path="/reset-password" element={<ResetPasswordPage />} />
      <Route path="/dashboard" element={<ProtectedRoute><BugBoardPage /></ProtectedRoute>} />
      {/* Add route for BugDetails page */}
      <Route path="/bug/:id" element={<ProtectedRoute><BugDetails /></ProtectedRoute>} />


    </Routes>
  );
};

export default AppRoutes;
