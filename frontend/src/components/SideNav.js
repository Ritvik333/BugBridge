import { Home, FileText } from "lucide-react";
import { useNavigate } from "react-router-dom";

const SideNav = () => {
  const navigate = useNavigate();

  return (
    <div className="w-48 bg-gray-800 text-white h-screen p-4"> 
      <nav className="flex flex-col space-y-4">
        <button
          onClick={() => navigate("/dashboard")}
          className="flex items-center space-x-2 p-3 rounded-md hover:bg-gray-700 transition"
        >
          <Home className="h-5 w-5" />
          <span>Home</span>
        </button>

        <button
          onClick={() => navigate("/saved-drafts")}
          className="flex items-center space-x-2 p-3 rounded-md hover:bg-gray-700 transition"
        >
          <FileText className="h-5 w-5" />
          <span>My Drafts</span>
        </button>
      </nav>
    </div>
  );
};

export default SideNav;
