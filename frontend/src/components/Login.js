import React, { useState } from 'react';
import axios from 'axios';
import './login.css';

function Login({ handleLogin }) {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const response = await axios.post('/api/login', { username, password });
      localStorage.setItem('token', response.data.token);
      handleLogin();
    } catch (error) {
      console.error('Login failed:', error);
    }
  };

  return (
    <div className="container">
      <header>
        <h1>Exchange Rate Checker Admin</h1>
      </header>

      <div className="login-container">
        <form className="login-form" onSubmit={handleSubmit}>
          <h2>Login</h2>
          <div className="form-group">
            <label htmlFor="username">Username</label>
            <input
              type="text"
              id="username"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              required
              autoComplete="username"
            />
          </div>
          <div className="form-group">
            <label htmlFor="password">Password</label>
            <input
              type="password"
              id="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
              autoComplete="current-password"
            />
          </div>
          <button type="submit" className="login-button">Login</button>
        </form>
      </div>

      <footer>
        <p>&copy; 2024 Exchange Rate Checker Admin. All rights reserved.</p>
      </footer>
    </div>
  );
}

export default Login;
