import type { ProductionSuggestionDTO } from "../models/production-suggestion";
import { formatCurrency } from "../../utils/formatters";

type SuggestionHeaderProps = {
    suggestion: ProductionSuggestionDTO | null;
};

export function SuggestionHeader({ suggestion }: SuggestionHeaderProps) {
    return (
        <div className="bg-gradient-to-r from-blue-600 to-indigo-700 rounded-2xl p-5 sm:p-8 text-white shadow-lg relative overflow-hidden">
            <h2 className="text-xl sm:text-2xl font-bold mb-2">Sugestão de Produção Otimizada</h2>
            <p className="text-blue-100 mb-4 sm:mb-6 text-sm sm:text-base">
                Baseado no estoque atual e priorizando produtos de maior valor.
            </p>
            <div className="bg-white/10 rounded-lg p-3 sm:p-4 inline-block">
                <p className="text-xs sm:text-sm font-medium text-blue-100 uppercase tracking-wider">
                    Receita Estimada
                </p>
                <p className="text-3xl sm:text-4xl font-bold">
                    {formatCurrency(suggestion?.totalValue)}
                </p>
            </div>
        </div>
    );
}
