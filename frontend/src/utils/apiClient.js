import axios from 'axios';

// Create an Axios instance
const apiClient = axios.create({
  baseURL: "http://localhost:8080/",  //process.env.REACT_APP_BACKEND_BASE_URL , // replace with your API base URL
  timeout: 10000, // optional timeout, in ms
  headers: {
    'Content-Type': 'application/json',
  },
});

// Intercept requests (optional)
apiClient.interceptors.request.use(
  (config) => {
    // Optionally attach token or modify headers
    // Example:
    // const token = localStorage.getItem('token');
    // if (token) {
    //   config.headers['Authorization'] = `Bearer ${token}`;
    // }
    return config;
  },
  (error) => Promise.reject(error)
);

// Intercept responses (optional)
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    // Handle errors globally (e.g., show toast notifications, redirect on 401)
    if (error.response && error.response.status === 401) {
      // Handle unauthorized access
    }
    return Promise.reject(error);
  }
);

export default apiClient;
