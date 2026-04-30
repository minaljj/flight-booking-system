import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import api from '@/lib/api-client';
import { Card, CardHeader, CardTitle, CardContent } from '@/components/ui/Card';
import { Button } from '@/components/ui/Button';
import { Badge } from '@/components/ui/Badge';
import { Plane, Hash, Trash2, ShieldAlert } from 'lucide-react';
import { format } from 'date-fns';
import { Link } from 'react-router-dom';
import { parseDate } from '@/lib/dateUtils';
import { cn } from '@/lib/utils';
import { withAuth } from '@/lib/withAuth';
import { toast } from 'sonner';

function BookingHistory() {
  const queryClient = useQueryClient();
  const userStr = localStorage.getItem('user');
  const user = userStr ? JSON.parse(userStr) : null;

  const { data: bookings, isLoading } = useQuery({
    queryKey: ['bookings', user?.email],
    queryFn: async () => {
      const response = await api.get(`/api/v1.0/flight/booking/history/${user?.email}`);
      return response.data;
    },
    enabled: !!user?.email
  });

  const cancelMutation = useMutation({
    mutationFn: (pnr) => api.delete(`/api/v1.0/flight/booking/cancel/${pnr}`),
    onSuccess: () => {
      toast.success('Reservation Decanonized Successfully');
      queryClient.invalidateQueries(['bookings']);
    },
    onError: () => toast.error('Cancellation threshold exceeded or server error.')
  });

  if (isLoading) return <div className="p-20 text-center text-slate-400 font-black animate-pulse uppercase tracking-[0.2em] text-xs">Synchronizing Reservation Archives...</div>;

  return (
    <div className="container mx-auto px-4 py-6 max-w-5xl">
      <div className="flex flex-col md:flex-row justify-between items-start md:items-center mb-6 gap-4">
         <div>
            <h1 className="text-4xl font-black tracking-tighter text-slate-900 flex items-center gap-3">
               My <span className="text-blue-600">Bookings</span>
            </h1>
            <p className="text-slate-500 font-medium mt-1">Management of your active and historical flight rotations.</p>
         </div>
         <div className="bg-slate-50 px-6 py-3 rounded-2xl border border-slate-100 flex items-center gap-3">
            <span className="text-sm font-black text-slate-400 uppercase tracking-widest">Total Vaulted</span>
            <span className="text-2xl font-black text-blue-600">{bookings?.length || 0}</span>
         </div>
      </div>

      <div className="space-y-6">
        {bookings?.sort((a,b) => parseDate(b.bookingDate).getTime() - parseDate(a.bookingDate).getTime()).map((booking) => (
          <Card key={booking.id} className="bg-white border-slate-100 hover:border-slate-200 transition-all rounded-[2rem] overflow-hidden shadow-sm hover:shadow-xl group">
             <CardContent className="p-0">
                <div className="flex flex-col lg:flex-row p-4 items-center gap-5">
               
                   <div className="flex flex-col items-center lg:items-start min-w-[140px]">
                      <div className="w-14 h-14 bg-slate-900 rounded-2xl flex items-center justify-center mb-4 shadow-xl shadow-slate-950/20">
                         <Hash className="w-6 h-6 text-blue-400" />
                      </div>
                      <p className="font-black text-slate-900 text-xl tracking-tighter leading-none">{booking.pnr}</p>
                      <p className="text-xs text-slate-400 font-black uppercase tracking-widest mt-2">PNR Reference</p>
                   </div>


                   <div className="flex-1 space-y-4 w-full">
                      <div className="flex items-center justify-between">
                         <div className="flex items-center gap-3">
                            <Plane className="w-4 h-4 text-blue-600" />
                            <span className="text-sm font-black text-slate-900 uppercase tracking-widest">Flight ID #{booking.flightId}</span>
                         </div>
                         <Badge 
                           className={cn(
                             "font-black px-4 py-1 text-xs uppercase tracking-[0.2em] border-none shadow-sm",
                             booking.status === 'BOOKED' ? "bg-emerald-50 text-emerald-600" : "bg-rose-50 text-rose-600"
                           )}
                         >
                           {booking.status}
                         </Badge>
                      </div>
                      
                      <div className="grid grid-cols-2 md:grid-cols-3 gap-6 pt-4 border-t border-slate-50">
                         <div>
                            <p className="text-xs font-black text-slate-400 uppercase tracking-widest mb-1">Traveler</p>
                            <p className="font-bold text-slate-900 text-sm truncate">{booking.name}</p>
                         </div>
                         <div>
                            <p className="text-xs font-black text-slate-400 uppercase tracking-widest mb-1">Booked On</p>
                            <p className="font-bold text-slate-900 text-sm">{format(parseDate(booking.bookingDate), 'dd MMM yy')}</p>
                         </div>
                         <div className="hidden md:block">
                            <p className="text-xs font-black text-slate-400 uppercase tracking-widest mb-1">Seats Allocated</p>
                            <p className="font-bold text-slate-900 text-sm">{booking.noOfSeats} Passengers</p>
                         </div>
                      </div>
                   </div>

                   <div className="flex flex-col gap-3 min-w-[180px]">
                      <Link to={`/ticket/${booking.pnr}`} className="w-full">
                         <Button className="w-full bg-slate-100 hover:bg-slate-200 text-slate-900 font-black h-12 rounded-xl border border-slate-200 shadow-sm transition-all group-hover:bg-white">
                            View Boarding Pass
                         </Button>
                      </Link>
                      {booking.status === 'BOOKED' && (
                        <Button 
                          variant="ghost" 
                          onClick={() => cancelMutation.mutate(booking.pnr)}
                          className="w-full text-slate-400 hover:text-rose-600 hover:bg-rose-50 text-xs font-black uppercase tracking-widest h-10 rounded-xl transition-all"
                        >
                          <Trash2 className="w-3.5 h-3.5 mr-2" /> Decanonize Invitation
                        </Button>
                      )}
                   </div>
                </div>
             </CardContent>
          </Card>
        ))}
        
        {bookings?.length === 0 && (
          <div className="text-center py-24 bg-slate-50 rounded-[2.5rem] border-2 border-dashed border-slate-100">
             <ShieldAlert className="w-16 h-16 text-slate-200 mx-auto mb-6" />
             <h3 className="text-xl font-black text-slate-900 tracking-tight">Archives are empty</h3>
             <p className="text-slate-500 font-medium mt-1">Start your journey by booking a flight from the home screen.</p>
          </div>
        )}
      </div>
    </div>
  );
}

export default withAuth(BookingHistory);
