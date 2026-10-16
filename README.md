# PayMyFee - Institutional Fee Management System

PayMyFee is a full-stack web application built to digitize student fee transactions, administrative oversight, and financial record-keeping for colleges and educational institutions. It was developed to eliminate manual paperwork, long office queues, and untracked fee payment receipts.

---

## The Motivation Behind This Project

Anyone who has stood in line at a university accounts department knows the friction: receipts get misplaced, account ledgers disagree, students are anxious about whether their fees were cleared before exams, and administrative staff are burdened with manual reconciliation.

PayMyFee was built to address that exact problem. The goal was simple: create a clean, dependable system where teachers can assign and review student dues, students can process payments transparently, and administrators can track real-time collection metrics without digging through spreadsheets.

---

## Core Capabilities

- **Real-Time Administrative Dashboard:** Live KPI cards for total enrolled students, faculty headcount, total revenue collected, and system health status. It includes a live recent-transactions table that refreshes automatically.
- **Student Directory & Roster:** View complete student rosters with instant balance inspection, enroll new students, search by ID or name, update personal details, and delete obsolete entries.
- **Faculty Management:** Onboard teachers and department heads, look up instructors by ID or name, view faculty rosters, and assign supervisory responsibilities.
- **Fee Payment Processor:** Enter student and teacher identifiers, specify the payment installment, compute the updated balance automatically, and generate an on-screen transaction receipt with unique payment identifiers.
- **Fee Allocation & Adjustments:** Faculty can set total fee amounts or revise existing fee balances for individual students.
- **Live API Response Inspector:** Every module includes a side-by-side JSON output viewer, giving developers and administrators full transparency into the exact payload returned by the server.

---

## Architectural & Engineering Decisions

### 1. Spring Security Without Browser Dialog Hijacking
In many standard Spring Security setups using HTTP Basic Authentication, an invalid login or 401 response triggers the `WWW-Authenticate: Basic` header. Web browsers intercept this header and display an intrusive native modal prompt over the screen, forcing the user to type credentials a second time.

To solve this, PayMyFee configures a custom `AuthenticationEntryPoint` in `SecurityConfig.java`. It returns a structured JSON 401 response without the `WWW-Authenticate` challenge header. This allows the custom login page to handle authentication cleanly in JavaScript and display friendly on-page alerts.

### 2. Sanitized Date of Birth Passwords
By design, default passwords for newly enrolled students and teachers are derived from their birthdates (`ddMMyyyy`). In real-world data entry, dates often arrive with hyphens or slashes (such as `15-08-2005` or `2005/08/15`). Both the service layer and the custom `PasswordEncoder` strip non-digit characters so that users can log in smoothly whether they type `15082005` or `15-08-2005`.

### 3. Case-Insensitive DTO Mapping
In Java and JavaScript, variable naming often creates edge-case bugs (such as `fatherName` vs `fathername`, or `studentId` vs `studentid`). Jackson `@JsonAlias` annotations were placed on all request DTOs so the API reliably deserializes incoming JSON regardless of client-side casing variations.

### 4. Resilient Exception Handling
Instead of throwing generic 500 Internal Server Errors when a record does not exist, the global exception handler inspects runtime errors and maps them to appropriate HTTP status codes:
- HTTP 404 for missing students or teachers.
- HTTP 400 for invalid inputs, zero-dollar payments, or negative balances.
- HTTP 500 for uncaught system exceptions.

---

## System Architecture

```text
       Browser / Client (Desktop & Mobile)
                       |
       +---------------+---------------+
       |                               |
Static Web Pages                Fetch API Calls
(HTML5, CSS3, JS)          (Headers: Basic Base64)
                                       |
                                       v
                       Spring Security Filter Chain
                       |-- Static Assets (permitAll)
                       |-- CORS & CSRF Configured
                       \-- REST APIs (authenticated)
                                       |
                                       v
                              REST Controllers
                 +-------------+-------+-----+------------+
                 |             |             |            |
             Student        Teacher     FeePayment    FeeUpdate
                 |             |             |            |
                 +-------------+-------+-----+------------+
                                       |
                                       v
                                 Service Layer
                      (Validation, Password Logic, DTOs)
                                       |
                                       v
                             Spring Data JPA Repos
                                       |
                                       v
                                MySQL Database
                        (student, teacher, feehistory)
```

---

## Tech Stack

### Backend
- **Java:** JDK 21 / 25
- **Framework:** Spring Boot, Spring Security, Spring Data JPA, Spring Web MVC
- **ORM / Persistence:** Hibernate
- **Database:** MySQL 8.0+
- **Build System:** Apache Maven with Lombok annotation processor

### Frontend
- **Markup & Styling:** Vanilla HTML5, CSS3 (custom responsive design system)
- **Scripting:** ES6+ JavaScript using native Fetch API
- **Design Philosophy:** Minimalist typography, card-based layouts, responsive breakpoints, high accessibility, zero emoji dependencies.

---

## Frontend Page Directory

All frontend templates are stored under `src/main/resources/static/` and share the master stylesheet `styles.css`:

| Page Path | Purpose |
| :--- | :--- |
| `index.html` | Public landing page introducing PayMyFee, feature overviews, and payment workflow preview. |
| `Login/Login.html` | Authentication portal validating against `/api/auth/login` and saving session tokens. |
| `dashboard.html` | Central administrative command center with live metrics and recent transaction tables. |
| `students/students.html` | Complete student management: live roster, enrollment form, ID/name lookups, updates, and deletion. |
| `teachers/teachers.html` | Faculty directory: live staff roster, registration form, ID/name lookups, updates, and deletion. |
| `feepayment/feepayment.html` | Fee collection interface with real-time amount preview and digital receipt generation. |
| `feeupdate/feeupdate.html` | Teacher fee assignment interface with balance preview and audit confirmation card. |

---

## User Authentication & Accounts

The system supports both built-in administrative accounts and dynamic database-driven credentials:

### Pre-Configured Accounts
| Username | Password | Assigned Role | Description |
| :--- | :--- | :--- | :--- |
| `admin` | `admin123` | ROLE_ADMIN | Full administrative access |
| `user` | `pwd` | ROLE_ADMIN | Default credentials from `application.properties` |
| `teacher` | `teacher123` | ROLE_TEACHER | Pre-configured faculty testing account |
| `student` | `student123` | ROLE_STUDENT | Pre-configured student testing account |

### Database-Driven Accounts
- **Teachers:** Any faculty member saved in the `teacher` table can sign in using their **email** or **full name**. Their initial password is their birthdate in `ddMMyyyy` format (e.g. `20051988`). If flagged as `isprincipal`, they receive administrative privileges.
- **Students:** Enrolled students can sign in using their **email** or **student name**. Their default password is their birthdate in `ddMMyyyy` format (e.g. `15082005`).

---

## REST API Reference

All protected endpoints require an `Authorization: Basic <base64>` header.

### 1. Authentication Endpoints

#### Verify Credentials
- **Method:** `GET`
- **Path:** `/api/auth/login`
- **Success Response (200 OK):**
  ```json
  {
    "status": "success",
    "username": "admin",
    "roles": ["ROLE_ADMIN"],
    "message": "User authenticated successfully"
  }
  ```

#### Current Session Info
- **Method:** `GET`
- **Path:** `/api/auth/me`

---

### 2. Dashboard Analytics

#### Real-Time Institutional Statistics
- **Method:** `GET`
- **Path:** `/api/dashboard/stats`
- **Success Response (200 OK):**
  ```json
  {
    "totalStudents": 24,
    "totalTeachers": 6,
    "totalFeesCollected": 185000.0,
    "totalPayments": 14,
    "systemStatus": "Online",
    "recentTransactions": [
      {
        "paymentId": 102,
        "studentId": 4,
        "studentName": "Rahul Sharma",
        "teacherName": "Dr. Priya Mehta",
        "amount": 5000.0,
        "date": "2026-10-16",
        "time": "11:45:00"
      }
    ]
  }
  ```

---

### 3. Student Management

#### Get All Students
- **Method:** `GET`
- **Path:** `/student/all`
- **Success Response (200 OK):** Array of all registered students.

#### Enroll Student
- **Method:** `POST`
- **Path:** `/student/addstudent`
- **Request Body:**
  ```json
  {
    "studentname": "Rahul Sharma",
    "fathername": "Rajesh Sharma",
    "email": "rahul.sharma@example.com",
    "feebalance": 45000,
    "birthdate": "2005-08-15"
  }
  ```
- **Success Response (201 Created):** Created student record with generated password and ID.

#### Get Student by ID
- **Method:** `GET`
- **Path:** `/student/getstudent/{id}`

#### Get Student by Name
- **Method:** `GET`
- **Path:** `/student/getstudent/byname?name=Rahul%20Sharma`

#### Update Student Details
- **Method:** `PUT`
- **Path:** `/student/updatestudent/{id}`
- **Request Body:**
  ```json
  {
    "studentname": "Rahul Kumar Sharma",
    "fatherName": "Rajesh Kumar Sharma"
  }
  ```

#### Delete Student
- **Method:** `DELETE`
- **Path:** `/student/deletestudent/{id}`

---

### 4. Teacher Management

#### Get All Teachers
- **Method:** `GET`
- **Path:** `/teachers/all`

#### Register Teacher
- **Method:** `POST`
- **Path:** `/teachers/addteacher`
- **Request Body:**
  ```json
  {
    "teachername": "Dr. Priya Mehta",
    "email": "priya.mehta@university.edu",
    "phone": 9876543210,
    "birthdate": "1988-05-20",
    "isprincipal": false
  }
  ```

#### Get Teacher by ID
- **Method:** `GET`
- **Path:** `/teachers/getteacher/{id}`

#### Get Teacher by Name
- **Method:** `GET`
- **Path:** `/teachers/getteacher/name?name=Dr.%20Priya%20Mehta`

#### Update Teacher Record
- **Method:** `PUT`
- **Path:** `/teachers/update/{id}`

#### Delete Teacher
- **Method:** `DELETE`
- **Path:** `/teachers/deleteteacher/{id}`

---

### 5. Fee Collection & Updates

#### Process Fee Payment
- **Method:** `POST`
- **Path:** `/paymyfee/student/student/{studentId}/feepayment`
- **Request Body:**
  ```json
  {
    "studentId": 1,
    "teacherId": 1,
    "amount": 5000.00
  }
  ```
- **Success Response (200 OK):**
  ```json
  {
    "feepaymentId": 101,
    "studentId": 1,
    "teacherId": 1,
    "amount": 5000.0,
    "newfeebalanceafterpayment": 40000.0,
    "date": "2026-10-16",
    "time": "11:30:15"
  }
  ```

#### Assign or Update Fee Balance
- **Method:** `POST`
- **Path:** `/teacherupdate/student/{studentId}/fee`
- **Request Body:**
  ```json
  {
    "studentid": 1,
    "teacherid": 1,
    "studentname": "Rahul Sharma",
    "email": "rahul.sharma@example.com",
    "feebalance": 45000
  }
  ```

---

## Database Schema

The database uses three normalized relational tables automatically maintained by Hibernate (`spring.jpa.hibernate.ddl-auto=update`):

```sql
-- 1. Faculty / Administrator Table
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

-- 3. Fee History & Transaction Ledger
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

## Setup & Running Locally

### Prerequisites
- JDK 21 or 25 installed and available on your PATH.
- MySQL 8.0+ service running locally.
- Apache Maven (or use the included `./mvnw` script).

### 1. Database Setup
Log in to MySQL and create the database schema:
```sql
CREATE DATABASE paymyfee;
```

Update your database credentials in `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://127.0.0.1:3306/paymyfee
spring.datasource.username=root
spring.datasource.password=YOUR_MYSQL_PASSWORD
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

spring.security.user.name=user
spring.security.user.password=pwd
```

### 2. Compile the Project
```bash
./mvnw clean compile -DskipTests
```

### 3. Run the Spring Boot Server
```bash
./mvnw spring-boot:run
```

### 4. Open in Your Browser
- Public Landing Page: `http://localhost:8080/index.html`
- Sign-In Portal: `http://localhost:8080/Login/Login.html`
- Administrative Dashboard: `http://localhost:8080/dashboard.html`

Sign in using `admin` / `admin123` or `user` / `pwd` to begin exploring.

---

## Author & Project Info

- **Developer:** Tarakeswar Reddy
- **Academic Program:** B.Tech in Computer Science and Engineering
- **Institution:** Lovely Professional University, Phagwara, India
- **Repository:** https://github.com/Tarakreddy011/FMS.git

Copyright 2026 PayMyFee. Built for dependable institutional record management.
