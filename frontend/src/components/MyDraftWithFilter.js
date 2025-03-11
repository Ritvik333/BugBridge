import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { AlertCircle, CheckCircle, Clock } from "lucide-react";
import { fetchUserDrafts } from "../services/auth";

const MyDraftWithFilter = ({ showAddButton = true }) => {
  const [filterSeverity, setFilterSeverity] = useState("");
  const [filterStatus, setFilterStatus] = useState("");
  const [sortOption, setSortOption] = useState("created_at");
  const [drafts, setDrafts] = useState([]);
  const navigate = useNavigate();
  const userId = localStorage.getItem("rememberMe");

  useEffect(() => {
    fetchDraftsList();
  }, [filterSeverity, filterStatus, sortOption]);

  const fetchDraftsList = async () => {
    try {
      const response = await fetchUserDrafts(userId);
      if (response?.status === "success") {
        setDrafts(response.body);
      } else {
        console.error("Failed to fetch drafts.");
        setDrafts([]);
      }
    } catch (error) {
      console.error("Error fetching drafts:", error);
      setDrafts([]);
    }
  };

  const filteredDrafts = drafts
      ?.filter((draft) => !filterSeverity || draft.severity === filterSeverity)
      .filter((draft) => !filterStatus || draft.status === filterStatus)
      .sort((a, b) =>
          sortOption === "language"
              ? a.language.localeCompare(b.language)
              : new Date(b.creationDate) - new Date(a.creationDate)
      );

  const getSeverityColor = (severity) => {
    switch (severity) {
      case "low":
        return "text-green-600";
      case "medium":
        return "text-yellow-600";
      case "high":
        return "text-orange-600";
      case "critical":
        return "text-red-600";
      default:
        return "text-gray-600";
    }
  };

  const getStatusColor = (status) => {
    switch (status) {
      case "resolved":
        return "text-green-600";
      case "in progress":
        return "text-orange-600";
      case "open":
        return "text-red-600";
      default:
        return "text-gray-600";
    }
  };

  const getStatusIcon = (status) => {
    switch (status) {
      case "open":
        return <AlertCircle className="h-5 w-5 text-red-500" />;
      case "in progress":
        return <Clock className="h-5 w-5 text-yellow-500" />;
      case "resolved":
        return <CheckCircle className="h-5 w-5 text-green-500" />;
      default:
        return null;
    }
  };

  return (
      <div className="relative flex-1 p-4 pt-16 ml-48">
        {/* Filters */}
        <div className="flex gap-4 mb-4">
          <select
              onChange={(e) => setFilterSeverity(e.target.value)}
              className="p-2 border rounded hover:border-gray-400"
          >
            <option value="">All Severities</option>
            <option value="low">Low</option>
            <option value="medium">Medium</option>
            <option value="high">High</option>
            <option value="critical">Critical</option>
          </select>
          <select
              onChange={(e) => setFilterStatus(e.target.value)}
              className="p-2 border rounded hover:border-gray-400"
          >
            <option value="">All Statuses</option>
            <option value="open">Open</option>
            <option value="in progress">In Progress</option>
            <option value="resolved">Resolved</option>
          </select>
          <select
              onChange={(e) => setSortOption(e.target.value)}
              className="p-2 border rounded hover:border-gray-400"
          >
            <option value="creationDate">Sort by Creation Date</option>
            <option value="language">Sort by Language</option>
          </select>
        </div>

        {/* Draft List */}
        <div className="bg-white p-6 rounded shadow w-full">
          <h2 className="font-semibold mb-2">Saved Drafts</h2>
          <div className="space-y-2">
            {filteredDrafts && filteredDrafts.length === 0 ? (
                <p className="text-gray-500">No drafts found.</p>
            ) : (
                filteredDrafts.map((draft) => (
                    <div
                        key={draft.id}
                        className="p-3 border rounded cursor-pointer hover:bg-gray-50 transition w-full"
                    >
                      <h3
                          className="font-medium text-blue-500 hover:underline"
                          onClick={() =>
                              navigate(`/bug-details/${draft.id}`, { state: draft })
                          }
                      >
                        {draft.bug.title || "Untitled Draft"}
                      </h3>
                      <p className="text-sm text-gray-500">
                  <span className={getSeverityColor(draft.bug.severity)}>
                    {draft.bug.severity}
                  </span>{" "}
                        |{" "}
                        <span className={getStatusColor(draft.bug.status)}>
                    {draft.bug.status}
                  </span>{" "}
                        | {draft.bug.language || "Unknown"}
                      </p>
                    </div>
                ))
            )}
          </div>
        </div>
      </div>
  );
};

export default MyDraftWithFilter;
