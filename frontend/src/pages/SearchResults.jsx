import { useLocation, Link } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import api from '@/lib/api-client';
import { Card, CardContent } from '@/components/ui/Card';
import { Button } from '@/components/ui/Button';
import { Badge } from '@/components/ui/Badge';
import { Plane, Clock, ArrowRight, ShieldCheck } from 'lucide-react';
import { format } from 'date-fns';

export default function SearchResults() {
  const location = useLocation();
  const { from: fromParam, to: toParam, date } = location.state || {};

  const { data: flights, isLoading } = useQuery({
    queryKey: ['flights', fromParam, toParam, date],
    queryFn: async () => {
      const response = await api.post('/api/v1.0/flight/search', {
        from: fromParam || '',
        to: toParam || '',
        date: date || ''
      });
      return response.data;
    }
  });

  if (isLoading) return <div className="p-20 text-center text-slate-400 font-bold animate-pulse uppercase tracking-[0.2em] text-xs">Scanning available rotations...</div>;

  return (
    <div className="container mx-auto px-4 py-6 max-w-5xl">
      <div className="flex flex-col md:flex-row justify-between items-end mb-6 gap-6 border-b border-slate-100 pb-8">
        <div>
          <div className="flex items-center gap-2 mb-2">
            <Badge className="bg-blue-600/10 text-blue-600 border-none font-black px-3 py-1 text-xs uppercase tracking-widest">Live Schedule</Badge>
          </div>
          <h1 className="text-4xl font-black flex items-center gap-4 text-slate-900 tracking-tighter">
            {fromParam} <ArrowRight className="text-blue-600 w-8 h-8" /> {toParam}
          </h1>
          <p className="text-slate-500 font-medium mt-2">{date ? format(new Date(date), 'EEEE, MMMM do yyyy') : 'All Available Dates'}</p>
        </div>
        <div className="bg-slate-50 px-6 py-3 rounded-2xl border border-slate-100">
          <span className="text-2xl font-black text-blue-600">{flights?.length || 0}</span>
          <span className="text-xs font-bold text-slate-400 uppercase tracking-widest ml-2">Flights Found</span>
        </div>
      </div>

      <div className="space-y-6">
        {flights?.map((flight) => (
          <FlightCard key={flight.id} flight={flight} />
        ))}
        {flights?.length === 0 && (
          <div className="text-center py-24 bg-slate-50 rounded-[2.5rem] border-2 border-dashed border-slate-100">
            <Plane className="w-16 h-16 text-slate-200 mx-auto mb-6" />
            <h3 className="text-xl font-black text-slate-900 tracking-tight">No matching rotations found</h3>
            <p className="text-slate-500 font-medium mt-1">Try adjusting your filters or checking alternative airports.</p>
          </div>
        )}
      </div>
    </div>
  );
}

function FlightCard({ flight }) {
  return (
    <Card className="bg-white border-slate-100 hover:border-blue-600/30 transition-all overflow-hidden rounded-[2rem] shadow-sm hover:shadow-xl group">
      <CardContent className="p-0">
        <div className="flex flex-col lg:flex-row p-4 items-center gap-5">
          {/* Airline Branding */}
          <div className="flex flex-col items-center lg:items-start min-w-[160px]">
            <div className="w-16 h-16 bg-slate-50 rounded-2xl flex items-center justify-center mb-4 border border-slate-100 p-3 shadow-inner">
              {flight.logo ? (
                <img src={flight.logo} alt="" className="object-contain w-full h-full" />
              ) : (
                <span className="text-slate-900 font-black text-lg uppercase tracking-tighter">{flight.airline.substring(0, 2)}</span>
              )}
            </div>
            <p className="font-black text-slate-900 text-lg leading-tight">{flight.airline}</p>
            <p className="text-xs text-slate-400 font-black uppercase tracking-widest mt-1">{flight.flightNumber}</p>
          </div>

          {/* Core Logistics */}
          <div className="flex-1 flex items-center justify-between gap-6 w-full">
            <div className="text-center lg:text-left">
              <p className="text-3xl font-black text-slate-900 tracking-tighter">{format(new Date(flight.startDateTime), 'HH:mm')}</p>
              <p className="text-xs font-black text-blue-600 uppercase tracking-[0.2em] mt-2">{flight.from}</p>
            </div>

            <div className="flex-1 flex flex-col items-center px-4">
              <div className="flex items-center gap-2 mb-3">
                <ShieldCheck className="w-3 h-3 text-emerald-500" />
                <span className="text-xs font-black text-emerald-600 uppercase tracking-widest leading-none">Verified Route</span>
              </div>
              <div className="w-full h-[3px] bg-slate-100 relative rounded-full">
                <div className="absolute left-0 top-0 h-full bg-blue-600 rounded-full w-2/3 shadow-sm shadow-blue-500/30" />
                <Plane className="w-5 h-5 text-blue-600 absolute left-2/3 top-1/2 -translate-x-1/2 -translate-y-1/2 bg-white rounded-full p-0.5" />
              </div>
              <span className="text-xs text-slate-400 font-black uppercase tracking-[0.2em] mt-4 flex items-center gap-2">
                <Clock className="w-3 h-3" /> Direct Rotation
              </span>
            </div>

            <div className="text-center lg:text-right">
              <p className="text-3xl font-black text-slate-900 tracking-tighter">{format(new Date(flight.endDateTime), 'HH:mm')}</p>
              <p className="text-xs font-black text-blue-600 uppercase tracking-[0.2em] mt-2">{flight.to}</p>
            </div>
          </div>

          {/* Selection Action */}
          <div className="min-w-[180px] flex flex-col items-center lg:items-end gap-4">
            <Link to={`/book/${flight.id}`} className="w-full">
              <Button className="w-full bg-blue-600 hover:bg-blue-700 h-14 text-lg font-black text-white rounded-2xl shadow-xl shadow-blue-500/20 active:scale-95 transition-all">
                Select Flight
              </Button>
            </Link>
          </div>
        </div>
        <div className="bg-slate-50/50 px-8 py-4 border-t border-slate-100 flex justify-between items-center">
          <div className="flex gap-4">
            <span className="text-xs font-black text-slate-400 uppercase tracking-widest">Aircraft: <span className="text-slate-900">{flight.instrumentUsed}</span></span>
            <span className="text-xs font-black text-slate-400 uppercase tracking-widest border-l border-slate-200 pl-4 ml-4">Seats: <span className="text-emerald-600">{flight.availableSeats} Remaining</span></span>
          </div>
          <div className="flex gap-2">
            <Badge variant="outline" className="text-xs font-black uppercase tracking-widest bg-white border-slate-200 px-3">Business</Badge>
            <Badge variant="outline" className="text-xs font-black uppercase tracking-widest bg-white border-slate-200 px-3">Economy</Badge>
          </div>
        </div>
      </CardContent>
    </Card>
  );
}
