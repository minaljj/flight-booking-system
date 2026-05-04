import * as React from "react"
import { cn } from "@/lib/utils"
import { ChevronDown } from "lucide-react"

const Select = ({ children, onValueChange, defaultValue, className }) => {
  return (
    <div className="relative group">
       <select 
         defaultValue={defaultValue}
         onChange={(e) => onValueChange?.(e.target.value)}
         className={cn(
           "flex h-12 w-full items-center justify-between rounded-xl border border-slate-100 bg-slate-50 px-4 py-2 text-sm font-bold text-slate-900 focus:outline-none focus:ring-2 focus:ring-blue-600/10 appearance-none transition-all cursor-pointer group-hover:border-slate-200",
           className
         )}
       >
         {children}
       </select>
       <ChevronDown className="absolute right-4 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-400 group-hover:text-blue-600 transition-colors pointer-events-none" />
    </div>
  )
}

const SelectTrigger = ({ children, className }) => <div className={className}>{children}</div>
const SelectValue = ({ placeholder }) => <span>{placeholder}</span>
const SelectContent = ({ children }) => <>{children}</>
const SelectItem = ({ value, children }) => <option value={value}>{children}</option>

export { Select, SelectTrigger, SelectValue, SelectContent, SelectItem }
