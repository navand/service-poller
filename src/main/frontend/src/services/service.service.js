import _ from 'lodash';
import { axiosInstance } from '../helpers';


const create = (service) => {
  const requestOptions = {
    headers: { 'Content-Type': 'application/json' },
    body: service,
  };

  return axiosInstance
    .post(`/services`, requestOptions.body, requestOptions.headers)
    .then(handleResponse)
    .then(data => {
      return data;
    })
    .catch(error => {
      handleResponse(error.response);
    });
}

const update = (service) => {
  const requestOptions = {
    headers: { 'Content-Type': 'application/json' },
    body: service,
  };

  const url = `/services/${service.id}`;
  return axiosInstance
    .put(url, requestOptions.body, requestOptions.headers)
    .then(handleResponse)
    .then(data => {
      return data;
    })
    .catch(error => {
      handleResponse(error.response);
    });
}

const _delete = (serviceId) => {
  return axiosInstance
    .delete(`/services/${serviceId}`)
    .then(handleResponse)
    .then(data => {
      return data;
    })
    .catch(error => {
      handleResponse(error.response);
    });
}

const findAll = (query) => {
  const url = '/services';
  return axiosInstance
    .get(url, { params: query })
    .then(handleResponse)
    .then(data => {
      return data;
    })
    .catch(error => {
      handleResponse(error.response);
    });
}

const handleResponse = response => {
  if (!response) throw { status: 500 };
  const data = response.data;
  if (!_.includes([200, 201, 204], response.status)) {
    if (response.status === 401 && !response.config.url.includes('/users/login')) {
      // auto logout if 401 response returned from api
      localStorage.removeItem('user');
      window.location.reload();
    }

    const error = (data && data.message) || response.statusText;

    // eslint-disable-next-line no-throw-literal
    throw { status: response.status, error };
  }

  return data;
};

export const serviceService = {
  create,
  update,
  delete: _delete,
  findAll,
};
