import axios from "axios";
import store from "@/store";

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
  // 요청
  instance.interceptors.request.use(
      config => {
        const token = store.getters.token;
        if (token) {
          config.headers['Authorization'] = `Bearer ${token}`;
        }
        return config;
      },
      error => {
        return Promise.reject(error);
      }
  )

  // 응답
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
