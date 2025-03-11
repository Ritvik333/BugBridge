"use client"

import { useState, useEffect } from "react"
import Navbar from "../components/Navbar"
import SideNav from "../components/SideNav"
import { CheckCircle, XCircle, Clock, AlertCircle } from "lucide-react"

export default function SubmissionsPage() {
  const [activeTab, setActiveTab] = useState("unapproved")
  const [approvedBugs, setApprovedBugs] = useState([])
  const [unapprovedBugs, setUnapprovedBugs] = useState([])
  const [isLoading, setIsLoading] = useState(true)

  useEffect(() => {
    // Fetch approved and unapproved bugs
    const fetchSubmissions = async () => {
      try {
        setIsLoading(true)
        // Replace with your actual API calls
        const approvedResponse = await fetch("/api/bugs?status=approved")
        const unapprovedResponse = await fetch("/api/bugs?status=pending")

        if (approvedResponse.ok && unapprovedResponse.ok) {
          const approvedData = await approvedResponse.json()
          const unapprovedData = await unapprovedResponse.json()

          setApprovedBugs(approvedData)
          setUnapprovedBugs(unapprovedData)
        }
      } catch (error) {
        console.error("Error fetching submissions:", error)
      } finally {
        setIsLoading(false)
      }
    }

    fetchSubmissions()
  }, [])

  const handleApprove = async (bugId) => {
    try {
      // Replace with your actual API call
      const response = await fetch(`/api/bugs/${bugId}/approve`, {
        method: "PUT",
      })

      if (response.ok) {
        // Move the bug from unapproved to approved list
        const bugToMove = unapprovedBugs.find((bug) => bug.id === bugId)
        if (bugToMove) {
          setUnapprovedBugs((prev) => prev.filter((bug) => bug.id !== bugId))
          setApprovedBugs((prev) => [...prev, { ...bugToMove, status: "approved" }])
        }
      }
    } catch (error) {
      console.error("Error approving bug:", error)
    }
  }

  const handleReject = async (bugId) => {
    try {
      // Replace with your actual API call
      const response = await fetch(`/api/bugs/${bugId}/reject`, {
        method: "PUT",
      })

      if (response.ok) {
        // Remove the bug from unapproved list
        setUnapprovedBugs((prev) => prev.filter((bug) => bug.id !== bugId))
      }
    } catch (error) {
      console.error("Error rejecting bug:", error)
    }
  }

  const renderBugItem = (bug, showActions = false) => {
    return (
      <div key={bug.id} className="p-4 bg-white rounded-lg shadow mb-3 border border-gray-200">
        <div className="flex justify-between items-start">
          <div>
            <h3 className="font-medium text-lg">{bug.title}</h3>
            <p className="text-sm text-gray-600 mt-1">{bug.description}</p>
            <div className="flex items-center gap-2 mt-2">
              <span className="bg-gray-100 text-gray-800 px-2 py-0.5 rounded-full text-xs font-medium inline-flex items-center">
                {bug.language}
              </span>
              <span
                className={`px-2 py-0.5 rounded-full text-xs font-medium inline-flex items-center ${
                  bug.severity === "low"
                    ? "bg-green-100 text-green-800"
                    : bug.severity === "medium"
                      ? "bg-yellow-100 text-yellow-800"
                      : bug.severity === "high"
                        ? "bg-orange-100 text-orange-800"
                        : "bg-red-100 text-red-800"
                }`}
              >
                {bug.severity}
              </span>
              <span className="text-xs text-gray-500">
                Submitted by {bug.creator?.username || "Anonymous"} on {new Date(bug.creationDate).toLocaleDateString()}
              </span>
            </div>
          </div>

          {showActions && (
            <div className="flex gap-2">
              <button
                onClick={() => handleApprove(bug.id)}
                className="p-2 bg-green-50 text-green-600 rounded-full hover:bg-green-100 transition-colors"
                title="Approve"
              >
                <CheckCircle className="h-5 w-5" />
              </button>
              <button
                onClick={() => handleReject(bug.id)}
                className="p-2 bg-red-50 text-red-600 rounded-full hover:bg-red-100 transition-colors"
                title="Reject"
              >
                <XCircle className="h-5 w-5" />
              </button>
            </div>
          )}
        </div>
      </div>
    )
  }

  return (
    <div className="flex flex-col h-screen">
      {/* Top Navigation Bar */}
      <Navbar />

      {/* Main Content Area */}
      <div className="flex flex-1">
        {/* Left Sidebar Navigation */}
        <div className="w-64 flex-shrink-0">
          <SideNav />
        </div>

        {/* Submissions Content */}
        <main className="flex-1 bg-gray-100 p-4 overflow-auto">
          <div className="max-w-6xl mx-auto">
            <h1 className="text-2xl font-bold mb-6">Bug Submissions</h1>

            {/* Custom Tabs */}
            <div className="mb-6">
              <div className="flex border-b border-gray-200">
                <button
                  onClick={() => setActiveTab("unapproved")}
                  className={`py-2 px-4 font-medium text-sm ${
                    activeTab === "unapproved"
                      ? "border-b-2 border-blue-500 text-blue-600"
                      : "text-gray-500 hover:text-gray-700"
                  }`}
                >
                  <div className="flex items-center">
                    Pending Review
                    {unapprovedBugs.length > 0 && (
                      <span className="ml-2 bg-red-100 text-red-800 text-xs font-medium px-2.5 py-0.5 rounded-full">
                        {unapprovedBugs.length}
                      </span>
                    )}
                  </div>
                </button>
                <button
                  onClick={() => setActiveTab("approved")}
                  className={`py-2 px-4 font-medium text-sm ${
                    activeTab === "approved"
                      ? "border-b-2 border-blue-500 text-blue-600"
                      : "text-gray-500 hover:text-gray-700"
                  }`}
                >
                  Approved
                </button>
              </div>
            </div>

            {/* Tab Content */}
            <div>
              {/* Unapproved Bugs Tab */}
              {activeTab === "unapproved" && (
                <div>
                  {isLoading ? (
                    <div className="flex justify-center items-center h-40">
                      <Clock className="h-6 w-6 text-blue-500 animate-spin" />
                      <span className="ml-2">Loading submissions...</span>
                    </div>
                  ) : unapprovedBugs.length === 0 ? (
                    <div className="bg-blue-50 text-blue-700 p-4 rounded-lg flex items-center">
                      <CheckCircle className="h-5 w-5 mr-2" />
                      <span>No pending submissions to review!</span>
                    </div>
                  ) : (
                    <div>
                      <p className="text-sm text-gray-600 mb-4">
                        {unapprovedBugs.length} bug submission{unapprovedBugs.length !== 1 ? "s" : ""} waiting for
                        review
                      </p>
                      {unapprovedBugs.map((bug) => renderBugItem(bug, true))}
                    </div>
                  )}
                </div>
              )}

              {/* Approved Bugs Tab */}
              {activeTab === "approved" && (
                <div>
                  {isLoading ? (
                    <div className="flex justify-center items-center h-40">
                      <Clock className="h-6 w-6 text-blue-500 animate-spin" />
                      <span className="ml-2">Loading approved bugs...</span>
                    </div>
                  ) : approvedBugs.length === 0 ? (
                    <div className="bg-yellow-50 text-yellow-700 p-4 rounded-lg flex items-center">
                      <AlertCircle className="h-5 w-5 mr-2" />
                      <span>No approved bugs yet.</span>
                    </div>
                  ) : (
                    <div>
                      <p className="text-sm text-gray-600 mb-4">
                        {approvedBugs.length} approved bug{approvedBugs.length !== 1 ? "s" : ""}
                      </p>
                      {approvedBugs.map((bug) => renderBugItem(bug))}
                    </div>
                  )}
                </div>
              )}
            </div>
          </div>
        </main>
      </div>
    </div>
  )
}

