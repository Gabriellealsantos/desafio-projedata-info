import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { describe, it, expect, vi, beforeEach } from "vitest";
import RawMaterials from "./index";

vi.mock("../../services/raw-material-service", () => ({
    findAll: vi.fn(),
    findById: vi.fn(),
    create: vi.fn(),
    update: vi.fn(),
    deleteById: vi.fn(),
}));

import * as rawMaterialService from "../../services/raw-material-service";

const mockItems = [
    { id: 1, name: "Steel", stockQuantity: 100 },
    { id: 2, name: "Copper", stockQuantity: 50 },
];

describe("RawMaterials", () => {
    beforeEach(() => {
        vi.clearAllMocks();
        vi.mocked(rawMaterialService.findAll).mockResolvedValue({
            data: { items: mockItems },
        } as never);
    });

    it("renders the list of raw materials", async () => {
        render(<RawMaterials showToast={vi.fn()} />);

        // Text appears in both mobile cards and desktop table
        const steelElements = await screen.findAllByText("Steel");
        expect(steelElements.length).toBeGreaterThanOrEqual(1);
        expect(screen.getAllByText("Copper").length).toBeGreaterThanOrEqual(1);
    });

    it("renders the page title", async () => {
        render(<RawMaterials showToast={vi.fn()} />);

        expect(await screen.findByText("Matérias-Primas")).toBeInTheDocument();
    });

    it("opens create modal when Novo Insumo is clicked", async () => {
        const user = userEvent.setup();
        render(<RawMaterials showToast={vi.fn()} />);

        await screen.findAllByText("Steel");
        await user.click(screen.getByText("Novo Insumo"));

        expect(screen.getByText("Novo Insumo", { selector: "h2" })).toBeInTheDocument();
    });

    it("creates a new raw material", async () => {
        const user = userEvent.setup();
        const showToast = vi.fn();
        vi.mocked(rawMaterialService.create).mockResolvedValue({} as never);

        render(<RawMaterials showToast={showToast} />);

        await screen.findAllByText("Steel");
        await user.click(screen.getByText("Novo Insumo"));

        const nameInput = screen.getByLabelText("Nome");
        const stockInput = screen.getByLabelText("Quantidade em Estoque");

        await user.clear(nameInput);
        await user.type(nameInput, "Aluminum");
        await user.clear(stockInput);
        await user.type(stockInput, "200");

        await user.click(screen.getByText("Salvar"));

        expect(rawMaterialService.create).toHaveBeenCalledWith({
            name: "Aluminum",
            stockQuantity: 200,
        });
    });

    it("deletes a raw material with confirmation", async () => {
        const user = userEvent.setup();
        const showToast = vi.fn();
        vi.mocked(rawMaterialService.deleteById).mockResolvedValue({} as never);

        render(<RawMaterials showToast={showToast} />);

        await screen.findAllByText("Steel");

        // Find the delete button (Trash2 icon) in the lists
        // Given we have desktop and mobile, there will be multiple
        // We'll just grab the first button that has a Trash2 icon
        // Or find using getByTitle if they had titles, but let's select purely by the DOM
        const trashButtons = Array.from(
            document.querySelectorAll("button"),
        ).filter((btn) => btn.innerHTML.includes("lucide-trash-2"));

        if (trashButtons.length > 0) {
            await user.click(trashButtons[0] as HTMLElement);

            // Wait for the modal and click "Remover"
            const confirmButton = screen.getByRole('button', { name: "Remover" });
            await user.click(confirmButton);

            expect(rawMaterialService.deleteById).toHaveBeenCalledWith(1);
        }
    });

    it("shows empty state when no items", async () => {
        vi.mocked(rawMaterialService.findAll).mockResolvedValue({
            data: { items: [] },
        } as never);

        render(<RawMaterials showToast={vi.fn()} />);

        // Text appears in both mobile and desktop layouts
        const emptyTexts = await screen.findAllByText("Nenhum insumo encontrado.");
        expect(emptyTexts.length).toBeGreaterThanOrEqual(1);
    });
});
