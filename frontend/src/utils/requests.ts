import axios from "axios";
import type { AxiosRequestConfig } from "axios";
import { BASE_URL } from "./system";

export function requestBackend(config: AxiosRequestConfig) {
  return axios({ ...config, baseURL: BASE_URL });
}

// Response interceptor
axios.interceptors.response.use(
  function (response) {
    return response;
  },
  function (error) {
    return Promise.reject(error);
  },
);
