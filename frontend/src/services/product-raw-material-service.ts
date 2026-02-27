import type { AxiosRequestConfig } from "axios";
import { requestBackend } from "../utils/requests";
import type { ProductRawMaterialCreateDTO } from "../models/product-raw-material";

export function findByProductId(productId: number) {
  return requestBackend({ url: `/api/products/${productId}/raw-materials` });
}

export function add(productId: number, data: ProductRawMaterialCreateDTO) {
  const config: AxiosRequestConfig = {
    method: "POST",
    url: `/api/products/${productId}/raw-materials`,
    data,
  };
  return requestBackend(config);
}

export function addBatch(
  productId: number,
  data: ProductRawMaterialCreateDTO[],
) {
  const config: AxiosRequestConfig = {
    method: "POST",
    url: `/api/products/${productId}/raw-materials/batch`,
    data,
  };
  return requestBackend(config);
}

export function updateQuantity(
  productId: number,
  rawMaterialId: number,
  data: ProductRawMaterialCreateDTO,
) {
  const config: AxiosRequestConfig = {
    method: "PUT",
    url: `/api/products/${productId}/raw-materials/${rawMaterialId}`,
    data,
  };
  return requestBackend(config);
}

export function remove(productId: number, rawMaterialId: number) {
  const config: AxiosRequestConfig = {
    method: "DELETE",
    url: `/api/products/${productId}/raw-materials/${rawMaterialId}`,
  };
  return requestBackend(config);
}
