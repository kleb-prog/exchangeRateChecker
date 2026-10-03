import React from 'react';

function UserExchangePairs({ user, onBack }) {
  return (
    <div className="user-exchange-pairs">
      <h2>Exchange Pairs for {user.firstName} {user.lastName}</h2>
      <button onClick={onBack} className="back-button">Back to User List</button>
      <table>
        <thead>
          <tr>
            <th>Base Currency</th>
            <th>Target Currency</th>
            <th>Threshold</th>
            <th>Created At</th>
          </tr>
        </thead>
        <tbody>
          {user.exchangePairs.map((pair, index) => (
            <tr key={index}>
              <td>{pair.baseCurrency}</td>
              <td>{pair.targetCurrency}</td>
              <td>{pair.threshold}</td>
              <td>{pair.createdAt}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default UserExchangePairs;