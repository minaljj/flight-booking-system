import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import api from '@/lib/api-client';
import { Card, CardHeader, CardTitle, CardContent } from '@/components/ui/Card';
import { Button } from '@/components/ui/Button';
import { Input } from '@/components/ui/Input';
import { Label } from '@/components/ui/Label';
import { Table, TableHeader, TableBody, TableHead, TableRow, TableCell } from '@/components/ui/Table';
import { Badge } from '@/components/ui/Badge';
import { Plane, Plus, Search, ShieldAlert, CheckCircle2, MoreHorizontal, Link2 } from 'lucide-react';
import { cn } from '@/lib/utils';
import { withAuth } from '@/lib/withAuth';
import { toast } from 'sonner';

function ManageAirlines() {
  const queryClient = useQueryClient();
  const [showAdd, setShowAdd] = useState(false);
  const [newAirline, setNewAirline] = useState({ name: '', logo: '', contactNumber: '', contactAddress: '' });

  const { data: airlines, isLoading } = useQuery({
    queryKey: ['airlines'],
    queryFn: async () => {
      const response = await api.get('/api/v1.0/flight/admin/airline/list');
      return response.data;
    }
  });

  const addMutation = useMutation({
    mutationFn: (data) => api.post('/api/v1.0/flight/airline', data),
    onSuccess: () => {
      toast.success('Airline registered in registry.');
      setShowAdd(false);
      queryClient.invalidateQueries(['airlines']);
    }
  });

  const toggleBlockMutation = useMutation({
    mutationFn: (name) => api.delete(`/api/v1.0/flight/airline/block/${name}`),
    onSuccess: () => {
      toast.info('Operator authorization state toggled.');
      queryClient.invalidateQueries(['airlines']);
    }
  });

  if (isLoading) return <div className="p-20 text-center text-slate-400 font-black animate-pulse uppercase tracking-[0.2em] text-xs">Querying Global Carrier Registry...</div>;

  return (
    <div className="container mx-auto px-4 py-6 max-w-6xl">
      <div className="flex flex-col md:flex-row justify-between items-start md:items-center mb-6 gap-6">
         <div>
            <h1 className="text-4xl font-black tracking-tighter text-slate-900 flex items-center gap-4">
               Airline <span className="text-blue-600">Registry</span>
            </h1>
            <p className="text-slate-500 font-medium mt-1">Authorized operator management and flight authorization logs.</p>
         </div>
         <Button 
            className={`${showAdd ? 'bg-slate-900' : 'bg-blue-600 hover:bg-blue-700 shadow-xl shadow-blue-500/20'} h-12 px-6 font-black text-white rounded-xl transition-all`} 
            onClick={() => setShowAdd(!showAdd)}
         >
            {showAdd ? 'Cancel Registration' : <><Plus className="mr-2 w-5 h-5" /> Register New Airline</>}
         </Button>
      </div>

      {showAdd && (
        <Card className="bg-white border-slate-100 shadow-[0_40px_80px_-15px_rgba(0,0,0,0.08)] mb-6 rounded-[2.5rem] overflow-hidden animate-in fade-in slide-in-from-top-4">
           <div className="bg-blue-600 p-4 text-white">
              <h3 className="text-xl font-black tracking-tight">Define New Airline</h3>
              <p className="text-blue-100/60 font-medium text-xs mt-1 uppercase tracking-widest">Enter official carrier documentation details</p>
           </div>
           <CardContent className="p-6 grid grid-cols-1 md:grid-cols-3 lg:grid-cols-5 gap-6">
              <FormGroup label="Airline Name" value={newAirline.name} onChange={v => setNewAirline({...newAirline, name: v})} placeholder="Ex: IndiGo" />
              <FormGroup label="Logo Source URL" value={newAirline.logo} onChange={v => setNewAirline({...newAirline, logo: v})} placeholder="https://..." />
              <FormGroup label="Registry Contact" value={newAirline.contactNumber} onChange={v => setNewAirline({...newAirline, contactNumber: v})} placeholder="+1 555-0101" />
              <FormGroup label="Carrier Address" value={newAirline.contactAddress} onChange={v => setNewAirline({...newAirline, contactAddress: v})} placeholder="HQ Location" />
              <div className="flex items-end">
                 <Button className="w-full h-14 bg-slate-900 text-white font-black rounded-2xl shadow-xl shadow-slate-950/20 active:scale-95 transition-all" onClick={() => addMutation.mutate(newAirline)}>
                    Add Airline
                 </Button>
              </div>
           </CardContent>
        </Card>
      )}

      <Card className="bg-white border-slate-100 shadow-sm rounded-[2.5rem] overflow-hidden">
        <Table>
          <TableHeader className="bg-slate-50 border-b border-slate-100 h-16">
            <TableRow className="hover:bg-transparent border-none">
              <TableHead className="pl-10 text-xs font-black uppercase tracking-widest text-slate-400">Branding</TableHead>
              <TableHead className="text-xs font-black uppercase tracking-widest text-slate-400">Carrier Identity</TableHead>
              <TableHead className="text-xs font-black uppercase tracking-widest text-slate-400">Operational Address</TableHead>
              <TableHead className="text-xs font-black uppercase tracking-widest text-slate-400">Status</TableHead>
              <TableHead className="pr-10 text-right text-xs font-black uppercase tracking-widest text-slate-400">Actions</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {airlines?.map((airline) => (
              <TableRow key={airline.id} className="hover:bg-slate-50/50 transition-colors h-16 border-slate-50">
                <TableCell className="pl-10">
                   <div className="w-14 h-14 bg-white border border-slate-100 rounded-2xl flex items-center justify-center p-2 shadow-inner">
                      {airline.logo ? (
                         <img src={airline.logo} alt="" className="object-contain w-full h-full" />
                      ) : (
                         <Plane className="w-6 h-6 text-slate-300" />
                      )}
                   </div>
                </TableCell>
                <TableCell>
                  <p className="font-black text-slate-900 text-base leading-none">{airline.name}</p>
                  <p className="text-xs text-slate-400 font-bold uppercase tracking-widest mt-2">{airline.contactNumber}</p>
                </TableCell>
                <TableCell className="text-slate-500 font-medium text-sm">{airline.contactAddress || 'Global Routing'}</TableCell>
                <TableCell>
                  <Badge 
                    className={cn(
                      "font-black px-4 py-1.5 text-xs uppercase tracking-widest border-none",
                      airline.isBlocked ? "bg-rose-50 text-rose-600 shadow-sm shadow-rose-500/5" : "bg-emerald-50 text-emerald-600 shadow-sm shadow-emerald-500/5"
                    )}
                  >
                    {airline.isBlocked ? 'Access Restricted' : 'Active Operator'}
                  </Badge>
                </TableCell>
                <TableCell className="pr-10 text-right">
                  <Button 
                    variant="outline" 
                    size="sm" 
                    className={cn(
                      "font-black text-xs uppercase tracking-widest h-10 px-6 rounded-xl transition-all shadow-sm",
                      airline.isBlocked ? "border-emerald-200 text-emerald-600 hover:bg-emerald-50" : "border-rose-200 text-rose-600 hover:bg-rose-50"
                    )}
                    onClick={() => toggleBlockMutation.mutate(airline.name)}
                  >
                    {airline.isBlocked ? <><CheckCircle2 className="w-3.5 h-3.5 mr-2" /> Allow Operation</> : <><ShieldAlert className="w-3.5 h-3.5 mr-2" /> Revoke Access</>}
                  </Button>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </Card>
    </div>
  );
}

function FormGroup({ label, value, onChange, placeholder }) {
  return (
    <div className="space-y-2">
       <Label className="text-xs font-black text-slate-400 uppercase tracking-widest ml-1 block">{label}</Label>
       <Input 
         className="h-14 bg-slate-50 border-slate-100 rounded-2xl font-bold text-slate-900 placeholder:text-slate-200" 
         placeholder={placeholder} 
         value={value}
         onChange={e => onChange(e.target.value)}
       />
    </div>
  );
}

export default withAuth(ManageAirlines);
