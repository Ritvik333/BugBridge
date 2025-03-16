import apiClient from "../utils/apiClient";

// Function for logging in
export const login = async (credentials) => {
  try {
    const response = await apiClient.post('/auth/login', credentials);
    if (response.data.body && response.data.body.id) {
      sessionStorage.setItem("userId", response.data.body.id);
      sessionStorage.setItem("authToken", response.data.body.token);
    } else {
      console.error("User ID not found in login response.");
    }
    return response.data;  // Assuming the API returns the user data or token
  } catch (error) {
    console.error("Login Error:", error.response ? error.response.data : error.message);
    throw error.response ? error.response.data : error.message;
  }
};

export const signup = async (userData) => {
  try {
    const response = await apiClient.post('/auth/register', userData);
    return response.data;
  } catch (error) {
    throw error.response ? error.response.data : error.message;
  }
};

export const forgot = async (userData) => {
  try {
    const response = await apiClient.post('/auth/forgot-password', userData);
    return response.data;
  } catch (error) {
    throw error.response ? error.response.data : error.message;
  }
};

export const validate_token = async (token) => {
  try {
    const response = await apiClient.get(`/auth/validate-reset-token?token=${token}`);
    return response.data;
  } catch (error) {
    throw error.response ? error.response.data : error.message;
  }
};

export const reset_password = async (userData) => {
  try {
    const response = await apiClient.post('/auth/reset-password', userData);
    return response.data;
  } catch (error) {
    throw error.response ? error.response.data : error.message;
  }
};

export const logout = () => {
  console.log(localStorage.getItem("authToken"));
  localStorage.removeItem("authToken"); // Remove authentication token
  localStorage.clear();
};

export const runCode = async (code, language) => {
  try {
    const response = await apiClient.post("/api/run", { code, language });
    return response.data.body.output;
  } catch (error) {
    throw error.response ? error.response.data : error.message;
  }
};

export const fetchCodeFile = async (userId, username, language, filename) => {
  try {
    const response = await apiClient.get(`/api/bugs/file/${userId}/${username}/${language}/${filename}`);
    return response.data;
  } catch (error) {
    throw error.response ? error.response.data : error.message;
  }
};

export const fetchBugs = async (filters) => {
  try {
    const queryParams = new URLSearchParams();

    // Add filters to query parameters
    if (filters.filterSeverity) queryParams.append("severity", filters.filterSeverity);
    if (filters.filterStatus) queryParams.append("status", filters.filterStatus);
    if (filters.filterCreator) queryParams.append("creator", filters.filterCreator);
    queryParams.append("sortBy", filters.sortOption);
    queryParams.append("order", "asc");

    const response = await apiClient.get(`/api/bugs?${queryParams}`);
    return response.data; // Return the fetched bugs
  } catch (error) {
    throw error.response ? error.response.data : error.message;
  }
};

export const fetchUsers = async () => {
  try {
    const response = await apiClient.get("/api/users");
    return response.data;
  } catch (error) {
    throw error.response ? error.response.data : error.message;
  }
};

export const updateBug = async (bug) => {
  try {
    const formData = new FormData();
    formData.append("title", bug.title);
    formData.append("severity", bug.severity);
    formData.append("status", bug.status);
    formData.append("creatorId", bug.creator.id);
    formData.append("language", bug.language);
    formData.append("description", bug.description);
    // Append file if provided
    if (bug.codeFile) {
      formData.append("codeFilePath", bug.codeFile);
    }

    const response = await apiClient.put(`/api/bugs/${bug.id}`, formData, {
      headers: {
        "Content-Type": "multipart/form-data",
      },
    });
    return response.data;
  } catch (error) {
    throw error.response ? error.response.data : error.message;
  }

};

export const fetchComments = async (bugId) => {
  try {
    const response = await apiClient.get(`/api/comments?bugId=${bugId}`);
    return response.data;
  } catch (error) {
    throw error.response ? error.response.data : error.message;
  }
};

export const addComment = async (commentData) => {
  try {
    const response = await apiClient.post("/api/comments", commentData);
    return response.data;
  } catch (error) {
    throw error.response ? error.response.data : error.message;
  }
};

export const saveDraft = async (userData) => {
  try {
    const response = await apiClient.post('/drafts/save', userData);
    return response.data; // Return the response data from the backend (e.g., success message or saved draft details)
  } catch (error) {
    throw error.response ? error.response.data : error.message;
  }
};

export const deleteComment = async (commendId) => {
  try {
    const response = await apiClient.delete(`/api/comments/${commendId}`);
    return response.data;
  } catch (error) {
    throw error.response ? error.response.data : error.message;
  }
};

export const submitBug = async (formData) => {
  try {
    const response = await apiClient.post("/api/bugs", formData, {
      headers: {
        "Content-Type": "multipart/form-data",
      },
    });
    return response.data;
  } catch (error) {
    throw error.response ? error.response.data : error.message;
  }
};

export const submitCode = async (userData) => {
  try {
    console.log(userData);
    const response = await apiClient.post('/submissions/save', userData);
    console.log(response);
    return response.data; // Return the response data from the backend
  } catch (error) {
    throw error.response ? error.response.data : error.message;
  }
};

export const fetchUserSubmissionsByBug = async (userId, bugId) => {
  try {
    console.log(userId);
    const response = await apiClient.get(`/submissions/user/${userId}/bug/${bugId}`);
    console.log(response);
    return response.data;
  } catch (error) {
    throw error.response ? error.response.data : error.message;
  }
};

export const getAuthenticatedUser = async (userId) => {
  try {
    if (!userId) {
      throw new Error("User ID is required but not provided.");
    }

    const response = await apiClient.get(`/api/users/${userId}`);

    // console.log("Fetched User Data:", response.data); //Log response data
    return response.data;
  } catch (error) {
    console.error("Error fetching user:", error.response ? error.response.data : error.message); //Log errors
    throw error.response ? error.response.data : error.message;
  }
};

export const updateUser = async (userData) => {
  try {
    const token = sessionStorage.getItem("authToken");
    if (!token) {
      throw new Error("Authentication token is missing. Please log in again.");
    }

    // console.log("Sending profile update request with token:", token);
    // console.log("User Data Being Sent:", userData);

    const response = await apiClient.put(`/api/users/update?userId=${userData.userId}`, userData, {
      headers: {
        Authorization: `Bearer ${token}`,
        "Content-Type": "application/json"
      },
    });

    // console.log("Profile Update Response:", response.data);
    // If backend asks to logout after email update, force re-login
    if (response.data.status === "logout") {
      console.warn("Email changed. Forcing re-login.");
      sessionStorage.removeItem("authToken");
      sessionStorage.removeItem("userId");
      window.location.href = "/"; // Redirect to login page
    }
    return response.data;
  } catch (error) {
    console.error("updateUser Error:", error.response ? error.response.data : error.message);
    throw error.response ? error.response.data : error.message;
  }
};

export const sendOtp = async (userId, email) => {
  try {
    // console.log("Calling send-verification-mail API with email:", email);
    const response = await apiClient.post("/api/users/send-verification-mail", null, {
      params: { userId, email },
    });
    // console.log("OTP Sent Response:", response.data);
    return response.data;
  } catch (error) {
    console.error("sendOtp Error:", error.response ? error.response.data : error.message);
    throw error.response ? error.response.data : error.message;
  }
};

export const verifyOtp = async (userId, otp) => {
  try {
    // console.log("Calling verify-email API with userId:", userId, "and otp:", otp);
    const response = await apiClient.post("/api/users/verify-email", null, {
      params: { userId, otp },
    });
    // console.log("OTP Verification Response:", response.data);
    return response.data;
  } catch (error) {
    console.error("verifyOtp Error:", error.response ? error.response.data : error.message);
    throw error.response ? error.response.data : error.message;
  }
};

