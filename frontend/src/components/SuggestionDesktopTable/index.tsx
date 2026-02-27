import type { ProductionSuggestionItemDTO } from "../../models/production-suggestion";
import { formatCurrency } from "../../utils/formatters";

type SuggestionDesktopTableProps = {
    items: ProductionSuggestionItemDTO[];
};

export function SuggestionDesktopTable({ items }: SuggestionDesktopTableProps) {
    return (
        <div className="hidden sm:block bg-white rounded-xl shadow-sm border border-slate-200 overflow-hidden">
            <table className="w-full text-left border-collapse">
                <thead>
                    <tr className="bg-slate-50 border-b border-slate-200 text-slate-600 uppercase text-sm">
                        <th className="p-4 font-semibold">Produto</th>
                        <th className="p-4 font-semibold text-right">Valor Unitário</th>
                        <th className="p-4 font-semibold text-right">Qtd. Sugerida</th>
                        <th className="p-4 font-semibold text-right">Subtotal</th>
                    </tr>
                </thead>
                <tbody className="divide-y divide-slate-100">
                    {items.map((item) => (
                        <tr key={item.productId} className="hover:bg-slate-50 transition-colors">
                            <td className="p-4 font-medium text-slate-800">{item.productName}</td>
                            <td className="p-4 text-right text-slate-600">
                                {formatCurrency(item.productValue)}
                            </td>
                            <td className="p-4 text-right font-bold text-blue-600">
                                {item.quantityToProduce}
                            </td>
                            <td className="p-4 text-right font-medium text-emerald-600">
                                {formatCurrency(item.subtotal)}
                            </td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
}
