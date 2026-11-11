# 🏦 Mini Banking Application

A web-based Mini Banking Application developed using **Java, Spring Boot, MySQL, HTML, CSS and JavaScript**. This project provides basic banking operations with secure authentication and separate Admin and Customer functionalities.

## 🚀 Features

### 👤 Customer Features
- Customer Registration and Login
- JWT-based Authentication
- View Account Balance
- Deposit Money
- Withdraw Money
- Transfer Money Between Accounts
- View Transaction History
- Search and Filter Transactions
- Generate Transaction Receipts
- Download Account Statements using Print to PDF
- View and Update Profile
- Change Password

### 🔐 Admin Features
- Admin Login
- Admin Dashboard
- View Total Customers
- View Total Bank Balance
- View Total Transactions
- View Deposit, Withdrawal and Transfer Statistics
- View All Customers
- View All Banking Transactions
- Filter Transactions by ID, Type and Date
- Admin Profile Management
- Change Password

## 🛠️ Technologies Used

| Technology | Purpose |
|---|---|
| Java 21 | Backend Programming |
| Spring Boot 3.5.5 | Backend Framework |
| Spring Data JPA | Database Operations |
| Spring Security | Authentication and Authorization |
| JWT | Token-based Authentication |
| BCrypt | Password Encryption |
| MySQL | Database |
| HTML5 | Frontend Structure |
| CSS3 | Frontend Styling |
| JavaScript | Frontend Functionality |
| Maven | Dependency Management |
| Postman | API Testing |
| Eclipse STS | Development IDE |

## 🏗️ Project Architecture

```text
MiniBankingApp
│
├── src/main/java
│   └── com.example.minibanking
│       ├── config
│       ├── controller
│       ├── dto
│       ├── entity
│       ├── repository
│       ├── security
│       └── service
│
├── src/main/resources
│   ├── static
│   │   ├── index.html
│   │   ├── login.html
│   │   ├── register.html
│   │   ├── customer.html
│   │   ├── style.css
│   │   ├── script.js
│   │   └── customer.js
│   │
│   └── application.properties
│
├── pom.xml
├── README.md
└── .gitignore
```

## ⚙️ Installation and Setup

### 1. Clone the Repository

```bash
git clone https://github.com/YOUR-USERNAME/MiniBankingApp.git
```

### 2. Open the Project

- Open Eclipse STS.
- Import the project as an Existing Maven Project.
- Wait for Maven dependencies to download.

### 3. Create MySQL Database

Open MySQL and execute:

```sql
CREATE DATABASE bank;
```

### 4. Configure Environment Variables

Set the following environment variables:

```text
DB_USERNAME=your_mysql_username
DB_PASSWORD=your_mysql_password
JWT_SECRET=your_base64_encoded_secret
```

Do not commit actual passwords or JWT secrets to GitHub.

### 5. Run the Application

Run the main Spring Boot application:

```text
MiniBankingApp.java
```

The application runs on:

```text
http://localhost:8080
```

## 🌐 Application URLs

| Page | URL |
|---|---|
| Admin Dashboard | http://localhost:8080/index.html |
| Customer Login | http://localhost:8080/login.html |
| Customer Registration | http://localhost:8080/register.html |

## 🧪 API Testing

REST APIs can be tested using Postman.

Authentication is handled using JWT Bearer tokens.

## 🔒 Security

- BCrypt password hashing
- JWT-based authentication
- Role-based authorization
- Protected Admin APIs
- Environment-based database credentials and JWT secret

## 🎯 Project Objective

The main objective of this project is to understand and implement backend development, REST API creation, database connectivity, authentication, authorization and frontend-backend integration using Spring Boot.

## ⚠️ Disclaimer

This is an educational Mini Banking Application developed for learning purposes. Deposits, withdrawals and transfers are simulated operations. It is not connected to any real banking or payment system.

## 👩‍💻 Author

**Your Name**

GitHub: https://github.com/YOUR-USERNAME

---

⭐ If you find this project useful, consider giving the repository a star!
