import { Link } from 'react-router-dom';
import { Card, CardHeader, CardTitle, CardContent } from '@/components/ui/Card';
import { Button } from '@/components/ui/Button';
import { 
  Plane, 
  ArrowRight, 
  Settings2, 
  Ticket,
  ShieldCheck
} from 'lucide-react';
import { withAuth } from '@/lib/withAuth';
import { Badge } from '@/components/ui/Badge';
import { cn } from '@/lib/utils';

function AdminDashboard() {
  return (
    <div className="container mx-auto px-4 py-6 max-w-6xl">
      <div className="flex flex-col md:flex-row justify-between items-start md:items-center mb-6 gap-6">
         <div>
            <h1 className="text-4xl font-black tracking-tighter text-slate-900 flex items-center gap-4">
               Control <span className="text-blue-600">Center</span>
            </h1>
            <p className="text-slate-500 font-medium mt-1">Administrative oversight for the FlightApp network.</p>
         </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4 mt-6">
        <ModuleCard 
           to="/admin/airlines" 
           title="Manage Airlines" 
           desc="Add new airline , block/activate operators, and verify logos."
           icon={Plane}
           accent="blue"
        />
        <ModuleCard 
           to="/admin/inventory" 
           title="Manage Flights" 
           desc="Add new flight rotations and manage flights."
           icon={Settings2}
           accent="indigo"
        />
        <ModuleCard 
           to="/admin/bookings" 
           title="All Bookings" 
           desc="Oversee all system-wide reservations, verify PNRs, and manage traveler logs."
           icon={Ticket}
           accent="violet"
        />
        <ModuleCard 
           to="/admin/users" 
           title="Manage Users" 
           desc="Monitor system access, block suspicious accounts, and manage security roles."
           icon={ShieldCheck}
           accent="emerald"
        />
      </div>
    </div>
  );
}

function StatCard({ icon: Icon, label, val, color, bg }) {
   return (
      <Card className="p-4 border-slate-100 bg-white shadow-xl shadow-slate-200/20 rounded-3xl relative overflow-hidden group">
         <div className={cn("w-14 h-14 rounded-2xl flex items-center justify-center mb-6 shadow-inner", bg)}>
            <Icon className={cn("w-7 h-7", color)} />
         </div>
         <p className="text-xs font-black text-slate-400 uppercase tracking-[0.2em] mb-2">{label}</p>
         <p className="text-4xl font-black text-slate-900 tracking-tighter">{val}</p>
         <div className="absolute top-0 right-0 h-full w-24 bg-gradient-to-l from-slate-50/50 to-transparent skew-x-[-20deg] translate-x-12 opacity-0 group-hover:opacity-100 transition-opacity" />
      </Card>
   );
}

function ModuleCard({ to, title, desc, icon: Icon, accent }) {
   const colors = {
      blue: "hover:border-blue-500/30 text-blue-600 bg-blue-50/50",
      indigo: "hover:border-indigo-500/30 text-indigo-600 bg-indigo-50/50",
      violet: "hover:border-violet-500/30 text-violet-600 bg-violet-50/50",
      emerald: "hover:border-emerald-500/30 text-emerald-600 bg-emerald-50/50"
   };

   return (
      <Link to={to} className="group h-full">
         <Card className={cn("p-4 h-full bg-white border-slate-100 hover:shadow-[0_30px_60px_-15px_rgba(0,0,0,0.1)] transition-all rounded-[2.5rem] flex flex-col items-start text-left relative overflow-hidden", colors[accent])}>
            <div className={cn("w-16 h-16 rounded-[1.25rem] flex items-center justify-center mb-10 shadow-lg border border-white/50 backdrop-blur-sm group-hover:scale-110 transition-transform", 
               accent === 'blue' ? 'bg-blue-600 text-white shadow-blue-500/20' : 
               accent === 'indigo' ? 'bg-indigo-600 text-white shadow-indigo-500/20' : 
               accent === 'emerald' ? 'bg-emerald-600 text-white shadow-emerald-500/20' :
               'bg-violet-600 text-white shadow-violet-500/20'
            )}>
               <Icon className="w-8 h-8" />
            </div>
            <h3 className="text-2xl font-black text-slate-900 tracking-tight mb-3 group-hover:text-slate-950 transition-colors">{title}</h3>
            <p className="text-sm font-medium text-slate-500 leading-relaxed mb-auto">{desc}</p>
            <div className="mt-8 flex items-center text-blue-600 font-black text-xs uppercase tracking-widest group-hover:translate-x-2 transition-transform">
               Enter Module <ArrowRight className="ml-2 w-4 h-4" />
            </div>
         </Card>
      </Link>
   );
}

export default withAuth(AdminDashboard);
