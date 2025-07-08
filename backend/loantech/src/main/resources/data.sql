-- File: src/main/resources/data.sql
-- Dati di test per LoanTech Solutions
-- Password: "password123" hashata correttamente con BCrypt

-- Inserimento utenti di test
INSERT INTO users (email, password_hash, first_name, last_name, fiscal_code, phone_number, date_of_birth, created_at, updated_at)
VALUES
('mario.rossi@email.com', '$2a$10$Jf9sbslL8Xh.wY/YRQz2/OYs/6iFtEkH5fpOdiIHTxrU5KHIiTaxu', 'Mario', 'Rossi', 'RSSMRA85M01H501X', '+39 3331234567', '1985-08-15', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('lucia.bianchi@email.com', '$2a$10$Jf9sbslL8Xh.wY/YRQz2/OYs/6iFtEkH5fpOdiIHTxrU5KHIiTaxu', 'Lucia', 'Bianchi', 'BNCLCU90F45H501Y', '+39 3339876543', '1990-06-05', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('giorgio.verdi@email.com', '$2a$10$Jf9sbslL8Xh.wY/YRQz2/OYs/6iFtEkH5fpOdiIHTxrU5KHIiTaxu', 'Giorgio', 'Verdi', 'VRDGRG88H20H501Z', '+39 3335551234', '1988-06-20', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('anna.ferrari@email.com', '$2a$10$Jf9sbslL8Xh.wY/YRQz2/OYs/6iFtEkH5fpOdiIHTxrU5KHIiTaxu', 'Anna', 'Ferrari', 'FRRANN92L30H501W', '+39 3337778899', '1992-07-30', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Inserimento credit scores per gli utenti
INSERT INTO credit_scores (user_id, score, risk_category, calculated_at)
VALUES
(1, 750, 'BASSO', CURRENT_TIMESTAMP),
(2, 680, 'MEDIO', CURRENT_TIMESTAMP),
(3, 820, 'BASSO', CURRENT_TIMESTAMP),
(4, 590, 'ALTO', CURRENT_TIMESTAMP);

-- Inserimento richieste prestito (NOMI CAMPO CORRETTI)
INSERT INTO loan_applications (user_id, loan_type, requested_amount, duration_months, monthly_income, employment_status, status, submitted_at, updated_at)
VALUES
(1, 'PERSONAL', 15000.00, 36, 2500.00, 'DIPENDENTE', 'APPROVED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'AUTO', 25000.00, 60, 1800.00, 'DIPENDENTE', 'PENDING', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'MORTGAGE', 250000.00, 240, 4000.00, 'DIPENDENTE', 'APPROVED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 'PERSONAL', 5000.00, 24, 1200.00, 'AUTONOMO', 'UNDER_REVIEW', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Inserimento prestiti attivi (NOMI CAMPO CORRETTI)
INSERT INTO loans (application_id, user_id, principal_amount, interest_rate, duration_months, monthly_payment, remaining_balance, start_date, end_date, status, created_at, updated_at)
VALUES
(1, 1, 15000.00, 4.5, 36, 451.58, 12500.00, '2024-01-15', '2027-01-15', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 3, 250000.00, 2.8, 240, 1189.43, 235000.00, '2024-02-01', '2044-02-01', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Inserimento pagamenti (NOMI CAMPO CORRETTI)
INSERT INTO payments (loan_id, amount, principal_paid, interest_paid, remaining_balance, payment_date, due_date, status, created_at)
VALUES
-- Pagamenti per il prestito personale di Mario (loan_id = 1)
(1, 451.58, 389.08, 62.50, 14610.92, '2024-02-15', '2024-02-15', 'COMPLETED', CURRENT_TIMESTAMP),
(1, 451.58, 390.53, 61.05, 14220.39, '2024-03-15', '2024-03-15', 'COMPLETED', CURRENT_TIMESTAMP),
(1, 451.58, 392.00, 59.58, 13828.39, '2024-04-15', '2024-04-15', 'COMPLETED', CURRENT_TIMESTAMP),
(1, 451.58, 393.47, 58.11, 13434.92, '2024-05-15', '2024-05-15', 'COMPLETED', CURRENT_TIMESTAMP),

-- Pagamenti per il mutuo di Giorgio (loan_id = 2)
(2, 1189.43, 606.09, 583.34, 249393.91, '2024-03-01', '2024-03-01', 'COMPLETED', CURRENT_TIMESTAMP),
(2, 1189.43, 607.51, 581.92, 248786.40, '2024-04-01', '2024-04-01', 'COMPLETED', CURRENT_TIMESTAMP),
(2, 1189.43, 608.92, 580.51, 248177.48, '2024-05-01', '2024-05-01', 'COMPLETED', CURRENT_TIMESTAMP);