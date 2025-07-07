import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import api from '../services/api';
import './LoanDetails.css';

const LoanDetails = ({ user }) => {
  const { loanId } = useParams();
  const navigate = useNavigate();
  const [loan, setLoan] = useState(null);
  const [payments, setPayments] = useState([]);
  const [amortization, setAmortization] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [activeTab, setActiveTab] = useState('overview');
  const [paymentAmount, setPaymentAmount] = useState('');
  const [paymentLoading, setPaymentLoading] = useState(false);

  useEffect(() => {
    fetchLoanData();
  }, [loanId]);

  const fetchLoanData = async () => {
    try {
      setLoading(true);

      // Fetch loan details
      const loanResponse = await api.get(`/loans/${loanId}`);
      if (loanResponse.data.success) {
        setLoan(loanResponse.data.data);
        setPaymentAmount(loanResponse.data.data.monthlyPayment);
      }

      // Fetch payments
      const paymentsResponse = await api.get(`/loans/${loanId}/payments`);
      if (paymentsResponse.data.success) {
        setPayments(paymentsResponse.data.data);
      }

      // Fetch amortization schedule
      const amortResponse = await api.get(`/loans/${loanId}/amortization`);
      if (amortResponse.data.success) {
        setAmortization(amortResponse.data.data);
      }
    } catch (err) {
      setError('Errore nel caricamento dei dati del prestito');
    } finally {
      setLoading(false);
    }
  };

  const handlePayment = async (e) => {
    e.preventDefault();
    setPaymentLoading(true);

    try {
      const response = await api.post(`/loans/${loanId}/payments`, {
        amount: parseFloat(paymentAmount)
      });

      if (response.data.success) {
        alert('Pagamento effettuato con successo!');
        fetchLoanData(); // Ricarica i dati
      }
    } catch (err) {
      alert(err.response?.data?.message || 'Errore nell\'effettuare il pagamento');
    } finally {
      setPaymentLoading(false);
    }
  };

  const formatCurrency = (amount) => {
    return new Intl.NumberFormat('it-IT', {
      style: 'currency',
      currency: 'EUR'
    }).format(amount);
  };

  const calculateProgress = () => {
    if (!loan) return 0;
    const paid = loan.principalAmount - loan.remainingBalance;
    return (paid / loan.principalAmount) * 100;
  };

  if (loading) {
    return <div className="loan-details-loading">Caricamento dettagli prestito...</div>;
  }

  if (error || !loan) {
    return (
      <div className="loan-details-error">
        <h2>Errore</h2>
        <p>{error || 'Prestito non trovato'}</p>
        <button onClick={() => navigate('/loans')} className="back-button">
          Torna ai Prestiti
        </button>
      </div>
    );
  }

  return (
    <div className="loan-details">
      <div className="loan-details-header">
        <button onClick={() => navigate('/loans')} className="back-button">
          ← Torna ai Prestiti
        </button>
        <h1>Prestito #{loan.loanId}</h1>
        <span className={`status-badge ${loan.status.toLowerCase()}`}>
          {loan.status}
        </span>
      </div>

      <div className="loan-overview">
        <div className="overview-cards">
          <div className="overview-card">
            <h3>Saldo Residuo</h3>
            <p className="amount">{formatCurrency(loan.remainingBalance)}</p>
          </div>
          <div className="overview-card">
            <h3>Rata Mensile</h3>
            <p className="amount">{formatCurrency(loan.monthlyPayment)}</p>
          </div>
          <div className="overview-card">
            <h3>Tasso Interesse</h3>
            <p className="amount">{loan.interestRate}%</p>
          </div>
          <div className="overview-card">
            <h3>Scadenza</h3>
            <p className="amount">{new Date(loan.endDate).toLocaleDateString('it-IT')}</p>
          </div>
        </div>

        <div className="progress-section">
          <h3>Progresso Rimborso</h3>
          <div className="progress-bar">
            <div
              className="progress-fill"
              style={{ width: `${calculateProgress()}%` }}
            ></div>
          </div>
          <div className="progress-labels">
            <span>Pagato: {formatCurrency(loan.principalAmount - loan.remainingBalance)}</span>
            <span>Totale: {formatCurrency(loan.principalAmount)}</span>
          </div>
        </div>
      </div>

      <div className="loan-details-tabs">
        <button
          className={`tab-button ${activeTab === 'overview' ? 'active' : ''}`}
          onClick={() => setActiveTab('overview')}
        >
          Panoramica
        </button>
        <button
          className={`tab-button ${activeTab === 'payments' ? 'active' : ''}`}
          onClick={() => setActiveTab('payments')}
        >
          Pagamenti
        </button>
        <button
          className={`tab-button ${activeTab === 'schedule' ? 'active' : ''}`}
          onClick={() => setActiveTab('schedule')}
        >
          Piano Ammortamento
        </button>
      </div>

      {activeTab === 'overview' && (
        <div className="tab-content">
          <div className="payment-form">
            <h3>Effettua Pagamento</h3>
            <form onSubmit={handlePayment}>
              <div className="form-group">
                <label htmlFor="paymentAmount">Importo Pagamento (€)</label>
                <input
                  type="number"
                  id="paymentAmount"
                  value={paymentAmount}
                  onChange={(e) => setPaymentAmount(e.target.value)}
                  min="0.01"
                  max={loan.remainingBalance}
                  step="0.01"
                  required
                />
                <small>Saldo residuo: {formatCurrency(loan.remainingBalance)}</small>
              </div>
              <button
                type="submit"
                className="payment-button"
                disabled={paymentLoading}
              >
                {paymentLoading ? 'Elaborazione...' : 'Paga Ora'}
              </button>
            </form>
          </div>
        </div>
      )}

      {activeTab === 'payments' && (
        <div className="tab-content">
          <div className="payments-history">
            <h3>Cronologia Pagamenti</h3>
            {payments.length === 0 ? (
              <p>Nessun pagamento effettuato ancora.</p>
            ) : (
              <div className="payments-table">
                <div className="table-header">
                  <span>Data</span>
                  <span>Importo</span>
                  <span>Capitale</span>
                  <span>Interessi</span>
                  <span>Saldo Residuo</span>
                </div>
                {payments.map(payment => (
                  <div key={payment.paymentId} className="table-row">
                    <span>{new Date(payment.paymentDate).toLocaleDateString('it-IT')}</span>
                    <span>{formatCurrency(payment.amount)}</span>
                    <span>{formatCurrency(payment.principalPaid)}</span>
                    <span>{formatCurrency(payment.interestPaid)}</span>
                    <span>{formatCurrency(payment.remainingBalance)}</span>
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>
      )}

      {activeTab === 'schedule' && (
        <div className="tab-content">
          <div className="amortization-schedule">
            <h3>Piano di Ammortamento</h3>
            <div className="schedule-table">
              <div className="table-header">
                <span>Mese</span>
                <span>Data</span>
                <span>Rata</span>
                <span>Capitale</span>
                <span>Interessi</span>
                <span>Saldo</span>
              </div>
              {amortization.slice(0, 12).map(row => (
                <div key={row.month} className="table-row">
                  <span>{row.month}</span>
                  <span>{new Date(row.date).toLocaleDateString('it-IT')}</span>
                  <span>{formatCurrency(row.monthlyPayment)}</span>
                  <span>{formatCurrency(row.principalPayment)}</span>
                  <span>{formatCurrency(row.interestPayment)}</span>
                  <span>{formatCurrency(row.remainingBalance)}</span>
                </div>
              ))}
            </div>
            {amortization.length > 12 && (
              <p className="schedule-note">
                Mostrati i primi 12 mesi. Piano completo di {amortization.length} rate.
              </p>
            )}
          </div>
        </div>
      )}
    </div>
  );
};

export default LoanDetails;