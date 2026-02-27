import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { describe, it, expect, vi } from "vitest";
import Modal from "./index";

describe("Modal", () => {
    it("renders nothing when isOpen is false", () => {
        render(
            <Modal isOpen={false} title="Test" onClose={vi.fn()}>
                <p>Content</p>
            </Modal>,
        );
        expect(screen.queryByText("Test")).not.toBeInTheDocument();
        expect(screen.queryByText("Content")).not.toBeInTheDocument();
    });

    it("renders title and children when isOpen is true", () => {
        render(
            <Modal isOpen={true} title="My Modal" onClose={vi.fn()}>
                <p>Hello World</p>
            </Modal>,
        );
        expect(screen.getByText("My Modal")).toBeInTheDocument();
        expect(screen.getByText("Hello World")).toBeInTheDocument();
    });

    it("calls onClose when close button is clicked", async () => {
        const user = userEvent.setup();
        const onClose = vi.fn();

        render(
            <Modal isOpen={true} title="Close Me" onClose={onClose}>
                <p>Body</p>
            </Modal>,
        );

        const closeButton = screen.getByRole("button");
        await user.click(closeButton);
        expect(onClose).toHaveBeenCalledOnce();
    });
});
