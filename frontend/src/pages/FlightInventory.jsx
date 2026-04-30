import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import api from '@/lib/api-client';
import { Card, CardHeader, CardTitle, CardContent } from '@/components/ui/Card';
import { Button } from '@/components/ui/Button';
import { Input } from '@/components/ui/Input';
import { Label } from '@/components/ui/Label';
import { Badge } from '@/components/ui/Badge';
import { Select, SelectItem } from '@/components/ui/Select';
import { Plane, Plus, Clock, MapPin, Settings2, Info, ChevronRight, Users, Zap, ArrowRight } from 'lucide-react';
import { withAuth } from '@/lib/withAuth';
import { toast } from 'sonner';

function FlightInventory() {
   const queryClient = useQueryClient();
   const [showForm, setShowForm] = useState(false);
   const [newFlight, setNewFlight] = useState({
      flightNumber: '',
      airline: '',
      from: '',
      to: '',
      startDateTime: '',
      endDateTime: '',
      scheduledDays: '',
      instrumentUsed: '',
      totalBusinessSeats: 0,
      totalNonBusinessSeats: 0,
      numberOfRows: 0,
      meal: 'NONE'
   });

   const { data: airlines } = useQuery({
      queryKey: ['airlines'],
      queryFn: async () => {
         const response = await api.get('/api/v1.0/flight/airline/list');
         return response.data;
      }
   });

   const addMutation = useMutation({
      mutationFn: (data) => api.post("/api/v1.0/flight/airline/inventory", data),
      onSuccess: () => {
         toast.success('Flight Rotation Added Successfully');
         setShowForm(false);
         queryClient.invalidateQueries(['flights-inventory']);
      }
   });
   const { data: flights, isLoading } = useQuery({
      queryKey: ['flights-inventory'],
      queryFn: async () => {
         const response = await api.post('/api/v1.0/flight/search', {});
         return response.data;
      }
   });
   return (
      <div className="container mx-auto px-4 py-6 max-w-6xl">
         <div className="flex flex-col md:flex-row justify-between items-start md:items-center mb-6 gap-6">
            <div>
               <h1 className="text-4xl font-black tracking-tighter text-slate-900 flex items-center gap-4">
                  Manage <span className="text-blue-600">Flights</span>
               </h1>
               <p className="text-slate-500 font-medium mt-1">Schedule complex flight rotations.</p>
            </div>
            <Button
               className={`${showForm ? 'bg-slate-900' : 'bg-blue-600 hover:bg-blue-700 shadow-xl shadow-blue-500/20'} h-12 px-6 font-black text-white rounded-xl transition-all`}
               onClick={() => setShowForm(!showForm)}>
               {showForm ? 'Abort Scheduling' : <><Plus className="mr-2 w-5 h-5" /> Schedule New Flight</>}
            </Button>
         </div>

         {showForm && (
            <Card className="bg-white border-slate-100 shadow-[0_40px_80px_-15px_rgba(0,0,0,0.08)] mb-16 rounded-[2.5rem] overflow-hidden animate-in fade-in slide-in-from-top-4 transition-all lg:p-4">
               <div className="bg-blue-600/5 p-4 border-b border-slate-50 flex items-center justify-between rounded-t-[1.5rem]">
                  <div className="flex items-center gap-4">
                     <div className="w-12 h-12 bg-blue-600 rounded-2xl flex items-center justify-center shadow-lg shadow-blue-500/20">
                        <Zap className="w-6 h-6 text-white" />
                     </div>
                     <div>
                        <h3 className="font-black text-slate-900 text-xl tracking-tight">Define New Flight</h3>
                        <p className="text-slate-500 font-bold text-xs uppercase tracking-widest mt-0.5">Define New Rotation Parameters</p>
                     </div>
                  </div>
               </div>

               <CardContent className="p-6">
                  <div className="grid grid-cols-1 lg:grid-cols-3 gap-12">
                     <div className="space-y-8">

                        <SectionLabel title="Global Identity" />
                        <FormGroup label="Airline Carrier">
                           <Select
                              onValueChange={v => setNewFlight({ ...newFlight, airline: v })}
                              defaultValue=""
                              className="h-14 bg-slate-50 border-slate-100 rounded-2xl font-bold text-slate-900">
                              <SelectItem value="">Select Airline</SelectItem>
                              {airlines?.map(a => <SelectItem key={a.id} value={a.name}>{a.name}</SelectItem>)}
                           </Select>
                        </FormGroup>
                        <FormGroup label="Flight Number">
                           <Input className="h-14 bg-slate-50 border-slate-100 rounded-2xl font-bold text-slate-900" placeholder="AI-101" onChange={e => setNewFlight({ ...newFlight, flightNumber: e.target.value })} />
                        </FormGroup>
                        <FormGroup label="Aircraft Instrument">
                           <Input className="h-14 bg-slate-50 border-slate-100 rounded-2xl font-bold text-slate-900" placeholder="Boeing 787-9" onChange={e => setNewFlight({ ...newFlight, instrumentUsed: e.target.value })} />
                        </FormGroup>
                     </div>

                     <div className="space-y-8">
                        <SectionLabel title="Rotation" />
                        <div className="grid grid-cols-2 gap-4">
                           <FormGroup label="Origin">
                              <Input className="h-14 bg-slate-50 border-slate-100 rounded-2xl font-bold text-slate-900" placeholder="New Delhi (DEL)" onChange={e => setNewFlight({ ...newFlight, from: e.target.value })} />
                           </FormGroup>
                           <FormGroup label="Destination">
                              <Input className="h-14 bg-slate-50 border-slate-100 rounded-2xl font-bold text-slate-900" placeholder="Bangalore (BLR)" onChange={e => setNewFlight({ ...newFlight, to: e.target.value })} />
                           </FormGroup>
                        </div>
                        <FormGroup label="Departure">
                           <Input type="datetime-local" className="h-14 bg-slate-50 border-slate-100 rounded-2xl font-bold text-slate-900" 
                           onChange={event=> setNewFlight({ ...newFlight, startDateTime: event.target.value })} />
                        </FormGroup>
                        <FormGroup label="Arrival">
                           <Input type="datetime-local" className="h-14 bg-slate-50 border-slate-100 rounded-2xl font-bold text-slate-900"
                            onChange={event => setNewFlight({ ...newFlight, endDateTime: event.target.value })} />
                        </FormGroup>
                        <FormGroup label="Scheduled Days">
                           <Select
                           defaultValue=""
                           onValueChange={(v) => setNewFlight({ ...newFlight, scheduledDays: v })}
                           className="h-14 bg-slate-50 border-slate-100 rounded-2xl font-bold text-slate-900">
                              <SelectItem value="DAILY">Daily</SelectItem>
                              <SelectItem value="MON-FRI">Mon-Fri</SelectItem>
                              <SelectItem value="WEEKEND">Weekend</SelectItem>
                              </Select>
                              </FormGroup>
                     </div>


                     <div className="space-y-8">
                        <SectionLabel title="Cabin Configuration" />
                        <div className="grid grid-cols-2 gap-4">
                           <FormGroup label="Bussiness Seats">
                              <Input type="number" step="1" min="0" className="h-14 bg-slate-50 border-slate-100 rounded-2xl font-bold text-slate-900" placeholder="20" 
                              onChange={event => setNewFlight({ ...newFlight, totalBusinessSeats: parseInt(event.target.value) })} />
                           </FormGroup>
                           <FormGroup label="Non Bussiness Seats">
                              <Input type="number" step="1" min="0" className="h-14 bg-slate-50 border-slate-100 rounded-2xl font-bold text-slate-900" placeholder="100"
                                onKeyDown={event => {
                                 if (event.key === '.' || event.key === 'e' || event.key === '-') {
                                    event.preventDefault(); }
                              }}
                               onChange={event => setNewFlight({ ...newFlight, totalNonBusinessSeats: parseInt(event.target.value) })} />
                           </FormGroup>
                        </div>

                        <Input type="number" step="1" min="0" className="h-14 bg-slate-50 border-slate-100 rounded-2xl font-bold text-slate-900" placeholder="30 Rows"
                          onKeyDown={event => {
                           if (event.key === '.' || event.key === 'e' || event.key === '-') {
                              event.preventDefault(); }
                              }}
                           onChange={event => setNewFlight({ ...newFlight, numberOfRows: parseInt(event.target.value) })} />

                        <FormGroup label="Meal Type">
                           <Select
                              defaultValue="NONE"
                              onValueChange={v => setNewFlight({ ...newFlight, meal: v })}
                              className="h-14 bg-slate-50 border-slate-100 rounded-2xl font-bold text-slate-900">
                              <SelectItem value="NONE"> No Meal</SelectItem>
                              <SelectItem value="VEG">Vegetarian</SelectItem>
                              <SelectItem value="NON_VEG">Non Veg</SelectItem>
                           </Select>
                        </FormGroup>
                     </div>
                  </div>
                  <div className="mt-12 pt-10 border-t border-slate-100 flex justify-end">
                     <Button className="bg-blue-600 hover:bg-blue-700 w-full md:w-auto h-16 px-16 text-lg font-black text-white rounded-2xl shadow-2xl shadow-blue-500/20 active:scale-95 transition-all"
                        onClick={() => {
                           const totalSeats =
                              (newFlight.totalBusinessSeats || 0) +(newFlight.totalNonBusinessSeats || 0);
                           const rows = newFlight.numberOfRows || 1;
                           const columns = totalSeats / rows;
                           if (!Number.isInteger(columns)) {
                              toast.error("Columns cannot be decimal");
                              return;
                           }
                           addMutation.mutate(newFlight);
                        }}>
                           Launch Flight Schedule</Button>
               </div>
            </CardContent>
        </Card>
   )
}
<div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
   {isLoading ? (
      <div className="lg:col-span-3 text-center py-12 text-slate-500 font-medium">Synchronizing Fleet Manifest...</div>
   ) : flights?.length > 0 ? (
      flights.map(flight => (
         <Card key={flight.id} className="bg-white border-slate-100 p-6 rounded-[2rem] hover:shadow-[0_20px_40px_-15px_rgba(0,0,0,0.05)] transition-all">
            <div className="flex justify-between items-start mb-4">
               <div className="flex items-center gap-3">
                  <div className="w-10 h-10 bg-blue-50 text-blue-600 rounded-xl flex items-center justify-center">
                     <Plane className="w-5 h-5" />
                  </div>
                  <div>
                     <h4 className="font-black text-slate-900">{flight.flightNumber}</h4>
                     <p className="text-xs font-bold text-slate-400 uppercase tracking-widest">{flight.airline}</p>
                  </div>
               </div>
               <Badge className="bg-emerald-50 text-emerald-600 border-none font-bold">Active</Badge>
            </div>

            <div className="flex items-center gap-4 py-4 border-y border-slate-50 my-4">
               <div className="flex-1">
                  <p className="text-xl font-black text-slate-900">{new Date(flight.startDateTime).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}</p>
                  <p className="text-xs text-slate-500 font-medium truncate">{flight.from}</p>
               </div>
               <div className="px-2">
                  <ArrowRight className="w-5 h-5 text-slate-300" />
               </div>
               <div className="flex-1 text-right">
                  <p className="text-xl font-black text-slate-900">{new Date(flight.endDateTime).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}</p>
                  <p className="text-xs text-slate-500 font-medium truncate">{flight.to}</p>
               </div>
            </div>

            <div className="flex items-center justify-between mt-4">
               <div className="flex items-center gap-2 text-slate-500">
                  <Users className="w-4 h-4" />
                  <span className="text-sm font-bold">{flight.availableSeats || (flight.totalBusinessSeats + flight.totalNonBusinessSeats)} Seats</span>
               </div>
               <div className="flex items-center gap-2 text-slate-500">
                  <Settings2 className="w-4 h-4" />
                  <span className="text-sm font-bold">{flight.instrumentUsed}</span>
               </div>
            </div>
         </Card>
      ))
   ) : (
      <div className="lg:col-span-3">
         <Card className="bg-slate-50 border-2 border-dashed border-slate-100 p-12 flex flex-col items-center justify-center text-center rounded-[2.5rem] h-64">
            <div className="w-16 h-16 bg-white rounded-2xl flex items-center justify-center shadow-lg border border-slate-50 mb-6">
               <Clock className="w-8 h-8 text-slate-300" />
            </div>
            <h4 className="font-black text-slate-900 uppercase tracking-widest text-xs">Active Rotations Ledger</h4>
            <p className="text-xs text-slate-400 font-medium max-w-[200px] mt-2">No active flights detected. Schedule a new rotation above.</p>
         </Card>
      </div>
   )}
</div>
    </div >
  );
}

function SectionLabel({ title }) {
   return <h4 className="text-xs font-black uppercase text-slate-400 tracking-[0.25em] mb-6 flex items-center gap-3">
      <div className="w-1.5 h-1.5 bg-blue-600 rounded-full" /> {title}
   </h4>;
}

function FormGroup({ label, children }) {
   return (
      <div className="space-y-2">
         <Label className="text-xs font-black text-slate-400 uppercase tracking-widest ml-1 block">{label}</Label>
         {children}
      </div>
   );
}

export default withAuth(FlightInventory);
