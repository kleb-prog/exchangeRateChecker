import React, { useState, useEffect } from 'react';
import axios from 'axios';

function Dashboard() {
  const [users, setUsers] = useState([]);
  const [message, setMessage] = useState('');
  const [activeTab, setActiveTab] = useState('users');

  useEffect(() => {
    fetchUsers();
  }, []);

  const fetchUsers = async () => {
    try {
      const response = await axios.get('/api/users', {
        headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
      });
      setUsers(response.data);
    } catch (error) {
      console.error('Failed to fetch users:', error);
    }
  };

  const handleSendMessage = async () => {
    try {
      await axios.post('/api/send-message', { message }, {
        headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
      });
      alert('Message sent successfully!');
      setMessage('');
    } catch (error) {
      console.error('Failed to send message:', error);
    }
  };

  return (
    <div className="dashboard">
      <h1>Exchange Rate Checker Admin Dashboard</h1>
      
      <div className="tabs">
        <button 
          onClick={() => setActiveTab('users')}
          className={activeTab === 'users' ? 'active' : ''}
        >
          Users
        </button>
        <button 
          onClick={() => setActiveTab('message')}
          className={activeTab === 'message' ? 'active' : ''}
        >
          Send Message
        </button>
      </div>

      {activeTab === 'users' && (
        <div className="user-table">
          <h2>User Data</h2>
          <table>
            <thead>
              <tr>
                <th>ID</th>
                <th>Username</th>
                <th>Email</th>
              </tr>
            </thead>
            <tbody>
              {users.map(user => (
                <tr key={user.id}>
                  <td>{user.id}</td>
                  <td>{user.username}</td>
                  <td>{user.email}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {activeTab === 'message' && (
        <div className="message-sender">
          <h2>Send Message to All Users</h2>
          <textarea
            value={message}
            onChange={(e) => setMessage(e.target.value)}
            placeholder="Enter your message here"
          />
          <button onClick={handleSendMessage}>Send to All Users</button>
        </div>
      )}
    </div>
  );
}

export default Dashboard;
