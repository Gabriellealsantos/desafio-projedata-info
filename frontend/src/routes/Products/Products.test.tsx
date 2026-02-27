import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { describe, it, expect, vi, beforeEach } from "vitest";
import Products from "./index";

vi.mock("../../services/product-service", () => ({
    findAll: vi.fn(),
    findById: vi.fn(),
    create: vi.fn(),
    update: vi.fn(),
    deleteById: vi.fn(),
}));

vi.mock("../../services/raw-material-service", () => ({
    findAll: vi.fn(),
}));

vi.mock("../../services/product-raw-material-service", () => ({
    findByProductId: vi.fn(),
    add: vi.fn(),
    addBatch: vi.fn(),
    updateQuantity: vi.fn(),
    remove: vi.fn(),
}));

import * as productService from "../../services/product-service";
import * as rawMaterialService from "../../services/raw-material-service";

const mockProducts = [
    { id: 1, name: "Electrical Cabinet", value: 4200.0, rawMaterials: [] },
    { id: 2, name: "Hydraulic Pump", value: 3500.0, rawMaterials: [] },
];

const mockRawMaterials = [
    { id: 1, name: "Steel", stockQuantity: 100 },
    { id: 2, name: "Copper", stockQuantity: 50 },
];

describe("Products", () => {
    beforeEach(() => {
        vi.clearAllMocks();
        vi.mocked(productService.findAll).mockResolvedValue({
            data: { items: mockProducts },
        } as never);
        vi.mocked(rawMaterialService.findAll).mockResolvedValue({
            data: { items: mockRawMaterials },
        } as never);
    });

    it("renders the list of products", async () => {
        render(<Products showToast={vi.fn()} />);

        expect(await screen.findByText("Electrical Cabinet")).toBeInTheDocument();
        expect(screen.getByText("Hydraulic Pump")).toBeInTheDocument();
    });

    it("renders product values formatted", async () => {
        render(<Products showToast={vi.fn()} />);

        expect(await screen.findByText("R$ 4200.00")).toBeInTheDocument();
        expect(screen.getByText("R$ 3500.00")).toBeInTheDocument();
    });

    it("renders the page title", async () => {
        render(<Products showToast={vi.fn()} />);

        expect(await screen.findByText("Produtos")).toBeInTheDocument();
    });

    it("opens create modal when Novo Produto is clicked", async () => {
        const user = userEvent.setup();
        render(<Products showToast={vi.fn()} />);

        await screen.findByText("Electrical Cabinet");
        await user.click(screen.getByText("Novo Produto"));

        expect(screen.getByText("Novo Produto", { selector: "h2" })).toBeInTheDocument();
    });

    it("creates a new product", async () => {
        const user = userEvent.setup();
        const showToast = vi.fn();
        vi.mocked(productService.create).mockResolvedValue({} as never);

        render(<Products showToast={showToast} />);

        await screen.findByText("Electrical Cabinet");
        await user.click(screen.getByText("Novo Produto"));

        const nameInput = screen.getByLabelText("Nome do Produto");
        const valueInput = screen.getByLabelText("Valor Unitário (Venda)");

        await user.clear(nameInput);
        await user.type(nameInput, "New Product");
        await user.clear(valueInput);
        await user.type(valueInput, "999.99");

        await user.click(screen.getByText("Salvar Produto"));

        expect(productService.create).toHaveBeenCalledWith({
            name: "New Product",
            value: 999.99,
        });
    });

    it("deletes a product with confirmation", async () => {
        const user = userEvent.setup();
        const showToast = vi.fn();
        vi.mocked(productService.deleteById).mockResolvedValue({} as never);

        render(<Products showToast={showToast} />);

        await screen.findByText("Electrical Cabinet");

        const deleteButtons = screen.getAllByTitle("Deletar");
        await user.click(deleteButtons[0]);

        // Find the confirm button inside ConfirmDialog
        const confirmButton = screen.getByRole('button', { name: "Remover" });
        await user.click(confirmButton);

        expect(productService.deleteById).toHaveBeenCalledWith(1);
    });

    it("shows empty state when no products", async () => {
        vi.mocked(productService.findAll).mockResolvedValue({
            data: { items: [] },
        } as never);

        render(<Products showToast={vi.fn()} />);

        // Text appears in both mobile cards and desktop grid
        const emptyTexts = await screen.findAllByText("Nenhum produto encontrado.");
        expect(emptyTexts.length).toBeGreaterThanOrEqual(1);
    });
});
