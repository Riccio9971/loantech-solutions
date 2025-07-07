import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../services/api';
import './LoanApplication.css';

const LoanApplication = ({ user }) => {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    loanType: 'PERSONAL_LOAN',
    requestedAmount: '',
    durationMonths: '',
    monthlyIncome: '',
    employmentStatus: 'EMPLOYED'
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const loanTypes = [
    { value: 'PERSONAL_LOAN', label: 'Prestito Personale' },
    { value: 'MORTGAGE', label: 'Mutuo Casa' },
    { value: 'CAR_LOAN', label: 'Prestito Auto' },
    { value: 'BUSINESS_LOAN', label: 'Prestito Business' }
  ];

  const employmentStatuses = [
    { value: 'EMPLOYED', label: 'Dipendente' },
    { value: 'SELF_EMPLOYED', label: 'Libero Professionista' },
    { value: 'UNEMPLOYED', label: 'Disoccupato' },
    { value: 'RETIRED', label: 'Pensionato' },
    { value: 'STUDENT', label: 'Studente' }
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
      const applicationData = {
        ...formData,
        userId: user.userId,
        requestedAmount: parseFloat(formData.requestedAmount),
        durationMonths: parseInt(formData.durationMonths),
        monthlyIncome: parseFloat(formData.monthlyIncome)
      };

      const response = await api.post('/loan-applications', applicationData);
      if (response.data.success) {
        navigate('/dashboard');
      } else {
        setError(response.data.message);
      }
    } catch (err) {
      setError(err.response?.data?.message || 'Errore nell\'invio della richiesta');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="loan-application">
      <div className="application-header">
        <h1>Richiesta Prestito</h1>
        <p>Compila il modulo per richiedere il tuo prestito</p>
      </div>

      <div className="application-content">
        <form onSubmit={handleSubmit} className="application-form">
          <div className="form-section">
            <h2>Dettagli del Prestito</h2>

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
                    {type.label}
                  </option>
                ))}
              </select>
            </div>

            <div className="form-row">
              <div className="form-group">
                <label htmlFor="requestedAmount">Importo Richiesto (€)</label>
                <input
                  type="number"
                  id="requestedAmount"
                  name="requestedAmount"
                  value={formData.requestedAmount}
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
          </div>

          <div className="form-section">
            <h2>Informazioni Finanziarie</h2>

            <div className="form-row">
              <div className="form-group">
                <label htmlFor="monthlyIncome">Reddito Mensile (€)</label>
                <input
                  type="number"
                  id="monthlyIncome"
                  name="monthlyIncome"
                  value={formData.monthlyIncome}
                  onChange={handleChange}
                  min="500"
                  step="100"
                  required
                />
              </div>

              <div className="form-group">
                <label htmlFor="employmentStatus">Stato Lavorativo</label>
                <select
                  id="employmentStatus"
                  name="employmentStatus"
                  value={formData.employmentStatus}
                  onChange={handleChange}
                  required
                >
                  {employmentStatuses.map(status => (
                    <option key={status.value} value={status.value}>
                      {status.label}
                    </option>
                  ))}
                </select>
              </div>
            </div>
          </div>

          {error && <div className="error-message">{error}</div>}

          <div className="form-actions">
            <button
              type="button"
              className="cancel-button"
              onClick={() => navigate('/dashboard')}
            >
              Annulla
            </button>
            <button
              type="submit"
              className="submit-button"
              disabled={loading}
            >
              {loading ? 'Invio in corso...' : 'Invia Richiesta'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default LoanApplication;