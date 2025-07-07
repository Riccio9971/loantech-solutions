import React, { useState } from 'react';
import api from '../services/api';
import './LoanSimulator.css';

const LoanSimulator = () => {
  const [formData, setFormData] = useState({
    loanType: 'PERSONAL_LOAN',
    amount: '',
    durationMonths: ''
  });
  const [simulation, setSimulation] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const loanTypes = [
    { value: 'PERSONAL_LOAN', label: 'Prestito Personale', rate: '8.5%' },
    { value: 'MORTGAGE', label: 'Mutuo Casa', rate: '3.2%' },
    { value: 'CAR_LOAN', label: 'Prestito Auto', rate: '6.0%' },
    { value: 'BUSINESS_LOAN', label: 'Prestito Business', rate: '7.5%' }
  ];

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      const simulationData = {
        ...formData,
        amount: parseFloat(formData.amount),
        durationMonths: parseInt(formData.durationMonths)
      };

      const response = await api.post('/loan-applications/simulate', simulationData);
      if (response.data.success) {
        setSimulation(response.data.data);
      } else {
        setError(response.data.message);
      }
    } catch (err) {
      setError(err.response?.data?.message || 'Errore nella simulazione');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="loan-simulator">
      <div className="simulator-header">
        <h1>Simulatore Prestito</h1>
        <p>Calcola la rata mensile e le condizioni del tuo prestito</p>
      </div>

      <div className="simulator-content">
        <form onSubmit={handleSubmit} className="simulator-form">
          <div className="form-group">
            <label htmlFor="loanType">Tipo di Prestito</label>
            <select
              id="loanType"
              name="loanType"
              value={formData.loanType}
              onChange={handleChange}
              required
            >
              {loanTypes.map(type => (
                <option key={type.value} value={type.value}>
                  {type.label} (Tasso: {type.rate})
                </option>
              ))}
            </select>
          </div>

          <div className="form-row">
            <div className="form-group">
              <label htmlFor="amount">Importo (€)</label>
              <input
                type="number"
                id="amount"
                name="amount"
                value={formData.amount}
                onChange={handleChange}
                min="1000"
                max="100000"
                step="100"
                required
              />
            </div>

            <div className="form-group">
              <label htmlFor="durationMonths">Durata (mesi)</label>
              <input
                type="number"
                id="durationMonths"
                name="durationMonths"
                value={formData.durationMonths}
                onChange={handleChange}
                min="6"
                max="120"
                required
              />
            </div>
          </div>

          {error && <div className="error-message">{error}</div>}

          <button
            type="submit"
            className="simulate-button"
            disabled={loading}
          >
            {loading ? 'Calcolando...' : 'Simula Prestito'}
          </button>
        </form>

        {simulation && (
          <div className="simulation-results">
            <h2>Risultati Simulazione</h2>
            <div className="results-grid">
              <div className="result-card">
                <h3>Importo Richiesto</h3>
                <p>€{simulation.requestedAmount.toLocaleString()}</p>
              </div>

              <div className="result-card">
                <h3>Tasso di Interesse</h3>
                <p>{simulation.interestRate}% annuo</p>
              </div>

              <div className="result-card highlight">
                <h3>Rata Mensile</h3>
                <p>€{simulation.monthlyPayment.toLocaleString()}</p>
              </div>

              <div className="result-card">
                <h3>Durata</h3>
                <p>{simulation.durationMonths} mesi</p>
              </div>

              <div className="result-card">
                <h3>Totale da Restituire</h3>
                <p>€{simulation.totalAmount.toLocaleString()}</p>
              </div>

              <div className="result-card">
                <h3>Interessi Totali</h3>
                <p>€{simulation.totalInterest.toLocaleString()}</p>
              </div>
            </div>

            <div className="simulation-actions">
              <button
                className="apply-button"
                onClick={() => window.location.href = '/apply'}
              >
                Richiedi questo Prestito
              </button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default LoanSimulator;