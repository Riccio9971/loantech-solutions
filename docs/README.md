# LoanTech Solutions 🏦

**Una piattaforma fintech moderna per la gestione digitale di prestiti e mutui**

[![Java](https://img.shields.io/badge/Java-17-orange?style=flat-square&logo=java)](https://openjdk.java.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.1.0-brightgreen?style=flat-square&logo=spring)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-18.2.0-blue?style=flat-square&logo=react)](https://reactjs.org/)
[![H2 Database](https://img.shields.io/badge/H2-Database-blue?style=flat-square)](https://www.h2database.com/)
[![License](https://img.shields.io/badge/License-Academic-green?style=flat-square)](LICENSE)

## 📋 Indice

- [Panoramica](#-panoramica)
- [Funzionalità](#-funzionalità)
- [Architettura](#-architettura)
- [Struttura](#-struttura-del-repository)
- [Tecnologie](#-tecnologie)
- [Installazione](#-installazione)
- [Configurazione](#-configurazione)
- [API Documentation](#-api-documentation)
- [Testing](#-testing)
- [Deployment](#-deployment)
- [Screenshots](#-screenshots)
- [Contribuire](#-contribuire)
- [Licenza](#-licenza)


- ### Diagramma delle Classi UML
![UML Class Diagram](docs/diagrams/uml-class-diagram.png)

### Diagramma ER
![ER Diagram](docs/diagrams/er-diagram.png)

## 🚀 Panoramica

**LoanTech Solutions** è una piattaforma fintech innovativa che digitalizza completamente il processo di richiesta, valutazione e gestione di prestiti e mutui. Sviluppata come project work universitario, dimostra l'implementazione di un sistema enterprise completo utilizzando tecnologie moderne.

### Contesto Aziendale

LoanTech Solutions opera come fintech specializzata in:
- **Prestiti Personali**: Finanziamenti fino a €100.000
- **Mutui Casa**: Soluzioni abitative con tassi competitivi
- **Prestiti Auto**: Finanziamenti dedicati all'acquisto di veicoli
- **Prestiti Business**: Supporto per PMI

### Valore Aggiunto

- ✅ **Processo 100% digitale**: Dalla richiesta all'erogazione
- ✅ **Valutazione automatizzata**: Credit scoring in tempo reale
- ✅ **Dashboard personalizzata**: Gestione completa dei propri prestiti
- ✅ **Simulatore avanzato**: Calcoli finanziari accurati

## ✨ Funzionalità

### 🔐 Gestione Utenti
- Registrazione sicura con validazione completa
- Sistema di login con password hashing (BCrypt)
- Profili utente con dati anagrafici e finanziari
- Credit scoring automatico

### 💰 Gestione Prestiti
- **Simulatore avanzato** con calcoli di ammortamento
- **Richiesta prestiti** con workflow di approvazione
- **Tipologie multiple**: Personale, Mutuo, Auto, Business
- **Tassi differenziati** per categoria

### 📊 Dashboard Intelligente
- Panoramica finanziaria personalizzata
- Statistiche prestiti attivi e richieste
- Visualizzazione credit score con categoria di rischio
- Grafici di progresso rimborsi

### 💳 Sistema Pagamenti
- Gestione rate mensili
- Calcolo automatico capitale/interessi
- Cronologia pagamenti dettagliata
- Piano di ammortamento completo

### 📈 Analytics e Reporting
- Metriche in tempo reale
- Report di performance
- Analisi del rischio creditizio
- Storico completo transazioni

## 🏗️ Architettura

### Pattern Architetturale
```
┌─────────────────────────────────────┐
│          Presentation Layer         │
│        (React Components)           │
├─────────────────────────────────────┤
│           API Gateway               │
│        (Spring Controllers)         │
├─────────────────────────────────────┤
│          Business Layer             │
│        (Spring Services)            │
├─────────────────────────────────────┤
│         Persistence Layer           │
│      (JPA Repositories)             │
├─────────────────────────────────────┤
│          Database Layer             │
│           (H2/PostgreSQL)           │
└─────────────────────────────────────┘
```

## 📁 Struttura del Repository

```
loantech-solutions/
├── README.md                           # Documentazione principale
├── .gitignore                          # File Git ignore
├── docker-compose.yml                  # Configurazione Docker
│
├── docs/                               # Documentazione progetto
│   ├── API_DOCUMENTATION.yaml         # Documentazione API Swagger
│   ├── ARCHITECTURE.md                # Architettura dettagliata
│   ├── DEPLOYMENT.md                  # Guida deployment
│   └── screenshots/                   # Screenshot applicazione
│       ├── dashboard.png
│       ├── simulator.png
│       ├── loans.png
│       └── mobile.png
│
├── backend/                           # Applicazione Spring Boot
│   ├── pom.xml                       # Dipendenze Maven
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/loantech/app/
│   │   │   │   ├── LoanTechApplication.java      # Main class
│   │   │   │   ├── config/                       # Configurazioni
│   │   │   │   │   ├── SecurityConfig.java
│   │   │   │   │   ├── WebConfig.java
│   │   │   │   │   └── JacksonConfig.java
│   │   │   │   ├── entity/                       # Entità JPA
│   │   │   │   │   ├── User.java
│   │   │   │   │   ├── LoanApplication.java
│   │   │   │   │   ├── Loan.java
│   │   │   │   │   ├── Payment.java
│   │   │   │   │   ├── Document.java
│   │   │   │   │   └── CreditScore.java
│   │   │   │   ├── repository/                   # Repository JPA
│   │   │   │   │   ├── UserRepository.java
│   │   │   │   │   ├── LoanApplicationRepository.java
│   │   │   │   │   ├── LoanRepository.java
│   │   │   │   │   ├── PaymentRepository.java
│   │   │   │   │   ├── DocumentRepository.java
│   │   │   │   │   └── CreditScoreRepository.java
│   │   │   │   ├── service/                      # Business Logic
│   │   │   │   │   ├── UserService.java
│   │   │   │   │   ├── LoanApplicationService.java
│   │   │   │   │   └── LoanService.java
│   │   │   │   ├── controller/                   # REST Controllers
│   │   │   │   │   ├── AuthController.java
│   │   │   │   │   ├── UserController.java
│   │   │   │   │   ├── LoanApplicationController.java
│   │   │   │   │   └── LoanController.java
│   │   │   │   ├── dto/                          # Data Transfer Objects
│   │   │   │   │   ├── ApiResponse.java
│   │   │   │   │   ├── RegisterRequest.java
│   │   │   │   │   ├── LoginRequest.java
│   │   │   │   │   ├── LoanApplicationRequest.java
│   │   │   │   │   ├── LoanSimulationRequest.java
│   │   │   │   │   └── PaymentRequest.java
│   │   │   │   └── enums/                        # Enumerazioni
│   │   │   │       ├── LoanType.java
│   │   │   │       ├── ApplicationStatus.java
│   │   │   │       ├── LoanStatus.java
│   │   │   │       └── PaymentStatus.java
│   │   │   └── resources/
│   │   │       ├── application.properties        # Configurazione app
│   │   │       └── data.sql                     # Dati iniziali
│   │   └── test/
│   │       └── java/com/loantech/app/
│   │           ├── LoanTechApplicationTests.java
│   │           ├── controller/                   # Test Controllers
│   │           │   ├── AuthControllerTest.java
│   │           │   └── LoanApplicationControllerTest.java
│   │           ├── service/                      # Test Services
│   │           │   ├── UserServiceTest.java
│   │           │   └── LoanApplicationServiceTest.java
│   │           └── repository/                   # Test Repository
│   │               └── UserRepositoryTest.java
│
├── frontend/                          # Applicazione React
│   ├── package.json                   # Dipendenze npm
│   ├── package-lock.json             # Lock file dipendenze
│   ├── public/                       # File pubblici
│   │   ├── index.html               # Template HTML principale
│   │   ├── manifest.json            # PWA manifest
│   │   ├── favicon.ico              # Favicon
│   │   ├── logo192.png             # Logo PWA 192x192
│   │   ├── logo512.png             # Logo PWA 512x512
│   │   └── robots.txt              # SEO robots
│   ├── src/
│   │   ├── index.js                # Entry point React
│   │   ├── index.css               # Stili globali
│   │   ├── App.js                  # Componente principale
│   │   ├── App.css                 # Stili App
│   │   ├── components/             # Componenti React
│   │   │   ├── Navbar.js
│   │   │   ├── Navbar.css
│   │   │   ├── Login.js
│   │   │   ├── Register.js
│   │   │   ├── Auth.css
│   │   │   ├── Dashboard.js
│   │   │   ├── Dashboard.css
│   │   │   ├── LoanSimulator.js
│   │   │   ├── LoanSimulator.css
│   │   │   ├── LoanApplication.js
│   │   │   ├── LoanApplication.css
│   │   │   ├── MyLoans.js
│   │   │   ├── MyLoans.css
│   │   │   ├── LoanDetails.js
│   │   │   └── LoanDetails.css
│   │   ├── services/               # API Services
│   │   │   └── api.js
│   │   └── utils/                  # Utility functions
│   │       ├── formatters.js
│   │       ├── validators.js
│   │       ├── constants.js
│   │       └── helpers.js
│   └── build/                      # Build produzione (generata)
│
└── .github/                        # GitHub Actions (opzionale)
    └── workflows/
        └── ci.yml                  # Pipeline CI/CD
```

### Design Patterns Implementati
- **Repository Pattern**: Separazione logica accesso dati
- **DTO Pattern**: Transfer objects per API sicure
- **Builder Pattern**: Costruzione oggetti complessi
- **Strategy Pattern**: Calcolo tassi per tipo prestito

### Entità del Sistema
- **User**: Gestione utenti registrati
- **LoanApplication**: Richieste prestito con workflow
- **Loan**: Prestiti attivi con calcoli finanziari
- **Payment**: Cronologia pagamenti e ammortamento
- **CreditScore**: Valutazione creditizia automatizzata
- **Document**: Gestione documentazione

## 🛠️ Tecnologie

### Backend
- **Java 17**: Linguaggio di programmazione
- **Spring Boot 3.1.0**: Framework applicativo
- **Spring Security**: Autenticazione e autorizzazione
- **Spring Data JPA**: Layer di persistenza
- **H2 Database**: Database in-memory per sviluppo
- **Maven**: Build tool e dependency management
- **BCrypt**: Hashing sicuro delle password

### Frontend
- **React 18.2.0**: Library per UI components
- **React Router 6**: Gestione routing client-side
- **Axios**: HTTP client per API calls
- **CSS3**: Styling moderno e responsive
- **PWA**: Progressive Web App capabilities

### DevOps & Tools
- **Git**: Version control
- **Maven**: Build automation
- **npm**: Package manager frontend
- **H2 Console**: Database administration
- **Swagger/OpenAPI**: API documentation

## 🚀 Installazione

### Prerequisiti

```bash
# Verifica versioni
java --version    # Java 17+
node --version    # Node.js 18+
npm --version     # npm 8+
git --version     # Git
```

### Clone Repository

```bash
git clone https://github.com/username/loantech-solutions.git
cd loantech-solutions
```

### Setup Backend

```bash
cd backend

# Installa dipendenze e compila
./mvnw clean install

# Avvia l'applicazione
./mvnw spring-boot:run
```

### Setup Frontend

```bash
cd frontend

# Installa dipendenze
npm install

# Avvia il server di sviluppo
npm start
```

## ⚙️ Configurazione

### Variabili d'Ambiente

```properties
# Backend (application.properties)
server.port=8080
spring.datasource.url=jdbc:h2:mem:loantech
spring.jpa.hibernate.ddl-auto=create-drop

# Frontend (.env)
REACT_APP_API_URL=http://localhost:8080/api
REACT_APP_ENV=development
```

### Database Configuration

Il sistema utilizza H2 in-memory per sviluppo:

```yaml
Database: loantech
URL: jdbc:h2:mem:loantech
Username: sa
Password: (vuota)
Console: http://localhost:8080/h2-console
```

### Dati di Test

Il sistema include utenti predefiniti:

```
Email: mario.rossi@email.com
Password: password123

Email: lucia.bianchi@email.com  
Password: password123
```

## 📚 API Documentation

### Endpoints Principali

#### Autenticazione
```http
POST /api/auth/register - Registrazione utente
POST /api/auth/login    - Login utente
```

#### Gestione Prestiti
```http
GET    /api/loan-applications/user/{userId} - Lista richieste
POST   /api/loan-applications               - Nuova richiesta
POST   /api/loan-applications/simulate      - Simulazione prestito
PUT    /api/loan-applications/{id}/approve  - Approva richiesta
```

#### Prestiti Attivi
```http
GET    /api/loans/user/{userId}            - Prestiti utente
GET    /api/loans/{loanId}                 - Dettagli prestito
POST   /api/loans/{loanId}/payments        - Effettua pagamento
GET    /api/loans/{loanId}/amortization    - Piano ammortamento
```

#### Dashboard e Utenti
```http
GET    /api/users/{userId}/dashboard       - Dashboard utente
GET    /api/users/{userId}/credit-score    - Credit score
```

### Swagger Documentation

La documentazione completa delle API è disponibile in:
- **File**: `docs/API_DOCUMENTATION.yaml`
- **Formato**: OpenAPI 3.0
- **Esempi**: Request/Response per ogni endpoint

## 🧪 Testing

### Backend Tests

```bash
cd backend

# Esegui tutti i test
./mvnw test

# Test specifici
./mvnw test -Dtest=UserServiceTest
./mvnw test -Dtest=LoanApplicationControllerTest
```

### Frontend Tests

```bash
cd frontend

# Esegui test unitari
npm test

# Test con coverage
npm test -- --coverage
```

### Test Funzionali

1. **Registrazione**: Nuovo utente con dati completi
2. **Login**: Autenticazione con credenziali valide
3. **Dashboard**: Caricamento dati personalizzati
4. **Simulatore**: Calcoli finanziari accurati
5. **Richiesta Prestito**: Workflow completo
6. **Gestione Pagamenti**: Calcolo rate e ammortamento

## 🚢 Deployment

### Ambiente di Sviluppo

```bash
# Backend
cd backend && ./mvnw spring-boot:run

# Frontend  
cd frontend && npm start
```

**URLs**:
- Frontend: http://localhost:3000
- Backend API: http://localhost:8080/api
- H2 Console: http://localhost:8080/h2-console

### Docker Deployment

```bash
# Build e avvio con Docker Compose
docker-compose up -d

# Verifica containers
docker-compose ps
```

### Produzione

Per il deployment in produzione vedere `docs/DEPLOYMENT.md`:
- Configurazione PostgreSQL
- Setup Nginx
- SSL/TLS con Let's Encrypt
- Environment variables
- Monitoring e logging

## 📸 Screenshots

### Dashboard Utente
![Dashboard](docs/screenshots/dashboard.png)
*Panoramica personalizzata con statistiche e credit score*

### Simulatore Prestiti
![Simulatore](docs/screenshots/simulator.png)
*Calcolo rate con breakdown dettagliato*

### Gestione Prestiti
![Prestiti](docs/screenshots/loans.png)
*Visualizzazione prestiti attivi e cronologia*

### Mobile Responsive
![Mobile](docs/screenshots/mobile.png)
*Design ottimizzato per dispositivi mobili*

## 📊 Metriche del Progetto

- **Linee di codice**: ~5,000+ (Backend + Frontend)
- **Entità JPA**: 6 entità principali
- **Endpoint API**: 15+ endpoint REST
- **Componenti React**: 12+ componenti
- **Test Coverage**: 80%+ (obiettivo)
- **Performance**: < 200ms response time

## 🔮 Roadmap Future

### Versione 2.0
- [ ] **Machine Learning**: Credit scoring predittivo
- [ ] **Open Banking**: Integrazione PSD2
- [ ] **Mobile App**: App nativa iOS/Android
- [ ] **Blockchain**: Smart contracts

### Versione 3.0
- [ ] **Multi-tenancy**: Supporto white-label
- [ ] **Marketplace**: Piattaforma multi-banca
- [ ] **API Pubbliche**: Ecosistema developer
- [ ] **Analytics AI**: Business intelligence avanzata

## 🤝 Contribuire

Questo progetto è sviluppato per scopi accademici. Per contribuire:

1. Fork del repository
2. Crea feature branch (`git checkout -b feature/nuova-funzionalita`)
3. Commit delle modifiche (`git commit -m 'Aggiunge nuova funzionalità'`)
4. Push al branch (`git push origin feature/nuova-funzionalita`)
5. Apri una Pull Request

### Code Standards

- **Java**: Google Java Style Guide
- **React**: Airbnb JavaScript Style Guide
- **Git**: Conventional Commits
- **Documentation**: JSDoc per JavaScript, Javadoc per Java

## 📄 Licenza

Questo progetto è sviluppato per scopi accademici nell'ambito del corso di [Nome Corso] presso [Nome Università].

**Autore**: [Il Tuo Nome]  
**Matricola**: [Numero Matricola]  
**Anno Accademico**: 2024/2025  
**Relatore**: [Nome Relatore]

---

## 📞 Contatti

- **Email**: [tua.email@università.it]
- **LinkedIn**: [Il Tuo Profilo]
- **GitHub**: [Il Tuo GitHub]

---

**⭐ Se questo progetto ti è stato utile, lascia una stella!**

---

*Ultimo aggiornamento: Gennaio 2025*