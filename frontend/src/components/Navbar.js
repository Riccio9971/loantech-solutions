import React from 'react';
import { Link, useLocation } from 'react-router-dom';
import './Navbar.css';

const Navbar = ({ user, onLogout }) => {
  const location = useLocation();

  if (!user) {
    return (
      <nav className="navbar">
        <div className="navbar-container">
          <Link to="/" className="navbar-brand">
            LoanTech Solutions
          </Link>
          <div className="navbar-menu">
            <Link
              to="/login"
              className={`navbar-item ${location.pathname === '/login' ? 'active' : ''}`}
            >
              Accedi
            </Link>
            <Link
              to="/register"
              className={`navbar-item ${location.pathname === '/register' ? 'active' : ''}`}
            >
              Registrati
            </Link>
          </div>
        </div>
      </nav>
    );
  }

  return (
    <nav className="navbar">
      <div className="navbar-container">
        <Link to="/dashboard" className="navbar-brand">
          LoanTech Solutions
        </Link>
        <div className="navbar-menu">
          <Link
            to="/dashboard"
            className={`navbar-item ${location.pathname === '/dashboard' ? 'active' : ''}`}
          >
            Dashboard
          </Link>
          <Link
            to="/simulator"
            className={`navbar-item ${location.pathname === '/simulator' ? 'active' : ''}`}
          >
            Simulatore
          </Link>
          <Link
            to="/apply"
            className={`navbar-item ${location.pathname === '/apply' ? 'active' : ''}`}
          >
            Richiedi Prestito
          </Link>
          <Link
            to="/loans"
            className={`navbar-item ${location.pathname === '/loans' ? 'active' : ''}`}
          >
            I Miei Prestiti
          </Link>
          <div className="navbar-user">
            <span>Ciao, {user.firstName}</span>
            <button onClick={onLogout} className="logout-btn">
              Esci
            </button>
          </div>
        </div>
      </div>
    </nav>
  );
};

export default Navbar;
