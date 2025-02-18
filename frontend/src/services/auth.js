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

export const fetchBugs = async ({ filterSeverity, filterStatus, filterCreator, sortOption }) => {
  try {
    const queryParams = new URLSearchParams();
    if (filterSeverity) queryParams.append("severity", filterSeverity);
    if (filterStatus) queryParams.append("status", filterStatus);
    if (filterCreator) queryParams.append("creator_id", filterCreator);
    queryParams.append("sortBy", sortOption);
    queryParams.append("order", "asc");

    const response = await apiClient.get(`/api/bugs?${queryParams}`);
    return response.data;
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

export const createBug = async (bugData) => {
  try {
    const response = await apiClient.post("/api/bugs", { ...bugData, creator_id: bugData.creator });
    return response.data;
  } catch (error) {
    throw error.response ? error.response.data : error.message;
  }
};

export const updateBug = async (bugId, updatedData) => {
  try {
    const response = await apiClient.put(`/api/bugs/${bugId}`, updatedData);
    return response.data;
  } catch (error) {
    throw error.response ? error.response.data : error.message;
  }
};

export const deleteBug = async (bugId) => {
  try {
    await apiClient.delete(`/api/bugs/${bugId}`);
  } catch (error) {
    throw error.response ? error.response.data : error.message;
  }
};

