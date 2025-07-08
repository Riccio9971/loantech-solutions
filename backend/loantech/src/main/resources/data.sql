-- Inserimento utenti di test (password: "password123" per tutti)
INSERT INTO users (email, password_hash, first_name, last_name, fiscal_code, phone_number, created_at, updated_at) VALUES
('mario.rossi@email.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'Mario', 'Rossi', 'RSSMRA80A01H501U', '+39 333 123 4567', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('lucia.bianchi@email.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'Lucia', 'Bianchi', 'BNCLCU85M01F205S', '+39 334 987 6543', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('giovanni.verdi@email.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'Giovanni', 'Verdi', 'VRDGNN75C15H501Z', '+39 335 111 2222', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('admin@loantech.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'Admin', 'System', 'DMNSYS90A01H501A', '+39 800 123 456', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Inserimento credit scores
INSERT INTO credit_scores (user_id, score, risk_category, calculated_at) VALUES
(1, 687, 'BUONO', CURRENT_TIMESTAMP),
(2, 745, 'OTTIMO', CURRENT_TIMESTAMP),
(3, 620, 'DISCRETO', CURRENT_TIMESTAMP),
(4, 800, 'ECCELLENTE', CURRENT_TIMESTAMP);

-- Inserimento richieste prestito di esempio (usando DATEADD per H2)
INSERT INTO loan_applications (user_id, loan_type, requested_amount, duration_months, monthly_income, employment_status, status, submitted_at, updated_at) VALUES
-- Mario Rossi - Richieste approvate
(1, 'PERSONAL_LOAN', 15000.00, 36, 2500.00, 'EMPLOYED', 'APPROVED', DATEADD('DAY', -30, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP),
(1, 'CAR_LOAN', 25000.00, 60, 2500.00, 'EMPLOYED', 'APPROVED', DATEADD('DAY', -90, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP),

-- Lucia Bianchi - Mix di stati
(2, 'MORTGAGE', 200000.00, 240, 3500.00, 'EMPLOYED', 'APPROVED', DATEADD('DAY', -60, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP),
(2, 'PERSONAL_LOAN', 8000.00, 24, 3500.00, 'EMPLOYED', 'PENDING', DATEADD('DAY', -2, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP),

-- Giovanni Verdi - Varie richieste
(3, 'BUSINESS_LOAN', 50000.00, 84, 4000.00, 'SELF_EMPLOYED', 'APPROVED', DATEADD('DAY', -45, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP),
(3, 'CAR_LOAN', 18000.00, 48, 4000.00, 'SELF_EMPLOYED', 'PENDING', DATEADD('DAY', -5, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP),
(3, 'PERSONAL_LOAN', 12000.00, 36, 4000.00, 'SELF_EMPLOYED', 'UNDER_REVIEW', DATEADD('DAY', -1, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP),

-- Richieste aggiuntive per test admin dashboard
(1, 'BUSINESS_LOAN', 30000.00, 60, 2500.00, 'EMPLOYED', 'PENDING', DATEADD('HOUR', -12, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP),
(2, 'CAR_LOAN', 22000.00, 48, 3500.00, 'EMPLOYED', 'UNDER_REVIEW', DATEADD('HOUR', -6, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP),
(1, 'PERSONAL_LOAN', 5000.00, 18, 2500.00, 'EMPLOYED', 'REJECTED', DATEADD('DAY', -15, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP);

-- Inserimento prestiti approvati (con calcoli corretti)
INSERT INTO loans (application_id, user_id, principal_amount, interest_rate, duration_months, monthly_payment, remaining_balance, start_date, end_date, status, created_at, updated_at) VALUES
-- Mario Rossi - Prestito Personale
(1, 1, 15000.00, 8.50, 36, 472.81, 12500.00, '2024-01-01', '2027-01-01', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
-- Mario Rossi - Prestito Auto
(2, 1, 25000.00, 6.00, 60, 483.32, 22000.00, '2023-10-01', '2028-10-01', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Lucia Bianchi - Mutuo Casa
(3, 2, 200000.00, 3.20, 240, 1158.03, 185000.00, '2024-01-01', '2044-01-01', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Giovanni Verdi - Prestito Business
(5, 3, 50000.00, 7.50, 84, 749.12, 45000.00, '2023-12-01', '2030-12-01', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Inserimento pagamenti di esempio
INSERT INTO payments (loan_id, amount, principal_paid, interest_paid, remaining_balance, payment_date, due_date, status, created_at) VALUES
-- Pagamenti per Mario Rossi - Prestito Personale (Loan ID 1)
(1, 472.81, 366.31, 106.50, 14633.69, '2024-02-01', '2024-02-01', 'COMPLETED', CURRENT_TIMESTAMP),
(1, 472.81, 368.89, 103.92, 14264.80, '2024-03-01', '2024-03-01', 'COMPLETED', CURRENT_TIMESTAMP),
(1, 472.81, 371.50, 101.31, 13893.30, '2024-04-01', '2024-04-01', 'COMPLETED', CURRENT_TIMESTAMP),
(1, 472.81, 374.13, 98.68, 13519.17, '2024-05-01', '2024-05-01', 'COMPLETED', CURRENT_TIMESTAMP),
(1, 472.81, 376.79, 96.02, 13142.38, '2024-06-01', '2024-06-01', 'COMPLETED', CURRENT_TIMESTAMP),

-- Pagamenti per Mario Rossi - Prestito Auto (Loan ID 2)
(2, 483.32, 358.32, 125.00, 24641.68, '2023-11-01', '2023-11-01', 'COMPLETED', CURRENT_TIMESTAMP),
(2, 483.32, 360.11, 123.21, 24281.57, '2023-12-01', '2023-12-01', 'COMPLETED', CURRENT_TIMESTAMP),
(2, 483.32, 361.91, 121.41, 23919.66, '2024-01-01', '2024-01-01', 'COMPLETED', CURRENT_TIMESTAMP),
(2, 483.32, 363.72, 119.60, 23555.94, '2024-02-01', '2024-02-01', 'COMPLETED', CURRENT_TIMESTAMP),

-- Pagamenti per Lucia Bianchi - Mutuo (Loan ID 3)
(3, 1158.03, 624.70, 533.33, 199375.30, '2024-02-01', '2024-02-01', 'COMPLETED', CURRENT_TIMESTAMP),
(3, 1158.03, 626.37, 531.66, 198748.93, '2024-03-01', '2024-03-01', 'COMPLETED', CURRENT_TIMESTAMP),
(3, 1158.03, 628.04, 529.99, 198120.89, '2024-04-01', '2024-04-01', 'COMPLETED', CURRENT_TIMESTAMP),

-- Pagamenti per Giovanni Verdi - Business Loan (Loan ID 4)
(4, 749.12, 436.87, 312.25, 49563.13, '2024-01-01', '2024-01-01', 'COMPLETED', CURRENT_TIMESTAMP),
(4, 749.12, 439.60, 309.52, 49123.53, '2024-02-01', '2024-02-01', 'COMPLETED', CURRENT_TIMESTAMP),
(4, 749.12, 442.35, 306.77, 48681.18, '2024-03-01', '2024-03-01', 'COMPLETED', CURRENT_TIMESTAMP);

-- Inserimento documenti di esempio
INSERT INTO documents (application_id, document_type, file_name, file_path, verified, uploaded_at) VALUES
-- Documenti per Mario Rossi
(1, 'BUSTA_PAGA', 'busta_paga_mario_gennaio.pdf', '/uploads/docs/busta_paga_mario_gennaio.pdf', true, CURRENT_TIMESTAMP),
(1, 'DOCUMENTO_IDENTITA', 'carta_identita_mario.pdf', '/uploads/docs/carta_identita_mario.pdf', true, CURRENT_TIMESTAMP),
(1, 'CONTRATTO_LAVORO', 'contratto_mario.pdf', '/uploads/docs/contratto_mario.pdf', true, CURRENT_TIMESTAMP),

(2, 'BUSTA_PAGA', 'busta_paga_mario_settembre.pdf', '/uploads/docs/busta_paga_mario_settembre.pdf', true, CURRENT_TIMESTAMP),
(2, 'DOCUMENTO_IDENTITA', 'carta_identita_mario.pdf', '/uploads/docs/carta_identita_mario.pdf', true, CURRENT_TIMESTAMP),

-- Documenti per Lucia Bianchi
(3, 'BUSTA_PAGA', 'busta_paga_lucia.pdf', '/uploads/docs/busta_paga_lucia.pdf', true, CURRENT_TIMESTAMP),
(3, 'DOCUMENTO_IDENTITA', 'carta_identita_lucia.pdf', '/uploads/docs/carta_identita_lucia.pdf', true, CURRENT_TIMESTAMP),
(3, 'CONTRATTO_LAVORO', 'contratto_lucia.pdf', '/uploads/docs/contratto_lucia.pdf', true, CURRENT_TIMESTAMP),
(3, 'CONTO_CORRENTE', 'estratto_conto_lucia.pdf', '/uploads/docs/estratto_conto_lucia.pdf', false, CURRENT_TIMESTAMP),

(4, 'BUSTA_PAGA', 'busta_paga_lucia_recente.pdf', '/uploads/docs/busta_paga_lucia_recente.pdf', false, CURRENT_TIMESTAMP),
(4, 'DOCUMENTO_IDENTITA', 'carta_identita_lucia.pdf', '/uploads/docs/carta_identita_lucia.pdf', false, CURRENT_TIMESTAMP),

-- Documenti per Giovanni Verdi
(5, 'DOCUMENTO_IDENTITA', 'patente_giovanni.pdf', '/uploads/docs/patente_giovanni.pdf', true, CURRENT_TIMESTAMP),
(5, 'DICHIARAZIONE_REDDITI', 'dichiarazione_redditi_giovanni.pdf', '/uploads/docs/dichiarazione_redditi_giovanni.pdf', true, CURRENT_TIMESTAMP),
(5, 'CONTO_CORRENTE', 'estratto_conto_giovanni.pdf', '/uploads/docs/estratto_conto_giovanni.pdf', true, CURRENT_TIMESTAMP),

(6, 'DOCUMENTO_IDENTITA', 'patente_giovanni.pdf', '/uploads/docs/patente_giovanni.pdf', false, CURRENT_TIMESTAMP),
(6, 'BUSTA_PAGA', 'busta_paga_giovanni.pdf', '/uploads/docs/busta_paga_giovanni.pdf', false, CURRENT_TIMESTAMP),

(7, 'DOCUMENTO_IDENTITA', 'patente_giovanni.pdf', '/uploads/docs/patente_giovanni.pdf', false, CURRENT_TIMESTAMP),

-- Documenti per richieste recenti
(8, 'BUSTA_PAGA', 'busta_paga_mario_recente.pdf', '/uploads/docs/busta_paga_mario_recente.pdf', false, CURRENT_TIMESTAMP),
(8, 'DOCUMENTO_IDENTITA', 'carta_identita_mario.pdf', '/uploads/docs/carta_identita_mario.pdf', false, CURRENT_TIMESTAMP),

(9, 'BUSTA_PAGA', 'busta_paga_lucia_auto.pdf', '/uploads/docs/busta_paga_lucia_auto.pdf', false, CURRENT_TIMESTAMP),
(9, 'DOCUMENTO_IDENTITA', 'carta_identita_lucia.pdf', '/uploads/docs/carta_identita_lucia.pdf', false, CURRENT_TIMESTAMP);

-- Aggiornamento saldi residui più realistici basati sui pagamenti
UPDATE loans SET remaining_balance = 13142.38 WHERE loan_id = 1; -- Mario Prestito Personale
UPDATE loans SET remaining_balance = 23555.94 WHERE loan_id = 2; -- Mario Prestito Auto
UPDATE loans SET remaining_balance = 198120.89 WHERE loan_id = 3; -- Lucia Mutuo
UPDATE loans SET remaining_balance = 48681.18 WHERE loan_id = 4; -- Giovanni Business

-- Inserimento di pagamenti futuri in pending per test
INSERT INTO payments (loan_id, amount, principal_paid, interest_paid, remaining_balance, payment_date, due_date, status, created_at) VALUES
(1, 472.81, 379.47, 93.34, 12762.91, NULL, '2024-07-01', 'PENDING', CURRENT_TIMESTAMP),
(1, 472.81, 382.17, 90.64, 12380.74, NULL, '2024-08-01', 'PENDING', CURRENT_TIMESTAMP),
(2, 483.32, 365.54, 117.78, 23190.40, NULL, '2024-07-01', 'PENDING', CURRENT_TIMESTAMP),
(3, 1158.03, 629.72, 528.31, 197491.17, NULL, '2024-07-01', 'PENDING', CURRENT_TIMESTAMP),
(4, 749.12, 445.12, 304.00, 48236.06, NULL, '2024-07-01', 'PENDING', CURRENT_TIMESTAMP);