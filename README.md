# 🎓 PayMyFee - Modern Fee Management System

A full-stack, secure, and responsive Fee Management System designed for educational institutions to streamline student record management, faculty management, fee allocation, payment processing, and instant receipt generation.

---

## 📌 Table of Contents
- [Overview](#-overview)
- [Key Features](#-key-features)
- [System Architecture](#-system-architecture)
- [Tech Stack](#-tech-stack)
- [Frontend Design System](#-frontend-design-system)
- [Spring Security & User Authentication](#-spring-security--user-authentication)
- [REST API Reference](#-rest-api-reference)
  - [Authentication](#authentication-apis)
  - [Student Management](#student-management-apis)
  - [Teacher Management](#teacher-management-apis)
  - [Fee Payment](#fee-payment-apis)
  - [Fee Allocation & Updates](#fee-allocation--update-apis)
- [Database Schema](#-database-schema)
- [Installation & Getting Started](#-installation--getting-started)
- [Recent Bug Fixes & Improvements](#-recent-bug-fixes--improvements)
- [Author & Acknowledgements](#-author--acknowledgements)

---

## 🌟 Overview

**PayMyFee** simplifies institutional finance. It replaces manual fee registers with a centralized web platform featuring:
- Role-aware administrative dashboard and quick KPI statistics.
- Interactive student & teacher CRUD workflows with live JSON inspectors.
- Real-time fee payment processing with instant, formatted digital receipts.
- Teacher fee balance assignment with confirmation summaries.
- Spring Security with hybrid authentication supporting pre-configured admin accounts and database-backed student/faculty accounts.

---

## ✨ Key Features

- **Responsive Multi-Page Portal:** Handcrafted modern UI following an integrated design system (`styles.css`), compatible across desktops, tablets, and mobile devices.
- **Instant Digital Receipts:** Generates timestamped payment receipts with balance calculation after every fee payment.
- **Live Response Inspector:** Embedded JSON preview panels on all operation pages for immediate feedback and developer transparency.
- **Robust Security:** Spring Security configuration with HTTP Basic Auth, CORS configuration, CSRF protection tailored for REST endpoints, and `sessionStorage` credential management.
- **Global Error Handling:** Structured exception responses with correct HTTP status codes (400 Bad Request, 404 Not Found, 500 Internal Server Error).

---

## 🏗️ System Architecture

```text
       Browser / Client (Desktop / Mobile)
                       │
       ┌───────────────┴───────────────┐
       ▼                               ▼
Static UI Pages                   Fetch API Calls
(HTML5, CSS3, JS)            (Basic Auth: btoa(user:pwd))
                                       │
                                       ▼
                       Spring Security Filter Chain
                       ├── Static Assets (permitAll)
                       ├── CORS / CSRF Disabled for REST
                       └── API Endpoints (authenticated)
                                       │
                                       ▼
                              REST Controllers
                 ┌─────────────┬───────┴─────┬────────────┐
                 ▼             ▼             ▼            ▼
             Student        Teacher     FeePayment    FeeUpdate
                 │             │             │            │
                 └─────────────┼─────────────┼────────────┘
                               ▼
                          Service Layer
                (Validation, Business Logic, DTO Mapping)
                               │
                               ▼
                       Spring Data JPA Repos
                               │
                               ▼
                         MySQL Database
                 (student, teacher, feehistory)
```

---

## 🛠️ Tech Stack

### Backend
- **Language:** Java 21 / 25
- **Framework:** Spring Boot, Spring Security, Spring Data JPA, Spring Web MVC
- **Data Persistence:** Hibernate ORM
- **Database:** MySQL
- **Build Tool:** Maven (with `maven-compiler-plugin` & Lombok annotation processing)
- **Utilities:** Lombok, Jackson

### Frontend
- **Languages:** HTML5, CSS3 (Vanilla custom design system), JavaScript (ES6+ Fetch API)
- **Design Elements:** Glassmorphism, CSS Grid & Flexbox, live previews, tabbed interfaces, responsive breakpoints.

---

## 🎨 Frontend Design System

All frontend pages share a unified design language established in [`index.html`](src/main/resources/static/index.html) and [`styles.css`](src/main/resources/static/styles.css):

| Page | File Location | Purpose & Capabilities |
| :--- | :--- | :--- |
| **Landing Page** | [`src/main/resources/static/index.html`](src/main/resources/static/index.html) | Brand introduction, feature cards, live fee sample widget, and direct links to the login portal. |
| **Login Portal** | [`src/main/resources/static/Login/Login.html`](src/main/resources/static/Login/Login.html) | Glassmorphic login card, authenticates against `/api/auth/login`, stores session credentials. |
| **Dashboard** | [`src/main/resources/static/dashboard.html`](src/main/resources/static/dashboard.html) | KPI metrics overview, current user & role badge, quick links to all operations, and 1-click Sign Out. |
| **Student Management** | [`src/main/resources/static/students/students.html`](src/main/resources/static/students/students.html) | 5 operation tabs: Add Student, Get by ID, Get by Name, Update Student, and Delete Student. |
| **Teacher Management** | [`src/main/resources/static/teachers/teachers.html`](src/main/resources/static/teachers/teachers.html) | 5 operation tabs: Add Teacher, Get by ID, Get by Name, Update Teacher, and Delete Teacher. |
| **Fee Payment** | [`src/main/resources/static/feepayment/feepayment.html`](src/main/resources/static/feepayment/feepayment.html) | Payment processing form, live amount preview, and auto-generated digital receipt slip. |
| **Fee Update** | [`src/main/resources/static/feeupdate/feeupdate.html`](src/main/resources/static/feeupdate/feeupdate.html) | Teacher fee balance allocation form, balance preview banner, and audit confirmation summary. |

---

## 🔐 Spring Security & User Authentication

### Default & Test Accounts
The application includes pre-configured administrative accounts as well as database-driven authentication:

| Username | Password | Role | Description |
| :--- | :--- | :--- | :--- |
| `admin` | `admin123` | `ROLE_ADMIN` | Primary administrative account |
| `user` | `pwd` | `ROLE_ADMIN` | Configured via `application.properties` |
| `teacher` | `teacher123` | `ROLE_TEACHER` | Built-in test faculty account |
| `student` | `student123` | `ROLE_STUDENT` | Built-in test student account |

### Database-Driven Accounts
- **Faculty / Teachers:** Teachers registered in the `teacher` table can log in using their **email** or **full name**; their initial password is their birthdate in `yyyy-MM-dd` format (e.g. `1990-05-20`). If `isprincipal` is `true`, they receive `ROLE_ADMIN`.
- **Students:** Students registered in the `student` table can log in using their **email** or **student name**; their initial password is their birthdate in `ddMMyyyy` format (e.g. `15082002`).

---

## 🔄 REST API Reference

All protected endpoints require HTTP Basic Authentication (`Authorization: Basic <base64(user:password)>`).

### Authentication APIs

#### 1. Verify Login Credentials
- **Endpoint:** `GET /api/auth/login`
- **Response `200 OK`:**
  ```json
  {
    "status": "success",
    "username": "admin",
    "roles": ["ROLE_ADMIN"],
    "message": "User authenticated successfully"
  }
  ```

#### 2. Get Current Session User
- **Endpoint:** `GET /api/auth/me`

---

### Student Management APIs

#### 1. Add Student
- **Endpoint:** `POST /student/addstudent`
- **Request Body:**
  ```json
  {
    "studentname": "Rahul Sharma",
    "fathername": "Rajesh Sharma",
    "email": "rahul@example.com",
    "feebalance": 45000,
    "birthdate": "2002-08-15"
  }
  ```
- **Response `201 Created`:** Returns created student object with generated password and ID.

#### 2. Get Student by ID
- **Endpoint:** `GET /student/getstudent/{id}`
- **Response `200 OK`:** Returns student details; `404 Not Found` if the record does not exist.

#### 3. Get Student by Name
- **Endpoint:** `GET /student/getstudent/byname?name=Rahul%20Sharma`
- **Response `200 OK`:** Returns student entity matching name.

#### 4. Update Student
- **Endpoint:** `PUT /student/updatestudent/{id}`
- **Request Body:**
  ```json
  {
    "studentname": "Rahul S. Sharma",
    "fatherName": "Rajesh Kumar Sharma"
  }
  ```
- **Response `200 OK`:** Returns updated student details.

#### 5. Delete Student
- **Endpoint:** `DELETE /student/deletestudent/{id}`
- **Response `200 OK`:** Returns deleted student entity.

---

### Teacher Management APIs

#### 1. Register Teacher
- **Endpoint:** `POST /teachers/addteacher`
- **Request Body:**
  ```json
  {
    "teachername": "Dr. Priya Mehta",
    "email": "priya.mehta@university.edu",
    "phone": 9876543210,
    "birthdate": "1985-04-12",
    "isprincipal": false
  }
  ```
- **Response `201 Created`:** Returns registered teacher profile.

#### 2. Get Teacher by ID
- **Endpoint:** `GET /teachers/getteacher/{id}`
- **Response `200 OK`:** Returns teacher details; `404 Not Found` if missing.

#### 3. Get Teacher by Name
- **Endpoint:** `GET /teachers/getteacher/name?name=Dr.%20Priya%20Mehta`
- **Response `200 OK`:** Returns teacher record matching name.

#### 4. Update Teacher
- **Endpoint:** `PUT /teachers/update/{id}`
- **Request Body:**
  ```json
  {
    "teachername": "Prof. Priya Mehta",
    "phone": 9811122233,
    "isprincipal": true
  }
  ```
- **Response `200 OK`:** Returns updated profile.

#### 5. Delete Teacher
- **Endpoint:** `DELETE /teachers/deleteteacher/{id}`
- **Response `200 OK`:** Returns deletion confirmation message and ID.

---

### Fee Payment APIs

#### Process Fee Payment
- **Endpoint:** `POST /paymyfee/student/student/{studentId}/feepayment`
- **Request Body:**
  ```json
  {
    "studentId": 1,
    "teacherId": 1,
    "amount": 5000.00
  }
  ```
- **Response `200 OK`:**
  ```json
  {
    "feepaymentId": 101,
    "studentId": 1,
    "teacherId": 1,
    "amount": 5000.0,
    "newfeebalanceafterpayment": 40000.0,
    "date": "2026-10-16",
    "time": "11:05:00"
  }
  ```

---

### Fee Allocation & Update APIs

#### Assign / Update Student Fee Balance
- **Endpoint:** `POST /teacherupdate/student/{studentId}/fee`
- **Request Body:**
  ```json
  {
    "studentid": 1,
    "teacherid": 1,
    "studentname": "Rahul Sharma",
    "email": "rahul@example.com",
    "feebalance": 45000
  }
  ```
- **Response `200 OK`:** Returns updated student fee confirmation.

---

## 🗄️ Database Schema

The database utilizes three relational tables created via Hibernate (`ddl-auto=update`):

```sql
-- 1. Teacher / Principal Table
CREATE TABLE teacher (
    teacherid INT AUTO_INCREMENT PRIMARY KEY,
    teachername VARCHAR(255),
    email VARCHAR(255),
    phone INT,
    birthdate DATE,
    password VARCHAR(255),
    isprincipal VARCHAR(255)
);

-- 2. Student Table
CREATE TABLE student (
    studentid INT AUTO_INCREMENT PRIMARY KEY,
    studentname VARCHAR(255),
    fathername VARCHAR(255),
    email VARCHAR(255),
    password VARCHAR(255),
    birthdate DATE,
    feebalance DOUBLE,
    teacher_id INT,
    FOREIGN KEY (teacher_id) REFERENCES teacher(teacherid)
);

-- 3. Fee History / Transaction Table
CREATE TABLE feehistory (
    feepayment_id INT AUTO_INCREMENT PRIMARY KEY,
    amount DOUBLE,
    date DATE,
    time TIME,
    student_id INT,
    teacher_id INT,
    FOREIGN KEY (student_id) REFERENCES student(studentid),
    FOREIGN KEY (teacher_id) REFERENCES teacher(teacherid)
);
```

---

## ⚙️ Installation & Getting Started

### Prerequisites
- **Java Development Kit (JDK):** Version 21 or higher
- **Maven:** Version 3.8+ (or use the included `./mvnw`)
- **MySQL Database Server:** 8.0+

### 1. Database Configuration
Create your MySQL database:
```sql
CREATE DATABASE paymyfee;
```

Update your connection credentials in [`src/main/resources/application.properties`](src/main/resources/application.properties):
```properties
spring.datasource.url=jdbc:mysql://127.0.0.1:3306/paymyfee
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# Application Admin Credentials
spring.security.user.name=user
spring.security.user.password=pwd
```

### 2. Build the Project
```bash
./mvnw clean compile -DskipTests
```

### 3. Run the Application
```bash
./mvnw spring-boot:run
```

### 4. Access the Web Application
Open your browser and navigate to:
- **Landing Page:** `http://localhost:8080/index.html`
- **Login Portal:** `http://localhost:8080/Login/Login.html`
- **Admin Dashboard:** `http://localhost:8080/dashboard.html`

Sign in with default credentials `admin` / `admin123` or `user` / `pwd`.

---

## 🐛 Recent Bug Fixes & Improvements

1. **Compiler & Lombok Processor:** Configured `maven-compiler-plugin` (v3.13.0) with explicit Lombok annotation processing paths in `pom.xml`, resolving Java 21/25 compilation issues.
2. **Missing `@RequestBody` in Student Controller:** Added missing `@RequestBody` to `updateStudentByID`, enabling proper JSON payload deserialization.
3. **Teacher by Name Lookup:** Fixed `getTeacherbyname` returning the Spring Security context object instead of the actual `PrincipalEntity` from the database.
4. **Service Null Safety:** Replaced unchecked `.findById().get()` calls across `StudentService` and `TeacherService` with `.orElseThrow()` to prevent `NoSuchElementException` crashes on non-existent records.
5. **Removed `@Test` Annotation from Production Code:** Cleaned up `FeeUpdateService` which had a `@Test` annotation on a production service method.
6. **Case-Insensitive DTO Mapping:** Added `@JsonAlias` to `StudentUpdate`, `AddFeeUpdateRequestDTO`, and `RequestFeePaymentDTO` to seamlessly support multiple casing styles (`fathername`, `fatherName`, `studentId`, `studentid`).
7. **Accurate Global Exception Status Codes:** Updated `GlobalExecption` handler to return HTTP 404 for "Not Found", HTTP 400 for validation errors, and HTTP 500 for internal server errors.
8. **Navigation & Route Integrity:** Standardized all static page links and fixed `script.js` to route cleanly to `/Login/Login.html`.

---

## 👨‍💻 Author & Acknowledgements

- **Author:** Tarakeswar Reddy
- **Degree:** B.Tech Computer Science and Engineering
- **Institution:** Lovely Professional University, Phagwara, India
- **Repository:** [https://github.com/Tarakreddy011/FMS.git](https://github.com/Tarakreddy011/FMS.git)

---
*© 2026 PayMyFee. Simple, secure and smarter fee payments.*
