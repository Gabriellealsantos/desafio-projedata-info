import { ChevronLeft, ChevronRight } from "lucide-react";

type PaginationProps = {
    page: number;
    totalPages: number;
    totalElements: number;
    onPageChange: (page: number) => void;
};

export default function Pagination({ page, totalPages, totalElements, onPageChange }: PaginationProps) {
    if (totalPages <= 1) return null;

    const pages = Array.from({ length: totalPages }, (_, i) => i);

    const visiblePages = pages.filter(
        (p) => p === 0 || p === totalPages - 1 || Math.abs(p - page) <= 1,
    );

    const rendered: (number | "ellipsis")[] = [];
    for (let i = 0; i < visiblePages.length; i++) {
        if (i > 0 && visiblePages[i] - visiblePages[i - 1] > 1) {
            rendered.push("ellipsis");
        }
        rendered.push(visiblePages[i]);
    }

    return (
        <div className="flex flex-col sm:flex-row items-center justify-between gap-3 pt-2">
            <span className="text-sm text-slate-500">
                {totalElements} {totalElements === 1 ? "item" : "itens"} no total
            </span>

            <div className="flex items-center gap-1">
                <button
                    onClick={() => onPageChange(page - 1)}
                    disabled={page === 0}
                    className="p-2 rounded-lg text-slate-500 hover:bg-slate-100 disabled:opacity-30 disabled:cursor-not-allowed transition-colors"
                >
                    <ChevronLeft size={18} />
                </button>

                {rendered.map((item, idx) =>
                    item === "ellipsis" ? (
                        <span key={`e-${idx}`} className="px-1 text-slate-400">
                            …
                        </span>
                    ) : (
                        <button
                            key={item}
                            onClick={() => onPageChange(item)}
                            className={`min-w-[36px] h-9 rounded-lg text-sm font-medium transition-colors ${item === page
                                    ? "bg-blue-600 text-white shadow-sm"
                                    : "text-slate-600 hover:bg-slate-100"
                                }`}
                        >
                            {item + 1}
                        </button>
                    ),
                )}

                <button
                    onClick={() => onPageChange(page + 1)}
                    disabled={page >= totalPages - 1}
                    className="p-2 rounded-lg text-slate-500 hover:bg-slate-100 disabled:opacity-30 disabled:cursor-not-allowed transition-colors"
                >
                    <ChevronRight size={18} />
                </button>
            </div>
        </div>
    );
}
