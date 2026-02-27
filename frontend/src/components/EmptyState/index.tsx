type EmptyStateProps = {
    message: string;
};

export function EmptyState({ message }: EmptyStateProps) {
    return (
        <div className="p-6 text-center text-slate-500 border border-dashed border-slate-300 rounded-xl mt-4 mb-4">
            {message}
        </div>
    );
}
