import { useState, useEffect } from "react";
import * as productRawMaterialService from "../../services/product-raw-material-service";
import Modal from "../Modal";
import ConfirmDialog from "../ConfirmDialog";
import type { ProductDTO } from "../../models/product";
import type { RawMaterialDTO } from "../../models/raw-material";
import type { ProductRawMaterialDTO, ProductRawMaterialCreateDTO } from "../../models/product-raw-material";
import { PendingBatchList, type PendingItem } from "../PendingBatchList";
import { CompositionForm } from "../CompositionForm";
import { LinkedItemsList } from "../LinkedItemsList";

type CompositionModalProps = {
    isOpen: boolean;
    onClose: () => void;
    product: ProductDTO | null;
    rawMaterials: RawMaterialDTO[];
    showToast: (message: string, type: "success" | "error") => void;
};

function getErrorMessage(err: unknown): string {
    const error = err as { response?: { data?: { message?: string } }; message?: string };
    return error.response?.data?.message ?? error.message ?? "Erro desconhecido";
}

export function CompositionModal({
    isOpen,
    onClose,
    product,
    rawMaterials,
    showToast,
}: CompositionModalProps) {
    const [composition, setComposition] = useState<ProductRawMaterialDTO[]>([]);
    const [compForm, setCompForm] = useState<{ rawMaterialId: string; quantity: number }>({
        rawMaterialId: "",
        quantity: 1,
    });
    const [pendingItems, setPendingItems] = useState<PendingItem[]>([]);
    const [isBatchMode, setIsBatchMode] = useState(false);

    const [confirmDialog, setConfirmDialog] = useState<{
        open: boolean;
        title: string;
        message: string;
        onConfirm: () => void;
    }>({ open: false, title: "", message: "", onConfirm: () => { } });

    useEffect(() => {
        if (isOpen && product) {
            loadComposition(product.id);
            setCompForm({ rawMaterialId: "", quantity: 1 });
            setPendingItems([]);
            setIsBatchMode(false);
        }
    }, [isOpen, product]);

    const loadComposition = (productId: number) => {
        productRawMaterialService
            .findByProductId(productId)
            .then((res) => {
                const data = res.data;
                setComposition(Array.isArray(data) ? data : []);
            })
            .catch((err) => showToast(getErrorMessage(err), "error"));
    };

    const getRawMaterial = (id: number): RawMaterialDTO | undefined =>
        rawMaterials.find((rm) => rm.id === id);

    const getSelectedRawMaterial = (): RawMaterialDTO | undefined =>
        compForm.rawMaterialId ? getRawMaterial(parseInt(compForm.rawMaterialId)) : undefined;

    const validateForm = () => {
        const rm = getSelectedRawMaterial();
        if (!rm) {
            showToast("Selecione um insumo.", "error");
            return null;
        }
        if (compForm.quantity <= 0) {
            showToast("A quantidade deve ser maior que zero.", "error");
            return null;
        }
        return rm;
    };

    const addToPendingList = () => {
        const rm = validateForm();
        if (!rm) return;

        setPendingItems((prev) => [
            ...prev,
            {
                rawMaterialId: rm.id,
                rawMaterialName: rm.name,
                quantity: compForm.quantity,
                stockQuantity: rm.stockQuantity,
            },
        ]);
        setCompForm({ rawMaterialId: "", quantity: 1 });
    };

    const submitSingleItem = async () => {
        const rm = validateForm();
        if (!rm || !product) return;

        try {
            const payload: ProductRawMaterialCreateDTO = {
                rawMaterialId: rm.id,
                quantity: compForm.quantity,
            };
            await productRawMaterialService.add(product.id, payload);
            showToast("Insumo vinculado!", "success");
            setCompForm({ rawMaterialId: "", quantity: 1 });
            loadComposition(product.id);
        } catch (err) {
            showToast(getErrorMessage(err), "error");
        }
    };

    const handleAddClick = () => {
        if (isBatchMode) {
            addToPendingList();
        } else {
            submitSingleItem();
        }
    };

    const removePendingItem = (rawMaterialId: number) => {
        setPendingItems((prev) => prev.filter((p) => p.rawMaterialId !== rawMaterialId));
    };

    const submitPendingItems = async () => {
        if (!product || pendingItems.length === 0) return;

        try {
            const payloads: ProductRawMaterialCreateDTO[] = pendingItems.map((item) => ({
                rawMaterialId: item.rawMaterialId,
                quantity: item.quantity,
            }));

            if (payloads.length === 1) {
                await productRawMaterialService.add(product.id, payloads[0]);
            } else {
                await productRawMaterialService.addBatch(product.id, payloads);
            }

            showToast(
                payloads.length === 1
                    ? "Insumo vinculado!"
                    : `${payloads.length} insumos vinculados!`,
                "success",
            );
            setPendingItems([]);
            setCompForm({ rawMaterialId: "", quantity: 1 });
            loadComposition(product.id);
        } catch (err) {
            showToast(getErrorMessage(err), "error");
        }
    };

    const updateLinkedItemQuantity = async (rawMaterialId: number, newQuantity: number) => {
        if (!product) return;

        if (newQuantity <= 0) {
            showToast("A quantidade deve ser maior que zero.", "error");
            return;
        }

        try {
            const payload: ProductRawMaterialCreateDTO = {
                rawMaterialId: rawMaterialId,
                quantity: newQuantity,
            };
            await productRawMaterialService.updateQuantity(product.id, rawMaterialId, payload);
            showToast("Quantidade atualizada!", "success");
            loadComposition(product.id);
        } catch (err) {
            showToast(getErrorMessage(err), "error");
        }
    };

    const requestRemoveComposition = (rawMaterialId: number, name: string) => {
        if (!product) return;
        setConfirmDialog({
            open: true,
            title: "Desvincular Insumo",
            message: `Remover "${name}" da composição deste produto?`,
            onConfirm: async () => {
                setConfirmDialog((prev) => ({ ...prev, open: false }));
                try {
                    await productRawMaterialService.remove(product.id, rawMaterialId);
                    showToast("Insumo desvinculado!", "success");
                    loadComposition(product.id);
                } catch (err) {
                    showToast(getErrorMessage(err), "error");
                }
            },
        });
    };

    // Derived states useful to child components
    const getAvailableRawMaterials = () => {
        const usedIds = new Set([
            ...composition.map((c) => c.rawMaterialId),
            ...pendingItems.map((p) => p.rawMaterialId),
        ]);
        return rawMaterials.filter((rm) => !usedIds.has(rm.id));
    };

    return (
        <>
            <Modal
                isOpen={isOpen}
                onClose={onClose}
                title={`Composição: ${product?.name ?? ""}`}
            >
                <div className="space-y-5">
                    <CompositionForm
                        rawMaterials={getAvailableRawMaterials()}
                        compForm={compForm}
                        isBatchMode={isBatchMode}
                        onChangeForm={setCompForm}
                        onToggleBatch={setIsBatchMode}
                        onSubmit={handleAddClick}
                    />

                    {isBatchMode && pendingItems.length > 0 && (
                        <PendingBatchList
                            pendingItems={pendingItems}
                            onRemoveItem={removePendingItem}
                            onSubmitBatch={submitPendingItems}
                        />
                    )}

                    <LinkedItemsList
                        composition={composition}
                        rawMaterials={rawMaterials}
                        onUpdateQuantity={updateLinkedItemQuantity}
                        onRemoveItem={requestRemoveComposition}
                    />
                </div>
            </Modal>

            <ConfirmDialog
                isOpen={confirmDialog.open}
                title={confirmDialog.title}
                message={confirmDialog.message}
                confirmLabel="Remover"
                onConfirm={confirmDialog.onConfirm}
                onCancel={() => setConfirmDialog((prev) => ({ ...prev, open: false }))}
            />
        </>
    );
}
