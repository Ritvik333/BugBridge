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