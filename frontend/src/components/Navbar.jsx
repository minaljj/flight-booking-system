import { useState, useEffect } from 'react';
import { Link, useLocation } from 'react-router-dom';
import { Plane, LogOut } from 'lucide-react';
import { Button } from './ui/Button';
import { cn } from '@/lib/utils';

export default function Navbar() {
  const [user, setUser] = useState(null);
  const location = useLocation();

  useEffect(() => {
    const storedUser = localStorage.getItem('user');
    if (storedUser) {
      setUser(JSON.parse(storedUser));
    }
 
    const handleStorage = () => {
      const u = localStorage.getItem('user');
      setUser(u ? JSON.parse(u) : null);
    };
    window.addEventListener('storage', handleStorage);
    return () => window.removeEventListener('storage', handleStorage);
  }, [location]);

  const logout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    setUser(null);
    window.location.href = '/';
  };

  const isAdmin = user?.roles?.includes('ROLE_ADMIN');

  return (
    <nav className="sticky top-0 z-50 w-full border-b bg-white dark:bg-slate-950/80 backdrop-blur-xl transition-all duration-300">
      <div className="container mx-auto flex h-16 items-center justify-between px-4">
        <Link to="/" className="flex items-center gap-2 group">
          <div className="bg-blue-600 p-1.5 rounded-lg rotate-3 group-hover:rotate-0 transition-transform shadow-lg shadow-blue-500/20">
             <Plane className="h-5 w-5 text-white" />
          </div>
          <span className="text-xl font-black tracking-tighter text-slate-900 dark:text-white">FlightApp</span>
        </Link>

        <div className="hidden md:flex items-center gap-8">
          <NavLink to="/" active={location.pathname === '/'}>Home</NavLink>
          {user && !isAdmin && <NavLink to="/history" active={location.pathname === '/history'}>My Bookings</NavLink>}
          {isAdmin && <NavLink to="/admin/dashboard" active={location.pathname.startsWith('/admin')}>Dashboard</NavLink>}
        </div>

        <div className="flex items-center gap-4">
          {user ? (
            <div className="flex items-center gap-3">
               <div className="hidden sm:flex flex-col items-end mr-2">
                  <span className="text-xs font-bold text-slate-400 uppercase tracking-widest leading-none mb-1">Authenticated</span>
                  <span className="text-sm font-black text-slate-900 dark:text-white leading-none">{user.username}</span>
               </div>
               <Button variant="ghost" size="icon" className="rounded-full hover:bg-slate-100 dark:hover:bg-slate-800" onClick={logout}>
                 <LogOut className="h-5 w-5 text-slate-500" />
               </Button>
            </div>
          ) : (
            <div className="flex items-center gap-2">
              <Link to="/signup" className="hidden sm:block">
                <Button variant="default" className="bg-emerald-600 hover:bg-emerald-700 text-white font-black text-xs uppercase tracking-widest h-10 px-6 rounded-xl shadow-lg shadow-emerald-500/20 border-none transition-all">
                  Sign Up
                </Button>
              </Link>
              <Link to="/login">
                <Button variant="default" className="bg-blue-600 hover:bg-blue-700 text-white font-black text-xs uppercase tracking-widest h-10 px-6 rounded-xl shadow-lg shadow-blue-500/20 border-none transition-all">
                  Log In
                </Button>
              </Link>
            </div>
          )}
        </div>
      </div>
    </nav>
  );
}

function NavLink({ to, children, active }) {
  return (
    <Link 
      to={to} 
      className={cn(
        "text-sm font-bold transition-all relative py-1",
        active 
          ? "text-blue-600 dark:text-blue-400" 
          : "text-slate-500 hover:text-slate-900 dark:hover:text-white"
      )}
    >
      {children}
      {active && <span className="absolute -bottom-1 left-0 w-full h-[2px] bg-blue-600 dark:bg-blue-400 rounded-full" />}
    </Link>
  );
}
