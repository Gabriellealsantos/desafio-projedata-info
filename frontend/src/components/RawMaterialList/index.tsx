import { RawMaterialMobileCards } from "../RawMaterialMobileCards";
import { RawMaterialDesktopTable } from "../RawMaterialDesktopTable";
import type { RawMaterialDTO } from "../../../../../models/raw-material";

type RawMaterialListProps = {
    items: RawMaterialDTO[];
    onEdit: (item: RawMaterialDTO) => void;
    onDelete: (id: number, name: string) => void;
};

export function RawMaterialList({ items, onEdit, onDelete }: RawMaterialListProps) {
    return (
        <>
            <RawMaterialMobileCards items={items} onEdit={onEdit} onDelete={onDelete} />
            <RawMaterialDesktopTable items={items} onEdit={onEdit} onDelete={onDelete} />
        </>
    );
}
