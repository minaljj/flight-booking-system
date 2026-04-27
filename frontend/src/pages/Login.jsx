import { useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { Card, CardHeader, CardTitle, CardContent } from '@/components/ui/Card';
import { Button } from '@/components/ui/Button';
import { Input } from '@/components/ui/Input';
import { Label } from '@/components/ui/Label';
import { Plane, Lock, User, ShieldCheck } from 'lucide-react';
import api from '@/lib/api-client';
import { toast } from 'sonner';

export default function Login() {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();
  const location = useLocation();
  const redirect = location.state?.redirect || '/';

  const handleLogin = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      const response = await api.post('/api/v1.0/flight/auth/login', { username, password });
      localStorage.setItem('token', response.data.token);
      localStorage.setItem('user', JSON.stringify(response.data));
      toast.success(`Welcome back, ${response.data.username}!`);
      navigate(decodeURIComponent(redirect));
    } catch (err) {
      toast.error('Invalid credentials. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="h-[calc(100vh-64px)] overflow-hidden flex items-center justify-center p-4 bg-white">
      <div className="w-full max-w-[440px] relative">
        {/* Abstract Background Decoration */}
        <div className="absolute -top-12 -left-12 w-64 h-64 bg-blue-600/5 rounded-full blur-3xl pointer-events-none" />
        <div className="absolute -bottom-12 -right-12 w-64 h-64 bg-indigo-600/5 rounded-full blur-3xl pointer-events-none" />
        
        <Card className="border-slate-100 shadow-[0_40px_80px_-15px_rgba(0,0,0,0.08)] bg-white rounded-[2.5rem] relative overflow-hidden">
          <div className="bg-blue-600 h-2 w-full" />
          <CardHeader className="pt-6 pb-2 text-center">
             <div className="w-10 h-10 bg-blue-50 rounded-xl flex items-center justify-center mx-auto mb-2 shadow-inner border border-blue-100">
                <Plane className="w-5 h-5 text-blue-600" />
             </div>
             <CardTitle className="text-2xl font-black tracking-tighter text-slate-900">Secure Access</CardTitle>
          </CardHeader>
          <CardContent className="px-6 pb-6">
            <form onSubmit={handleLogin} className="space-y-3">
              <div className="space-y-1">
                <Label className="text-[10px] font-black text-slate-400 uppercase tracking-widest ml-1 block">Username</Label>
                <div className="relative">
                  <User className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-400" />
                  <Input 
                    required 
                    className="pl-9 h-10 text-sm bg-slate-50 border-slate-100 rounded-xl font-bold text-slate-900 focus:ring-2 focus:ring-blue-600/10 placeholder:text-slate-200 transition-all" 
                    placeholder="admin or user"
                    value={username}
                    onChange={e => setUsername(e.target.value)}
                  />
                </div>
              </div>
              <div className="space-y-1">
                <Label className="text-[10px] font-black text-slate-400 uppercase tracking-widest ml-1 block">Password</Label>
                <div className="relative">
                  <Lock className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-400" />
                  <Input 
                    required 
                    type="password" 
                    className="pl-9 h-10 text-sm bg-slate-50 border-slate-100 rounded-xl font-bold text-slate-900 focus:ring-2 focus:ring-blue-600/10 placeholder:text-slate-200 transition-all" 
                    placeholder="••••••••"
                    value={password}
                    onChange={e => setPassword(e.target.value)}
                  />
                </div>
              </div>
              <Button 
                type="submit" 
                disabled={loading}
                className="w-full h-10 bg-blue-600 hover:bg-blue-700 text-sm font-black text-white rounded-xl shadow-xl shadow-blue-500/20 active:scale-95 transition-all mt-2"
              >
                {loading ? 'Authenticating...' : 'Sign In'}
              </Button>

              <div className="flex items-center justify-center gap-2 pt-4 border-t border-slate-50 text-[10px] font-black text-slate-400 uppercase tracking-[0.15em]">
                 <ShieldCheck className="w-3.5 h-3.5 text-emerald-500" />
                 Encrypted Authentication Session
              </div>
            </form>
          </CardContent>
        </Card>
      </div>
    </div>
  );
}
