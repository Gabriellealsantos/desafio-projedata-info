import { Edit, Trash2 } from "lucide-react";
import type { RawMaterialDTO } from "../../../../../models/raw-material";

type RawMaterialMobileCardsProps = {
    items: RawMaterialDTO[];
    onEdit: (item: RawMaterialDTO) => void;
    onDelete: (id: number, name: string) => void;
};

export function RawMaterialMobileCards({ items, onEdit, onDelete }: RawMaterialMobileCardsProps) {
    if (items.length === 0) {
        return (
            <div className="p-6 text-center text-slate-500 border border-dashed border-slate-300 rounded-xl sm:hidden">
                Nenhum insumo encontrado.
            </div>
        );
    }

    return (
        <div className="sm:hidden space-y-3">
            {items.map((item) => (
                <div key={item.id} className="bg-white rounded-xl border border-slate-200 shadow-sm p-4">
                    <div className="flex justify-between items-start">
                        <div>
                            <p className="font-bold text-slate-800">{item.name}</p>
                            <p className="text-sm text-slate-500 mt-1">
                                Estoque: <span className="font-medium text-slate-700">{item.stockQuantity}</span>
                            </p>
                        </div>
                        <div className="flex gap-1">
                            <button
                                onClick={() => onEdit(item)}
                                className="p-2 text-slate-400 hover:text-blue-600 hover:bg-blue-50 rounded-lg transition-colors"
                            >
                                <Edit size={18} />
                            </button>
                            <button
                                onClick={() => onDelete(item.id, item.name)}
                                className="p-2 text-slate-400 hover:text-red-600 hover:bg-red-50 rounded-lg transition-colors"
                            >
                                <Trash2 size={18} />
                            </button>
                        </div>
                    </div>
                </div>
            ))}
        </div>
    );
}
