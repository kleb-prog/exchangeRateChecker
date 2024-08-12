import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './dashboard.css';
import UserExchangePairs from './UserExchangePairs';

const Notification = ({ message, type }) => (
  <div className={`notification ${type}`}>
    {message}
  </div>
);

function Dashboard({ handleLogout }) {
  const [users, setUsers] = useState([]);
  const [message, setMessage] = useState('');
  const [activeTab, setActiveTab] = useState('users');
  const [notification, setNotification] = useState(null);
  const [selectedUser, setSelectedUser] = useState(null);

  useEffect(() => {
    fetchUsers();
  }, []);

  const fetchUsers = async () => {
    try {
      const response = await axios.get('/api/chats', {
        headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
      });
      setUsers(response.data);
    } catch (error) {
      setNotification({ message: 'Failed to fetch users. Please try again later.', type: 'error' });
      setTimeout(() => setNotification(null), 5000);
    }
  };

  const handleSendMessage = async () => {
    try {
      await axios.post('/api/send-message', { message }, {
        headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
      });
      setNotification({ message: 'Message sent successfully!', type: 'success' });
      setMessage('');
    } catch (error) {
      setNotification({ message: 'Failed to send message. Please try again.', type: 'error' });
    }
    setTimeout(() => setNotification(null), 5000);
  };

  const fetchUserExchangePairs = async (userId) => {
    try {
      const response = await axios.get(`/api/user-exchange-pairs/${userId}`, {
        headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
      });
      setSelectedUser({ ...users.find(user => user.chatId === userId), exchangePairs: response.data });
    } catch (error) {
      setNotification({ message: 'Failed to fetch user exchange pairs. Please try again.', type: 'error' });
      setTimeout(() => setNotification(null), 5000);
    }
  };

  const renderUserList = () => (
    <div className="user-list">
      <h2>Chat User Data</h2>
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
            <tr key={user.chatId} onClick={() => fetchUserExchangePairs(user.chatId)}>
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
        <button onClick={() => handleLogout()} className="logout-button">Log Out</button>
      </header>

      {notification && <Notification message={notification.message} type={notification.type} />}

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
          {activeTab === 'users' && (
            selectedUser ? (
              <UserExchangePairs user={selectedUser} onBack={() => setSelectedUser(null)} />
            ) : (
              renderUserList()
            )
          )}
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