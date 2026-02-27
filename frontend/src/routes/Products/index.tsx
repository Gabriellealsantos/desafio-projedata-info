import { useState, useEffect, useCallback } from "react";
import { Plus, Edit, Trash2, Save, Link as LinkIcon } from "lucide-react";
import * as productService from "../../services/product-service";
import * as rawMaterialService from "../../services/raw-material-service";
import Modal from "../../components/Modal";
import Pagination from "../../components/Pagination";
import ConfirmDialog from "../../components/ConfirmDialog";
import { CompositionModal } from "../../components/CompositionModal/index";
import type { ProductDTO, ProductCreateDTO } from "../../models/product";
import type { RawMaterialDTO } from "../../models/raw-material";

const PAGE_SIZE = 9;

type ProductsProps = {
    showToast: (message: string, type: "success" | "error") => void;
};

function getErrorMessage(err: unknown): string {
    const error = err as { response?: { data?: { message?: string } }; message?: string };
    return error.response?.data?.message ?? error.message ?? "Erro desconhecido";
}

export default function Products({ showToast }: ProductsProps) {
    const [products, setProducts] = useState<ProductDTO[]>([]);
    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [totalElements, setTotalElements] = useState(0);
    const [rawMaterials, setRawMaterials] = useState<RawMaterialDTO[]>([]);

    const [productModalOpen, setProductModalOpen] = useState(false);
    const [editingProduct, setEditingProduct] = useState<ProductDTO | null>(null);
    const [productForm, setProductForm] = useState<ProductCreateDTO>({ name: "", value: 0 });

    const [compositionModalOpen, setCompositionModalOpen] = useState(false);
    const [currentProduct, setCurrentProduct] = useState<ProductDTO | null>(null);

    const [confirmDialog, setConfirmDialog] = useState<{
        open: boolean;
        title: string;
        message: string;
        onConfirm: () => void;
    }>({ open: false, title: "", message: "", onConfirm: () => { } });

    const loadProducts = useCallback(
        (targetPage = page) => {
            productService
                .findAll(targetPage, PAGE_SIZE)
                .then((res) => {
                    setProducts(res.data.items ?? []);
                    setTotalPages(res.data.totalPages ?? 0);
                    setTotalElements(res.data.totalElements ?? 0);
                    setPage(res.data.page ?? targetPage);
                })
                .catch((err) => showToast(getErrorMessage(err), "error"));
        },
        [page, showToast],
    );

    const loadRawMaterials = useCallback(() => {
        rawMaterialService
            .findAll(0, 100)
            .then((res) => setRawMaterials(res.data.items ?? []))
            .catch(console.error);
    }, []);

    useEffect(() => {
        loadProducts();
        loadRawMaterials();
    }, [loadProducts, loadRawMaterials]);

    // ---- Product CRUD ----

    const handleProductSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        try {
            if (editingProduct) {
                await productService.update(editingProduct.id, productForm);
                showToast("Produto atualizado!", "success");
            } else {
                await productService.create(productForm);
                showToast("Produto criado!", "success");
            }
            setProductModalOpen(false);
            loadProducts();
        } catch (err) {
            showToast(getErrorMessage(err), "error");
        }
    };

    const requestProductDelete = (id: number, name: string) => {
        setConfirmDialog({
            open: true,
            title: "Remover Produto",
            message: `Tem certeza que deseja remover "${name}"? Esta ação não poderá ser desfeita.`,
            onConfirm: async () => {
                setConfirmDialog((prev) => ({ ...prev, open: false }));
                try {
                    await productService.deleteById(id);
                    showToast("Produto removido!", "success");
                    loadProducts();
                } catch (err) {
                    showToast(getErrorMessage(err), "error");
                }
            },
        });
    };

    const openProductForm = (prod: ProductDTO | null = null) => {
        if (prod) {
            productService.findById(prod.id).then((res) => {
                const data = res.data;
                setEditingProduct(data);
                setProductForm({ name: data.name, value: data.value });
                setProductModalOpen(true);
            });
        } else {
            setEditingProduct(null);
            setProductForm({ name: "", value: 0 });
            setProductModalOpen(true);
        }
    };

    const openCompositionModal = (product: ProductDTO) => {
        setCurrentProduct(product);
        setCompositionModalOpen(true);
    };

    return (
        <div className="space-y-6">
            <div className="flex justify-between items-center">
                <div>
                    <h2 className="text-2xl font-bold text-slate-800">Produtos</h2>
                    <p className="text-slate-500">Catálogo de produtos e suas composições (Ficha Técnica).</p>
                </div>
                <button
                    onClick={() => openProductForm()}
                    className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg font-medium flex items-center gap-2 transition-colors"
                >
                    <Plus size={18} /> Novo Produto
                </button>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                {products.map((product) => (
                    <div
                        key={product.id}
                        className="bg-white border border-slate-200 rounded-xl shadow-sm p-6 hover:shadow-md transition-shadow"
                    >
                        <div className="flex justify-between items-start mb-4">
                            <div>
                                <h3 className="text-lg font-bold text-slate-800">{product.name}</h3>
                                <p className="text-2xl font-light text-emerald-600 mt-1">
                                    R$ {product.value?.toFixed(2)}
                                </p>
                            </div>
                            <div className="flex gap-1">
                                <button
                                    onClick={() => openProductForm(product)}
                                    className="p-1.5 text-slate-400 hover:text-blue-600 bg-slate-50 hover:bg-blue-50 rounded-md transition-colors"
                                    title="Editar Produto"
                                >
                                    <Edit size={16} />
                                </button>
                                <button
                                    onClick={() => requestProductDelete(product.id, product.name)}
                                    className="p-1.5 text-slate-400 hover:text-red-600 bg-slate-50 hover:bg-red-50 rounded-md transition-colors"
                                    title="Deletar"
                                >
                                    <Trash2 size={16} />
                                </button>
                            </div>
                        </div>

                        <div className="pt-4 border-t border-slate-100">
                            <button
                                onClick={() => openCompositionModal(product)}
                                className="w-full flex justify-center items-center gap-2 bg-slate-50 hover:bg-slate-100 text-slate-700 py-2.5 rounded-lg text-sm font-medium transition-colors"
                            >
                                <LinkIcon size={16} /> Ficha Técnica (Matérias-Primas)
                            </button>
                        </div>
                    </div>
                ))}
                {products.length === 0 && (
                    <div className="col-span-full p-8 text-center text-slate-500 border border-dashed border-slate-300 rounded-xl">
                        Nenhum produto encontrado.
                    </div>
                )}
            </div>

            <Pagination
                page={page}
                totalPages={totalPages}
                totalElements={totalElements}
                onPageChange={(newPage) => {
                    setPage(newPage);
                    loadProducts(newPage);
                }}
            />

            {/* Product Create/Edit Modal */}
            <Modal
                isOpen={productModalOpen}
                onClose={() => setProductModalOpen(false)}
                title={editingProduct ? "Editar Produto" : "Novo Produto"}
            >
                <form onSubmit={handleProductSubmit} className="space-y-4">
                    <div>
                        <label htmlFor="productName" className="block text-sm font-medium text-slate-700 mb-1">
                            Nome do Produto
                        </label>
                        <input
                            id="productName"
                            required
                            type="text"
                            value={productForm.name}
                            onChange={(e) => setProductForm({ ...productForm, name: e.target.value })}
                            className="w-full border border-slate-300 rounded-lg p-2.5 focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition-all"
                        />
                    </div>
                    <div>
                        <label htmlFor="productValue" className="block text-sm font-medium text-slate-700 mb-1">
                            Valor Unitário (Venda)
                        </label>
                        <input
                            id="productValue"
                            required
                            type="number"
                            min="0"
                            step="0.01"
                            value={productForm.value}
                            onChange={(e) =>
                                setProductForm({ ...productForm, value: parseFloat(e.target.value) })
                            }
                            className="w-full border border-slate-300 rounded-lg p-2.5 focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition-all"
                        />
                    </div>
                    <div className="flex justify-end pt-4">
                        <button
                            type="submit"
                            className="bg-blue-600 text-white px-6 py-2 rounded-lg font-medium hover:bg-blue-700 transition-colors flex items-center gap-2"
                        >
                            <Save size={18} /> Salvar Produto
                        </button>
                    </div>
                </form>
            </Modal>

            {/* Composition Modal (Extracted) */}
            <CompositionModal
                isOpen={compositionModalOpen}
                onClose={() => setCompositionModalOpen(false)}
                product={currentProduct}
                rawMaterials={rawMaterials}
                showToast={showToast}
            />

            {/* Confirm Dialog */}
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
