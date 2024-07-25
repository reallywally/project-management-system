import axios from "axios";

function create(url) {
  const request = Object.assign({
    baseURL: url,
    withCredentials: true,
    headers: {
      "Content-Type": "application/json"
    },
  });
  const instance = axios.create(request);
  registerInterceptor(instance);
  return instance;
}

function registerInterceptor(instance) {
  instance.interceptors.response.use(
    function (response) {
      return response;
    },
    function (error) {
      return Promise.reject(error);
    }
  );
}

export const request = create(`http://localhost:8082`);
