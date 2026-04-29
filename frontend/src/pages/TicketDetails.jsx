import { useParams } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import api from '@/lib/api-client';
import { Card, CardHeader, CardTitle, CardContent, CardFooter } from '@/components/ui/Card';
import { Button } from '@/components/ui/Button';
import { Badge } from '@/components/ui/Badge';
import { Plane, Scissors, User, Printer, ShieldCheck } from 'lucide-react';
import { format } from 'date-fns';
import { useRef } from 'react';
import { useReactToPrint } from 'react-to-print';
import { parseDate } from '@/lib/dateUtils';
import { withAuth } from '@/lib/withAuth';
import QRCode from 'react-qr-code';

function TicketDetails() {
   const { pnr } = useParams();
   const componentRef = useRef(null);

   const handlePrint = useReactToPrint({
      contentRef: componentRef,
      documentTitle: `FlightApp-Ticket-${pnr}`,
   });

   const { data: booking, isLoading } = useQuery({
      queryKey: ['ticket', pnr],
      queryFn: async () => {
         const response = await api.get(`/api/v1.0/flight/booking/ticket/${pnr}`);
         return response.data;
      }
   });
   const { data: flight } = useQuery({
      queryKey: ['flight', booking?.flightId],
      queryFn: async () => {
         const res = await api.get(`/api/v1.0/flight/${booking.flightId}`);
         return res.data;
      },
      enabled: !!booking?.flightId
   });
   if (isLoading) return <div className="p-20 text-center text-slate-400 font-black animate-pulse uppercase tracking-[0.2em] text-xs">Generating boarding pass signature...</div>;
   if (!booking) return <div className="p-20 text-center text-rose-500 font-black tracking-tight">Access Denied: Invalid PNR Signature</div>;

   return (
      <div className="container mx-auto px-4 py-6 max-w-3xl">
         <div className="flex flex-col md:flex-row justify-between items-start md:items-center mb-10 gap-4">
            <div>
               <h1 className="text-3xl font-black text-slate-900 tracking-tight">Electronic Ticket</h1>
               <p className="text-sm text-slate-500 font-medium mt-1">Official air travel authorization for {booking.passengers[0].name}.</p>
            </div>
            <div className="flex gap-2">
               <Button
                  onClick={() => handlePrint()}
                  className="bg-blue-600 hover:bg-blue-700 text-white font-bold h-12 px-6 rounded-xl shadow-lg shadow-blue-500/20 active:scale-95 transition-all"
               >
                  <Printer className="w-5 h-5 mr-3" /> Print Ticket
               </Button>
            </div>
         </div>

         <div className="relative group">

            <div ref={componentRef} className="bg-white rounded-[2.5rem] overflow-hidden shadow-2xl print:shadow-none print:m-0 w-full border border-slate-100 transition-transform duration-500">
               <Card className="bg-white text-slate-950 border-none rounded-none shadow-none">

                  <div className="bg-blue-600 p-6 text-white relative flex justify-between items-center print:bg-blue-600 print:text-white">
                     <div className="z-10">
                        <p className="text-xs opacity-80 uppercase font-black tracking-[0.3em] mb-2 text-blue-100">Official Boarding Pass</p>
                        <h2 className="text-5xl font-black tracking-tighter">{booking.pnr}</h2>
                     </div>
                     <Plane className="w-16 h-16 opacity-20 transform -rotate-45 z-10" />
                  </div>

                  <CardContent className="p-6 space-y-12">
                     <div className="flex justify-between items-center">
                        <div className="text-center md:text-left">
                           <p className="text-4xl font-black text-blue-600 tracking-tighter">{flight?.from}</p>
                           <p className="text-xs text-slate-400 uppercase font-black tracking-widest mt-1">Flight #{booking.flightId}</p>
                        </div>
                        <div className="flex-1 flex flex-col items-center px-10">
                           <div className="w-full h-[2px] bg-slate-100 border-dashed border-t-2 border-slate-200 mb-3 relative">
                              <div className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 bg-white px-3">
                                 <ShieldCheck className="w-5 h-5 text-emerald-500" />
                              </div>
                           </div>
                           <Badge variant="outline" className="bg-emerald-50 text-emerald-600 border-emerald-100 font-black px-4 py-1 text-xs uppercase tracking-widest">
                              Confirmed & Cleared
                           </Badge>
                        </div>
                        <div className="text-center md:text-right">
                           <p className="text-4xl font-black text-blue-600 tracking-tighter">{flight?.to}</p>
                           <p className="text-xs text-slate-400 uppercase font-black tracking-widest mt-1">Direct Flow</p>
                        </div>
                     </div>

                     <div className="grid grid-cols-2 md:grid-cols-4 gap-4 py-8 border-y border-slate-50">
                        <TicketInfo label="Primary Passenger" val={booking.passengers[0].name} />
                        <TicketInfo label="Registry Email" val={booking.emailId} />
                        <TicketInfo label="Issuance Date" val={format(parseDate(booking.bookingDate), 'dd MMM yyyy')} />
                        <TicketInfo label="Meal Service" val={booking.meal} />
                     </div>

                     <div className="space-y-6">
                        <h4 className="text-xs font-black text-slate-400 uppercase tracking-[0.25em] mb-4">Passenger Manifest - Verified Section</h4>
                        <div className="grid grid-cols-1 gap-4">
                           {booking.passengers.map((p, idx) => (
                              <div key={idx} className="flex justify-between items-center bg-slate-50/50 p-6 rounded-2xl border border-slate-100/50">
                                 <div className="flex items-center gap-4 min-w-0 flex-1">
                                    <div className="w-12 h-12 bg-blue-100/50 rounded-xl flex items-center justify-center border border-blue-200/20 shrink-0">
                                       <User className="w-5 h-5 text-blue-600" />
                                    </div>
                                    <div className="min-w-0 pr-4">
                                       <p className="font-black text-slate-900 text-lg tracking-tight truncate">{p.name}</p>
                                       <p className="text-xs text-slate-500 uppercase font-bold">{p.gender} • Age {p.age}</p>
                                    </div>
                                 </div>
                                 <div className="text-right shrink-0 border-l border-slate-100 pl-8 h-12 flex flex-col justify-center">
                                    <p className="text-xs font-black text-slate-400 uppercase leading-tight">Seat</p>
                                    <span className="text-3xl font-black text-blue-600 tracking-tighter leading-none">{p.seatNumber}</span>
                                 </div>
                              </div>
                           ))}
                        </div>
                     </div>
                  </CardContent>

                  <div className="bg-slate-50 p-6 border-t-2 border-dashed border-slate-200 flex flex-col md:flex-row justify-between items-center gap-4">
                     <div className="flex items-center gap-6">
                        <div className="bg-white p-2 rounded-2xl shadow-sm border border-slate-100 flex items-center justify-center">
                           <QRCode
                              value={JSON.stringify({
                                 pnr: booking.pnr,
                                 name: booking.passengers[0].name,
                                 flightId: booking.flightId,
                                 date: booking.bookingDate
                              })}
                              size={80}
                              style={{ height: "auto", maxWidth: "100%", width: "100%" }}
                           />
                        </div>
                        <div>
                           <p className="text-xs font-black text-slate-400 uppercase mb-2 tracking-widest">Digital Auth Token</p>
                           <p className="text-xs font-mono text-slate-500 bg-white px-3 py-1.5 rounded-lg border border-slate-100 shadow-inner max-w-[200px] truncate">
                              {booking.pnr}-{booking.emailId.split('@')[0]}
                           </p>
                        </div>
                     </div>
                     <div className="text-center md:text-right">
                        <p className="text-xs font-black text-slate-400 uppercase mb-3 tracking-wider">FlightApp Security Core</p>
                        <div className="flex items-center gap-2 text-slate-800 justify-end bg-slate-200/50 px-4 py-2 rounded-full border border-slate-200/50">
                           <Scissors className="w-4 h-4 text-slate-400" />
                           <span className="text-xs font-black uppercase tracking-tight">Detach for gate entry</span>
                        </div>
                     </div>
                  </div>
               </Card>
            </div>
         </div>
      </div>
   );
}

function TicketInfo({ label, val }) {
   return (
      <div className="space-y-1.5">
         <p className="text-xs font-black text-slate-400 uppercase tracking-widest">{label}</p>
         <p className="font-black text-slate-900 text-sm border-l-4 border-blue-600 pl-3 leading-tight">{val}</p>
      </div>
   );
}

export default withAuth(TicketDetails);
