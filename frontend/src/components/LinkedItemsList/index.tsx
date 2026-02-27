import { useState } from "react";
import { Check, X, Trash2 } from "lucide-react";
import type { ProductRawMaterialDTO } from "../../models/product-raw-material";
import type { RawMaterialDTO } from "../../models/raw-material";

type LinkedItemsListProps = {
    composition: ProductRawMaterialDTO[];
    rawMaterials: RawMaterialDTO[];
    onUpdateQuantity: (rawMaterialId: number, newQuantity: number) => Promise<void>;
    onRemoveItem: (rawMaterialId: number, name: string) => void;
};

export function LinkedItemsList({
    composition,
    rawMaterials,
    onUpdateQuantity,
    onRemoveItem
}: LinkedItemsListProps) {
    const [editingCompId, setEditingCompId] = useState<number | null>(null);
    const [editingCompQty, setEditingCompQty] = useState<number>(0);

    const getRawMaterial = (id: number): RawMaterialDTO | undefined =>
        rawMaterials.find((rm) => rm.id === id);

    const startEditingQty = (rawMaterialId: number, currentQty: number) => {
        setEditingCompId(rawMaterialId);
        setEditingCompQty(currentQty);
    };

    const cancelEditingQty = () => {
        setEditingCompId(null);
    };

    const saveEditingQty = async () => {
        if (editingCompId !== null) {
            await onUpdateQuantity(editingCompId, editingCompQty);
            setEditingCompId(null);
        }
    };

    const renderCompositionItem = (item: ProductRawMaterialDTO) => {
        const isEditing = editingCompId === item.rawMaterialId;
        const rm = getRawMaterial(item.rawMaterialId);

        return (
            <div
                key={item.rawMaterialId}
                className="bg-white rounded-lg border p-3 hover:border-slate-300 transition-colors border-slate-200"
            >
                <div className="flex justify-between items-center gap-3">
                    <div className="min-w-0 flex-1">
                        <p className="font-medium text-slate-700 text-sm truncate">{item.rawMaterialName}</p>
                        {rm && (
                            <p className="text-xs text-slate-400 mt-0.5">
                                Estoque: {rm.stockQuantity}
                            </p>
                        )}
                    </div>
                    <div className="flex items-center gap-2">
                        {isEditing ? (
                            <div className="flex items-center gap-1">
                                <input
                                    type="number"
                                    min="0.01"
                                    step="0.01"
                                    value={editingCompQty}
                                    onChange={(e) => setEditingCompQty(parseFloat(e.target.value))}
                                    onKeyDown={(e) => {
                                        if (e.key === "Enter") saveEditingQty();
                                        if (e.key === "Escape") cancelEditingQty();
                                    }}
                                    autoFocus
                                    className="w-20 border border-blue-400 rounded-md p-1.5 text-sm text-right focus:ring-2 focus:ring-blue-500 outline-none"
                                />
                                <button
                                    onClick={saveEditingQty}
                                    className="p-1 text-emerald-600 hover:bg-emerald-50 rounded transition-colors"
                                    title="Salvar"
                                >
                                    <Check size={16} />
                                </button>
                                <button
                                    onClick={cancelEditingQty}
                                    className="p-1 text-slate-400 hover:bg-slate-100 rounded transition-colors"
                                    title="Cancelar"
                                >
                                    <X size={16} />
                                </button>
                            </div>
                        ) : (
                            <>
                                <button
                                    onClick={() => startEditingQty(item.rawMaterialId, item.quantity)}
                                    className="text-sm font-bold px-3 py-1 rounded-md transition-colors cursor-pointer text-slate-800 bg-slate-100 hover:bg-blue-50 hover:text-blue-700"
                                    title="Clique para editar a quantidade"
                                >
                                    {item.quantity}
                                </button>
                                <button
                                    onClick={() => onRemoveItem(item.rawMaterialId, item.rawMaterialName)}
                                    className="p-1.5 text-slate-400 hover:text-red-600 hover:bg-red-50 rounded-md transition-colors"
                                    title="Remover insumo"
                                >
                                    <Trash2 size={14} />
                                </button>
                            </>
                        )}
                    </div>
                </div>
            </div>
        );
    };

    return (
        <div>
            <div className="flex items-center justify-between mb-3">
                <p className="text-xs font-semibold text-slate-500 uppercase tracking-wide">
                    Insumos Vinculados
                </p>
                {composition.length > 0 && (
                    <span className="text-xs text-slate-400">
                        Clique na quantidade para editar
                    </span>
                )}
            </div>

            {composition.length === 0 ? (
                <div className="p-8 text-center text-slate-400 border border-dashed border-slate-200 rounded-xl">
                    <p className="text-sm">Nenhum insumo vinculado.</p>
                    <p className="text-xs mt-1">Use o formulário acima para adicionar.</p>
                </div>
            ) : (
                <div className="space-y-2">
                    {composition.map(renderCompositionItem)}
                </div>
            )}
        </div>
    );
}
