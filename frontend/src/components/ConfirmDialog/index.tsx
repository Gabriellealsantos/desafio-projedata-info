import { AlertTriangle } from "lucide-react";

type ConfirmDialogProps = {
    isOpen: boolean;
    title: string;
    message: string;
    confirmLabel?: string;
    cancelLabel?: string;
    variant?: "danger" | "warning";
    onConfirm: () => void;
    onCancel: () => void;
};

export default function ConfirmDialog({
    isOpen,
    title,
    message,
    confirmLabel = "Confirmar",
    cancelLabel = "Cancelar",
    variant = "danger",
    onConfirm,
    onCancel,
}: ConfirmDialogProps) {
    if (!isOpen) return null;

    const colors = variant === "danger"
        ? { icon: "text-red-500 bg-red-50", btn: "bg-red-600 hover:bg-red-700 focus:ring-red-500" }
        : { icon: "text-amber-500 bg-amber-50", btn: "bg-amber-600 hover:bg-amber-700 focus:ring-amber-500" };

    return (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center p-4 z-50">
            <div className="bg-white rounded-2xl shadow-2xl w-full max-w-sm overflow-hidden animate-[fade-in-up_0.2s_ease-out]">
                <div className="p-6 text-center">
                    <div className={`mx-auto w-14 h-14 rounded-full ${colors.icon} flex items-center justify-center mb-4`}>
                        <AlertTriangle size={28} />
                    </div>
                    <h3 className="text-lg font-bold text-slate-800 mb-2">{title}</h3>
                    <p className="text-sm text-slate-500 leading-relaxed">{message}</p>
                </div>
                <div className="flex border-t border-slate-100">
                    <button
                        onClick={onCancel}
                        className="flex-1 py-3.5 text-sm font-medium text-slate-600 hover:bg-slate-50 transition-colors"
                    >
                        {cancelLabel}
                    </button>
                    <button
                        onClick={onConfirm}
                        className={`flex-1 py-3.5 text-sm font-medium text-white ${colors.btn} transition-colors`}
                    >
                        {confirmLabel}
                    </button>
                </div>
            </div>
        </div>
    );
}
