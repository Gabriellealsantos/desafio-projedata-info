export type ProductRawMaterialDTO = {
  id: number;
  rawMaterialId: number;
  rawMaterialName: string;
  quantity: number;
};

export type ProductRawMaterialCreateDTO = {
  rawMaterialId: number;
  quantity: number;
};
