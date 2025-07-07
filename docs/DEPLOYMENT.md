# Guida Deployment LoanTech Solutions

## Prerequisiti

### Ambiente Sviluppo
- Java 17+
- Node.js 18+
- Maven 3.8+
- Git

### Ambiente Produzione
- Docker & Docker Compose
- PostgreSQL 15+
- Nginx (optional)
- SSL Certificate

## Setup Locale

### 1. Clone Repository
```bash
git clone https://github.com/username/loantech-solutions.git
cd loantech-solutions
```

### 2. Setup Backend
```bash
cd backend
./mvnw clean install
./mvnw spring-boot:run
```

Il backend sarà disponibile su: http://localhost:8080

### 3. Setup Frontend
```bash
cd frontend
npm install
npm start
```

Il frontend sarà disponibile su: http://localhost:3000

### 4. Verifica Setup
- Frontend: http://localhost:3000
- Backend API: http://localhost:8080/api
- H2 Console: http://localhost:8080/h2-console
    - JDBC URL: `jdbc:h2:mem:loantech`
    - Username: `sa`
    - Password: (vuota)

## Deployment con Docker

### 1. Build Images
```bash
# Backend
cd backend
./mvnw clean package
docker build -t loantech-backend .

# Frontend
cd frontend
npm run build
docker build -t loantech-frontend .
```

### 2. Deploy con Docker Compose
```bash
docker-compose up -d
```

### 3. Verifica Deployment
```bash
docker-compose ps
docker-compose logs backend
docker-compose logs frontend
```

## Deployment Produzione

### 1. Configurazione Database
```bash
# Setup PostgreSQL
sudo apt update
sudo apt install postgresql postgresql-contrib

sudo -u postgres createuser --interactive loantech
sudo -u postgres createdb loantech -O loantech
```

### 2. Configurazione Ambiente
```bash
# File: .env
SPRING_PROFILES_ACTIVE=production
DATABASE_URL=jdbc:postgresql://localhost:5432/loantech
DATABASE_USERNAME=loantech
DATABASE_PASSWORD=your_secure_password
JWT_SECRET=your_jwt_secret_key
```

### 3. Build Produzione
```bash
# Backend
./mvnw clean package -Pprod

# Frontend
npm run build
```

### 4. Deploy con Nginx
```nginx
# /etc/nginx/sites-available/loantech
server {
    listen 80;
    server_name your-domain.com;

    location / {
        root /var/www/loantech/build;
        try_files $uri $uri/ /index.html;
    }

    location /api {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

### 5. SSL Setup (Let's Encrypt)
```bash
sudo apt install certbot python3-certbot-nginx
sudo certbot --nginx -d your-domain.com
```

## Monitoraggio

### Health Checks
- Backend: http://localhost:8080/actuator/health
- Database connection test
- API response time monitoring

### Logging
```bash
# Application logs
tail -f /var/log/loantech/application.log

# Database logs
tail -f /var/log/postgresql/postgresql-15-main.log

# Nginx logs
tail -f /var/log/nginx/access.log
```

### Backup Strategy
```bash
# Database backup
pg_dump loantech > backup_$(date +%Y%m%d_%H%M%S).sql

# Automated backup script
#!/bin/bash
BACKUP_DIR="/backups/loantech"
DATE=$(date +%Y%m%d_%H%M%S)
pg_dump loantech > $BACKUP_DIR/loantech_$DATE.sql
find $BACKUP_DIR -name "*.sql" -mtime +7 -delete
```

## Troubleshooting

### Problemi Comuni

#### Backend non si avvia
```bash
# Verifica Java version
java -version

# Verifica configurazione database
./mvnw spring-boot:run --debug

# Check logs
tail -f logs/application.log
```

#### Frontend errori build
```bash
# Clear cache
npm cache clean --force
rm -rf node_modules package-lock.json
npm install

# Verifica versione Node
node --version
npm --version
```

#### Database connection issues
```bash
# Test connessione PostgreSQL
psql -h localhost -U loantech -d loantech

# Verifica configurazione
cat backend/src/main/resources/application-production.properties
```

### Performance Tuning

#### Backend
```properties
# application-production.properties
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
spring.datasource.hikari.maximum-pool-size=20
server.tomcat.max-threads=200
```

#### Database
```sql
-- PostgreSQL tuning
-- postgresql.conf
shared_buffers = 256MB
effective_cache_size = 1GB
work_mem = 4MB
```