import { Plus, Check } from "lucide-react";
import type { RawMaterialDTO } from "../../models/raw-material";

type CompositionFormProps = {
    rawMaterials: RawMaterialDTO[];
    compForm: { rawMaterialId: string; quantity: number };
    isBatchMode: boolean;
    onChangeForm: (form: { rawMaterialId: string; quantity: number }) => void;
    onToggleBatch: (checked: boolean) => void;
    onSubmit: () => void;
};

export function CompositionForm({
    rawMaterials,
    compForm,
    isBatchMode,
    onChangeForm,
    onToggleBatch,
    onSubmit
}: CompositionFormProps) {
    const getSelectedRawMaterial = () =>
        compForm.rawMaterialId ? rawMaterials.find(rm => rm.id === parseInt(compForm.rawMaterialId)) : undefined;

    const selectedRm = getSelectedRawMaterial();

    return (
        <div className="bg-slate-50 p-4 rounded-lg border border-slate-200 space-y-3">
            <p className="text-xs font-semibold text-slate-500 uppercase tracking-wide">
                Adicionar Insumos
            </p>
            <div className="flex flex-col sm:flex-row gap-3">
                <div className="flex-1">
                    <select
                        value={compForm.rawMaterialId}
                        onChange={(e) => onChangeForm({ ...compForm, rawMaterialId: e.target.value })}
                        className="w-full border border-slate-300 rounded-lg p-2.5 focus:ring-2 focus:ring-blue-500 bg-white text-sm"
                    >
                        <option value="" disabled>
                            Selecione o insumo...
                        </option>
                        {rawMaterials.map((rm) => (
                            <option key={rm.id} value={rm.id}>
                                {rm.name} (Estoque: {rm.stockQuantity})
                            </option>
                        ))}
                    </select>
                </div>
                <div className="w-full sm:w-28">
                    <input
                        type="number"
                        min="0.01"
                        step="0.01"
                        placeholder="Qtd"
                        value={compForm.quantity}
                        onChange={(e) =>
                            onChangeForm({ ...compForm, quantity: parseFloat(e.target.value) })
                        }
                        className="w-full border border-slate-300 rounded-lg p-2.5 focus:ring-2 focus:ring-blue-500 text-sm"
                    />
                </div>
                <button
                    type="button"
                    onClick={onSubmit}
                    disabled={!compForm.rawMaterialId}
                    className="w-full sm:w-auto bg-slate-600 text-white px-4 py-2.5 rounded-lg font-medium hover:bg-slate-700 disabled:opacity-40 disabled:cursor-not-allowed transition-colors text-sm flex items-center justify-center gap-1.5"
                >
                    {isBatchMode ? (
                        <>
                            <Plus size={16} /> Adicionar à Lista
                        </>
                    ) : (
                        <>
                            <Check size={16} /> Vincular
                        </>
                    )}
                </button>
            </div>
            <div className="flex items-center gap-2 mt-2">
                <input
                    type="checkbox"
                    id="batchMode"
                    checked={isBatchMode}
                    onChange={(e) => onToggleBatch(e.target.checked)}
                    className="rounded text-blue-600 focus:ring-blue-500"
                />
                <label htmlFor="batchMode" className="text-sm text-slate-600 cursor-pointer">
                    Adicionar múltiplos insumos de uma vez
                </label>
            </div>
        </div>
    );
}
