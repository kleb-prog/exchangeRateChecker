import React, { useState, useEffect } from 'react';
import axios from 'axios';
import Login from './components/Login';
import Dashboard from './components/Dashboard';

function App() {
  const [isAuthenticated, setIsAuthenticated] = useState(false);

  useEffect(() => {
      checkAuthStatus();
  }, []);

  const checkAuthStatus = async () => {
    const token = localStorage.getItem('token');
    if (token) {
      try {
        const response = await axios.get('/api/validate-token', {
            headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
        });
        setIsAuthenticated(true);
      } catch (error) {
        console.error('Token validation failed: ', error);
        localStorage.removeItem('token');
        setIsAuthenticated(false);
      }
    }
  };

  if (!isAuthenticated) {
    return <Login setIsAuthenticated={setIsAuthenticated} />;
  }

  return <Dashboard setIsAuthenticated={setIsAuthenticated} />;
}

export default App;
