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
};

export const runCode = async (code, language) => {
  try {
    const response = await apiClient.post("/api/run", { code, language });
    return response.data.body.output;
  } catch (error) {
    throw error.response ? error.response.data : error.message;
  }
};

export const fetchUsers = async () => {
  try {
    const response = await apiClient.get("/api/users");
    return response.data;
  } catch (error) {
    console.error("Error fetching users:", error);
    return [];
  }
};

export const fetchBugs = async (filters) => {
  try {
    const queryParams = new URLSearchParams(filters).toString();
    const response = await apiClient.get(`/api/bugs?${queryParams}`);
    return response.data;
  } catch (error) {
    console.error("Error fetching bugs:", error);
    return [];
  }
};

export const createBug = async (bugData) => {
  try {
    await apiClient.post("/api/bugs", { ...bugData, creator_id: bugData.creator });
  } catch (error) {
    console.error("Error creating bug:", error);
  }
};

export const updateBug = async (bugId, updatedData) => {
  try {
    await apiClient.put(`/api/bugs/${bugId}`, updatedData);
  } catch (error) {
    console.error("Error updating bug:", error);
  }
};

export const deleteBug = async (bugId) => {
  try {
    await apiClient.delete(`/api/bugs/${bugId}`);
  } catch (error) {
    console.error("Error deleting bug:", error);
  }
};

