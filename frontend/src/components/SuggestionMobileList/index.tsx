import type { ProductionSuggestionItemDTO } from "../models/production-suggestion";
import { formatCurrency } from "../../utils/formatters";

type SuggestionMobileListProps = {
    items: ProductionSuggestionItemDTO[];
};

export function SuggestionMobileList({ items }: SuggestionMobileListProps) {
    return (
        <div className="sm:hidden space-y-3">
            {items.map((item) => (
                <div key={item.productId} className="bg-white rounded-xl border border-slate-200 shadow-sm p-4">
                    <p className="font-bold text-slate-800 mb-2">{item.productName}</p>
                    <div className="grid grid-cols-2 gap-2 text-sm">
                        <div>
                            <span className="text-slate-500">Valor Unit.</span>
                            <p className="font-medium text-slate-700">{formatCurrency(item.productValue)}</p>
                        </div>
                        <div className="text-right">
                            <span className="text-slate-500">Qtd. Sugerida</span>
                            <p className="font-bold text-blue-600">{item.quantityToProduce}</p>
                        </div>
                    </div>
                    <div className="mt-2 pt-2 border-t border-slate-100 text-right">
                        <span className="text-xs text-slate-500 uppercase">Subtotal</span>
                        <p className="text-lg font-bold text-emerald-600">{formatCurrency(item.subtotal)}</p>
                    </div>
                </div>
            ))}
        </div>
    );
}
