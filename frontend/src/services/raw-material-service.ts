import type { AxiosRequestConfig } from "axios";
import { requestBackend } from "../utils/requests";
import type { RawMaterialCreateDTO } from "../models/raw-material";

export function findAll(page: number, size = 10) {
  const config: AxiosRequestConfig = {
    method: "GET",
    url: "/api/raw-materials",
    params: { page, size },
  };
  return requestBackend(config);
}

export function findById(id: number) {
  return requestBackend({ url: `/api/raw-materials/${id}` });
}

export function create(data: RawMaterialCreateDTO) {
  const config: AxiosRequestConfig = {
    method: "POST",
    url: "/api/raw-materials",
    data,
  };
  return requestBackend(config);
}

export function update(id: number, data: RawMaterialCreateDTO) {
  const config: AxiosRequestConfig = {
    method: "PUT",
    url: `/api/raw-materials/${id}`,
    data,
  };
  return requestBackend(config);
}

export function deleteById(id: number) {
  const config: AxiosRequestConfig = {
    method: "DELETE",
    url: `/api/raw-materials/${id}`,
  };
  return requestBackend(config);
}
