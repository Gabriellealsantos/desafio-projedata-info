import { useState, useEffect, useCallback } from "react";
import * as rawMaterialService from "../../services/raw-material-service";
import Pagination from "../../components/Pagination";
import ConfirmDialog from "../../components/ConfirmDialog";

import type { RawMaterialDTO, RawMaterialCreateDTO } from "../../models/raw-material";
import { PageHeader } from "../../components/PageHeader";
import { RawMaterialList } from "../../components/RawMaterialList";
import { RawMaterialFormModal } from "../../components/RawMaterialFormModal";

const PAGE_SIZE = 10;

type RawMaterialsProps = {
    showToast: (message: string, type: "success" | "error") => void;
};

export default function RawMaterials({ showToast }: RawMaterialsProps) {
    const [items, setItems] = useState<RawMaterialDTO[]>([]);
    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [totalElements, setTotalElements] = useState(0);
    const [modalOpen, setModalOpen] = useState(false);
    const [editingItem, setEditingItem] = useState<RawMaterialDTO | null>(null);
    const [formData, setFormData] = useState<RawMaterialCreateDTO>({
        name: "",
        stockQuantity: 0,
    });
    const [confirmDialog, setConfirmDialog] = useState<{
        open: boolean;
        title: string;
        message: string;
        onConfirm: () => void;
    }>({ open: false, title: "", message: "", onConfirm: () => { } });

    const loadItems = useCallback(
        (targetPage = page) => {
            rawMaterialService
                .findAll(targetPage, PAGE_SIZE)
                .then((res) => {
                    setItems(res.data.items ?? []);
                    setTotalPages(res.data.totalPages ?? 0);
                    setTotalElements(res.data.totalElements ?? 0);
                    setPage(res.data.page ?? targetPage);
                })
                .catch((err) => showToast(err.response?.data?.message ?? err.message, "error"));
        },
        [page, showToast],
    );

    useEffect(() => {
        loadItems();
    }, [loadItems]);

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        try {
            if (editingItem) {
                await rawMaterialService.update(editingItem.id, formData);
                showToast("Insumo atualizado com sucesso!", "success");
            } else {
                await rawMaterialService.create(formData);
                showToast("Insumo criado com sucesso!", "success");
            }
            setModalOpen(false);
            loadItems();
        } catch (err: unknown) {
            const error = err as { response?: { data?: { message?: string } }; message?: string };
            showToast(error.response?.data?.message ?? error.message ?? "Erro desconhecido", "error");
        }
    };

    const handleDelete = (id: number, name: string) => {
        setConfirmDialog({
            open: true,
            title: "Remover Insumo",
            message: `Tem certeza que deseja remover "${name}"? Esta ação não poderá ser desfeita.`,
            onConfirm: async () => {
                setConfirmDialog((prev) => ({ ...prev, open: false }));
                try {
                    await rawMaterialService.deleteById(id);
                    showToast("Insumo removido com sucesso!", "success");
                    loadItems();
                } catch (err: unknown) {
                    const error = err as { response?: { data?: { message?: string } }; message?: string };
                    showToast(error.response?.data?.message ?? error.message ?? "Erro desconhecido", "error");
                }
            },
        });
    };

    const openForm = (item: RawMaterialDTO | null = null) => {
        if (item) {
            rawMaterialService.findById(item.id).then((res) => {
                const data = res.data;
                setEditingItem(data);
                setFormData({ name: data.name, stockQuantity: data.stockQuantity });
                setModalOpen(true);
            });
        } else {
            setEditingItem(null);
            setFormData({ name: "", stockQuantity: 0 });
            setModalOpen(true);
        }
    };

    const handlePageChange = (newPage: number) => {
        setPage(newPage);
        loadItems(newPage);
    };

    return (
        <div className="space-y-6">
            <PageHeader
                title="Matérias-Primas"
                subtitle="Gerencie o estoque de insumos."
                buttonText="Novo Insumo"
                onAddClick={() => openForm()}
            />

            <RawMaterialList items={items} onEdit={openForm} onDelete={handleDelete} />

            <Pagination
                page={page}
                totalPages={totalPages}
                totalElements={totalElements}
                onPageChange={handlePageChange}
            />

            <RawMaterialFormModal
                isOpen={modalOpen}
                onClose={() => setModalOpen(false)}
                editingItem={editingItem}
                formData={formData}
                setFormData={setFormData}
                onSubmit={handleSubmit}
            />

            <ConfirmDialog
                isOpen={confirmDialog.open}
                title={confirmDialog.title}
                message={confirmDialog.message}
                confirmLabel="Remover"
                onConfirm={confirmDialog.onConfirm}
                onCancel={() => setConfirmDialog((prev) => ({ ...prev, open: false }))}
            />
        </div>
    );
}
