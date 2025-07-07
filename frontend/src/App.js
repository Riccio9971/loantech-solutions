import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import './App.css';

// Components
import Navbar from './components/Navbar';
import Login from './components/Login';
import Register from './components/Register';
import Dashboard from './components/Dashboard';
import LoanApplication from './components/LoanApplication';
import LoanSimulator from './components/LoanSimulator';
import LoanDetails from './components/LoanDetails';
import MyLoans from './components/MyLoans';

function App() {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // Check if user is logged in from localStorage
    const savedUser = localStorage.getItem('user');
    if (savedUser) {
      setUser(JSON.parse(savedUser));
    }
    setLoading(false);
  }, []);

  const handleLogin = (userData) => {
    setUser(userData);
    localStorage.setItem('user', JSON.stringify(userData));
  };

  const handleLogout = () => {
    setUser(null);
    localStorage.removeItem('user');
  };

  if (loading) {
    return (
      <div className="loading-container">
        <div className="loading-spinner"></div>
        <p>Caricamento...</p>
      </div>
    );
  }

  return (
    <Router>
      <div className="App">
        <Navbar user={user} onLogout={handleLogout} />
        <main className="main-content">
          <Routes>
            <Route
              path="/login"
              element={!user ? <Login onLogin={handleLogin} /> : <Navigate to="/dashboard" />}
            />
            <Route
              path="/register"
              element={!user ? <Register onRegister={handleLogin} /> : <Navigate to="/dashboard" />}
            />
            <Route
              path="/dashboard"
              element={user ? <Dashboard user={user} /> : <Navigate to="/login" />}
            />
            <Route
              path="/simulator"
              element={user ? <LoanSimulator /> : <Navigate to="/login" />}
            />
            <Route
              path="/apply"
              element={user ? <LoanApplication user={user} /> : <Navigate to="/login" />}
            />
            <Route
              path="/loans"
              element={user ? <MyLoans user={user} /> : <Navigate to="/login" />}
            />
            <Route
              path="/loans/:loanId"
              element={user ? <LoanDetails user={user} /> : <Navigate to="/login" />}
            />
            <Route
              path="/"
              element={user ? <Navigate to="/dashboard" /> : <Navigate to="/login" />}
            />
          </Routes>
        </main>
      </div>
    </Router>
  );
}

export default App;