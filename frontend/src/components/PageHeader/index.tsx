import { Plus } from "lucide-react";

type PageHeaderProps = {
    title: string;
    subtitle: string;
    buttonText: string;
    onAddClick: () => void;
};

export function PageHeader({ title, subtitle, buttonText, onAddClick }: PageHeaderProps) {
    return (
        <div className="flex justify-between items-center">
            <div>
                <h2 className="text-2xl font-bold text-slate-800">{title}</h2>
                <p className="text-slate-500">{subtitle}</p>
            </div>
            <button
                onClick={onAddClick}
                className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg font-medium flex items-center gap-2 transition-colors"
            >
                <Plus size={18} /> {buttonText}
            </button>
        </div>
    );
}
