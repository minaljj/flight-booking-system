import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080',
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;
      try {
        const userData = JSON.parse(localStorage.getItem('user'));
        const refreshToken = userData?.refreshToken;
        if (refreshToken) {
          const res = await axios.post(
            `${api.defaults.baseURL}/api/v1.0/flight/auth/refresh`,
            { refreshToken }
          );
          const newToken = res.data.token;
          if (newToken) {
            localStorage.setItem('token', newToken);
            userData.token = newToken;
            localStorage.setItem('user', JSON.stringify(userData));
            originalRequest.headers['Authorization'] = `Bearer ${newToken}`;
            return api(originalRequest);
          }
        }
      } catch (refreshError) {
        localStorage.clear();
        window.location.href = '/login';
        return Promise.reject(refreshError);
      }
    }
    return Promise.reject(error);
  }
);

export default api;