import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import api from '../services/api';
import './Dashboard.css';

const Dashboard = ({ user }) => {
  const [dashboardData, setDashboardData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    fetchDashboardData();
  }, [user.userId]);

  const fetchDashboardData = async () => {
    try {
      const response = await api.get(`/users/${user.userId}/dashboard`);
      if (response.data.success) {
        setDashboardData(response.data.data);
      } else {
        setError(response.data.message);
      }
    } catch (err) {
      setError('Errore nel caricamento dei dati');
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return <div className="dashboard-loading">Caricamento dashboard...</div>;
  }

  if (error) {
    return <div className="dashboard-error">Errore: {error}</div>;
  }

  const { pendingApplications, activeLoans, totalDebt, creditScore } = dashboardData;

  return (
    <div className="dashboard">
      <div className="dashboard-header">
        <h1>Benvenuto, {user.firstName} {user.lastName}</h1>
        <p>Gestisci i tuoi prestiti e richieste in un unico posto</p>
      </div>

      <div className="dashboard-stats">
        <div className="stat-card">
          <div className="stat-icon">📋</div>
          <div className="stat-content">
            <h3>{pendingApplications}</h3>
            <p>Richieste in attesa</p>
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-icon">💰</div>
          <div className="stat-content">
            <h3>{activeLoans}</h3>
            <p>Prestiti attivi</p>
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-icon">📊</div>
          <div className="stat-content">
            <h3>€{totalDebt.toLocaleString()}</h3>
            <p>Debito totale</p>
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-icon">🎯</div>
          <div className="stat-content">
            <h3>{creditScore?.score || 'N/A'}</h3>
            <p>Credit Score ({creditScore?.riskCategory || 'N/A'})</p>
          </div>
        </div>
      </div>

      <div className="dashboard-actions">
        <h2>Azioni rapide</h2>
        <div className="action-cards">
          <Link to="/simulator" className="action-card">
            <div className="action-icon">🧮</div>
            <h3>Simula Prestito</h3>
            <p>Calcola rate e condizioni per il tuo prestito</p>
          </Link>

          <Link to="/apply" className="action-card">
            <div className="action-icon">📝</div>
            <h3>Richiedi Prestito</h3>
            <p>Invia una nuova richiesta di finanziamento</p>
          </Link>

          <Link to="/loans" className="action-card">
            <div className="action-icon">📚</div>
            <h3>I Miei Prestiti</h3>
            <p>Visualizza e gestisci i tuoi prestiti attivi</p>
          </Link>
        </div>
      </div>

      {creditScore && (
        <div className="credit-score-section">
          <h2>Il tuo Credit Score</h2>
          <div className="credit-score-card">
            <div className="score-circle">
              <span className="score-number">{creditScore.score}</span>
              <span className="score-max">/850</span>
            </div>
            <div className="score-info">
              <h3>Categoria: {creditScore.riskCategory}</h3>
              <div className="score-bar">
                <div
                  className="score-fill"
                  style={{ width: `${(creditScore.score / 850) * 100}%` }}
                ></div>
              </div>
              <p>Un buon credit score ti aiuta ad ottenere condizioni migliori sui prestiti.</p>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default Dashboard;