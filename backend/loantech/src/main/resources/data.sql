-- Inserimento utenti di test (password: "password123" per tutti)
INSERT INTO users (email, password_hash, first_name, last_name, fiscal_code, phone_number, created_at, updated_at) VALUES
('mario.rossi@email.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'Mario', 'Rossi', 'RSSMRA80A01H501U', '+39 333 123 4567', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('lucia.bianchi@email.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'Lucia', 'Bianchi', 'BNCLCU85M01F205S', '+39 334 987 6543', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('giovanni.verdi@email.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'Giovanni', 'Verdi', 'VRDGNN75C15H501Z', '+39 335 111 2222', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Inserimento credit scores
INSERT INTO credit_scores (user_id, score, risk_category, calculated_at) VALUES
(1, 687, 'BUONO', CURRENT_TIMESTAMP),
(2, 745, 'OTTIMO', CURRENT_TIMESTAMP),
(3, 620, 'DISCRETO', CURRENT_TIMESTAMP);

-- Inserimento richieste prestito di esempio (usando DATEADD per H2)
INSERT INTO loan_applications (user_id, loan_type, requested_amount, duration_months, monthly_income, employment_status, status, submitted_at, updated_at) VALUES
(1, 'PERSONAL_LOAN', 15000.00, 36, 2500.00, 'EMPLOYED', 'APPROVED', DATEADD('DAY', -7, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP),
(2, 'MORTGAGE', 200000.00, 240, 3500.00, 'EMPLOYED', 'PENDING', DATEADD('DAY', -2, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP),
(1, 'CAR_LOAN', 25000.00, 60, 2500.00, 'EMPLOYED', 'UNDER_REVIEW', DATEADD('DAY', -1, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP),
(3, 'BUSINESS_LOAN', 50000.00, 84, 4000.00, 'SELF_EMPLOYED', 'PENDING', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Inserimento prestiti approvati
INSERT INTO loans (application_id, user_id, principal_amount, interest_rate, duration_months, monthly_payment, remaining_balance, start_date, end_date, status, created_at, updated_at) VALUES
(1, 1, 15000.00, 8.50, 36, 472.81, 12500.00, '2024-01-01', '2027-01-01', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Inserimento pagamenti di esempio
INSERT INTO payments (loan_id, amount, principal_paid, interest_paid, remaining_balance, payment_date, due_date, status, created_at) VALUES
(1, 472.81, 366.31, 106.50, 14633.69, '2024-02-01', '2024-02-01', 'COMPLETED', CURRENT_TIMESTAMP),
(1, 472.81, 368.89, 103.92, 14264.80, '2024-03-01', '2024-03-01', 'COMPLETED', CURRENT_TIMESTAMP),
(1, 472.81, 371.50, 101.31, 13893.30, '2024-04-01', '2024-04-01', 'COMPLETED', CURRENT_TIMESTAMP),
(1, 472.81, 374.13, 98.68, 13519.17, '2024-05-01', '2024-05-01', 'COMPLETED', CURRENT_TIMESTAMP),
(1, 472.81, 376.79, 96.02, 13142.38, '2024-06-01', '2024-06-01', 'COMPLETED', CURRENT_TIMESTAMP);

-- Inserimento documenti di esempio
INSERT INTO documents (application_id, document_type, file_name, file_path, verified, uploaded_at) VALUES
(1, 'BUSTA_PAGA', 'busta_paga_mario.pdf', '/uploads/docs/busta_paga_mario.pdf', true, CURRENT_TIMESTAMP),
(1, 'DOCUMENTO_IDENTITA', 'carta_identita_mario.pdf', '/uploads/docs/carta_identita_mario.pdf', true, CURRENT_TIMESTAMP),
(2, 'BUSTA_PAGA', 'busta_paga_lucia.pdf', '/uploads/docs/busta_paga_lucia.pdf', false, CURRENT_TIMESTAMP),
(3, 'DOCUMENTO_IDENTITA', 'patente_giovanni.pdf', '/uploads/docs/patente_giovanni.pdf', true, CURRENT_TIMESTAMP);