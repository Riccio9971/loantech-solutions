import React, { useState, useEffect, useRef } from 'react';
import api from '../services/api';
import './DocumentManager.css';

const DocumentManager = ({ applicationId, user, onDocumentsChange }) => {
  const [documents, setDocuments] = useState([]);
  const [uploading, setUploading] = useState(false);
  const [dragOver, setDragOver] = useState(false);
  const [previewDocument, setPreviewDocument] = useState(null);
  const [showPreview, setShowPreview] = useState(false);
  const [error, setError] = useState('');
  const fileInputRef = useRef(null);

  const documentTypes = [
    { value: 'DOCUMENTO_IDENTITA', label: 'Documento di Identità', icon: '🆔', required: true },
    { value: 'BUSTA_PAGA', label: 'Busta Paga', icon: '💰', required: true },
    { value: 'CONTRATTO_LAVORO', label: 'Contratto di Lavoro', icon: '📋', required: false },
    { value: 'CONTO_CORRENTE', label: 'Estratto Conto', icon: '🏦', required: false },
    { value: 'DICHIARAZIONE_REDDITI', label: 'Dichiarazione dei Redditi', icon: '📊', required: false },
    { value: 'ALTRO', label: 'Altro Documento', icon: '📄', required: false }
  ];

  useEffect(() => {
    if (applicationId) {
      fetchDocuments();
    }
  }, [applicationId]);

  const fetchDocuments = async () => {
    try {
      const response = await api.get(`/documents/application/${applicationId}`);
      if (response.data.success) {
        setDocuments(response.data.data);
        if (onDocumentsChange) {
          onDocumentsChange(response.data.data);
        }
      }
    } catch (err) {
      setError('Errore nel caricamento dei documenti');
    }
  };

  const handleFileSelect = (files, documentType = null) => {
    const fileList = Array.from(files);

    fileList.forEach(file => {
      if (validateFile(file)) {
        uploadDocument(file, documentType);
      }
    });
  };

  const validateFile = (file) => {
    const maxSize = 10 * 1024 * 1024; // 10MB
    const allowedTypes = ['application/pdf', 'image/jpeg', 'image/png', 'image/jpg'];

    if (file.size > maxSize) {
      setError('File troppo grande. Massimo 10MB');
      return false;
    }

    if (!allowedTypes.includes(file.type)) {
      setError('Formato file non supportato. Usa PDF, JPG o PNG');
      return false;
    }

    return true;
  };

  const uploadDocument = async (file, documentType) => {
    setUploading(true);
    setError('');

    try {
      const formData = new FormData();
      formData.append('file', file);
      formData.append('documentType', documentType || 'ALTRO');
      formData.append('applicationId', applicationId);

      const response = await api.post('/documents/upload', formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      });

      if (response.data.success) {
        fetchDocuments(); // Ricarica la lista
      } else {
        setError(response.data.message);
      }
    } catch (err) {
      setError(err.response?.data?.message || 'Errore nel caricamento del file');
    } finally {
      setUploading(false);
    }
  };

  const deleteDocument = async (documentId) => {
    if (!window.confirm('Sei sicuro di voler eliminare questo documento?')) {
      return;
    }

    try {
      const response = await api.delete(`/documents/${documentId}`);
      if (response.data.success) {
        fetchDocuments();
      }
    } catch (err) {
      setError('Errore nell\'eliminazione del documento');
    }
  };

  const previewDocumentHandler = (document) => {
    setPreviewDocument(document);
    setShowPreview(true);
  };

  const downloadDocument = async (documentId, fileName) => {
    try {
      const response = await api.get(`/documents/${documentId}/download`, {
        responseType: 'blob'
      });

      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', fileName);
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      window.URL.revokeObjectURL(url);
    } catch (err) {
      setError('Errore nel download del documento');
    }
  };

  const handleDragOver = (e) => {
    e.preventDefault();
    setDragOver(true);
  };

  const handleDragLeave = (e) => {
    e.preventDefault();
    setDragOver(false);
  };

  const handleDrop = (e) => {
    e.preventDefault();
    setDragOver(false);

    const files = e.dataTransfer.files;
    handleFileSelect(files);
  };

  const getDocumentIcon = (documentType, mimeType) => {
    const typeConfig = documentTypes.find(type => type.value === documentType);
    if (typeConfig) return typeConfig.icon;

    if (mimeType?.includes('pdf')) return '📄';
    if (mimeType?.includes('image')) return '🖼️';
    return '📎';
  };

  const getFileSize = (bytes) => {
    if (bytes < 1024) return bytes + ' B';
    if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB';
    return (bytes / (1024 * 1024)).toFixed(1) + ' MB';
  };

  const getUploadProgress = () => {
    const requiredDocs = documentTypes.filter(type => type.required);
    const uploadedRequired = requiredDocs.filter(type =>
      documents.some(doc => doc.documentType === type.value)
    );

    return {
      completed: uploadedRequired.length,
      total: requiredDocs.length,
      percentage: (uploadedRequired.length / requiredDocs.length) * 100
    };
  };

  const progress = getUploadProgress();

  return (
    <div className="document-manager">
      <div className="document-header">
        <h3>📁 Gestione Documenti</h3>
        <div className="upload-progress">
          <div className="progress-info">
            <span>Documenti obbligatori: {progress.completed}/{progress.total}</span>
            <span className="percentage">{progress.percentage.toFixed(0)}%</span>
          </div>
          <div className="progress-bar">
            <div
              className="progress-fill"
              style={{ width: `${progress.percentage}%` }}
            ></div>
          </div>
        </div>
      </div>

      {error && <div className="error-message">❌ {error}</div>}

      {/* Upload Area */}
      <div
        className={`upload-area ${dragOver ? 'drag-over' : ''}`}
        onDragOver={handleDragOver}
        onDragLeave={handleDragLeave}
        onDrop={handleDrop}
        onClick={() => fileInputRef.current?.click()}
      >
        <div className="upload-content">
          <div className="upload-icon">
            {uploading ? '⏳' : '☁️'}
          </div>
          <h4>
            {uploading ? 'Caricamento in corso...' : 'Trascina i file qui o clicca per selezionare'}
          </h4>
          <p>Formati supportati: PDF, JPG, PNG (max 10MB)</p>
        </div>

        <input
          ref={fileInputRef}
          type="file"
          multiple
          accept=".pdf,.jpg,.jpeg,.png"
          onChange={(e) => handleFileSelect(e.target.files)}
          style={{ display: 'none' }}
        />
      </div>

      {/* Upload per tipo specifico */}
      <div className="document-types-grid">
        {documentTypes.map(type => {
          const hasDocument = documents.some(doc => doc.documentType === type.value);

          return (
            <div key={type.value} className={`document-type-card ${hasDocument ? 'completed' : ''} ${type.required ? 'required' : ''}`}>
              <div className="card-header">
                <span className="type-icon">{type.icon}</span>
                <div className="type-info">
                  <h4>{type.label}</h4>
                  {type.required && <span className="required-badge">Obbligatorio</span>}
                </div>
                {hasDocument && <span className="status-check">✅</span>}
              </div>

              {!hasDocument && (
                <button
                  className="upload-type-btn"
                  onClick={() => {
                    const input = document.createElement('input');
                    input.type = 'file';
                    input.accept = '.pdf,.jpg,.jpeg,.png';
                    input.onchange = (e) => handleFileSelect(e.target.files, type.value);
                    input.click();
                  }}
                >
                  📤 Carica {type.label}
                </button>
              )}
            </div>
          );
        })}
      </div>

      {/* Lista documenti caricati */}
      {documents.length > 0 && (
        <div className="documents-list">
          <h4>📋 Documenti Caricati</h4>

          <div className="documents-grid">
            {documents.map(doc => (
              <div key={doc.documentId} className={`document-item ${doc.verified ? 'verified' : 'pending'}`}>
                <div className="document-info">
                  <div className="document-icon">
                    {getDocumentIcon(doc.documentType, doc.mimeType)}
                  </div>
                  <div className="document-details">
                    <h5>{doc.fileName}</h5>
                    <p className="document-type">
                      {documentTypes.find(t => t.value === doc.documentType)?.label || doc.documentType}
                    </p>
                    <p className="document-meta">
                      {getFileSize(doc.fileSize)} • {new Date(doc.uploadedAt).toLocaleDateString('it-IT')}
                    </p>
                  </div>
                  <div className="document-status">
                    {doc.verified ? (
                      <span className="status verified">✅ Verificato</span>
                    ) : (
                      <span className="status pending">⏳ In Verifica</span>
                    )}
                  </div>
                </div>

                <div className="document-actions">
                  <button
                    className="action-btn preview"
                    onClick={() => previewDocumentHandler(doc)}
                    title="Anteprima"
                  >
                    👁️
                  </button>
                  <button
                    className="action-btn download"
                    onClick={() => downloadDocument(doc.documentId, doc.fileName)}
                    title="Download"
                  >
                    📥
                  </button>
                  <button
                    className="action-btn delete"
                    onClick={() => deleteDocument(doc.documentId)}
                    title="Elimina"
                  >
                    🗑️
                  </button>
                </div>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Modal Preview */}
      {showPreview && previewDocument && (
        <div className="preview-modal" onClick={() => setShowPreview(false)}>
          <div className="preview-content" onClick={(e) => e.stopPropagation()}>
            <div className="preview-header">
              <h3>👁️ Anteprima Documento</h3>
              <button
                className="close-btn"
                onClick={() => setShowPreview(false)}
              >
                ✕
              </button>
            </div>

            <div className="preview-body">
              <div className="document-info-preview">
                <h4>{previewDocument.fileName}</h4>
                <p>{documentTypes.find(t => t.value === previewDocument.documentType)?.label}</p>
              </div>

              <div className="preview-area">
                {previewDocument.mimeType?.includes('image') ? (
                  <img
                    src={`/api/documents/${previewDocument.documentId}/preview`}
                    alt={previewDocument.fileName}
                    className="preview-image"
                  />
                ) : previewDocument.mimeType?.includes('pdf') ? (
                  <iframe
                    src={`/api/documents/${previewDocument.documentId}/preview`}
                    className="preview-pdf"
                    title={previewDocument.fileName}
                  />
                ) : (
                  <div className="preview-placeholder">
                    <div className="placeholder-icon">📄</div>
                    <p>Anteprima non disponibile per questo tipo di file</p>
                    <button
                      className="download-btn"
                      onClick={() => downloadDocument(previewDocument.documentId, previewDocument.fileName)}
                    >
                      📥 Scarica per Visualizzare
                    </button>
                  </div>
                )}
              </div>
            </div>

            <div className="preview-footer">
              <button
                className="btn secondary"
                onClick={() => downloadDocument(previewDocument.documentId, previewDocument.fileName)}
              >
                📥 Download
              </button>
              <button
                className="btn primary"
                onClick={() => setShowPreview(false)}
              >
                Chiudi
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default DocumentManager;