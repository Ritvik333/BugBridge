import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import LoginPage from "../pages/login";
import SignupPage from "../pages/register";
import HomePage from "../pages/home";

const AppRoutes = () => {
  return (
      <Routes>
        <Route path="/" element={<HomePage />} />
        <Route path="/login"  element={<LoginPage />} />
        <Route path="/register" element={<SignupPage />} />
      </Routes>
  );
};

export default AppRoutes;
