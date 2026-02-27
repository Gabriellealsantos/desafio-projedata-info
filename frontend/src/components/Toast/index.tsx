import { useEffect } from "react";
import { AlertCircle, Check } from "lucide-react";


type ToastProps = {
    message: string;
    type: "success" | "error";
    onClose: () => void;
};

export default function Toast({ message, type, onClose }: ToastProps) {
    useEffect(() => {
        const timer = setTimeout(onClose, 3000);
        return () => clearTimeout(timer);
    }, [onClose]);

    const bg = type === "error" ? "bg-red-500" : "bg-green-500";

    return (
        <div
            className={`fixed bottom-4 right-4 ${bg} text-white px-6 py-3 rounded-lg shadow-lg flex items-center gap-2 animate-fade-in-up z-50`}
        >
            {type === "error" ? <AlertCircle size={20} /> : <Check size={20} />}
            <span>{message}</span>
        </div>
    );
}
