# Architettura Sistema LoanTech Solutions

## Overview

LoanTech Solutions implementa una architettura moderna basata su microservizi con API REST, seguendo i principi di Clean Architecture e Domain Driven Design.

## Stack Tecnologico

### Backend
- **Java 17**: Linguaggio di programmazione principale
- **Spring Boot 3.1.0**: Framework applicativo
- **Spring Security**: Gestione autenticazione e autorizzazione
- **Spring Data JPA**: Layer di persistenza
- **H2 Database**: Database in-memory per sviluppo
- **Maven**: Build tool e dependency management

### Frontend
- **React 18.2.0**: Library per UI components
- **React Router 6**: Gestione routing client-side
- **Axios**: HTTP client per API calls
- **CSS3**: Styling con responsive design

## Architettura a Layer

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

## Design Patterns

### Backend Patterns
- **Repository Pattern**: Separazione logica accesso dati
- **DTO Pattern**: Transfer objects per API
- **Builder Pattern**: Costruzione oggetti complessi
- **Strategy Pattern**: Calcolo tassi interesse

### Frontend Patterns
- **Component Pattern**: Componenti React riutilizzabili
- **Custom Hooks**: Logic sharing tra componenti
- **Context Pattern**: State management globale
- **HOC Pattern**: Higher-Order Components per auth

## Database Design

### Entità Principali
- **User**: Gestione utenti registrati
- **LoanApplication**: Richieste prestito
- **Loan**: Prestiti attivi
- **Payment**: Cronologia pagamenti
- **CreditScore**: Valutazione creditizia

### Relazioni
- User 1:N LoanApplication
- LoanApplication 1:1 Loan
- User 1:1 CreditScore
- Loan 1:N Payment

## Security Architecture

### Autenticazione
- Password hashing con BCrypt
- Sessione gestita via localStorage
- CORS configuration per cross-origin

### Autorizzazione
- Route protection lato client
- API endpoint protection
- User context validation

## API Design

### REST Principles
- Resource-based URLs
- HTTP methods semantici
- Status codes appropriati
- JSON content type

### Endpoint Structure
```
/api/auth/*          - Autenticazione
/api/users/*         - Gestione utenti
/api/loan-applications/* - Richieste prestito
/api/loans/*         - Gestione prestiti
```

## Scalabilità

### Horizontal Scaling
- Stateless API design
- Database connection pooling
- Load balancer ready

### Performance
- Lazy loading JPA entities
- Client-side caching
- Optimized database queries