import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import api from '../services/api';
import './AdminDashboard.css';

const AdminDashboard = () => {
  const [dashboardData, setDashboardData] = useState(null);
  const [applications, setApplications] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [activeTab, setActiveTab] = useState('pending');

  useEffect(() => {
    fetchDashboardData();
    fetchApplications();
  }, []);

  const fetchDashboardData = async () => {
    try {
      const response = await api.get('/admin/dashboard');
      if (response.data.success) {
        setDashboardData(response.data.data);
      }
    } catch (err) {
      setError('Errore nel caricamento della dashboard');
    }
  };

  const fetchApplications = async (status = 'PENDING') => {
    try {
      const response = await api.get(`/admin/applications?status=${status}&size=20`);
      if (response.data.success) {
        setApplications(response.data.data.applications);
      }
    } catch (err) {
      setError('Errore nel caricamento delle richieste');
    } finally {
      setLoading(false);
    }
  };

  const handleStatusChange = async (applicationId, newStatus) => {
    try {
      let endpoint;
      switch (newStatus) {
        case 'APPROVED':
          endpoint = `/admin/applications/${applicationId}/approve`;
          break;
        case 'REJECTED':
          endpoint = `/admin/applications/${applicationId}/reject`;
          break;
        case 'UNDER_REVIEW':
          endpoint = `/admin/applications/${applicationId}/review`;
          break;
        default:
          return;
      }

      const response = await api.put(endpoint);
      if (response.data.success) {
        alert(`Richiesta ${newStatus.toLowerCase()} con successo`);
        fetchApplications(activeTab);
        fetchDashboardData();
      }
    } catch (err) {
      alert(err.response?.data?.message || 'Errore nell\'aggiornamento');
    }
  };

  const formatCurrency = (amount) => {
    return new Intl.NumberFormat('it-IT', {
      style: 'currency',
      currency: 'EUR'
    }).format(amount);
  };

  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleDateString('it-IT');
  };

  const getStatusColor = (status) => {
    switch (status) {
      case 'PENDING': return '#ffc107';
      case 'UNDER_REVIEW': return '#17a2b8';
      case 'APPROVED': return '#28a745';
      case 'REJECTED': return '#dc3545';
      default: return '#6c757d';
    }
  };

  const getLoanTypeLabel = (type) => {
    const types = {
      'PERSONAL_LOAN': 'Prestito Personale',
      'MORTGAGE': 'Mutuo Casa',
      'CAR_LOAN': 'Prestito Auto',
      'BUSINESS_LOAN': 'Prestito Business'
    };
    return types[type] || type;
  };

  if (loading && !dashboardData) {
    return <div className="admin-loading">Caricamento dashboard admin...</div>;
  }

  return (
    <div className="admin-dashboard">
      <div className="admin-header">
        <h1>Dashboard Amministrativa</h1>
        <p>Gestione richieste prestiti e approvazioni</p>
      </div>

      {error && <div className="error-message">{error}</div>}

      {/* Statistiche */}
      {dashboardData && (
        <div className="admin-stats">
          <div className="stat-card pending">
            <div className="stat-number">{dashboardData.pendingApplications}</div>
            <div className="stat-label">In Attesa</div>
          </div>
          <div className="stat-card review">
            <div className="stat-number">{dashboardData.underReviewApplications}</div>
            <div className="stat-label">In Revisione</div>
          </div>
          <div className="stat-card approved">
            <div className="stat-number">{dashboardData.approvedApplications}</div>
            <div className="stat-label">Approvate</div>
          </div>
          <div className="stat-card rejected">
            <div className="stat-number">{dashboardData.rejectedApplications}</div>
            <div className="stat-label">Respinte</div>
          </div>
          <div className="stat-card total">
            <div className="stat-number">{dashboardData.totalApplications}</div>
            <div className="stat-label">Totale</div>
          </div>
        </div>
      )}

      {/* Tabs per filtri */}
      <div className="admin-tabs">
        <button
          className={`tab-button ${activeTab === 'pending' ? 'active' : ''}`}
          onClick={() => {
            setActiveTab('pending');
            fetchApplications('PENDING');
          }}
        >
          In Attesa ({dashboardData?.pendingApplications || 0})
        </button>
        <button
          className={`tab-button ${activeTab === 'review' ? 'active' : ''}`}
          onClick={() => {
            setActiveTab('review');
            fetchApplications('UNDER_REVIEW');
          }}
        >
          In Revisione ({dashboardData?.underReviewApplications || 0})
        </button>
        <button
          className={`tab-button ${activeTab === 'approved' ? 'active' : ''}`}
          onClick={() => {
            setActiveTab('approved');
            fetchApplications('APPROVED');
          }}
        >
          Approvate ({dashboardData?.approvedApplications || 0})
        </button>
        <button
          className={`tab-button ${activeTab === 'rejected' ? 'active' : ''}`}
          onClick={() => {
            setActiveTab('rejected');
            fetchApplications('REJECTED');
          }}
        >
          Respinte ({dashboardData?.rejectedApplications || 0})
        </button>
      </div>

      {/* Lista richieste */}
      <div className="applications-list">
        {applications.length === 0 ? (
          <div className="empty-state">
            <h3>Nessuna richiesta trovata</h3>
            <p>Non ci sono richieste con stato "{activeTab}"</p>
          </div>
        ) : (
          <div className="applications-grid">
            {applications.map(app => (
              <div key={app.applicationId} className="application-card">
                <div className="card-header">
                  <div className="application-info">
                    <h3>#{app.applicationId}</h3>
                    <span className="loan-type">{getLoanTypeLabel(app.loanType)}</span>
                  </div>
                  <span
                    className="status-badge"
                    style={{ backgroundColor: getStatusColor(app.status) }}
                  >
                    {app.status}
                  </span>
                </div>

                <div className="card-content">
                  <div className="user-info">
                    <strong>{app.user?.firstName} {app.user?.lastName}</strong>
                    <span className="email">{app.user?.email}</span>
                  </div>

                  <div className="loan-details">
                    <div className="detail-row">
                      <span>Importo:</span>
                      <strong>{formatCurrency(app.requestedAmount)}</strong>
                    </div>
                    <div className="detail-row">
                      <span>Durata:</span>
                      <strong>{app.durationMonths} mesi</strong>
                    </div>
                    <div className="detail-row">
                      <span>Reddito mensile:</span>
                      <strong>{formatCurrency(app.monthlyIncome)}</strong>
                    </div>
                    <div className="detail-row">
                      <span>Data richiesta:</span>
                      <strong>{formatDate(app.submittedAt)}</strong>
                    </div>
                  </div>
                </div>

                <div className="card-actions">
                  {app.status === 'PENDING' && (
                    <>
                      <button
                        className="action-btn review"
                        onClick={() => handleStatusChange(app.applicationId, 'UNDER_REVIEW')}
                      >
                        Metti in Revisione
                      </button>
                      <button
                        className="action-btn approve"
                        onClick={() => handleStatusChange(app.applicationId, 'APPROVED')}
                      >
                        Approva
                      </button>
                      <button
                        className="action-btn reject"
                        onClick={() => handleStatusChange(app.applicationId, 'REJECTED')}
                      >
                        Respingi
                      </button>
                    </>
                  )}

                  {app.status === 'UNDER_REVIEW' && (
                    <>
                      <button
                        className="action-btn approve"
                        onClick={() => handleStatusChange(app.applicationId, 'APPROVED')}
                      >
                        Approva
                      </button>
                      <button
                        className="action-btn reject"
                        onClick={() => handleStatusChange(app.applicationId, 'REJECTED')}
                      >
                        Respingi
                      </button>
                    </>
                  )}

                  <Link
                    to={`/admin/applications/${app.applicationId}`}
                    className="action-btn details"
                  >
                    Dettagli
                  </Link>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};

export default AdminDashboard;