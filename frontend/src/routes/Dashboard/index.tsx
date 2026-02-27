import { Loader2 } from "lucide-react";
import { useProductionSuggestion } from "../../hooks/useProductionSuggestion";
import { SuggestionDesktopTable } from "../../components/SuggestionDesktopTable";
import { EmptyState } from "../../components/EmptyState";
import { SuggestionHeader } from "../../components/SuggestionHeader";
import { SuggestionMobileList } from "../../components/SuggestionMobileList";

type DashboardProps = {
    showToast: (message: string, type: "success" | "error") => void;
};

export default function Dashboard({ showToast }: DashboardProps) {
    const { suggestion, loading } = useProductionSuggestion({ showToast });

    if (loading) {
        return (
            <div className="flex justify-center p-12">
                <Loader2 className="animate-spin text-blue-500" size={32} />
            </div>
        );
    }

    const suggestionItems = suggestion?.items ?? [];
    const isEmpty = suggestionItems.length === 0;

    return (
        <div className="space-y-6">
            <SuggestionHeader suggestion={suggestion} />

            {isEmpty ? (
                <EmptyState message="Nenhum produto pode ser produzido com o estoque atual." />
            ) : (
                <>
                    <SuggestionMobileList items={suggestionItems} />
                    <SuggestionDesktopTable items={suggestionItems} />
                </>
            )}
        </div>
    );
}
