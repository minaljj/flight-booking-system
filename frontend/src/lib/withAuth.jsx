import { useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';

export const withAuth = (Component) => {
  return function AuthenticatedComponent(props) {
    const navigate = useNavigate();
    const location = useLocation();

    useEffect(() => {
      const token = localStorage.getItem('token');
      if (!token) {
        // Capture the current URL to redirect back after login
        const redirectPath = encodeURIComponent(location.pathname + location.search);
        navigate(`/login?redirect=${redirectPath}`);
      }
    }, [navigate, location]);

    const token = localStorage.getItem('token');
    if (!token) return null; // Prevent flicker during redirect

    return <Component {...props} />;
  };
};
