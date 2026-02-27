import { Save } from "lucide-react";
import Modal from "../Modal";
import type { RawMaterialCreateDTO, RawMaterialDTO } from "../../models/raw-material";

type RawMaterialFormModalProps = {
    isOpen: boolean;
    onClose: () => void;
    editingItem: RawMaterialDTO | null;
    formData: RawMaterialCreateDTO;
    setFormData: React.Dispatch<React.SetStateAction<RawMaterialCreateDTO>>;
    onSubmit: (e: React.FormEvent) => Promise<void>;
};

export function RawMaterialFormModal({
    isOpen,
    onClose,
    editingItem,
    formData,
    setFormData,
    onSubmit,
}: RawMaterialFormModalProps) {
    return (
        <Modal
            isOpen={isOpen}
            onClose={onClose}
            title={editingItem ? "Editar Insumo" : "Novo Insumo"}
        >
            <form onSubmit={onSubmit} className="space-y-4">
                <div>
                    <label htmlFor="rmName" className="block text-sm font-medium text-slate-700 mb-1">
                        Nome
                    </label>
                    <input
                        id="rmName"
                        required
                        type="text"
                        value={formData.name}
                        onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                        className="w-full border border-slate-300 rounded-lg p-2.5 focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition-all"
                    />
                </div>
                <div>
                    <label htmlFor="rmStock" className="block text-sm font-medium text-slate-700 mb-1">
                        Quantidade em Estoque
                    </label>
                    <input
                        id="rmStock"
                        required
                        type="number"
                        min="0"
                        step="0.01"
                        value={formData.stockQuantity}
                        onChange={(e) =>
                            setFormData({ ...formData, stockQuantity: parseFloat(e.target.value) })
                        }
                        className="w-full border border-slate-300 rounded-lg p-2.5 focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition-all"
                    />
                </div>
                <div className="flex justify-end pt-4">
                    <button
                        type="submit"
                        className="bg-blue-600 text-white px-6 py-2 rounded-lg font-medium hover:bg-blue-700 transition-colors flex items-center gap-2"
                    >
                        <Save size={18} /> Salvar
                    </button>
                </div>
            </form>
        </Modal>
    );
}
