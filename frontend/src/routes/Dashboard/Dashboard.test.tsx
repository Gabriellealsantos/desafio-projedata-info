import { render, screen } from "@testing-library/react";
import { describe, it, expect, vi, beforeEach } from "vitest";
import Dashboard from "./index";

vi.mock("../../services/production-suggestion-service", () => ({
    getSuggestion: vi.fn(),
}));

import * as productionSuggestionService from "../../services/production-suggestion-service";

const mockSuggestion = {
    items: [
        {
            productId: 1,
            productName: "Electrical Cabinet",
            productValue: 4200.0,
            quantityToProduce: 16,
            subtotal: 67200.0,
        },
        {
            productId: 2,
            productName: "Hydraulic Pump",
            productValue: 3500.0,
            quantityToProduce: 6,
            subtotal: 21000.0,
        },
    ],
    totalValue: 88200.0,
};

describe("Dashboard", () => {
    beforeEach(() => {
        vi.clearAllMocks();
    });

    it("shows loading state initially", () => {
        vi.mocked(productionSuggestionService.getSuggestion).mockReturnValue(
            new Promise(() => { }),
        );

        render(<Dashboard showToast={vi.fn()} />);
        expect(document.querySelector(".animate-spin")).toBeInTheDocument();
    });

    it("renders suggestion data after loading", async () => {
        vi.mocked(productionSuggestionService.getSuggestion).mockResolvedValue({
            data: mockSuggestion,
        } as never);

        render(<Dashboard showToast={vi.fn()} />);

        expect(await screen.findByText(/88\.200,00/)).toBeInTheDocument();
        // Text appears in both mobile cards and desktop table
        expect(screen.getAllByText("Electrical Cabinet").length).toBeGreaterThanOrEqual(1);
        expect(screen.getAllByText("Hydraulic Pump").length).toBeGreaterThanOrEqual(1);
    });

    it("shows empty state when no products can be produced", async () => {
        vi.mocked(productionSuggestionService.getSuggestion).mockResolvedValue({
            data: { items: [], totalValue: 0 },
        } as never);

        render(<Dashboard showToast={vi.fn()} />);

        expect(
            await screen.findByText(/0,00/),
        ).toBeInTheDocument();
    });

    it("shows toast on error", async () => {
        const showToast = vi.fn();
        vi.mocked(productionSuggestionService.getSuggestion).mockRejectedValue({
            message: "Network Error",
        });

        render(<Dashboard showToast={showToast} />);

        await vi.waitFor(() => {
            expect(showToast).toHaveBeenCalledWith("Network Error", "error");
        });
    });
});
