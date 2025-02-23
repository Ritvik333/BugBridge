import apiClient from "../utils/apiClient";

// Function for logging in
export const login = async (credentials) => {
  try {
    const response = await apiClient.post('/auth/login', credentials); // Replace with your login endpoint
    return response.data;  // Assuming the API returns the user data or token
  } catch (error) {
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
    // First try to fetch from API
    const response = await fetch("http://localhost:8080/api/bugs");
    if (response.ok) {
      const data = await response.json();
      // Store the fetched data in localStorage
      localStorage.setItem("bugs", JSON.stringify(data));
      return data;
    }
    
    // If API fails, try to get from localStorage
    const storedBugs = localStorage.getItem("bugs");
    if (storedBugs) {
      const bugs = JSON.parse(storedBugs);
      return Array.isArray(bugs) ? bugs : [];
    }
    
    return []; // Return empty array if both API and localStorage fail
  } catch (error) {
    console.error("Error fetching bugs:", error);
    
    // Try localStorage as fallback
    try {
      const storedBugs = localStorage.getItem("bugs");
      if (storedBugs) {
        const bugs = JSON.parse(storedBugs);
        return Array.isArray(bugs) ? bugs : [];
      }
    } catch (storageError) {
      console.error("Error reading from localStorage:", storageError);
    }
    
    return []; // Return empty array if everything fails
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
    console.log(userData)
    const response = await apiClient.post('/drafts/save', userData);
    return response.data; // Return the response data from the backend (e.g., success message or saved draft details)
  } catch (error) {
    throw error.response ? error.response.data : error.message;
  }
};
