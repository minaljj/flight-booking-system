import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import api from '@/lib/api-client';
import { Card, CardContent } from '@/components/ui/Card';
import { Button } from '@/components/ui/Button';
import { Badge } from '@/components/ui/Badge';
import {  ShieldAlert, ShieldCheck, Search,Filter,Mail,User as UserIcon,CheckCircle2,XCircle} from 'lucide-react';
import { Table, TableHeader, TableRow, TableHead, TableBody, TableCell } from '@/components/ui/Table';
import { Input } from '@/components/ui/Input';
import { toast } from 'sonner';
import { withAuth } from '@/lib/withAuth';

function ManageUsers() {
  const queryClient = useQueryClient();
  const [searchTerm, setSearchTerm] = useState('');

  const { data: users, isLoading } = useQuery({
    queryKey: ['admin-users'],
    queryFn: async () => {
      const response = await api.get('/api/v1.0/flight/auth/admin/users');
      return response.data;
    }
  });

  const blockMutation = useMutation({
    mutationFn: async ({ username, block }) => {
      return api.post('/api/v1.0/flight/auth/admin/block-user', { username, block });
    },
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries(['admin-users']);
      toast.success(`User ${variables.username} ${variables.block ? 'blocked' : 'unblocked'} successfully`);
    },
    onError: () => {
      toast.error('Failed to update user status');
    }
  });

  const filteredUsers = users?.filter(u => 
    u.username.toLowerCase().includes(searchTerm.toLowerCase()) ||
    u.email.toLowerCase().includes(searchTerm.toLowerCase())
  );

  if (isLoading) return <div className="p-20 text-center animate-pulse font-black text-slate-300">INITIALIZING SECURITY PROTOCOLS...</div>;

  return (
    <div className="container mx-auto px-4 py-6 max-w-6xl">
      <div className="flex flex-col md:flex-row justify-between items-start md:items-center mb-8 gap-6">
         <div>
            <div className="flex items-center gap-2 mb-2">
               <Badge className="bg-emerald-600/10 text-emerald-600 border-none font-black px-3 py-1 text-xs uppercase tracking-widest">Access Control</Badge>
            </div>
            <h1 className="text-4xl font-black tracking-tighter text-slate-900 flex items-center gap-4">
               User <span className="text-blue-600">Management</span>
            </h1>
            <p className="text-slate-500 font-medium mt-1">Monitor and manage access privileges and account security.</p>
         </div>
      </div>

      <div className="flex gap-4 mb-6">
         <div className="relative flex-1 max-w-md">
            <Search className="absolute left-4 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-400" />
            <Input 
               className="pl-11 h-12 bg-white border-slate-200 rounded-2xl shadow-sm focus:ring-blue-600/10 transition-all font-bold" 
               placeholder="Search by username or email..." 
               value={searchTerm}
               onChange={e => setSearchTerm(e.target.value)}
            />
         </div>
      </div>

      <Card className="bg-white border-slate-100 shadow-sm rounded-[2.5rem] overflow-hidden">
        <Table>
          <TableHeader className="bg-slate-50/50">
            <TableRow className="hover:bg-transparent border-slate-100">
              <TableHead className="font-black text-slate-400 uppercase tracking-widest text-[10px] py-4 pl-8">User Identity</TableHead>
              <TableHead className="font-black text-slate-400 uppercase tracking-widest text-[10px] py-4">Roles</TableHead>
              <TableHead className="font-black text-slate-400 uppercase tracking-widest text-[10px] py-4">Status</TableHead>
              <TableHead className="font-black text-slate-400 uppercase tracking-widest text-[10px] py-4 text-right pr-8">Governance</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {filteredUsers?.map((user) => (
              <TableRow key={user.id} className="border-slate-50 hover:bg-slate-50/30 transition-colors">
                <TableCell className="py-5 pl-8">
                  <div className="flex items-center gap-4">
                     <div className="w-10 h-10 bg-slate-100 rounded-xl flex items-center justify-center text-slate-400">
                        <UserIcon className="w-5 h-5" />
                     </div>
                     <div>
                        <p className="font-black text-slate-900 leading-none mb-1">{user.username}</p>
                        <p className="text-xs font-medium text-slate-400 flex items-center gap-1">
                           <Mail className="w-3 h-3" /> {user.email}
                        </p>
                     </div>
                  </div>
                </TableCell>
                <TableCell>
                  <div className="flex gap-1">
                    {user.roles?.map((role, idx) => {
                      const roleName = typeof role === 'string' ? role : role.name;
                      return (
                      <Badge key={idx} variant="outline" className="bg-white border-slate-200 text-slate-600 font-bold text-[10px] uppercase tracking-tighter">
                        {roleName?.replace('ROLE_', '')}
                      </Badge>
                      );
                    })}
                  </div>
                </TableCell>
                <TableCell>
                  {user.isBlocked ? (
                    <div className="flex items-center gap-2 text-rose-600">
                       <XCircle className="w-4 h-4" />
                       <span className="text-xs font-black uppercase tracking-widest">Restricted</span>
                    </div>
                  ) : (
                    <div className="flex items-center gap-2 text-emerald-600">
                       <CheckCircle2 className="w-4 h-4" />
                       <span className="text-xs font-black uppercase tracking-widest">Active</span>
                    </div>
                  )}
                </TableCell>
                <TableCell className="text-right pr-8">
                  <Button 
                    size="sm"
                    variant={user.isBlocked ? "default" : "destructive"}
                    className={user.isBlocked ? "bg-emerald-600 hover:bg-emerald-700 text-white rounded-xl font-black text-xs px-4" : "bg-rose-50 text-rose-600 hover:bg-rose-100 border-none rounded-xl font-black text-xs px-4"}
                    onClick={() => blockMutation.mutate({ username: user.username, block: !user.isBlocked })}
                    disabled={blockMutation.isLoading}
                  >
                    {user.isBlocked ? (
                       <><ShieldCheck className="mr-2 w-3.5 h-3.5" /> Restore Access</>
                    ) : (
                       <><ShieldAlert className="mr-2 w-3.5 h-3.5" /> Block Account</>
                    )}
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

export default withAuth(ManageUsers);
