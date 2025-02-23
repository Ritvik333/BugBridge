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
export const saveDraft = async (userData) => {
  try {
    console.log(userData)
    const response = await apiClient.post('/drafts/save', userData);
    return response.data; // Return the response data from the backend (e.g., success message or saved draft details)
  } catch (error) {
    throw error.response ? error.response.data : error.message;
  }
};
