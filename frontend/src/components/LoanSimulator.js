import React, { useState, useEffect } from 'react';
import api from '../services/api';
import './LoanSimulator.css';

const LoanSimulator = () => {
  const [formData, setFormData] = useState({
    loanType: 'PERSONAL_LOAN',
    amount: '',
    durationMonths: ''
  });
  const [simulation, setSimulation] = useState(null);
  const [amortizationPlan, setAmortizationPlan] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [activeTab, setActiveTab] = useState('summary');
  const [comparisonData, setComparisonData] = useState([]);

  const loanTypes = [
    { value: 'PERSONAL_LOAN', label: 'Prestito Personale', rate: '8.5%', maxAmount: 100000, minDuration: 6, maxDuration: 120 },
    { value: 'MORTGAGE', label: 'Mutuo Casa', rate: '3.2%', maxAmount: 500000, minDuration: 60, maxDuration: 360 },
    { value: 'CAR_LOAN', label: 'Prestito Auto', rate: '6.0%', maxAmount: 80000, minDuration: 12, maxDuration: 84 },
    { value: 'BUSINESS_LOAN', label: 'Prestito Business', rate: '7.5%', maxAmount: 200000, minDuration: 12, maxDuration: 120 }
  ];

  const getCurrentLoanType = () => {
    return loanTypes.find(type => type.value === formData.loanType);
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));

    // Reset simulation quando cambiano i parametri
    if (simulation) {
      setSimulation(null);
      setAmortizationPlan([]);
    }
  };

  const calculateAmortizationPlan = (principal, interestRate, months, monthlyPayment) => {
    const plan = [];
    let remainingBalance = principal;
    const monthlyRate = interestRate / 100 / 12;

    for (let month = 1; month <= months; month++) {
      const interestPayment = remainingBalance * monthlyRate;
      const principalPayment = monthlyPayment - interestPayment;
      remainingBalance = Math.max(0, remainingBalance - principalPayment);

      plan.push({
        month,
        monthlyPayment: monthlyPayment,
        principalPayment: Math.max(0, principalPayment),
        interestPayment: Math.max(0, interestPayment),
        remainingBalance: Math.max(0, remainingBalance)
      });

      if (remainingBalance <= 0) break;
    }

    return plan;
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
        const simResult = response.data.data;
        setSimulation(simResult);

        // Calcola piano ammortamento
        const plan = calculateAmortizationPlan(
          simResult.requestedAmount,
          simResult.interestRate,
          simResult.durationMonths,
          simResult.monthlyPayment
        );
        setAmortizationPlan(plan);

        // Genera comparazioni automatiche
        generateComparisons(simulationData);
      } else {
        setError(response.data.message);
      }
    } catch (err) {
      setError(err.response?.data?.message || 'Errore nella simulazione');
    } finally {
      setLoading(false);
    }
  };

  const generateComparisons = async (baseData) => {
    const comparisons = [];
    const baseDuration = baseData.durationMonths;

    try {
      // Confronto con durate diverse
      const durations = [24, 36, 48, 60, 84].filter(d => d !== baseDuration);

      for (const duration of durations.slice(0, 2)) {
        const compData = { ...baseData, durationMonths: duration };
        const response = await api.post('/loan-applications/simulate', compData);
        if (response.data.success) {
          comparisons.push({
            ...response.data.data,
            label: `${duration} mesi`,
            type: 'duration'
          });
        }
      }

      setComparisonData(comparisons);
    } catch (err) {
      console.error('Errore nel calcolo comparazioni:', err);
    }
  };

  const formatCurrency = (amount) => {
    return new Intl.NumberFormat('it-IT', {
      style: 'currency',
      currency: 'EUR',
      minimumFractionDigits: 2
    }).format(amount);
  };

  const formatPercentage = (value) => {
    return `${value.toFixed(2)}%`;
  };

  const calculateTotalInterest = () => {
    return amortizationPlan.reduce((total, payment) => total + payment.interestPayment, 0);
  };

  const calculateTAEG = () => {
    if (!simulation) return 0;
    // Calcolo semplificato del TAEG (includendo costi aggiuntivi stimati)
    const additionalCosts = simulation.requestedAmount * 0.01; // 1% di costi aggiuntivi stimati
    const totalCost = simulation.totalAmount + additionalCosts;
    const taeg = ((totalCost / simulation.requestedAmount) ** (12 / simulation.durationMonths) - 1) * 100;
    return taeg;
  };

  return (
    <div className="loan-simulator">
      <div className="simulator-header">
        <h1>🧮 Simulatore Prestito Avanzato</h1>
        <p>Calcola rate, piano di ammortamento e confronta le opzioni</p>
      </div>

      <div className="simulator-container">
        {/* Form di simulazione */}
        <div className="simulator-form-section">
          <form onSubmit={handleSubmit} className="simulator-form">
            <div className="form-group">
              <label htmlFor="loanType">💼 Tipo di Prestito</label>
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
              <small className="form-hint">
                Max: {formatCurrency(getCurrentLoanType()?.maxAmount || 0)} |
                Durata: {getCurrentLoanType()?.minDuration}-{getCurrentLoanType()?.maxDuration} mesi
              </small>
            </div>

            <div className="form-row">
              <div className="form-group">
                <label htmlFor="amount">💰 Importo (€)</label>
                <input
                  type="number"
                  id="amount"
                  name="amount"
                  value={formData.amount}
                  onChange={handleChange}
                  min="1000"
                  max={getCurrentLoanType()?.maxAmount || 100000}
                  step="100"
                  required
                  placeholder="es. 15000"
                />
              </div>

              <div className="form-group">
                <label htmlFor="durationMonths">📅 Durata (mesi)</label>
                <input
                  type="number"
                  id="durationMonths"
                  name="durationMonths"
                  value={formData.durationMonths}
                  onChange={handleChange}
                  min={getCurrentLoanType()?.minDuration || 6}
                  max={getCurrentLoanType()?.maxDuration || 120}
                  required
                  placeholder="es. 36"
                />
              </div>
            </div>

            {error && <div className="error-message">❌ {error}</div>}

            <button
              type="submit"
              className="simulate-button"
              disabled={loading}
            >
              {loading ? '⏳ Calcolando...' : '🚀 Simula Prestito'}
            </button>
          </form>
        </div>

        {/* Risultati simulazione */}
        {simulation && (
          <div className="simulation-results">
            <div className="results-tabs">
              <button
                className={`tab-btn ${activeTab === 'summary' ? 'active' : ''}`}
                onClick={() => setActiveTab('summary')}
              >
                📊 Riepilogo
              </button>
              <button
                className={`tab-btn ${activeTab === 'amortization' ? 'active' : ''}`}
                onClick={() => setActiveTab('amortization')}
              >
                📋 Piano Ammortamento
              </button>
              <button
                className={`tab-btn ${activeTab === 'comparison' ? 'active' : ''}`}
                onClick={() => setActiveTab('comparison')}
              >
                ⚖️ Confronti
              </button>
              <button
                className={`tab-btn ${activeTab === 'charts' ? 'active' : ''}`}
                onClick={() => setActiveTab('charts')}
              >
                📈 Grafici
              </button>
            </div>

            {/* Tab Riepilogo */}
            {activeTab === 'summary' && (
              <div className="summary-tab">
                <div className="results-grid">
                  <div className="result-card primary">
                    <div className="card-icon">💰</div>
                    <h3>Rata Mensile</h3>
                    <div className="amount">{formatCurrency(simulation.monthlyPayment)}</div>
                  </div>

                  <div className="result-card">
                    <div className="card-icon">📊</div>
                    <h3>Importo Richiesto</h3>
                    <div className="amount">{formatCurrency(simulation.requestedAmount)}</div>
                  </div>

                  <div className="result-card">
                    <div className="card-icon">⏰</div>
                    <h3>Durata</h3>
                    <div className="amount">{simulation.durationMonths} mesi</div>
                  </div>

                  <div className="result-card">
                    <div className="card-icon">📈</div>
                    <h3>Tasso Interesse</h3>
                    <div className="amount">{formatPercentage(simulation.interestRate)}</div>
                  </div>

                  <div className="result-card warning">
                    <div className="card-icon">💸</div>
                    <h3>Totale da Restituire</h3>
                    <div className="amount">{formatCurrency(simulation.totalAmount)}</div>
                  </div>

                  <div className="result-card info">
                    <div className="card-icon">🧾</div>
                    <h3>Interessi Totali</h3>
                    <div className="amount">{formatCurrency(calculateTotalInterest())}</div>
                  </div>

                  <div className="result-card secondary">
                    <div className="card-icon">📋</div>
                    <h3>TAEG Stimato</h3>
                    <div className="amount">{formatPercentage(calculateTAEG())}</div>
                  </div>

                  <div className="result-card success">
                    <div className="card-icon">💡</div>
                    <h3>Risparmio vs Max Rate</h3>
                    <div className="amount">-{formatCurrency(simulation.totalAmount * 0.02)}</div>
                  </div>
                </div>

                <div className="summary-details">
                  <h3>📝 Dettagli Finanziamento</h3>
                  <div className="details-grid">
                    <div className="detail-item">
                      <span className="label">Tipo prestito:</span>
                      <span className="value">{loanTypes.find(t => t.value === formData.loanType)?.label}</span>
                    </div>
                    <div className="detail-item">
                      <span className="label">Prima rata:</span>
                      <span className="value">{formatCurrency(amortizationPlan[0]?.monthlyPayment || 0)}</span>
                    </div>
                    <div className="detail-item">
                      <span className="label">Ultima rata:</span>
                      <span className="value">{formatCurrency(amortizationPlan[amortizationPlan.length - 1]?.monthlyPayment || 0)}</span>
                    </div>
                    <div className="detail-item">
                      <span className="label">Interessi primo anno:</span>
                      <span className="value">{formatCurrency(amortizationPlan.slice(0, 12).reduce((sum, p) => sum + p.interestPayment, 0))}</span>
                    </div>
                  </div>
                </div>
              </div>
            )}

            {/* Tab Piano Ammortamento */}
            {activeTab === 'amortization' && (
              <div className="amortization-tab">
                <div className="amortization-summary">
                  <h3>📋 Piano di Ammortamento Dettagliato</h3>
                  <p>Evoluzione mensile del rimborso capitale e interessi</p>
                </div>

                <div className="amortization-table-container">
                  <table className="amortization-table">
                    <thead>
                      <tr>
                        <th>Mese</th>
                        <th>Rata</th>
                        <th>Capitale</th>
                        <th>Interessi</th>
                        <th>Saldo Residuo</th>
                        <th>% Capitale</th>
                      </tr>
                    </thead>
                    <tbody>
                      {amortizationPlan.map((payment, index) => (
                        <tr key={index} className={index % 12 === 11 ? 'year-end' : ''}>
                          <td>{payment.month}</td>
                          <td>{formatCurrency(payment.monthlyPayment)}</td>
                          <td className="principal">{formatCurrency(payment.principalPayment)}</td>
                          <td className="interest">{formatCurrency(payment.interestPayment)}</td>
                          <td>{formatCurrency(payment.remainingBalance)}</td>
                          <td>
                            <div className="percentage-bar">
                              <div
                                className="percentage-fill"
                                style={{ width: `${(payment.principalPayment / payment.monthlyPayment) * 100}%` }}
                              ></div>
                              <span>{((payment.principalPayment / payment.monthlyPayment) * 100).toFixed(1)}%</span>
                            </div>
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>

                <div className="amortization-insights">
                  <div className="insight-card">
                    <h4>💡 Insight</h4>
                    <ul>
                      <li>Nei primi mesi paghi principalmente interessi</li>
                      <li>Il capitale aumenta progressivamente nel tempo</li>
                      <li>Estinguere anticipatamente conviene nei primi anni</li>
                    </ul>
                  </div>
                </div>
              </div>
            )}

            {/* Tab Confronti */}
            {activeTab === 'comparison' && (
              <div className="comparison-tab">
                <h3>⚖️ Confronto Opzioni</h3>

                <div className="comparison-grid">
                  <div className="comparison-card current">
                    <div className="card-header">
                      <h4>Simulazione Corrente</h4>
                      <span className="badge current">Selezionata</span>
                    </div>
                    <div className="comparison-details">
                      <div className="detail">Durata: {simulation.durationMonths} mesi</div>
                      <div className="detail">Rata: {formatCurrency(simulation.monthlyPayment)}</div>
                      <div className="detail">Totale: {formatCurrency(simulation.totalAmount)}</div>
                      <div className="detail">Interessi: {formatCurrency(calculateTotalInterest())}</div>
                    </div>
                  </div>

                  {comparisonData.map((comp, index) => (
                    <div key={index} className="comparison-card">
                      <div className="card-header">
                        <h4>{comp.label}</h4>
                        <span className={`badge ${comp.monthlyPayment < simulation.monthlyPayment ? 'better' : 'worse'}`}>
                          {comp.monthlyPayment < simulation.monthlyPayment ? 'Rata minore' : 'Rata maggiore'}
                        </span>
                      </div>
                      <div className="comparison-details">
                        <div className="detail">Durata: {comp.durationMonths} mesi</div>
                        <div className="detail">
                          Rata: {formatCurrency(comp.monthlyPayment)}
                          <span className={`difference ${comp.monthlyPayment < simulation.monthlyPayment ? 'positive' : 'negative'}`}>
                            ({comp.monthlyPayment < simulation.monthlyPayment ? '-' : '+'}{formatCurrency(Math.abs(comp.monthlyPayment - simulation.monthlyPayment))})
                          </span>
                        </div>
                        <div className="detail">
                          Totale: {formatCurrency(comp.totalAmount)}
                          <span className={`difference ${comp.totalAmount < simulation.totalAmount ? 'positive' : 'negative'}`}>
                            ({comp.totalAmount < simulation.totalAmount ? '-' : '+'}{formatCurrency(Math.abs(comp.totalAmount - simulation.totalAmount))})
                          </span>
                        </div>
                        <div className="detail">Interessi: {formatCurrency(comp.totalInterest)}</div>
                      </div>
                    </div>
                  ))}
                </div>

                <div className="comparison-tips">
                  <h4>💡 Consigli per la scelta</h4>
                  <ul>
                    <li><strong>Durata più breve:</strong> Rate più alte ma meno interessi totali</li>
                    <li><strong>Durata più lunga:</strong> Rate più basse ma più interessi nel tempo</li>
                    <li><strong>Considera la tua situazione finanziaria:</strong> Scegli una rata sostenibile</li>
                  </ul>
                </div>
              </div>
            )}

            {/* Tab Grafici */}
            {activeTab === 'charts' && (
              <div className="charts-tab">
                <h3>📈 Visualizzazione Grafici</h3>

                <div className="charts-grid">
                  <div className="chart-container">
                    <h4>Evoluzione Saldo Residuo</h4>
                    <div className="simple-chart">
                      <svg viewBox="0 0 400 200" className="balance-chart">
                        <defs>
                          <linearGradient id="balanceGradient" x1="0%" y1="0%" x2="0%" y2="100%">
                            <stop offset="0%" stopColor="#667eea" stopOpacity="0.8"/>
                            <stop offset="100%" stopColor="#764ba2" stopOpacity="0.1"/>
                          </linearGradient>
                        </defs>

                        <path
                          d={`M 0,20 ${amortizationPlan.map((p, i) =>
                            `L ${(i / (amortizationPlan.length - 1)) * 400},${20 + (p.remainingBalance / simulation.requestedAmount) * 160}`
                          ).join(' ')}`}
                          fill="url(#balanceGradient)"
                          stroke="#667eea"
                          strokeWidth="2"
                        />

                        <text x="10" y="15" className="chart-label">
                          {formatCurrency(simulation.requestedAmount)}
                        </text>
                        <text x="350" y="195" className="chart-label">€0</text>
                      </svg>
                    </div>
                  </div>

                  <div className="chart-container">
                    <h4>Ripartizione Capitale vs Interessi</h4>
                    <div className="pie-chart-simple">
                      <div className="pie-slice">
                        <div
                          className="slice capital"
                          style={{
                            '--percentage': `${(simulation.requestedAmount / simulation.totalAmount) * 100}%`
                          }}
                        >
                        </div>
                        <div className="slice interest"></div>
                      </div>
                      <div className="pie-legend">
                        <div className="legend-item">
                          <span className="color-box capital"></span>
                          <span>Capitale: {formatCurrency(simulation.requestedAmount)}</span>
                        </div>
                        <div className="legend-item">
                          <span className="color-box interest"></span>
                          <span>Interessi: {formatCurrency(calculateTotalInterest())}</span>
                        </div>
                      </div>
                    </div>
                  </div>

                  <div className="chart-container">
                    <h4>Evoluzione Rate: Capitale vs Interessi</h4>
                    <div className="stacked-bar-chart">
                      {amortizationPlan.slice(0, 24).map((payment, index) => (
                        <div key={index} className="bar-month">
                          <div className="stacked-bar">
                            <div
                              className="bar-segment principal"
                              style={{
                                height: `${(payment.principalPayment / payment.monthlyPayment) * 100}%`
                              }}
                              title={`Capitale: ${formatCurrency(payment.principalPayment)}`}
                            ></div>
                            <div
                              className="bar-segment interest"
                              style={{
                                height: `${(payment.interestPayment / payment.monthlyPayment) * 100}%`
                              }}
                              title={`Interessi: ${formatCurrency(payment.interestPayment)}`}
                            ></div>
                          </div>
                          <div className="bar-label">{payment.month}</div>
                        </div>
                      ))}
                    </div>
                    <div className="chart-legend">
                      <div className="legend-item">
                        <span className="color-box principal"></span>
                        <span>Quota Capitale</span>
                      </div>
                      <div className="legend-item">
                        <span className="color-box interest"></span>
                        <span>Quota Interessi</span>
                      </div>
                    </div>
                  </div>

                  <div className="chart-container">
                    <h4>Risparmio Cumulativo per Anno</h4>
                    <div className="savings-chart">
                      {[1, 2, 3, 4, 5].map(year => {
                        const yearlyInterest = amortizationPlan
                          .slice((year - 1) * 12, year * 12)
                          .reduce((sum, p) => sum + p.interestPayment, 0);
                        const maxInterest = simulation.requestedAmount * 0.1; // 10% del capitale

                        return (
                          <div key={year} className="savings-year">
                            <div className="savings-bar">
                              <div
                                className="savings-fill"
                                style={{
                                  width: `${(yearlyInterest / maxInterest) * 100}%`
                                }}
                              ></div>
                            </div>
                            <div className="year-label">Anno {year}</div>
                            <div className="year-amount">{formatCurrency(yearlyInterest)}</div>
                          </div>
                        );
                      })}
                    </div>
                  </div>
                </div>

                <div className="chart-insights">
                  <div className="insight-grid">
                    <div className="insight-item">
                      <h5>🎯 Punto di Pareggio</h5>
                      <p>Dopo {Math.ceil(amortizationPlan.findIndex(p => p.principalPayment > p.interestPayment) + 1)} mesi pagherai più capitale che interessi</p>
                    </div>
                    <div className="insight-item">
                      <h5>💰 Costo Totale</h5>
                      <p>Gli interessi rappresentano il {((calculateTotalInterest() / simulation.requestedAmount) * 100).toFixed(1)}% del capitale</p>
                    </div>
                    <div className="insight-item">
                      <h5>⏰ Estinzione Anticipata</h5>
                      <p>Estinguendo dopo 2 anni risparmieresti circa {formatCurrency(calculateTotalInterest() * 0.6)}</p>
                    </div>
                  </div>
                </div>
              </div>
            )}

            <div className="simulation-actions">
              <button
                className="action-button primary"
                onClick={() => window.location.href = '/apply'}
              >
                🚀 Richiedi questo Prestito
              </button>
              <button
                className="action-button secondary"
                onClick={() => {
                  setSimulation(null);
                  setAmortizationPlan([]);
                  setComparisonData([]);
                  setActiveTab('summary');
                }}
              >
                🔄 Nuova Simulazione
              </button>
              <button
                className="action-button outline"
                onClick={() => window.print()}
              >
                🖨️ Stampa Risultati
              </button>
              <button
                className="action-button info"
                onClick={() => {
                  const data = {
                    simulation,
                    amortizationPlan: amortizationPlan.slice(0, 12), // Solo primo anno
                    totalInterest: calculateTotalInterest(),
                    taeg: calculateTAEG()
                  };
                  navigator.clipboard.writeText(JSON.stringify(data, null, 2));
                  alert('Dati simulazione copiati negli appunti!');
                }}
              >
                📋 Copia Dati
              </button>
            </div>

            {/* Quick Actions Panel */}
            <div className="quick-actions-panel">
              <h4>🚀 Azioni Rapide</h4>
              <div className="quick-actions-grid">
                <button
                  className="quick-action"
                  onClick={() => {
                    setFormData({...formData, durationMonths: Math.max(12, parseInt(formData.durationMonths) - 12)});
                  }}
                >
                  ⬇️ Riduci Durata (-1 anno)
                </button>
                <button
                  className="quick-action"
                  onClick={() => {
                    setFormData({...formData, durationMonths: Math.min(120, parseInt(formData.durationMonths) + 12)});
                  }}
                >
                  ⬆️ Aumenta Durata (+1 anno)
                </button>
                <button
                  className="quick-action"
                  onClick={() => {
                    setFormData({...formData, amount: Math.max(1000, parseInt(formData.amount) - 5000)});
                  }}
                >
                  💰 Riduci Importo (-5k)
                </button>
                <button
                  className="quick-action"
                  onClick={() => {
                    const maxAmount = getCurrentLoanType()?.maxAmount || 100000;
                    setFormData({...formData, amount: Math.min(maxAmount, parseInt(formData.amount) + 5000)});
                  }}
                >
                  💰 Aumenta Importo (+5k)
                </button>
              </div>
            </div>

            {/* Recommendation Panel */}
            {simulation && (
              <div className="recommendation-panel">
                <h4>🎯 Raccomandazioni Personalizzate</h4>
                <div className="recommendations">
                  {simulation.monthlyPayment > simulation.requestedAmount * 0.05 && (
                    <div className="recommendation warning">
                      <span className="rec-icon">⚠️</span>
                      <div className="rec-content">
                        <strong>Rata Alta</strong>
                        <p>La rata rappresenta più del 5% del capitale. Considera di allungare la durata.</p>
                      </div>
                    </div>
                  )}

                  {calculateTotalInterest() > simulation.requestedAmount * 0.3 && (
                    <div className="recommendation info">
                      <span className="rec-icon">💡</span>
                      <div className="rec-content">
                        <strong>Interessi Elevati</strong>
                        <p>Gli interessi superano il 30% del capitale. Valuta una durata più breve se possibile.</p>
                      </div>
                    </div>
                  )}

                  {simulation.durationMonths > 60 && formData.loanType === 'PERSONAL_LOAN' && (
                    <div className="recommendation success">
                      <span className="rec-icon">✅</span>
                      <div className="rec-content">
                        <strong>Durata Ottimale</strong>
                        <p>Durata equilibrata per questo tipo di prestito. Rate sostenibili e interessi contenuti.</p>
                      </div>
                    </div>
                  )}
                </div>
              </div>
            )}
          </div>
        )}
      </div>
    </div>
  );
};

export default LoanSimulator;