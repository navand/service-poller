import _ from 'lodash';
import { axiosInstance } from '../helpers';

const login = (username, password) => {
  const requestOptions = {
    headers: { 'Content-Type': 'application/json' },
    body: { username, password },
  };

  return axiosInstance
    .post(`/users/login`, requestOptions.body, requestOptions.headers)
    .then(handleResponse)
    .then(user => {
      // store user details and jwt token in local storage to keep user logged in between page refreshes
      localStorage.setItem('user', JSON.stringify(user));

      return user;
    })
    .catch(error => {
      handleResponse(error.response);
    });
};

const logout = async () => {
  let user = JSON.parse(localStorage.getItem('user'));
  if (user) {
    // remove user from local storage to log user out
    localStorage.removeItem('user');
  }
};

const handleResponse = response => {
  if (!response) throw { status: 500 };
  const data = response.data;
  if (!_.includes([200, 201, 204], response.status)) {
    if (response.status === 401 && !response.config.url.includes('/users/login')) {
      // auto logout if 401 response returned from api
      logout();
      window.location.reload(true);
    }

    const error = (data && data.message) || response.statusText;

    // eslint-disable-next-line no-throw-literal
    throw { status: response.status, error };
  }

  return data;
};

export const userService = {
  login,
  logout,
};
