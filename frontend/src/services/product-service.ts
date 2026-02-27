import type { AxiosRequestConfig } from "axios";
import { requestBackend } from "../utils/requests";
import type { ProductCreateDTO } from "../models/product";

export function findAll(page: number, size = 10) {
  const config: AxiosRequestConfig = {
    method: "GET",
    url: "/api/products",
    params: { page, size },
  };
  return requestBackend(config);
}

export function findById(id: number) {
  return requestBackend({ url: `/api/products/${id}` });
}

export function create(data: ProductCreateDTO) {
  const config: AxiosRequestConfig = {
    method: "POST",
    url: "/api/products",
    data,
  };
  return requestBackend(config);
}

export function update(id: number, data: ProductCreateDTO) {
  const config: AxiosRequestConfig = {
    method: "PUT",
    url: `/api/products/${id}`,
    data,
  };
  return requestBackend(config);
}

export function deleteById(id: number) {
  const config: AxiosRequestConfig = {
    method: "DELETE",
    url: `/api/products/${id}`,
  };
  return requestBackend(config);
}
