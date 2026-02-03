# 💇‍♂️ Salon Management System

A full-stack web application to manage salon operations such as appointments, services, products, suppliers, billing, and authentication — built with Spring Boot and designed with real-world deployment and security practices.

## 🚀 Features

### 👤 Customer Side
- **Browse salon services** with details
- **Book appointments** online
- **Secure login** using Google OAuth
- **Mobile-friendly** responsive UI

### 🛠️ Admin Side
- **Manage services**, products, and inventory
- **Supplier & purchase order** management
- **View bookings** and operational data
- **Secure role-based access**

### 🔐 Security
- **Google OAuth 2.0** authentication
- **Secrets managed** via environment variables
- **GitHub-safe configuration** (no hard-coded secrets)

## 🧰 Tech Stack

| Layer | Technology |
|---|---|
| **Backend** | Java, Spring Boot, Spring Security |
| **Frontend** | HTML5, CSS3, JavaScript (Vanilla) |
| **Database** | MySQL |
| **ORM** | Hibernate / JPA |
| **Authentication** | Google OAuth 2.0 |
| **Build Tool** | Maven |
| **Containerization** | Docker & Docker Compose |

## 📁 Project Structure

```text
salon-management-system
├── src/main/java
│   └── com.salon.management
├── src/main/resources
│   ├── application.properties
│   └── static/
├── docker-compose.yml
├── Dockerfile
├── pom.xml
└── README.md
```

## ⚙️ Environment Variables (IMPORTANT)

This project uses environment variables for secrets.

### Required Variables
```properties
DB_USERNAME=your_db_username
DB_PASSWORD=your_db_password

GOOGLE_CLIENT_ID=your_google_client_id
GOOGLE_CLIENT_SECRET=your_google_client_secret
```

> [!CAUTION]
> **Never commit secrets to GitHub**. Use OS / Docker / Cloud environment variables instead.

## 🐳 Quick Start (Docker – Recommended)

1. **Build the JAR**
   ```sh
   ./mvnw clean package -DskipTests
   ```

2. **Run with Docker Compose**
   ```sh
   docker-compose up --build
   ```

3. **Access Application**
   Open [http://localhost:8080](http://localhost:8080)

## 💻 Quick Start (Local Development)

1. **Set Environment Variables (Windows)**
   ```cmd
   setx DB_USERNAME root
   setx DB_PASSWORD your_password
   setx GOOGLE_CLIENT_ID your_client_id
   setx GOOGLE_CLIENT_SECRET your_client_secret
   ```
   *Restart terminal / IDE after setting variables.*

2. **Run the Application**
   - Open project in IDE
   - Run `SalonManagementApplication.java`

3. **Access Application**
   Open [http://localhost:8080](http://localhost:8080)

## 🌍 Deployment (Production)

This application is deployment-ready and can be hosted on platforms like:
- Render
- Railway
- AWS / EC2
- Docker-based servers

### Production Checklist
- [ ] Set environment variables on server
- [ ] Use cloud MySQL (Railway / PlanetScale)
- [ ] Update Google OAuth redirect URI: `https://your-domain.com/login/oauth2/code/google`
