import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom'
import Navbar from './components/Navbar'
import Home from './pages/Home'
import SearchResults from './pages/SearchResults'
import Login from './pages/Login'
import Signup from './pages/Signup'
import AdminDashboard from './pages/AdminDashboard'
import ManageAirlines from './pages/ManageAirlines'
import FlightInventory from './pages/FlightInventory'

function RootRedirect() {
  const userStr = localStorage.getItem('user');
  const user = userStr ? JSON.parse(userStr) : null;
  const isAdmin = user?.roles?.includes('ROLE_ADMIN');
  
  if (isAdmin) {
    return <Navigate to="/admin/dashboard" replace />;
  }
  return <Home />;
}

function App() {
  return (
    <Router>
      <div className="min-h-screen bg-background">
        <Navbar />
        <main>
          <Routes>
            <Route path="/" element={<RootRedirect />} />
            <Route path="/search" element={<SearchResults />} />
            <Route path="/login" element={<Login />} />
            <Route path="/signup" element={<Signup />} />
        
            <Route path="/admin/dashboard" element={<AdminDashboard />} />
            <Route path="/admin/airlines" element={<ManageAirlines />} />
            <Route path="/admin/inventory" element={<FlightInventory />} />
            
            <Route path="*" element={<Navigate to="/" replace />} />
          </Routes>
        </main>
      </div>
    </Router>
  )
}

export default App
