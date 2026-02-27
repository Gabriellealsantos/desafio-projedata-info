import type { ProductRawMaterialDTO } from "./product-raw-material";

export type ProductDTO = {
  id: number;
  name: string;
  value: number;
  rawMaterials: ProductRawMaterialDTO[];
};

export type ProductCreateDTO = {
  name: string;
  value: number;
};
