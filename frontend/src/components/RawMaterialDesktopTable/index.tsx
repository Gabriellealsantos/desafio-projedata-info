import { Edit, Trash2 } from "lucide-react";
import type { RawMaterialDTO } from "../../../../../models/raw-material";

type RawMaterialDesktopTableProps = {
    items: RawMaterialDTO[];
    onEdit: (item: RawMaterialDTO) => void;
    onDelete: (id: number, name: string) => void;
};

export function RawMaterialDesktopTable({ items, onEdit, onDelete }: RawMaterialDesktopTableProps) {
    return (
        <div className="hidden sm:block bg-white rounded-xl shadow-sm border border-slate-200 overflow-hidden">
            <table className="w-full text-left border-collapse">
                <thead>
                    <tr className="bg-slate-50 border-b border-slate-200 text-slate-600 uppercase text-sm">
                        <th className="p-4 font-semibold">Nome do Insumo</th>
                        <th className="p-4 font-semibold text-right">Estoque</th>
                        <th className="p-4 font-semibold text-center w-24">Ações</th>
                    </tr>
                </thead>
                <tbody className="divide-y divide-slate-100">
                    {items.length === 0 && (
                        <tr>
                            <td colSpan={3} className="p-4 text-center text-slate-400">
                                Nenhum insumo encontrado.
                            </td>
                        </tr>
                    )}
                    {items.map((item) => (
                        <tr key={item.id} className="hover:bg-slate-50 transition-colors">
                            <td className="p-4 font-medium text-slate-800">{item.name}</td>
                            <td className="p-4 text-right font-medium text-slate-600">
                                {item.stockQuantity}
                            </td>
                            <td className="p-4 text-center">
                                <div className="flex items-center justify-center gap-2">
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
                            </td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
}
