# 📊 Diagrammi del Sistema LoanTech Solutions

## Indice
- [Diagramma delle Classi UML](#diagramma-delle-classi-uml)
- [Diagramma Entità-Relazioni](#diagramma-entità-relazioni-er)
- [Architettura del Sistema](#architettura-del-sistema)
- [Diagramma dei Casi d'Uso](#diagramma-dei-casi-duso)
- [Sequence Diagrams](#sequence-diagrams)

---

## Diagramma delle Classi UML

### Overview
Il diagramma delle classi rappresenta la struttura object-oriented del sistema, evidenziando le entità del dominio e le loro relazioni.

```mermaid
classDiagram
    class User {
        -Long userId
        -String email
        -String passwordHash
        -String firstName
        -String lastName
        -String fiscalCode
        -String phoneNumber
        -LocalDate dateOfBirth
        -LocalDateTime createdAt
        +register(RegisterRequest) User
        +login(LoginRequest) boolean
        +updateProfile(UserUpdateRequest) void
        +getLoanApplications() List~LoanApplication~
        +getCreditScore() CreditScore
    }
    
    class LoanApplication {
        -Long applicationId
        -Long userId
        -LoanType loanType
        -BigDecimal requestedAmount
        -Integer durationMonths
        -BigDecimal monthlyIncome
        -String employmentStatus
        -ApplicationStatus status
        -LocalDateTime submittedAt
        -LocalDateTime processedAt
        +submitApplication() void
        +updateStatus(ApplicationStatus) void
        +calculateMonthlyPayment(BigDecimal) BigDecimal
        +approve() Loan
        +reject() void
    }
    
    class Loan {
        -Long loanId
        -Long applicationId
        -Long userId
        -BigDecimal principalAmount
        -BigDecimal interestRate
        -Integer durationMonths
        -BigDecimal monthlyPayment
        -BigDecimal remainingBalance
        -LocalDate startDate
        -LocalDate endDate
        -LoanStatus status
        +makePayment(BigDecimal) Payment
        +calculateAmortization() List~AmortizationEntry~
        +getPaymentHistory() List~Payment~
        +checkOverduePayments() List~Payment~
    }
    
    class Payment {
        -Long paymentId
        -Long loanId
        -BigDecimal amount
        -BigDecimal principalPaid
        -BigDecimal interestPaid
        -BigDecimal remainingBalance
        -LocalDate paymentDate
        -LocalDate dueDate
        -PaymentStatus status
        +processPayment() void
        +markAsOverdue() void
        +calculateLateFee() BigDecimal
        +reversePayment() void
    }
    
    class Document {
        -Long documentId
        -Long applicationId
        -String documentType
        -String fileName
        -String filePath
        -Boolean verified
        -LocalDateTime uploadedAt
        +uploadDocument(MultipartFile) void
        +verifyDocument() void
        +deleteDocument() void
        +downloadDocument() byte[]
    }
    
    class CreditScore {
        -Long scoreId
        -Long userId
        -Integer score
        -String riskCategory
        -LocalDateTime calculatedAt
        +calculateScore() Integer
        +updateScore(Integer) void
        +getRiskAssessment() String
        +getScoreHistory() List~CreditScore~
    }
    
    User ||--o{ LoanApplication : "submits"
    LoanApplication ||--o| Loan : "becomes"
    Loan ||--o{ Payment : "has"
    LoanApplication ||--o{ Document : "requires"
    User ||--o| CreditScore : "has"
```

### Relazioni Principali
- **User ↔ LoanApplication**: Un utente può avere multiple richieste prestito
- **LoanApplication ↔ Loan**: Una richiesta approvata diventa un prestito
- **Loan ↔ Payment**: Un prestito ha multiple rate di pagamento
- **User ↔ CreditScore**: Ogni utente ha un credit score

---

## Diagramma Entità-Relazioni (ER)

### Overview
Il modello dati relazionale ottimizzato per performance e integrità referenziale.

```mermaid
erDiagram
    USERS {
        bigint user_id PK "Primary Key"
        varchar email UK "Unique constraint"
        varchar password_hash "BCrypt hash"
        varchar first_name "Nome utente"
        varchar last_name "Cognome utente"
        varchar fiscal_code UK "Codice fiscale italiano"
        varchar phone_number "Telefono"
        date date_of_birth "Data nascita"
        timestamp created_at "Data registrazione"
        timestamp updated_at "Ultimo aggiornamento"
    }
    
    LOAN_APPLICATIONS {
        bigint application_id PK "Primary Key"
        bigint user_id FK "Foreign Key -> USERS"
        varchar loan_type "PERSONAL_LOAN, MORTGAGE, CAR_LOAN, BUSINESS_LOAN"
        decimal requested_amount "Importo richiesto (15,2)"
        int duration_months "Durata in mesi"
        decimal monthly_income "Reddito mensile (15,2)"
        varchar employment_status "Stato lavorativo"
        varchar status "PENDING, APPROVED, REJECTED"
        timestamp submitted_at "Data invio richiesta"
        timestamp processed_at "Data elaborazione"
    }
    
    LOANS {
        bigint loan_id PK "Primary Key"
        bigint application_id FK "Foreign Key -> LOAN_APPLICATIONS"
        bigint user_id FK "Foreign Key -> USERS"
        decimal principal_amount "Capitale prestito (15,2)"
        decimal interest_rate "Tasso interesse (5,2)"
        int duration_months "Durata mesi"
        decimal monthly_payment "Rata mensile (15,2)"
        decimal remaining_balance "Saldo residuo (15,2)"
        date start_date "Data inizio prestito"
        date end_date "Data fine prestito"
        varchar status "ACTIVE, PAID_OFF, DEFAULTED"
        timestamp created_at "Data creazione"
        timestamp updated_at "Ultimo aggiornamento"
    }
    
    PAYMENTS {
        bigint payment_id PK "Primary Key"
        bigint loan_id FK "Foreign Key -> LOANS"
        decimal amount "Importo pagamento (15,2)"
        decimal principal_paid "Quota capitale (15,2)"
        decimal interest_paid "Quota interessi (15,2)"
        decimal remaining_balance "Saldo dopo pagamento (15,2)"
        date payment_date "Data pagamento effettivo"
        date due_date "Data scadenza rata"
        varchar status "PENDING, COMPLETED, FAILED, OVERDUE"
        timestamp created_at "Data registrazione"
    }
    
    DOCUMENTS {
        bigint document_id PK "Primary Key"
        bigint application_id FK "Foreign Key -> LOAN_APPLICATIONS"
        varchar document_type "BUSTA_PAGA, DOCUMENTO_IDENTITA, etc."
        varchar file_name "Nome file originale"
        varchar file_path "Path storage file"
        boolean verified "Documento verificato"
        timestamp uploaded_at "Data upload"
    }
    
    CREDIT_SCORES {
        bigint score_id PK "Primary Key"
        bigint user_id FK "Foreign Key -> USERS"
        int score "Punteggio 300-850"
        varchar risk_category "OTTIMO, BUONO, DISCRETO, SCARSO, PESSIMO"
        timestamp calculated_at "Data calcolo"
    }
    
    USERS ||--o{ LOAN_APPLICATIONS : "user_id"
    LOAN_APPLICATIONS ||--o| LOANS : "application_id"
    LOANS ||--o{ PAYMENTS : "loan_id"
    LOAN_APPLICATIONS ||--o{ DOCUMENTS : "application_id"
    USERS ||--o| CREDIT_SCORES : "user_id"
```

### Vincoli di Integrità
- **Primary Keys**: Auto-incrementali per tutte le tabelle
- **Foreign Keys**: Constraint di integrità referenziale
- **Unique Constraints**: Email e codice fiscale unici
- **Check Constraints**: Validazione range per importi e score

---

## Architettura del Sistema

### Layered Architecture

```mermaid
graph TB
    subgraph "🎨 Presentation Layer"
        A[React Components]
        B[React Router]
        C[State Management]
        D[CSS Responsive]
    end
    
    subgraph "🌐 API Gateway Layer"
        E[Spring Controllers]
        F[Security Filters]
        G[CORS Config]
        H[Error Handlers]
    end
    
    subgraph "💼 Business Logic Layer"
        I[UserService]
        J[LoanApplicationService]
        K[LoanService]
        L[PaymentService]
    end
    
    subgraph "🔍 Data Access Layer"
        M[JPA Repositories]
        N[Entity Managers]
        O[Transaction Managers]
    end
    
    subgraph "🗄️ Database Layer"
        P[(H2 Database)]
        Q[(PostgreSQL)]
    end
    
    A --> E
    B --> E
    C --> E
    D --> E
    E --> I
    E --> J
    E --> K
    E --> L
    I --> M
    J --> M
    K --> M
    L --> M
    M --> P
    M --> Q
    N --> P
    N --> Q
    O --> P
    O --> Q
```

---

## Diagramma dei Casi d'Uso

```mermaid
graph LR
    subgraph "LoanTech System"
        UC1[Registrazione Utente]
        UC2[Login/Logout]
        UC3[Visualizza Dashboard]
        UC4[Simula Prestito]
        UC5[Richiedi Prestito]
        UC6[Carica Documenti]
        UC7[Visualizza Prestiti]
        UC8[Effettua Pagamento]
        UC9[Visualizza Piano Ammortamento]
        UC10[Gestisci Credit Score]
        UC11[Approva Richieste]
        UC12[Gestisci Utenti]
    end
    
    Cliente --> UC1
    Cliente --> UC2
    Cliente --> UC3
    Cliente --> UC4
    Cliente --> UC5
    Cliente --> UC6
    Cliente --> UC7
    Cliente --> UC8
    Cliente --> UC9
    Cliente --> UC10
    
    Operatore --> UC11
    Amministratore --> UC12
```

---

## Sequence Diagrams

### Processo Richiesta Prestito

```mermaid
sequenceDiagram
    participant U as User
    participant F as Frontend
    participant API as Controllers
    participant S as Services
    participant DB as Database
    
    U->>F: Compila form richiesta
    F->>API: POST /loan-applications
    API->>S: LoanApplicationService.create()
    S->>DB: Salva LoanApplication
    DB-->>S: Application ID
    S->>DB: Genera Credit Score
    S-->>API: LoanApplication
    API-->>F: Response 201 Created
    F-->>U: Conferma invio richiesta
    
    Note over S,DB: Workflow Approvazione
    S->>S: Valuta richiesta
    S->>DB: Aggiorna status
    S->>DB: Crea Loan (se approvata)
```

### Processo Pagamento

```mermaid
sequenceDiagram
    participant U as User
    participant F as Frontend
    participant API as Controllers
    participant LS as LoanService
    participant PS as PaymentService
    participant DB as Database
    
    U->>F: Inserisce importo pagamento
    F->>API: POST /loans/{id}/payments
    API->>LS: makePayment()
    LS->>PS: calculatePayment()
    PS->>PS: Calcola capitale/interessi
    PS->>DB: Salva Payment
    LS->>DB: Aggiorna saldo Loan
    DB-->>LS: Conferma transazione
    LS-->>API: Payment details
    API-->>F: Response 200 OK
    F-->>U: Conferma pagamento
```

---

## Note Tecniche

### Convenzioni di Naming
- **Classi**: PascalCase (es. `LoanApplication`)
- **Metodi**: camelCase (es. `calculateMonthlyPayment`)
- **Tabelle**: snake_case (es. `loan_applications`)
- **Colonne**: snake_case (es. `user_id`)

### Pattern Implementati
- **Repository Pattern**: Data access abstraction
- **DTO Pattern**: Data transfer objects
- **Builder Pattern**: Object construction
- **Strategy Pattern**: Different loan types
- **Observer Pattern**: Status change notifications