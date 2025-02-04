import axios from 'axios';

// Function to get auth token from local storage
export const getAuthToken = () => {
  return window.localStorage.getItem('auth_token');
};

// Function to set or remove auth token
export const setAuthHeader = (token) => {
  if (token) {
    window.localStorage.setItem("auth_token", token);
  } else {
    window.localStorage.removeItem("auth_token");
  }
};

// Create an Axios instance
const apiClient = axios.create({
  baseURL: 'http://localhost:8080', // Replace with your API base URL
  timeout: 10000, // Optional timeout in ms
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request Interceptor - Adds Authorization Header
apiClient.interceptors.request.use(
  (config) => {
    const token = getAuthToken();
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Response Interceptor - Handles Unauthorized Requests
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 401) {
      // Handle unauthorized access (e.g., redirect to login)
      console.error("Unauthorized: Redirecting to login...");
      window.localStorage.removeItem('auth_token'); // Clear token
      window.location.href = "/login"; // Redirect to login page
    }
    return Promise.reject(error);
  }
);

// General API request function
export const request = (method, url, data) => {
  return apiClient({
    method,
    url,
    data,
  });
};

export default apiClient;
