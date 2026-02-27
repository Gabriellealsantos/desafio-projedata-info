import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { describe, it, expect } from "vitest";
import { MemoryRouter, useLocation } from "react-router-dom";
import { LayoutDashboard, Package, Boxes } from "lucide-react";
import Sidebar from "./index";
import type { NavItem } from "./index";

const NAV_ITEMS: NavItem[] = [
    { id: "/dashboard", label: "Dashboard", icon: LayoutDashboard },
    { id: "/products", label: "Produtos", icon: Package },
    { id: "/raw-materials", label: "Matérias-Primas", icon: Boxes },
];

function LocationDisplay() {
    const location = useLocation();
    return <div data-testid="location-display">{location.pathname}</div>;
}

describe("Sidebar", () => {
    it("renders all navigation items", () => {
        render(
            <MemoryRouter initialEntries={["/dashboard"]}>
                <Sidebar navItems={NAV_ITEMS} />
            </MemoryRouter>
        );

        expect(screen.getByText("Dashboard")).toBeInTheDocument();
        expect(screen.getByText("Produtos")).toBeInTheDocument();
        expect(screen.getByText("Matérias-Primas")).toBeInTheDocument();
    });

    it("renders the app title", () => {
        render(
            <MemoryRouter initialEntries={["/dashboard"]}>
                <Sidebar navItems={NAV_ITEMS} />
            </MemoryRouter>
        );

        expect(screen.getByText("AutoFlex")).toBeInTheDocument();
        expect(screen.getByText("ERP Industrial")).toBeInTheDocument();
    });

    it("navigates when a nav item is clicked", async () => {
        const user = userEvent.setup();

        render(
            <MemoryRouter initialEntries={["/dashboard"]}>
                <Sidebar navItems={NAV_ITEMS} />
                <LocationDisplay />
            </MemoryRouter>
        );

        await user.click(screen.getByText("Produtos"));
        expect(screen.getByTestId("location-display")).toHaveTextContent("/products");
    });

    it("highlights the active tab", () => {
        render(
            <MemoryRouter initialEntries={["/products"]}>
                <Sidebar navItems={NAV_ITEMS} />
            </MemoryRouter>
        );

        const activeLink = screen.getByText("Produtos").closest("a");
        expect(activeLink?.className).toContain("bg-blue-600");
    });
});
