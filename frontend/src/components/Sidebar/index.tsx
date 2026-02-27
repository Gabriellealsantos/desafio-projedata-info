import { Settings } from "lucide-react";
import type { LucideIcon } from "lucide-react";
import { NavLink } from "react-router-dom";

export type NavItem = {
    id: string;
    label: string;
    icon: LucideIcon;
};

type SidebarProps = {
    navItems: NavItem[];
};

export default function Sidebar({ navItems }: SidebarProps) {
    return (
        <aside className="bg-slate-900 text-white w-full md:w-64 flex-shrink-0 flex flex-col">
            <div className="p-6 border-b border-slate-800">
                <h1 className="text-2xl font-black text-transparent bg-clip-text bg-gradient-to-r from-blue-400 to-indigo-400 flex items-center gap-2">
                    <Settings className="text-blue-500" />
                    AutoFlex
                </h1>
                <p className="text-slate-400 text-xs mt-1 uppercase tracking-widest font-semibold">
                    ERP Industrial
                </p>
            </div>
            <nav className="flex-1 py-4 px-3 space-y-1">
                {navItems.map((item) => {
                    const Icon = item.icon;
                    return (
                        <NavLink
                            key={item.id}
                            to={item.id}
                            className={({ isActive }) =>
                                `w-full flex items-center gap-3 px-4 py-3 rounded-xl transition-all font-medium ${isActive
                                    ? "bg-blue-600 text-white shadow-md"
                                    : "text-slate-400 hover:bg-slate-800 hover:text-white"
                                }`
                            }
                        >

                            {({ isActive }) => (
                                <>
                                    <Icon
                                        size={20}
                                        className={isActive ? "text-blue-100" : "text-slate-500"}
                                    />
                                    {item.label}
                                </>
                            )}
                        </NavLink>
                    );
                })}
            </nav>
        </aside>
    );
}