import React, { useState, useEffect } from 'react';
import { Routes, Route, Navigate, useNavigate } from 'react-router-dom';
import axios from 'axios';
import Login from './components/Login';
import Dashboard from './components/Dashboard';

function App() {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [isLoading, setIsLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
      checkAuthStatus();
  }, []);

  const handleLogin = async (username, password) => {
     setIsAuthenticated(true);
     navigate("/dashboard");
  };

  const handleLogout = () => {
      localStorage.removeItem('token');
      setIsAuthenticated(false);
      navigate("/", { replace: true });
  };

  const checkAuthStatus = async () => {
    setIsLoading(true);
    const token = localStorage.getItem('token');
    if (token) {
      try {
        await axios.get('/api/validate-token', {
            headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
        });
        setIsAuthenticated(true);
      } catch (error) {
        console.error('Token validation failed: ', error);
        localStorage.removeItem('token');
        setIsAuthenticated(false);
      } finally {
        setIsLoading(false);
      }
    } else {
        setIsLoading(false);
    }
  };

  if (isLoading) {
    return <div> Loading...</div>;
  }

 return (
    <Routes>
        <Route path="/login" element={<Login handleLogin={handleLogin} />} />
        <Route
            path="/dashboard"
            element={
                <RequireAuth isAuthenticated={isAuthenticated} >
                    <Dashboard handleLogout={handleLogout} />
                </RequireAuth>
            }
        />
        <Route path="/" element={<Navigate to="/login" replace />} />
    </Routes>
 );
}

function RequireAuth({ isAuthenticated, children }) {
  return isAuthenticated ? children : <Navigate to="/login" />;
}

export default App;
