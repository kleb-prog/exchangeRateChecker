import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './dashboard.css';
axios.defaults.baseURL = 'http://localhost:8080';

function Dashboard() {
  const [users, setUsers] = useState([]);
  const [message, setMessage] = useState('');
  const [activeTab, setActiveTab] = useState('users');

  useEffect(() => {
    fetchUsers();
  }, []);

  const fetchUsers = async () => {
    try {
      const response = await axios.get('/api/chats', {
      // TODO remove withCredentials
        withCredentials: false,
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

  const renderUserTab = () => (
  <div className="user-table">
        <h2>User Data</h2>
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>First name</th>
              <th>Last name</th>
              <th>Creation time</th>
            </tr>
          </thead>
          <tbody>
            {users.map(user => (
              <tr key={user.chatId}>
                <td>{user.chatId}</td>
                <td>{user.firstName}</td>
                <td>{user.lastName}</td>
                <td>{user.createdAt}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    );

    const renderMessageTab = () => (
      <div className="message-sender">
        <h2>Send Message to All Users</h2>
        <textarea
          value={message}
          onChange={(e) => setMessage(e.target.value)}
          placeholder="Enter your message here"
        />
        <button onClick={handleSendMessage} className="send-button">Send to All Users</button>
      </div>
    );

    return (
      <div className="container">
        <header>
          <h1>Exchange Rate Checker Admin Dashboard</h1>
        </header>

        <div className="dashboard">
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

          <div className="tab-content">
            {activeTab === 'users' && renderUserTab()}
            {activeTab === 'message' && renderMessageTab()}
          </div>
        </div>

        <footer>
          <p>&copy; 2024 Exchange Rate Checker Admin. All rights reserved.</p>
        </footer>
      </div>
    );
  }

  export default Dashboard;