import { Minus, Save } from "lucide-react";

export type PendingItem = {
    rawMaterialId: number;
    rawMaterialName: string;
    quantity: number;
    stockQuantity: number;
};

type PendingBatchListProps = {
    pendingItems: PendingItem[];
    onRemoveItem: (id: number) => void;
    onSubmitBatch: () => void;
};

export function PendingBatchList({
    pendingItems,
    onRemoveItem,
    onSubmitBatch
}: PendingBatchListProps) {
    if (pendingItems.length === 0) return null;

    return (
        <div className="bg-blue-50 p-4 rounded-lg border border-blue-200 space-y-3">
            <div className="flex items-center justify-between">
                <p className="text-xs font-semibold text-blue-700 uppercase tracking-wide">
                    Pendentes ({pendingItems.length})
                </p>
            </div>
            <div className="space-y-2">
                {pendingItems.map((item) => (
                    <div
                        key={item.rawMaterialId}
                        className="flex items-center justify-between bg-white rounded-md px-3 py-2 border border-blue-100"
                    >
                        <div>
                            <span className="text-sm font-medium text-slate-700">
                                {item.rawMaterialName}
                            </span>
                            <span className="text-xs text-slate-400 ml-2">
                                Qtd: {item.quantity}
                            </span>
                        </div>
                        <button
                            onClick={() => onRemoveItem(item.rawMaterialId)}
                            className="p-1 text-slate-400 hover:text-red-600 hover:bg-red-50 rounded transition-colors"
                            title="Remover da lista"
                        >
                            <Minus size={14} />
                        </button>
                    </div>
                ))}
            </div>
            <button
                onClick={onSubmitBatch}
                className="w-full bg-blue-600 text-white py-2.5 rounded-lg font-medium hover:bg-blue-700 transition-colors text-sm flex items-center justify-center gap-2"
            >
                <Save size={16} />
                Vincular {pendingItems.length} {pendingItems.length === 1 ? "Insumo" : "Insumos"}
            </button>
        </div>
    );
}
