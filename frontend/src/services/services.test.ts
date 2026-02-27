import { describe, it, expect, vi, beforeEach } from "vitest";

vi.mock("../utils/requests", () => ({
  requestBackend: vi.fn(),
}));

import { requestBackend } from "../utils/requests";
import * as productService from "./product-service";
import * as rawMaterialService from "./raw-material-service";
import * as productRawMaterialService from "./product-raw-material-service";
import * as productionSuggestionService from "./production-suggestion-service";

describe("Services", () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe("product-service", () => {
    it("findAll calls GET /api/products with pagination", () => {
      productService.findAll(0, 10);
      expect(requestBackend).toHaveBeenCalledWith(
        expect.objectContaining({
          method: "GET",
          url: "/api/products",
          params: { page: 0, size: 10 },
        }),
      );
    });

    it("findById calls GET /api/products/:id", () => {
      productService.findById(42);
      expect(requestBackend).toHaveBeenCalledWith({ url: "/api/products/42" });
    });

    it("create calls POST /api/products", () => {
      const data = { name: "Widget", value: 99.9 };
      productService.create(data);
      expect(requestBackend).toHaveBeenCalledWith(
        expect.objectContaining({
          method: "POST",
          url: "/api/products",
          data,
        }),
      );
    });

    it("update calls PUT /api/products/:id", () => {
      const data = { name: "Updated", value: 50 };
      productService.update(1, data);
      expect(requestBackend).toHaveBeenCalledWith(
        expect.objectContaining({
          method: "PUT",
          url: "/api/products/1",
          data,
        }),
      );
    });

    it("deleteById calls DELETE /api/products/:id", () => {
      productService.deleteById(5);
      expect(requestBackend).toHaveBeenCalledWith(
        expect.objectContaining({
          method: "DELETE",
          url: "/api/products/5",
        }),
      );
    });
  });

  describe("raw-material-service", () => {
    it("findAll calls GET /api/raw-materials with pagination", () => {
      rawMaterialService.findAll(1, 20);
      expect(requestBackend).toHaveBeenCalledWith(
        expect.objectContaining({
          method: "GET",
          url: "/api/raw-materials",
          params: { page: 1, size: 20 },
        }),
      );
    });

    it("create calls POST /api/raw-materials", () => {
      const data = { name: "Iron", stockQuantity: 500 };
      rawMaterialService.create(data);
      expect(requestBackend).toHaveBeenCalledWith(
        expect.objectContaining({
          method: "POST",
          url: "/api/raw-materials",
          data,
        }),
      );
    });
  });

  describe("product-raw-material-service", () => {
    it("findByProductId calls GET /api/products/:id/raw-materials", () => {
      productRawMaterialService.findByProductId(10);
      expect(requestBackend).toHaveBeenCalledWith({
        url: "/api/products/10/raw-materials",
      });
    });

    it("add calls POST /api/products/:id/raw-materials", () => {
      const data = { rawMaterialId: 3, quantity: 5 };
      productRawMaterialService.add(10, data);
      expect(requestBackend).toHaveBeenCalledWith(
        expect.objectContaining({
          method: "POST",
          url: "/api/products/10/raw-materials",
          data,
        }),
      );
    });

    it("remove calls DELETE /api/products/:pid/raw-materials/:rmid", () => {
      productRawMaterialService.remove(10, 3);
      expect(requestBackend).toHaveBeenCalledWith(
        expect.objectContaining({
          method: "DELETE",
          url: "/api/products/10/raw-materials/3",
        }),
      );
    });
  });

  describe("production-suggestion-service", () => {
    it("getSuggestion calls GET /api/production/suggestion", () => {
      productionSuggestionService.getSuggestion();
      expect(requestBackend).toHaveBeenCalledWith({
        url: "/api/production/suggestion",
      });
    });
  });
});
