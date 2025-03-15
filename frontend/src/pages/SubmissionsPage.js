// export default function SubmissionsPage() {
//     return (
//         <div className="p-4 bg-white shadow-md rounded-lg">
//             <h2 className="text-xl font-semibold mb-2">Submissions</h2>
//             <p>List of submissions will be displayed here...</p>
//         </div>
//     );
// }
"use client"

import { useState, useEffect, useMemo } from "react"
import Navbar from "../components/Navbar"
import SideNav from "../components/SideNav"
import {
  CheckCircle,
  XCircle,
  Clock,
  AlertCircle,
  RefreshCcw,
  ChevronDown,
  ChevronUp,
  Search,
  Filter,
} from "lucide-react"

// Dummy data for bugs
const bugsList = [
  {
    id: 101,
    title: "Navigation bar breaks on mobile view",
    description:
      "When viewing the application on mobile devices, the navigation bar elements overlap and become unusable.",
    severity: "high",
    status: "open",
  },
  {
    id: 102,
    title: "Database connection timeout during peak hours",
    description:
      "Users report errors when trying to save data between 2-4pm daily, likely due to connection pool exhaustion.",
    severity: "critical",
    status: "open",
  },
  {
    id: 103,
    title: "Form validation fails with special characters",
    description: "Input validation incorrectly rejects valid inputs containing hyphens and apostrophes.",
    severity: "medium",
    status: "open",
  },
  {
    id: 104,
    title: "Memory leak in image processing module",
    description: "Application memory usage grows steadily when processing multiple images in sequence.",
    severity: "high",
    status: "open",
  },
  {
    id: 105,
    title: "API rate limiting not working correctly",
    description:
      "The rate limiting middleware is not correctly tracking requests per IP, allowing potential DoS vulnerabilities.",
    severity: "medium",
    status: "open",
  },
]

// Dummy data for solutions to bugs
const initialPendingSolutions = [
  {
    id: 1,
    bugId: 101,
    title: "Fix for navigation bar on mobile devices",
    description:
      "Implemented responsive design with media queries to adjust the navigation bar layout on smaller screens. Added hamburger menu for mobile view and fixed z-index issues causing overlap.",
    severity: "high",
    language: "JavaScript",
    codeSnippet: `@media (max-width: 768px) {
  .navbar {
    flex-direction: column;
    position: relative;
  }
  .navbar-toggle {
    display: block;
  }
  .nav-links {
    display: none;
    width: 100%;
  }
  .nav-links.active {
    display: flex;
    flex-direction: column;
  }
}`,
    creator: { username: "sarah_dev" },
    creationDate: "2025-03-11T12:58:10Z",
    status: "pending",
  },
  {
    id: 2,
    bugId: 101,
    title: "Alternative navigation fix using CSS Grid",
    description:
      "Implemented a CSS Grid-based solution that maintains the navigation structure on mobile without requiring JavaScript. This approach is more accessible and performs better on low-end devices.",
    severity: "high",
    language: "CSS",
    codeSnippet: `.navbar {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
}

@media (max-width: 768px) {
  .navbar {
    grid-template-columns: 1fr;
    gap: 0.5rem;
  }
}`,
    creator: { username: "css_wizard" },
    creationDate: "2025-03-10T09:32:45Z",
    status: "pending",
  },
  {
    id: 3,
    bugId: 102,
    title: "Database connection pool optimization",
    description:
      "Implemented connection pooling with proper timeout settings and increased max connections during peak hours. Added retry mechanism with exponential backoff to handle temporary connection failures.",
    severity: "critical",
    language: "SQL",
    codeSnippet: `// Connection pool configuration
const pool = new Pool({
  max: 20,
  min: 5,
  idle: 10000,
  acquire: 30000,
  maxUses: 50,
  strategy: 'lifo'
});

// Dynamic scaling based on time of day
const getPoolSize = () => {
  const hour = new Date().getHours();
  return (hour >= 14 && hour <= 16) ? 30 : 20;
};`,
    creator: { username: "db_master" },
    creationDate: "2025-03-09T14:22:00Z",
    status: "pending",
  },
  {
    id: 4,
    bugId: 102,
    title: "Database query optimization and caching",
    description:
      "Optimized slow queries and implemented Redis caching for frequently accessed data. This reduces database load during peak hours and improves response times.",
    severity: "critical",
    language: "JavaScript",
    codeSnippet: `// Cache middleware
async function cacheMiddleware(req, res, next) {
  const cacheKey = \`data:\${req.url}\`;
  
  try {
    // Check if data exists in cache
    const cachedData = await redisClient.get(cacheKey);
    
    if (cachedData) {
      return res.json(JSON.parse(cachedData));
    }
    
    // Store original res.json method
    const originalJson = res.json;
    
    // Override res.json to cache the response
    res.json = function(data) {
      // Cache the data with expiration
      redisClient.set(cacheKey, JSON.stringify(data), 'EX', 300);
      return originalJson.call(this, data);
    };
    
    next();
  } catch (err) {
    console.error('Cache error:', err);
    next();
  }
}`,
    creator: { username: "cache_expert" },
    creationDate: "2025-03-08T16:45:30Z",
    status: "pending",
  },
  {
    id: 5,
    bugId: 103,
    title: "Form validation fix for special characters",
    description:
      "Updated regex patterns in form validation to properly handle hyphens, apostrophes and other special characters. Added comprehensive test cases to verify all edge cases.",
    severity: "medium",
    language: "TypeScript",
    codeSnippet: `// Updated validation regex
const nameRegex = /^[a-zA-Z0-9\\s\\-'\\.]+$/;

// Validation function with proper error messages
function validateInput(input: string): ValidationResult {
  if (!nameRegex.test(input)) {
    return {
      valid: false,
      error: 'Name may contain letters, numbers, spaces, hyphens and apostrophes'
    };
  }
  return { valid: true };
}`,
    creator: { username: "form_wizard" },
    creationDate: "2025-03-07T09:15:00Z",
    status: "pending",
  },
  {
    id: 6,
    bugId: 104,
    title: "Memory leak fix in image processing module",
    description:
      "Identified and fixed memory leak by properly disposing of image resources after processing. Implemented proper cleanup in finally blocks and added memory usage monitoring.",
    severity: "high",
    language: "C++",
    codeSnippet: `void processImage(const Image& img) {
  try {
    // Allocate resources
    auto* buffer = new uint8_t[img.width * img.height * 4];
    
    // Process image
    // ...
    
    // Clean up properly
    delete[] buffer;
  } catch (const std::exception& e) {
    // Log error
    std::cerr << "Error processing image: " << e.what() << std::endl;
    
    // Still clean up even on error
    if (buffer) delete[] buffer;
    
    throw; // Re-throw the exception
  }
}`,
    creator: { username: "memory_hunter" },
    creationDate: "2025-03-06T16:45:00Z",
    status: "pending",
  },
  {
    id: 7,
    bugId: 104,
    title: "Smart pointer implementation for memory management",
    description:
      "Refactored the image processing module to use smart pointers (std::unique_ptr) instead of raw pointers, eliminating the memory leak by ensuring proper resource cleanup.",
    severity: "high",
    language: "C++",
    codeSnippet: `void processImage(const Image& img) {
  // Use smart pointer for automatic cleanup
  std::unique_ptr<uint8_t[]> buffer(new uint8_t[img.width * img.height * 4]);
  
  try {
    // Process image using buffer.get() to access the raw pointer
    // ...
    
    // No need for manual cleanup - will be handled automatically
  } catch (const std::exception& e) {
    std::cerr << "Error processing image: " << e.what() << std::endl;
    throw; // Smart pointer will still clean up on exception
  }
}`,
    creator: { username: "cpp_guru" },
    creationDate: "2025-03-05T11:20:15Z",
    status: "pending",
  },
  {
    id: 8,
    bugId: 105,
    title: "API rate limiting implementation",
    description:
      "Implemented proper rate limiting using Redis to track requests per IP address. Added sliding window algorithm to prevent DoS attacks while maintaining good user experience.",
    severity: "medium",
    language: "Node.js",
    codeSnippet: `// Rate limiting middleware
const rateLimiter = async (req, res, next) => {
  const ip = req.ip;
  const key = \`ratelimit:\${ip}\`;
  
  try {
    // Get current count for this IP
    const current = await redisClient.get(key) || 0;
    
    if (current > RATE_LIMIT) {
      return res.status(429).json({
        error: 'Too many requests, please try again later'
      });
    }
    
    // Increment count and set expiry
    await redisClient.multi()
      .incr(key)
      .expire(key, WINDOW_SIZE_IN_SECONDS)
      .exec();
      
    next();
  } catch (err) {
    console.error('Rate limiting error:', err);
    next(); // Fail open to prevent blocking all traffic
  }
};`,
    creator: { username: "api_guru" },
    creationDate: "2025-03-04T11:30:00Z",
    status: "pending",
  },
]

export default function SubmissionsPage() {
  const [activeTab, setActiveTab] = useState("unapproved")
  const [approvedSolutions, setApprovedSolutions] = useState([])
  const [unapprovedSolutions, setUnapprovedSolutions] = useState([])
  const [rejectedSolutions, setRejectedSolutions] = useState([])
  const [isLoading, setIsLoading] = useState(true)
  const [expandedSolutions, setExpandedSolutions] = useState({})
  const [filterBugId, setFilterBugId] = useState("")
  const [showBugSuggestions, setShowBugSuggestions] = useState(false)
  const [searchTerm, setSearchTerm] = useState("")

  // Initialize with dummy data
  useEffect(() => {
    // Simulate API loading
    const loadDummyData = async () => {
      setIsLoading(true)
      // Simulate network delay
      await new Promise((resolve) => setTimeout(resolve, 1000))
      setUnapprovedSolutions(initialPendingSolutions)
      setApprovedSolutions([])
      setRejectedSolutions([])
      setIsLoading(false)
    }

    loadDummyData()
  }, [])

  // Toggle solution expanded state
  const toggleSolutionExpanded = (solutionId) => {
    setExpandedSolutions((prev) => ({
      ...prev,
      [solutionId]: !prev[solutionId],
    }))
  }

  // Format date in the required format
  const formatDate = (dateString) => {
    const date = new Date(dateString)
    return (
      date.toLocaleDateString("en-US", {
        weekday: "long",
        year: "numeric",
        month: "long",
        day: "numeric",
      }) +
      " at " +
      date.toLocaleTimeString("en-US")
    )
  }

  // Filter solutions based on selected bug
  const filteredSolutions = useMemo(() => {
    let solutions = []

    if (activeTab === "unapproved") {
      solutions = unapprovedSolutions
    } else if (activeTab === "approved") {
      solutions = approvedSolutions
    } else {
      solutions = rejectedSolutions
    }

    // Filter by bug if selected
    if (filterBugId) {
      solutions = solutions.filter((solution) => solution.bugId === Number.parseInt(filterBugId))
    }

    // Filter by search term if provided
    if (searchTerm) {
      const term = searchTerm.toLowerCase()
      solutions = solutions.filter(
        (solution) =>
          solution.title.toLowerCase().includes(term) ||
          solution.description.toLowerCase().includes(term) ||
          solution.creator.username.toLowerCase().includes(term) ||
          solution.language.toLowerCase().includes(term),
      )
    }

    return solutions
  }, [activeTab, unapprovedSolutions, approvedSolutions, rejectedSolutions, filterBugId, searchTerm])

  // Get bug details by ID
  const getBugById = (bugId) => {
    return bugsList.find((bug) => bug.id === bugId) || { title: "Unknown Bug" }
  }

  const handleApprove = (solutionId) => {
    // Find the solution to approve
    const solutionToApprove = unapprovedSolutions.find((solution) => solution.id === solutionId)

    if (solutionToApprove) {
      // Remove from unapproved list
      setUnapprovedSolutions((prev) => prev.filter((solution) => solution.id !== solutionId))

      // Add to approved list with updated status
      setApprovedSolutions((prev) => [
        ...prev,
        { ...solutionToApprove, status: "approved", approvedDate: new Date().toISOString() },
      ])
    }
  }

  const handleReject = (solutionId) => {
    // Find the solution to reject
    const solutionToReject = unapprovedSolutions.find((solution) => solution.id === solutionId)

    if (solutionToReject) {
      // Remove from unapproved list
      setUnapprovedSolutions((prev) => prev.filter((solution) => solution.id !== solutionId))

      // Add to rejected list with updated status
      setRejectedSolutions((prev) => [
        ...prev,
        { ...solutionToReject, status: "rejected", rejectedDate: new Date().toISOString() },
      ])
    }
  }

  // Function to reconsider a rejected solution (move back to pending)
  const handleReconsider = (solutionId) => {
    // Find the solution to reconsider
    const solutionToReconsider = rejectedSolutions.find((solution) => solution.id === solutionId)

    if (solutionToReconsider) {
      // Remove from rejected list
      setRejectedSolutions((prev) => prev.filter((solution) => solution.id !== solutionId))

      // Add back to unapproved list with updated status
      setUnapprovedSolutions((prev) => [...prev, { ...solutionToReconsider, status: "pending" }])
    }
  }

  const renderSolutionItem = (solution, showActions = false, showReconsider = false) => {
    const isExpanded = expandedSolutions[solution.id] || false
    const bug = getBugById(solution.bugId)

    return (
      <div key={solution.id} className="bg-white rounded-lg shadow mb-3 border border-gray-200 overflow-hidden">
        {/* Solution header - always visible */}
        <div
          className="p-4 cursor-pointer hover:bg-gray-50 transition-colors"
          onClick={() => toggleSolutionExpanded(solution.id)}
        >
          <div className="flex justify-between items-center">
            <div>
              <div className="text-sm text-gray-500">
                <span className="font-medium text-gray-700">
                  Solution by: {solution.creator?.username || "Anonymous"}
                </span>
              </div>
              <div className="text-xs text-gray-400 mt-1">{formatDate(solution.creationDate)}</div>
            </div>
            <div className="flex items-center">
              <span
                className={`px-2 py-0.5 mr-2 rounded-full text-xs font-medium inline-flex items-center ${
                  solution.severity === "low"
                    ? "bg-green-100 text-green-800"
                    : solution.severity === "medium"
                      ? "bg-yellow-100 text-yellow-800"
                      : solution.severity === "high"
                        ? "bg-orange-100 text-orange-800"
                        : "bg-red-100 text-red-800"
                }`}
              >
                {solution.severity}
              </span>
              {isExpanded ? (
                <ChevronUp className="h-5 w-5 text-gray-400" />
              ) : (
                <ChevronDown className="h-5 w-5 text-gray-400" />
              )}
            </div>
          </div>
        </div>

        {/* Expanded solution details */}
        {isExpanded && (
          <div className="border-t border-gray-100">
            <div className="p-4">
              <div className="mb-3 bg-blue-50 p-3 rounded-md">
                <div className="text-xs font-medium text-blue-800 mb-1">Related Bug:</div>
                <div className="text-sm font-medium">{bug.title}</div>
                <div className="text-xs text-blue-700 mt-1">{bug.description}</div>
              </div>

              <h3 className="font-medium text-lg mb-2">{solution.title}</h3>
              <p className="text-sm text-gray-600 mb-3">{solution.description}</p>

              {solution.codeSnippet && (
                <div className="mb-3 bg-gray-50 p-3 rounded border border-gray-200 overflow-x-auto">
                  <pre className="text-xs text-gray-800 font-mono whitespace-pre-wrap">{solution.codeSnippet}</pre>
                </div>
              )}

              <div className="flex items-center gap-2 mt-3">
                <span className="bg-gray-100 text-gray-800 px-2 py-0.5 rounded-full text-xs font-medium inline-flex items-center">
                  {solution.language}
                </span>
                {solution.approvedDate && (
                  <span className="text-xs text-green-600">Approved on {formatDate(solution.approvedDate)}</span>
                )}
                {solution.rejectedDate && (
                  <span className="text-xs text-red-600">Rejected on {formatDate(solution.rejectedDate)}</span>
                )}
              </div>
            </div>

            {/* Action buttons */}
            {(showActions || showReconsider) && (
              <div className="bg-gray-50 px-4 py-3 flex justify-end border-t border-gray-100">
                {showActions && (
                  <>
                    <button
                      onClick={(e) => {
                        e.stopPropagation()
                        handleApprove(solution.id)
                      }}
                      className="px-3 py-1 bg-green-50 text-green-600 rounded-md hover:bg-green-100 transition-colors mr-2 flex items-center"
                    >
                      <CheckCircle className="h-4 w-4 mr-1" />
                      Approve
                    </button>
                    <button
                      onClick={(e) => {
                        e.stopPropagation()
                        handleReject(solution.id)
                      }}
                      className="px-3 py-1 bg-red-50 text-red-600 rounded-md hover:bg-red-100 transition-colors flex items-center"
                    >
                      <XCircle className="h-4 w-4 mr-1" />
                      Reject
                    </button>
                  </>
                )}

                {showReconsider && (
                  <button
                    onClick={(e) => {
                      e.stopPropagation()
                      handleReconsider(solution.id)
                    }}
                    className="px-3 py-1 bg-blue-50 text-blue-600 rounded-md hover:bg-blue-100 transition-colors flex items-center"
                  >
                    <RefreshCcw className="h-4 w-4 mr-1" />
                    Reconsider
                  </button>
                )}
              </div>
            )}
          </div>
        )}
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
            <h1 className="text-2xl font-bold mb-6">Solution Submissions</h1>

            {/* Filters */}
            <div className="mb-6 bg-white p-4 rounded-lg shadow">
              <div className="flex flex-col md:flex-row gap-4">
                {/* Search input */}
                <div className="flex-1 relative">
                  <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                    <Search className="h-4 w-4 text-gray-400" />
                  </div>
                  <input
                    type="text"
                    placeholder="Search solutions..."
                    className="pl-10 pr-4 py-2 w-full border rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                  />
                </div>

                {/* Bug filter */}
                <div className="relative">
                  <div className="flex items-center">
                    <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                      <Filter className="h-4 w-4 text-gray-400" />
                    </div>
                    <button
                      className="pl-10 pr-4 py-2 border rounded-md bg-white text-gray-700 flex items-center justify-between min-w-[200px]"
                      onClick={() => setShowBugSuggestions(!showBugSuggestions)}
                    >
                      <span>
                        {filterBugId
                          ? getBugById(Number.parseInt(filterBugId)).title.substring(0, 20) +
                            (getBugById(Number.parseInt(filterBugId)).title.length > 20 ? "..." : "")
                          : "Filter by bug"}
                      </span>
                      <ChevronDown className="h-4 w-4 ml-2" />
                    </button>
                  </div>

                  {/* Bug suggestions dropdown */}
                  {showBugSuggestions && (
                    <div className="absolute z-10 mt-1 w-full bg-white border rounded-md shadow-lg max-h-60 overflow-auto">
                      <div
                        className="p-2 hover:bg-gray-100 cursor-pointer text-sm"
                        onClick={() => {
                          setFilterBugId("")
                          setShowBugSuggestions(false)
                        }}
                      >
                        All Bugs
                      </div>
                      {bugsList.map((bug) => (
                        <div
                          key={bug.id}
                          className="p-2 hover:bg-gray-100 cursor-pointer text-sm"
                          onClick={() => {
                            setFilterBugId(bug.id.toString())
                            setShowBugSuggestions(false)
                          }}
                        >
                          {bug.title}
                        </div>
                      ))}
                    </div>
                  )}
                </div>
              </div>
            </div>

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
                    {unapprovedSolutions.length > 0 && (
                      <span className="ml-2 bg-red-100 text-red-800 text-xs font-medium px-2.5 py-0.5 rounded-full">
                        {unapprovedSolutions.length}
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
                <button
                  onClick={() => setActiveTab("rejected")}
                  className={`py-2 px-4 font-medium text-sm ${
                    activeTab === "rejected"
                      ? "border-b-2 border-blue-500 text-blue-600"
                      : "text-gray-500 hover:text-gray-700"
                  }`}
                >
                  <div className="flex items-center">
                    Rejected
                    {rejectedSolutions.length > 0 && (
                      <span className="ml-2 bg-gray-100 text-gray-800 text-xs font-medium px-2.5 py-0.5 rounded-full">
                        {rejectedSolutions.length}
                      </span>
                    )}
                  </div>
                </button>
              </div>
            </div>

            {/* Tab Content */}
            <div>
              {/* Loading state */}
              {isLoading ? (
                <div className="flex justify-center items-center h-40">
                  <Clock className="h-6 w-6 text-blue-500 animate-spin" />
                  <span className="ml-2">Loading submissions...</span>
                </div>
              ) : (
                <>
                  {/* Filter information */}
                  {filterBugId && (
                    <div className="mb-4 p-3 bg-blue-50 rounded-md text-sm text-blue-700 flex items-center">
                      <AlertCircle className="h-4 w-4 mr-2" />
                      <span>
                        Showing solutions for bug: <strong>{getBugById(Number.parseInt(filterBugId)).title}</strong>
                      </span>
                    </div>
                  )}

                  {/* Unapproved Solutions Tab */}
                  {activeTab === "unapproved" && (
                    <div>
                      {filteredSolutions.length === 0 ? (
                        <div className="bg-blue-50 text-blue-700 p-4 rounded-lg flex items-center">
                          <CheckCircle className="h-5 w-5 mr-2" />
                          <span>No pending solutions to review!</span>
                        </div>
                      ) : (
                        <div>
                          <p className="text-sm text-gray-600 mb-4">
                            {filteredSolutions.length} solution{filteredSolutions.length !== 1 ? "s" : ""} waiting for
                            review
                          </p>
                          {filteredSolutions.map((solution) => renderSolutionItem(solution, true))}
                        </div>
                      )}
                    </div>
                  )}

                  {/* Approved Solutions Tab */}
                  {activeTab === "approved" && (
                    <div>
                      {filteredSolutions.length === 0 ? (
                        <div className="bg-yellow-50 text-yellow-700 p-4 rounded-lg flex items-center">
                          <AlertCircle className="h-5 w-5 mr-2" />
                          <span>No approved solutions yet.</span>
                        </div>
                      ) : (
                        <div>
                          <p className="text-sm text-gray-600 mb-4">
                            {filteredSolutions.length} approved solution{filteredSolutions.length !== 1 ? "s" : ""}
                          </p>
                          {filteredSolutions.map((solution) => renderSolutionItem(solution))}
                        </div>
                      )}
                    </div>
                  )}

                  {/* Rejected Solutions Tab */}
                  {activeTab === "rejected" && (
                    <div>
                      {filteredSolutions.length === 0 ? (
                        <div className="bg-gray-50 text-gray-700 p-4 rounded-lg flex items-center">
                          <CheckCircle className="h-5 w-5 mr-2" />
                          <span>No rejected solutions.</span>
                        </div>
                      ) : (
                        <div>
                          <p className="text-sm text-gray-600 mb-4">
                            {filteredSolutions.length} rejected solution{filteredSolutions.length !== 1 ? "s" : ""}
                          </p>
                          {filteredSolutions.map((solution) => renderSolutionItem(solution, false, true))}
                        </div>
                      )}
                    </div>
                  )}
                </>
              )}
            </div>
          </div>
        </main>
      </div>
    </div>
  )
}
