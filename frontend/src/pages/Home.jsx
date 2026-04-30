import { useState } from 'react';
import { Calendar, PlaneTakeoff, PlaneLanding, ArrowRight } from 'lucide-react';
import { Button } from '@/components/ui/Button';
import { Card } from '@/components/ui/Card';
import { Input } from '@/components/ui/Input';
import { Label } from '@/components/ui/Label';
import { motion } from 'framer-motion';
import { useNavigate } from 'react-router-dom';
import { cn } from '@/lib/utils';
import { toast } from 'react-hot-toast';

export default function Home() {
  const [date, setDate] = useState(new Date().toISOString().split('T')[0]);
  const [tripType, setTripType] = useState('one-way');
  const [from, setFrom] = useState('');
  const [to, setTo] = useState('');
  const navigate = useNavigate();

  const handleSearch = () => {
    if (!from.trim() || !to.trim()) {
      toast.error('Please specify both departure and arrival locations.');
      return;
    }
    navigate('/search', { 
      state: { from, to, date, tripType } 
    });
  };

  return (
    <div className="relative h-[calc(100vh-64px)] overflow-hidden bg-white transition-colors duration-300 flex items-center justify-center">
      <div className="container relative mx-auto px-4 text-center">
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.6 }}
        >
          <h1 className="text-3xl md:text-5xl lg:text-6xl font-black tracking-tighter text-slate-900 mb-4">
            Fly Beyond <span className="text-blue-600">Boundaries</span>
          </h1>
          <p className="mx-auto max-w-2xl text-base md:text-lg text-slate-500 font-medium mb-6">
            Precision flight search and secure booking for the modern explorer. Experience the next generation of travel management.
          </p>
        </motion.div>

        <motion.div
          initial={{ opacity: 0, scale: 0.95 }}
          animate={{ opacity: 1, scale: 1 }}
          transition={{ duration: 0.4, delay: 0.2 }}
          className="mx-auto max-w-3xl"
        >
          <Card className="p-6 bg-white border border-slate-100 shadow-[0_40px_80px_-15px_rgba(0,0,0,0.08)] rounded-[2rem]">
            <div className="flex flex-col gap-4">
              <div className="flex gap-1 p-1 bg-slate-50 border border-slate-100 rounded-xl w-fit">
                <Button
                  variant={tripType === 'one-way' ? 'default' : 'ghost'}
                  size="sm"
                  onClick={() => setTripType('one-way')}
                  className={cn(
                    "rounded-xl px-4 font-bold transition-all",
                    tripType === 'one-way' ? "bg-blue-600 text-white shadow-md shadow-blue-500/20" : "text-slate-500"
                  )} > One Way</Button>
                <Button
                  variant={tripType === 'round-trip' ? 'default' : 'ghost'}
                  size="sm"
                  onClick={() => setTripType('round-trip')}
                  className={cn(
                    "rounded-xl px-4 font-bold transition-all",
                    tripType === 'round-trip' ? "bg-blue-600 text-white shadow-md shadow-blue-500/20" : "text-slate-500"
                  )}> Round Trip</Button>
              </div>

              <div className="flex flex-col md:flex-row gap-3 text-left items-end">
                <div className="flex-1 w-full">
                  <SearchField label="From" icon={PlaneTakeoff} placeholder="New Delhi (DEL)" value={from} onChange={setFrom} />
                </div>
                <div className="flex-1 w-full">
                  <SearchField label="To" icon={PlaneLanding} placeholder="Bangalore (BLR)" value={to} onChange={setTo} />
                </div>
                <div className="flex-1 w-full">
                  <SearchField label="Travel Date" icon={Calendar} type="date" value={date} onChange={setDate} />
                </div>
                <Button
                  onClick={handleSearch}
                  className="w-full md:w-auto h-12 px-8 bg-blue-600 hover:bg-blue-700 text-base font-black text-white rounded-xl shadow-xl shadow-blue-500/30 group transition-all shrink-0"
                >
                  Search Flights <ArrowRight className="ml-2 w-5 h-5 group-hover:translate-x-1 transition-transform" />
                </Button>
              </div>
            </div>
          </Card>
        </motion.div>
      </div>
    </div>
  );
}

function SearchField({ label, icon: Icon, placeholder, value, onChange, type = "text" }) {
  return (
    <div className="space-y-2">
      <Label className="text-xs font-black text-slate-400 uppercase tracking-widest ml-1 mb-2 block">{label}</Label>
      <div className="relative">
        <Icon className="absolute left-4 top-1/2 -translate-y-1/2 w-4 h-4 text-blue-600" />
        <Input
          type={type}
          className="pl-10 h-10 bg-slate-50 border-slate-100 rounded-xl focus:ring-2 focus:ring-blue-600/10 text-slate-900 font-bold placeholder:text-slate-300 transition-all text-sm"
          placeholder={placeholder}
          value={value}
          onChange={(event) => onChange(event.target.value)}
        />
      </div>
    </div>
  );
}
