import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import api from '../services/api';
import './MyLoans.css';

const MyLoans = ({ user }) => {
  const [loans, setLoans] = useState([]);
  const [applications, setApplications] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [activeTab, setActiveTab] = useState('loans');

  useEffect(() => {
    fetchData();
  }, [user.userId]);

  const fetchData = async () => {
    try {
      setLoading(true);

      // Fetch prestiti attivi
      const loansResponse = await api.get(`/loans/user/${user.userId}`);
      if (loansResponse.data.success) {
        setLoans(loansResponse.data.data);
      }

      // Fetch richieste prestito
      const appsResponse = await api.get(`/loan-applications/user/${user.userId}`);
      if (appsResponse.data.success) {
        setApplications(appsResponse.data.data);
      }
    } catch (err) {
      setError('Errore nel caricamento dei dati');
    } finally {
      setLoading(false);
    }
  };

  const getStatusColor = (status) => {
    switch (status) {
      case 'ACTIVE': return '#28a745';
      case 'APPROVED': return '#28a745';
      case 'PENDING': return '#ffc107';
      case 'UNDER_REVIEW': return '#17a2b8';
      case 'REJECTED': return '#dc3545';
      case 'PAID_OFF': return '#6c757d';
      default: return '#6c757d';
    }
  };

  const formatCurrency = (amount) => {
    return new Intl.NumberFormat('it-IT', {
      style: 'currency',
      currency: 'EUR'
    }).format(amount);
  };

  if (loading) {
    return <div className="loans-loading">Caricamento prestiti...</div>;
  }

  return (
    <div className="my-loans">
      <div className="loans-header">
        <h1>I Miei Prestiti</h1>
        <p>Gestisci i tuoi prestiti attivi e le richieste in corso</p>
      </div>

      <div className="loans-tabs">
        <button
          className={`tab-button ${activeTab === 'loans' ? 'active' : ''}`}
          onClick={() => setActiveTab('loans')}
        >
          Prestiti Attivi ({loans.length})
        </button>
        <button
          className={`tab-button ${activeTab === 'applications' ? 'active' : ''}`}
          onClick={() => setActiveTab('applications')}
        >
          Richieste ({applications.length})
        </button>
      </div>

      {error && <div className="error-message">{error}</div>}

      {activeTab === 'loans' && (
        <div className="loans-section">
          {loans.length === 0 ? (
            <div className="empty-state">
              <div className="empty-icon">📋</div>
              <h3>Nessun prestito attivo</h3>
              <p>Non hai ancora prestiti attivi. Richiedi il tuo primo prestito!</p>
              <Link to="/apply" className="cta-button">
                Richiedi Prestito
              </Link>
            </div>
          ) : (
            <div className="loans-grid">
              {loans.map(loan => (
                <div key={loan.loanId} className="loan-card">
                  <div className="loan-header">
                    <h3>Prestito #{loan.loanId}</h3>
                    <span
                      className="status-badge"
                      style={{ backgroundColor: getStatusColor(loan.status) }}
                    >
                      {loan.status}
                    </span>
                  </div>

                  <div className="loan-details">
                    <div className="detail-item">
                      <span className="label">Importo Originale:</span>
                      <span className="value">{formatCurrency(loan.principalAmount)}</span>
                    </div>
                    <div className="detail-item">
                      <span className="label">Saldo Residuo:</span>
                      <span className="value highlight">{formatCurrency(loan.remainingBalance)}</span>
                    </div>
                    <div className="detail-item">
                      <span className="label">Rata Mensile:</span>
                      <span className="value">{formatCurrency(loan.monthlyPayment)}</span>
                    </div>
                    <div className="detail-item">
                      <span className="label">Tasso:</span>
                      <span className="value">{loan.interestRate}%</span>
                    </div>
                  </div>

                  <div className="loan-actions">
                    <Link
                      to={`/loans/${loan.loanId}`}
                      className="action-button primary"
                    >
                      Dettagli
                    </Link>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      )}

      {activeTab === 'applications' && (
        <div className="applications-section">
          {applications.length === 0 ? (
            <div className="empty-state">
              <div className="empty-icon">📝</div>
              <h3>Nessuna richiesta</h3>
              <p>Non hai ancora inviato richieste di prestito.</p>
              <Link to="/apply" className="cta-button">
                Nuova Richiesta
              </Link>
            </div>
          ) : (
            <div className="applications-list">
              {applications.map(app => (
                <div key={app.applicationId} className="application-card">
                  <div className="app-header">
                    <div>
                      <h3>{app.loanType.replace('_', ' ')}</h3>
                      <p className="app-date">
                        Richiesta il {new Date(app.submittedAt).toLocaleDateString('it-IT')}
                      </p>
                    </div>
                    <span
                      className="status-badge"
                      style={{ backgroundColor: getStatusColor(app.status) }}
                    >
                      {app.status}
                    </span>
                  </div>

                  <div className="app-details">
                    <div className="detail-row">
                      <span>Importo Richiesto:</span>
                      <span>{formatCurrency(app.requestedAmount)}</span>
                    </div>
                    <div className="detail-row">
                      <span>Durata:</span>
                      <span>{app.durationMonths} mesi</span>
                    </div>
                    <div className="detail-row">
                      <span>Reddito Dichiarato:</span>
                      <span>{formatCurrency(app.monthlyIncome)}/mese</span>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      )}
    </div>
  );
};

export default MyLoans;
