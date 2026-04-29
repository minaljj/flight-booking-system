import { useState, useEffect } from 'react';
import { useParams, useNavigate, useLocation } from 'react-router-dom';
import { useQuery, useMutation } from '@tanstack/react-query';
import api from '@/lib/api-client';
import { Card, CardHeader, CardTitle, CardContent, CardFooter } from '@/components/ui/Card';
import { Button } from '@/components/ui/Button';
import { Input } from '@/components/ui/Input';
import { Label } from '@/components/ui/Label';
import { Plane, Plus, Trash2, ShieldCheck, ArrowRight, Calendar, Clock, Mail } from 'lucide-react';
import { withAuth } from '@/lib/withAuth';
import { toast } from 'sonner';
import { format, isValid } from 'date-fns';

function BookFlight() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [passengers, setPassengers] = useState([{ name: '', age: '', gender: 'MALE', seatNumber: '' }]);
  const [email, setEmail] = useState('');
  const [phone, setPhone] = useState('');
  const [meal, setMeal] = useState('NONE');
  const { state } = useLocation();
  const selectedFlight  = state?.flight;

  const { data: apiflight, isLoading, error } = useQuery({
    queryKey: ['flight', id],
    queryFn: async () => {
      const response = await api.get(`/api/v1.0/flight/${id}`);
      return response.data;
    },
    enabled: !!id
  });
const flight = selectedFlight || apiflight;
  const bookMutation = useMutation({
    mutationFn: (data) => api.post(`/api/v1.0/flight/booking/${id}`, data),
    onSuccess: (response) => {
      toast.success('Reservation Confirmed!');
      navigate(`/ticket/${response.data.pnr}`);
    },
    onError: () => toast.error('Booking failed. Please try again.')
  });

  const addPassenger = () => setPassengers([...passengers, { name: '', age: '', gender: 'MALE', seatNumber: '' }]);
  const removePassenger = (idx) => setPassengers(passengers.filter((_, i) => i !== idx));
  const updatePassenger = (idx, field, val) => {
    const newPs = [...passengers];
    newPs[idx][field] = val;
    setPassengers(newPs);
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!flight) return;
    bookMutation.mutate({
      emailId: email,
      phoneNumber: phone,
      meal: meal,
      noOfSeats: passengers.length,
      passengers: passengers.map(p => ({ ...p, flightId: parseInt(id) }))
    });
  };

  const formatDate = (dateStr, formatStr) => {
    if (!dateStr) return 'N/A';
    const date = new Date(dateStr);
    if (!isValid(date)) return 'Invalid Date';
    return format(date, formatStr);
  };

  if (isLoading) return (
    <div className="min-h-screen flex items-center justify-center bg-white">
      <div className="text-center">
        <div className="w-16 h-16 border-4 border-blue-600 border-t-transparent rounded-full animate-spin mx-auto mb-4" />
        <p className="text-slate-400 font-black uppercase tracking-[0.2em] text-xs">Accessing Reservation Ledger...</p>
      </div>
    </div>
  );

  if (error || !flight) return (
    <div className="min-h-screen flex items-center justify-center bg-white">
       <div className="text-center p-4 bg-slate-50 rounded-3xl border border-slate-100 max-w-md">
          <ShieldCheck className="w-12 h-12 text-rose-500 mx-auto mb-4 opacity-20" />
          <h2 className="text-xl font-black text-slate-900 mb-2">Registry Access Failed</h2>
          <p className="text-slate-500 text-sm font-medium mb-6">We could not retrieve the requested flight rotation. It may have been decanonized or restricted.</p>
          <Button onClick={() => navigate('/')} className="w-full bg-slate-900 text-white rounded-xl h-12 font-black">Return Home</Button>
       </div>
    </div>
  );

  return (
    <div className="container mx-auto px-4 py-6 max-w-6xl animate-in fade-in duration-700">
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-5">
        <div className="lg:col-span-2 space-y-8">
           <div className="flex flex-col gap-2">
              <h1 className="text-4xl font-black tracking-tighter text-slate-900">Passenger Details</h1>
              <p className="text-slate-500 font-medium">Please ensure identity documents match the manifest entries.</p>
           </div>

           <form id="booking-form" onSubmit={handleSubmit} className="space-y-6">
              {passengers.map((p, idx) => (
                <Card key={idx} className="border-slate-100 shadow-[0_20px_40px_-10px_rgba(0,0,0,0.05)] bg-white rounded-[2rem] overflow-hidden group transition-all">
                   <div className="bg-slate-50 px-8 py-4 flex justify-between items-center border-b border-slate-100">
                      <span className="text-xs font-black text-slate-400 uppercase tracking-widest">Passenger {idx + 1} Entry</span>
                      {idx > 0 && (
                        <Button type="button" variant="ghost" size="sm" onClick={() => removePassenger(idx)} className="text-rose-500 hover:text-rose-600 hover:bg-rose-50 rounded-xl">
                          <Trash2 className="w-4 h-4 mr-2" /> Remove
                        </Button>
                      )}
                   </div>
                    <CardContent className="p-4 grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
                       <div className="space-y-2">
                         <Label className="text-xs font-black text-slate-400 uppercase tracking-widest ml-1 mb-1 block">Full Name</Label>
                         <Input required className="h-12 bg-slate-50 border-slate-100 rounded-xl font-bold text-slate-900" placeholder="John Doe" value={p.name} onChange={e => updatePassenger(idx, 'name', e.target.value)} />
                       </div>
                       <div className="space-y-2">
                         <Label className="text-xs font-black text-slate-400 uppercase tracking-widest ml-1 mb-1 block">Age</Label>
                         <Input required type="number" className="h-12 bg-slate-50 border-slate-100 rounded-xl font-bold text-slate-900" placeholder="30" value={p.age} onChange={e => updatePassenger(idx, 'age', e.target.value)} />
                       </div>
                       <div className="space-y-2">
                         <Label className="text-xs font-black text-slate-400 uppercase tracking-widest ml-1 mb-1 block">Gender</Label>
                         <div className="relative">
                            <select className="flex h-12 w-full rounded-xl border border-slate-100 bg-slate-50 px-4 text-sm font-bold text-slate-900 focus:outline-none appearance-none cursor-pointer" value={p.gender} onChange={e => updatePassenger(idx, 'gender', e.target.value)}>
                               <option value="MALE">Male</option>
                               <option value="FEMALE">Female</option>
                               <option value="OTHER">Other</option>
                            </select>
                            <Plus className="absolute right-4 top-1/2 -translate-y-1/2 w-3 h-3 text-slate-300 pointer-events-none rotate-45" />
                         </div>
                       </div>
                       <div className="space-y-2">
                         <Label className="text-xs font-black text-slate-400 uppercase tracking-widest ml-1 mb-1 block">Seat Number</Label>
                         <Input required className="h-12 bg-slate-50 border-slate-100 rounded-xl font-bold text-slate-900" placeholder="e.g. 12A" value={p.seatNumber} onChange={e => updatePassenger(idx, 'seatNumber', e.target.value.toUpperCase())} />
                       </div>
                    </CardContent>
                </Card>
              ))}

              <Button type="button" onClick={addPassenger} variant="outline" className="w-full h-14 border-dashed border-2 border-slate-200 text-slate-500 font-bold rounded-2xl hover:bg-slate-50 hover:border-blue-600/30 transition-all group">
                <Plus className="w-5 h-5 mr-2 group-hover:rotate-90 transition-transform" /> Add Additional Passenger
              </Button>

              <div className="pt-8">
                  <Card className="bg-slate-50 border-blue-600/10 border-2 rounded-[2rem] p-4 text-slate-900 shadow-xl overflow-hidden relative group">
                     <div className="absolute top-0 right-0 w-32 h-32 bg-blue-600/5 rounded-full blur-3xl -translate-y-16 translate-x-16" />
                     <h3 className="text-xl font-black mb-6 flex items-center gap-3 relative z-10">
                        <Mail className="w-5 h-5 text-blue-600" /> Registry Communication & Preferences
                     </h3>
                     <div className="grid grid-cols-1 md:grid-cols-2 gap-6 relative z-10">
                        <div className="space-y-2">
                           <Label className="text-xs font-black text-slate-400 uppercase tracking-widest ml-1 block">Confirmation Email</Label>
                           <Input required type="email" className="h-14 bg-white border-slate-100 rounded-2xl text-slate-900 font-bold placeholder:text-slate-200 focus:ring-blue-600/10 transition-all" placeholder="traveler@example.com" value={email} onChange={e => setEmail(e.target.value)} />
                        </div>
                        <div className="space-y-2">
                           <Label className="text-xs font-black text-slate-400 uppercase tracking-widest ml-1 block">Phone Number</Label>
                           <Input required type="tel" pattern="^[6-9]\d{9}$" className="h-14 bg-white border-slate-100 rounded-2xl text-slate-900 font-bold placeholder:text-slate-200 focus:ring-blue-600/10 transition-all" placeholder="9876543210" value={phone} onChange={e => setPhone(e.target.value)} />
                        </div>
                        <div className="space-y-2">
                           <Label className="text-xs font-black text-slate-400 uppercase tracking-widest ml-1 block">Meal Preference</Label>
                           <select className="flex h-14 w-full rounded-2xl border border-slate-100 bg-white px-4 text-sm font-bold text-slate-900 focus:outline-none appearance-none cursor-pointer" value={meal} onChange={e => setMeal(e.target.value)}>
                               <option value="NONE">No Special Meal</option>
                               <option value="VEG">Vegetarian</option>
                               <option value="NON_VEG">Non-Vegetarian</option>
                           </select>
                        </div>
                        <div className="flex items-end pb-2">
                           <p className="text-xs text-slate-400 font-medium">Electronic tickets and gate updates will be dispatched via these channels.</p>
                        </div>
                     </div>
                  </Card>
              </div>
           </form>
        </div>

        {/* Summary Sidebar */}
        <div className="space-y-8">
           <Card className="bg-white border-slate-100 shadow-[0_40px_80px_-15px_rgba(0,0,0,0.08)] rounded-[2.5rem] overflow-hidden sticky top-24">
              <div className="bg-blue-600 p-4 text-white relative overflow-hidden">
                 <div className="absolute -top-4 -right-4 w-16 h-16 bg-white/10 rounded-full blur-xl" />
                 <p className="text-xs font-black uppercase tracking-[0.2em] mb-2 opacity-80">Itinerary Summary</p>
                 <h2 className="text-3xl font-black tracking-tighter">{flight?.flightNumber}</h2>
              </div>
              <CardContent className="p-4 space-y-8">
                 <div className="flex justify-between items-center text-slate-900">
                    <div className="text-center">
                       <p className="text-2xl font-black">{flight?.from}</p>
                       <p className="text-xs font-bold text-slate-400 uppercase tracking-widest mt-1">Origin</p>
                    </div>
                    <div className="flex-1 flex flex-col items-center px-4">
                       <Plane className="w-5 h-5 text-blue-600 mb-1" />
                       <div className="w-full h-[1px] bg-slate-100" />
                    </div>
                    <div className="text-center">
                       <p className="text-2xl font-black">{flight?.to}</p>
                       <p className="text-xs font-bold text-slate-400 uppercase tracking-widest mt-1">Dest</p>
                    </div>
                 </div>

                 <div className="space-y-4 pt-6 border-t border-slate-50">
                    <SummaryInfo icon={Calendar} label="Date" val={formatDate(flight?.startDateTime, 'dd MMM yyyy')} />
                    <SummaryInfo icon={Clock} label="Departure" val={formatDate(flight?.startDateTime, 'HH:mm')} />
                    <SummaryInfo icon={ShieldCheck} label="Fares" val={`${passengers.length} Passenger(s)`} />
                 </div>

                 <Button 
                    form="booking-form"
                    type="submit"
                    disabled={bookMutation.isLoading}
                    className="w-full h-16 bg-blue-600 hover:bg-blue-700 text-lg font-black text-white rounded-2xl shadow-xl shadow-blue-500/20 active:scale-95 transition-all mt-6 group"
                 >
                    {bookMutation.isLoading ? 'Processing...' : <>Confirm & Reserve <ArrowRight className="ml-2 w-5 h-5 group-hover:translate-x-1 transition-transform" /></>}
                 </Button>
                 
                 <div className="pt-4 flex items-center justify-center gap-2 text-xs font-black text-slate-300 uppercase tracking-widest">
                    <ShieldCheck className="w-3 h-3 text-emerald-500" />
                    Secure Transaction Layer
                 </div>
              </CardContent>
           </Card>
        </div>
      </div>
    </div>
  );
}

function SummaryInfo({ icon: Icon, label, val }) {
  return (
    <div className="flex items-center justify-between text-sm">
       <div className="flex items-center gap-2 text-slate-400 font-bold uppercase tracking-widest text-xs">
          <Icon className="w-3.5 h-3.5" /> {label}
       </div>
       <span className="font-black text-slate-900">{val}</span>
    </div>
  );
}

export default withAuth(BookFlight);
