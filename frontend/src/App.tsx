import { useState } from "react";
import { BrowserRouter as Router, Routes, Route, Navigate } from "react-router-dom";
import { LayoutDashboard, Package, Boxes } from "lucide-react";
import Sidebar from "./components/Sidebar";
import type { NavItem } from "./components/Sidebar";
import Toast from "./components/Toast";
import Dashboard from "./routes/Dashboard";
import Products from "./routes/Products";
import RawMaterials from "./routes/RawMaterials";

type ToastState = {
  message: string;
  type: "success" | "error";
};


const NAV_ITEMS: NavItem[] = [
  { id: "/dashboard", label: "Sugestão Produção", icon: LayoutDashboard },
  { id: "/products", label: "Produtos", icon: Package },
  { id: "/raw-materials", label: "Matérias-Primas", icon: Boxes },
];

export default function App() {
  const [toast, setToast] = useState<ToastState | null>(null);

  const showToast = (message: string, type: "success" | "error" = "success") =>
    setToast({ message, type });

  return (
    <Router>
      <div className="min-h-screen bg-slate-50 flex flex-col md:flex-row font-sans">
        <Sidebar navItems={NAV_ITEMS} />

        <main className="flex-1 p-4 sm:p-6 md:p-10 overflow-y-auto">
          <div className="max-w-6xl mx-auto">
            <Routes>
              <Route path="/" element={<Navigate to="/dashboard" replace />} />
              <Route path="/dashboard" element={<Dashboard showToast={showToast} />} />
              <Route path="/products" element={<Products showToast={showToast} />} />
              <Route path="/raw-materials" element={<RawMaterials showToast={showToast} />} />
            </Routes>
          </div>
        </main>

        {toast && (
          <Toast message={toast.message} type={toast.type} onClose={() => setToast(null)} />
        )}
      </div>
    </Router>
  );
}