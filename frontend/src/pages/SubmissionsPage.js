"use client";

import { useState, useEffect, useMemo } from "react";
import Navbar from "../components/Navbar";
import SideNav from "../components/SideNav";
import { fetchSubmissionByCreator, approveSubmission, rejectSubmission } from "../services/auth";
import { Search, ChevronDown, ChevronUp } from "lucide-react";
import SubmissionItem from "../components/SubmissionItem";

export default function SubmissionsPage() {
    const [submissions, setSubmissions] = useState([]);
    const [isLoading, setIsLoading] = useState(true);
    const [expandedBugs, setExpandedBugs] = useState({});
    const [expandedSubmission, setExpandedSubmission] = useState(null);
    const [Submittedcode, setSubmittedcode] = useState("");
    const [userId, setUserId] = useState(() => {
        const storedUserId = localStorage.getItem("rememberMe");
        return storedUserId ? parseInt(storedUserId, 10) : null;
    });
    const [searchTerm, setSearchTerm] = useState("");
    const [codeLoading, setCodeLoading] = useState(false);

    useEffect(() => {
        const fetchSubmissions = async () => {
            setIsLoading(true);
            try {
                if (userId) {
                    const data = await fetchSubmissionByCreator(userId);
                    setSubmissions(data.body || []);
                } else {
                    console.warn("User ID not found in localStorage");
                    setSubmissions([]);
                }
            } catch (error) {
                console.error("Error fetching submissions:", error);
                setSubmissions([]);
            } finally {
                setIsLoading(false);
            }
        };

        fetchSubmissions();
    }, [userId]);

    const toggleBugExpanded = (bugId) => {
        setExpandedBugs((prev) => ({
            ...prev,
            [bugId]: !prev[bugId],
        }));
    };

    const filteredBugs = useMemo(() => {
        const uniqueBugs = [];
        const bugIds = new Set();

        submissions.forEach((submission) => {
            if (!bugIds.has(submission.bug.id)) {
                uniqueBugs.push(submission.bug);
                bugIds.add(submission.bug.id);
            }
        });

        if (searchTerm) {
            const term = searchTerm.toLowerCase();
            return uniqueBugs.filter((bug) =>
                bug.title.toLowerCase().includes(term) || bug.description.toLowerCase().includes(term)
            );
        }

        return uniqueBugs;
    }, [submissions, searchTerm]);

    const getSubmissionsForBug = (bugId) => {
        return submissions.filter((submission) => submission.bug.id === bugId);
    };

    const handleApprove = async (submissionId) => {
        try {
            const approverId = userId;
            if (!approverId) {
                console.error("Approver ID is missing.");
                return;
            }
            const response = await approveSubmission(submissionId, approverId);
            console.log("Approval response:", response);

            setSubmissions((prevSubmissions) =>
                prevSubmissions.map((sub) =>
                    sub.id === submissionId ? { ...sub, approvalStatus: "approved" } : sub
                )
            );
            setExpandedSubmission(null);
        } catch (error) {
            console.error("Error approving submission:", error);
        }
    };

    const handleReject = async (submissionId) => {
        try {
            const rejecterId = userId;
            if (!rejecterId) {
                console.error("Rejecter ID is missing.");
                return;
            }

            const response = await rejectSubmission(submissionId, rejecterId);
            console.log("Reject response:", response);

            setSubmissions((prevSubmissions) =>
                prevSubmissions.map((sub) =>
                    sub.id === submissionId ? { ...sub, approvalStatus: "rejected" } : sub
                )
            );
            setExpandedSubmission(null);
        } catch (error) {
            console.error("Error rejecting submission:", error);
        }
    };

    return (
      <div className="flex flex-col h-screen">
      {/* Navbar at the top, fixed and full-width */}
      <div >
          <Navbar />
      </div>
    
      {/* Main content with SideNav and Submissions */}
      <div className="flex flex-1">
          {/* SideNav with fixed width */}
          <div className="w-64 flex-shrink-0">
              <SideNav />
          </div>
  
          {/* Main content area */}
          <main className="flex-1 bg-gray-100 p-4 overflow-auto pt-16">
              <div className="max-w-6xl mx-auto">
                  <h1 className="text-2xl font-bold mb-6">Submissions</h1>
                  <div className="relative mb-4">
                      <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                          <Search className="h-4 w-4 text-gray-400" />
                      </div>
                      <input
                          type="text"
                          placeholder="Search bugs..."
                          className="pl-10 pr-4 py-2 w-full border rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                          value={searchTerm}
                          onChange={(e) => setSearchTerm(e.target.value)}
                      />
                  </div>
  
                  {isLoading ? (
                      <div className="text-center py-4">Loading submissions...</div>
                  ) : (
                      <>
                          {filteredBugs.length === 0 ? (
                              <div className="text-center py-4">No submissions found for any bugs created by you.</div>
                          ) : (
                              filteredBugs.map((bug) => (
                                  <div
                                      key={bug.id}
                                      className="bg-white rounded-lg shadow mb-3 border border-gray-200 overflow-hidden"
                                  >
                                      <div
                                          className="p-4 cursor-pointer hover:bg-gray-50 transition-colors"
                                          onClick={() => toggleBugExpanded(bug.id)}
                                      >
                                          <div className="flex justify-between items-center">
                                              <div className="text-lg font-medium">{bug.title}</div>
                                              <div className="flex items-center">
                                                  {expandedBugs[bug.id] ? (
                                                      <ChevronUp className="h-5 w-5 text-gray-400" />
                                                  ) : (
                                                      <ChevronDown className="h-5 w-5 text-gray-400" />
                                                  )}
                                              </div>
                                          </div>
                                      </div>
  
                                      {expandedBugs[bug.id] && (
                                          <div className="border-t border-gray-100">
                                              {getSubmissionsForBug(bug.id).map((submission) => (
                                                  <SubmissionItem
                                                      key={submission.id}
                                                      submission={submission}
                                                      isExpanded={expandedSubmission === submission.id}
                                                      onToggle={() =>
                                                          setExpandedSubmission(
                                                              expandedSubmission === submission.id ? null : submission.id
                                                          )
                                                      }
                                                      onApprove={handleApprove}
                                                      onReject={handleReject}
                                                      userId={userId}
                                                      codeLoading={codeLoading}
                                                      Submittedcode={Submittedcode}
                                                  />
                                              ))}
                                          </div>
                                      )}
                                  </div>
                              ))
                          )}
                      </>
                  )}
              </div>
          </main>
      </div>
  </div>
    );
}