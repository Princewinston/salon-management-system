# Salon Management System

A comprehensive web application for managing salon appointments, services, products, and billing.

## Features
- **User Booking**: Customers can browse services, view details, and book appointments.
- **Admin Dashboard**: Manage products, suppliers, purchase orders, and view analytics.
- **Authentication**: Secure login/signup with Google OAuth support.
- **Responsive Design**: Works on desktop and mobile.

## Tech Stack
- **Backend**: Java Spring Boot
- **Frontend**: HTML5, CSS3, JavaScript (Vanilla)
- **Database**: MySQL
- **Containerization**: Docker & Docker Compose

## Quick Start (Docker)
1. **Build the JAR**:
   ```sh
   ./mvnw clean package -DskipTests
   ```
2. **Run with Docker Compose**:
   ```sh
   docker-compose up --build
   ```
3. **Access**: `http://localhost:8080`

## Quick Start (Local)
1. Configure `application.properties` with your local MySQL credentials.
2. Run `SalonManagementApplication.java` in your IDE.

## License
MIT
