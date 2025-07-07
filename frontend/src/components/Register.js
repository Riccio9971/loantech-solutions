import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import api from '../services/api';
import './Auth.css';

const Register = ({ onRegister }) => {
  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    email: '',
    password: '',
    confirmPassword: '',
    fiscalCode: '',
    phoneNumber: ''
  });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
  };

  const validateForm = () => {
    if (formData.password !== formData.confirmPassword) {
      setError('Le password non coincidono');
      return false;
    }
    if (formData.password.length < 6) {
      setError('La password deve essere di almeno 6 caratteri');
      return false;
    }
    return true;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!validateForm()) return;

    setLoading(true);
    setError('');

    try {
      const { confirmPassword, ...registerData } = formData;
      const response = await api.post('/auth/register', registerData);

      if (response.data.success) {
        onRegister(response.data.data);
      } else {
        setError(response.data.message);
      }
    } catch (err) {
      setError(err.response?.data?.message || 'Errore durante la registrazione');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-container">
      <div className="auth-card">
        <h2>Crea il tuo account</h2>
        {error && <div className="error-message">{error}</div>}

        <form onSubmit={handleSubmit} className="auth-form">
          <div className="form-row">
            <div className="form-group">
              <label htmlFor="firstName">Nome</label>
              <input
                type="text"
                id="firstName"
                name="firstName"
                value={formData.firstName}
                onChange={handleChange}
                required
              />
            </div>
            <div className="form-group">
              <label htmlFor="lastName">Cognome</label>
              <input
                type="text"
                id="lastName"
                name="lastName"
                value={formData.lastName}
                onChange={handleChange}
                required
              />
            </div>
          </div>

          <div className="form-group">
            <label htmlFor="email">Email</label>
            <input
              type="email"
              id="email"
              name="email"
              value={formData.email}
              onChange={handleChange}
              required
            />
          </div>

          <div className="form-group">
            <label htmlFor="fiscalCode">Codice Fiscale</label>
            <input
              type="text"
              id="fiscalCode"
              name="fiscalCode"
              value={formData.fiscalCode}
              onChange={handleChange}
              placeholder="RSSMRA80A01H501U"
              required
            />
          </div>

          <div className="form-group">
            <label htmlFor="phoneNumber">Telefono</label>
            <input
              type="tel"
              id="phoneNumber"
              name="phoneNumber"
              value={formData.phoneNumber}
              onChange={handleChange}
              placeholder="+39 123 456 7890"
            />
          </div>

          <div className="form-row">
            <div className="form-group">
              <label htmlFor="password">Password</label>
              <input
                type="password"
                id="password"
                name="password"
                value={formData.password}
                onChange={handleChange}
                required
              />
            </div>
            <div className="form-group">
              <label htmlFor="confirmPassword">Conferma Password</label>
              <input
                type="password"
                id="confirmPassword"
                name="confirmPassword"
                value={formData.confirmPassword}
                onChange={handleChange}
                required
              />
            </div>
          </div>

          <button
            type="submit"
            className="auth-button"
            disabled={loading}
          >
            {loading ? 'Registrazione in corso...' : 'Registrati'}
          </button>
        </form>

        <p className="auth-link">
          Hai già un account? <Link to="/login">Accedi qui</Link>
        </p>
      </div>
    </div>
  );
};

export default Register;