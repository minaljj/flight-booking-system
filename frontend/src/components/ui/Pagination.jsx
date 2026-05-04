import { Button } from './Button';
import { ChevronLeft, ChevronRight } from 'lucide-react';
import { Select, SelectItem } from './Select';
export function Pagination({ currentPage, totalPages, onPageChange, pageSize, onPageSizeChange }) {
  if (totalPages === 0 && !onPageSizeChange) return null;

  return (
    <div className="flex flex-col sm:flex-row items-center justify-between gap-6 mt-12 pt-8 border-t border-slate-100">
      {onPageSizeChange && (
        <div className="flex items-center gap-3">
          <span className="text-xs font-black text-slate-400 uppercase tracking-widest">Show</span>
          <Select 
            value={pageSize.toString()} 
            onValueChange={(val) => {
              onPageSizeChange(parseInt(val));
              onPageChange(0); 
            }}
            className="w-20 h-10 bg-slate-50 border-slate-200 rounded-xl font-bold text-slate-900"
          >
            <SelectItem value="5">5</SelectItem>
            <SelectItem value="10">10</SelectItem>
            <SelectItem value="20">20</SelectItem>
            <SelectItem value="50">50</SelectItem>
          </Select>
          <span className="text-xs font-black text-slate-400 uppercase tracking-widest">Records</span>
        </div>
      )}

      {totalPages > 0 && (
        <div className="flex items-center gap-2">
      <Button
        variant="outline"
        size="icon"
        onClick={() => onPageChange(currentPage - 1)}
        disabled={currentPage === 0}
        className="w-10 h-10 rounded-xl border-slate-200 hover:bg-slate-50 disabled:opacity-30"
      >
        <ChevronLeft className="w-5 h-5" />
      </Button>
      
      <div className="flex items-center gap-2">
         {[...Array(totalPages)].map((_, i) => {
              if (totalPages > 7) {
                if (i !== 0 && i !== totalPages - 1 && Math.abs(i - currentPage) > 1) {
                  if (i === 1 || i === totalPages - 2) return <span key={i} className="text-slate-300 px-1">...</span>;
                  return null;
                }
              }

              return (
          <Button
            key={i}
            variant={currentPage === i ? "default" : "outline"}
            size="sm"
            onClick={() => onPageChange(i)}
            className={`w-10 h-10 rounded-xl font-black transition-all ${
                    currentPage === i 
                      ? "bg-blue-600 text-white shadow-lg shadow-blue-500/30 border-none scale-110" 
                      : "text-slate-500 border-slate-200 hover:border-blue-600/30"
            }`}
          >
            {i + 1}
          </Button>
              );
})}
      </div>

      <Button
        variant="outline"
        size="icon"
        onClick={() => onPageChange(currentPage + 1)}
        disabled={currentPage === totalPages - 1}
        className="w-10 h-10 rounded-xl border-slate-200 hover:bg-slate-50 disabled:opacity-30"
      >
        <ChevronRight className="w-5 h-5" />
      </Button>
       </div>
      )}
    </div>
  );
}
