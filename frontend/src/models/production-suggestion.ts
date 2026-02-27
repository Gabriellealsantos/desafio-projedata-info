export type ProductionItemDTO = {
  productId: number;
  productName: string;
  productValue: number;
  quantityToProduce: number;
  subtotal: number;
};

export type ProductionSuggestionDTO = {
  items: ProductionItemDTO[];
  totalValue: number;
};
