import { render, screen, act } from "@testing-library/react";
import { describe, it, expect, vi, beforeEach, afterEach } from "vitest";
import Toast from "./index";

describe("Toast", () => {
    beforeEach(() => {
        vi.useFakeTimers();
    });

    afterEach(() => {
        vi.useRealTimers();
    });

    it("renders success message with check icon", () => {
        render(<Toast message="Item criado!" type="success" onClose={vi.fn()} />);
        expect(screen.getByText("Item criado!")).toBeInTheDocument();
    });

    it("renders error message with alert icon", () => {
        render(<Toast message="Erro ao salvar" type="error" onClose={vi.fn()} />);
        expect(screen.getByText("Erro ao salvar")).toBeInTheDocument();
    });

    it("calls onClose automatically after 3 seconds", () => {
        const onClose = vi.fn();
        render(<Toast message="Auto close" type="success" onClose={onClose} />);

        expect(onClose).not.toHaveBeenCalled();

        act(() => {
            vi.advanceTimersByTime(3000);
        });

        expect(onClose).toHaveBeenCalledOnce();
    });
});
