import axios from 'axios';

export const axiosInstance = axios.create({
  baseURL: '/api/v1',
});

axiosInstance.interceptors.request.use(
  config => {
    let user = JSON.parse(localStorage.getItem('user'));
    if (user && user.token) {
      config.headers.Authorization = `Bearer ${user.token}`;
    }
    config.withCredentials = true;
    return config;
  },
  error => {
    return error;
  },
);
