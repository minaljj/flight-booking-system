import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { Card, CardHeader, CardTitle, CardContent } from '@/components/ui/Card';
import { Button } from '@/components/ui/Button';
import { Input } from '@/components/ui/Input';
import { Label } from '@/components/ui/Label';
import { Plane, Lock, User, Mail, ShieldPlus } from 'lucide-react';
import { toast } from 'sonner';
import axios from 'axios';

export default function Signup() {
  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleSignup = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      await axios.post('http://localhost:8080/api/v1.0/flight/auth/register', { 
        username, email, password,
        roles: ["ROLE_USER"] 
      });
      toast.success('Registration successful! Please, login-in');
      navigate('/login');
    } catch (err) {
      const msg = err.response?.data?.message || 'Registration failed. Please try again.';
      toast.error(msg);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="h-[calc(100vh-64px)] overflow-hidden flex items-center justify-center p-4 bg-white">
      <div className="w-full max-w-[440px] relative">
        {/* Abstract Background Decoration */}
        <div className="absolute -top-12 -left-12 w-64 h-64 bg-emerald-600/5 rounded-full blur-3xl pointer-events-none" />
        <div className="absolute -bottom-12 -right-12 w-64 h-64 bg-teal-600/5 rounded-full blur-3xl pointer-events-none" />
        
        <Card className="border-slate-100 shadow-[0_40px_80px_-15px_rgba(0,0,0,0.08)] bg-white rounded-[2.5rem] relative overflow-hidden">
          <div className="bg-emerald-600 h-2 w-full" />
          <CardHeader className="pt-6 pb-2 text-center">
             <div className="w-10 h-10 bg-emerald-50 rounded-xl flex items-center justify-center mx-auto mb-2 shadow-inner border border-emerald-100">
                <Plane className="w-5 h-5 text-emerald-600" />
             </div>
             <CardTitle className="text-2xl font-black tracking-tighter text-slate-900">Create Account</CardTitle>
          </CardHeader>
          <CardContent className="px-6 pb-6">
            <form onSubmit={handleSignup} className="space-y-3">
              <div className="space-y-1">
                <Label className="text-[10px] font-black text-slate-400 uppercase tracking-widest ml-1 block">Username</Label>
                <div className="relative">
                  <User className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-400" />
                  <Input 
                    required 
                    className="pl-9 h-10 text-sm bg-slate-50 border-slate-100 rounded-xl font-bold text-slate-900 focus:ring-2 focus:ring-emerald-600/10 placeholder:text-slate-200 transition-all" 
                    placeholder="Choose a username"
                    value={username}
                    onChange={e => setUsername(e.target.value)}
                  />
                </div>
              </div>

              <div className="space-y-1">
                <Label className="text-[10px] font-black text-slate-400 uppercase tracking-widest ml-1 block">Email Address</Label>
                <div className="relative">
                  <Mail className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-400" />
                  <Input 
                    required 
                    type="email"
                    className="pl-9 h-10 text-sm bg-slate-50 border-slate-100 rounded-xl font-bold text-slate-900 focus:ring-2 focus:ring-emerald-600/10 placeholder:text-slate-200 transition-all" 
                    placeholder="email@example.com"
                    value={email}
                    onChange={e => setEmail(e.target.value)}
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
                    className="pl-9 h-10 text-sm bg-slate-50 border-slate-100 rounded-xl font-bold text-slate-900 focus:ring-2 focus:ring-emerald-600/10 placeholder:text-slate-200 transition-all" 
                    placeholder="••••••••"
                    value={password}
                    onChange={e => setPassword(e.target.value)}
                  />
                </div>
              </div>

              <Button 
                type="submit" 
                disabled={loading}
                className="w-full h-10 bg-emerald-600 hover:bg-emerald-700 text-sm font-black text-white rounded-xl shadow-xl shadow-emerald-500/20 active:scale-95 transition-all mt-2"
              >
                {loading ? 'Creating Account...' : 'Get Started'}
              </Button>

              <p className="text-center text-sm font-bold text-slate-500 mt-4">
                Already have an account?{' '}
                <Link to="/login" className="text-emerald-600 hover:text-emerald-700 underline underline-offset-4">
                  Sign In
                </Link>
              </p>

              <div className="flex items-center justify-center gap-2 pt-4 border-t border-slate-50 text-[10px] font-black text-slate-400 uppercase tracking-[0.15em]">
                 <ShieldPlus className="w-3.5 h-3.5 text-emerald-500" />
                 Safe & Secure Registration
              </div>
            </form>
          </CardContent>
        </Card>
      </div>
    </div>
  );
}
