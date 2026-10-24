# PayMyFee - Institutional Fee Management System

PayMyFee is a full-stack academic web application created to simplify educational fee transactions, faculty supervision, and administrative accounting for schools and universities. It replaces traditional manual receipts, mismatched ledgers, and office queues with a secure, role-segregated digital platform.

---

## Why PayMyFee Exists

Every academic term, students and parents face the stress of verifying fee deadlines, queuing at accounts counters, and waiting for manual confirmation receipts. At the same time, faculty mentors often lack clear visibility into which students in their cohort have pending dues, and accounts administrators struggle with manual reconciliations between ledgers and bank deposits.

PayMyFee was developed to solve this friction:
- **Students** have a private, dedicated space where they see only their own fee balance, inspect their assigned mentor, make instant fee payments, and view verified receipts.
- **Faculty** can monitor their assigned student cohort, check payment standings, adjust fee allocations, and view institutional rosters.
- **The Principal & Administrators** have an executive Control Center to manage student-teacher mentor relationships, allocate fees, oversee faculty workloads, and track real-time collection metrics.

---

## Role-Based Access Control (RBAC) Matrix

Security and privacy boundaries are enforced at both the Spring Security filter layer and the frontend routing level.

| Capability / Resource | Student (`ROLE_STUDENT`) | Teacher (`ROLE_TEACHER`) | Principal / Admin (`ROLE_ADMIN`) |
| :--- | :---: | :---: | :---: |
| Access Public Landing Page | Yes | Yes | Yes |
| View Personal Profile & Due Balance | Yes (`/student/me`) | Yes (`/teachers/me`) | Yes |
| Inspect Assigned Faculty Mentor | Yes | N/A | Yes |
| Make Fee Payment for Self | Yes (`/student/me/pay`) | N/A | Yes |
| View Personal Payment Receipts | Yes (`/student/me/payments`) | N/A | Yes |
| View Assigned Student Cohort | No (Blocked 403) | Yes (`/teachers/my-students`) | Yes |
| Adjust / Assign Student Fee Balance | No (Blocked 403) | Yes (`/teacherupdate/**`) | Yes (`/api/principal/student/{id}/fee`) |
| View Institution-wide Student Roster | No (Blocked 403) | Yes (`/student/all`) | Yes |
| Manage Student-Teacher Relationships | No (Blocked 403) | No (Blocked 403) | Yes (`/api/principal/assign-mentor`) |
| Onboard New Students or Teachers | No (Blocked 403) | No (Blocked 403) | Yes |
| Delete Student or Teacher Records | No (Blocked 403) | No (Blocked 403) | Yes |
| Access Principal Command Center | No (Blocked 403) | No (Blocked 403) | Yes (`/principal-dashboard.html`) |

---

## Dedicated Entities & Roles

1. **`Principal` (`principal` table):**
   - Independent dedicated entity representing the top administrative authority (Principal / Admin).
   - Fields: `principalId`, `name`, `username`, `email`, `password`, `phone`, `role` (`ROLE_ADMIN`).
   - Loaded and authenticated via `PrincipalRepo` in `CustomUserDetailsService`.
2. **`PrincipalEntity` / Teacher (`teacher` table):**
   - Clean entity representing faculty members and mentors.
   - Removed legacy `isprincipal` flag; all faculty members operate strictly under `ROLE_TEACHER`.
3. **`StudentEntity` (`student` table):**
   - Represents enrolled students, linked to their faculty mentor via `@ManyToOne private PrincipalEntity teacher;`.

---

## Core Technical Solutions & Enhancements

### 1. Accurate Fee Calculation & Transaction Integrity
Previous versions suffered from type mismatches (`int` in DTOs vs `double` in database entities) and unrounded floating-point subtractions that produced fractional anomalies or negative balances.

**The Solution:**
- **Standardized Precision:** All monetary amounts (`feebalance`, `amount`, and `newfeebalanceafterpayment`) now consistently use `double` across all entities, repositories, and DTOs.
- **Strict Two-Decimal Rounding:** All balance adjustments enforce financial rounding using `Math.round(val * 100.0) / 100.0`.
- **Atomic Database Commits:** `@Transactional` annotations on `FeeRecipt` and `FeeUpdateService` ensure that student account balances and `FeeHistory` payment slips commit or roll back together atomically.
- **Null-Safe Fee Adjustments:** In `FeeUpdateService`, when adjusting fees without specifying a new teacher ID, the student's existing teacher is safely preserved rather than throwing `The given id must not be null`.
- **Overpayment Safeguards:** The payment engine prevents transactions where `paymentAmount > currentBalance`, returning a descriptive validation error instead of negative balances.
- **Automatic Faculty Linking:** When a student settles a fee, the system automatically resolves and credits their assigned mentor from the student record, eliminating manual ID entry errors.

### 2. Spring Security Without Browser Dialog Hijacking
Standard HTTP Basic Authentication triggers the browser's native credential modal prompt when a 401 response contains `WWW-Authenticate: Basic`. This creates an intrusive user experience where users are prompted twice.

**The Solution:**
- Configured a custom `AuthenticationEntryPoint` in `SecurityConfig.java` that returns clean JSON responses (`{"status":401,"error":"Unauthorized"}`) without the challenge header.
- Configured a dedicated `AccessDeniedHandler` returning clean JSON HTTP 403 Forbidden responses when users attempt actions outside their role permissions.

### 3. Sanitized Date of Birth Credentials
Newly enrolled students and faculty have default passwords generated from their birth date (`ddMMyyyy`). In real-world data entry, dates often contain separators (e.g., `15-08-2005` or `15/08/2005`).
- The enrollment services sanitize stored birthdate passwords using `replaceAll("[^0-9]", "")`.
- A resilient `PasswordEncoder` strips non-alphanumeric characters during credential matching, allowing users to log in smoothly regardless of separator formatting.

---

## Dedicated Frontend Portals

### 1. Principal Control Center (`principal-dashboard.html`)
- **Executive Metrics Ribbon:** Total Enrolled Students, Faculty Mentors, Total Fees Collected, and Outstanding Fee Deficit.
- **Campus Fee Recovery Index:** Live animated recovery bar showing percentage of billed tuition collected.
- **Student-Teacher Mentorship Hub:** Interactive pairing roster with dropdown mentor selection for each student, instant assignment, and "Show Unassigned Only" toggle.
- **Institutional Fee Management:** Direct fee balance updates for any student without requiring teacher ID entry.
- **Faculty Supervision Panel:** Workload distribution table showing mentee count and total cohort fees for each teacher, plus onboard/remove actions.
- **Real-Time Financial Audit Stream:** Live feed of verified student payment slips.

### 2. Student Portal (`student-portal.html`)
- **Outstanding Balance Display:** Prominent highlight card displaying real-time due fees in formatted Indian Rupees.
- **Assigned Mentor Card:** Displays the name, ID, email, and contact number of the student's assigned supervisor.
- **Fee Payment Box:** Lets students enter an installment or select quick amounts ("Pay Full Outstanding", "+₹1,000", "+₹5,000") with instant balance recalculation.
- **Personal Receipt Ledger:** Tabular history of previous payment slips with receipt numbers, amounts, dates, and verification tags.
- **Zero Exposure:** Completely isolated from administrative controls and other students' information.

### 3. Faculty Portal (`teacher-portal.html`)
- **Assigned Cohort Table:** View all students mentored by the faculty member, their fee status, and quick-action fee adjust buttons.
- **Quick Fee Adjustment Modal:** Allows faculty to assign or modify student fee balances directly.
- **Institution Student Roster:** Searchable table of all students across the institution for academic coordination.

---

## Installation & Setup Guide

### 1. Database Preparation
Start your local MySQL service and execute:
```sql
CREATE DATABASE paymyfee;
```

Check `src/main/resources/application.properties` and verify connection parameters:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/paymyfee?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
```

### 2. Build the Application
In the `FeeManagementSystem` folder, build the project using the Maven wrapper:
```powershell
.\mvnw.cmd clean compile
```

### 3. Run the Server
Launch the Spring Boot application:
```powershell
.\mvnw.cmd spring-boot:run
```
The server will initialize on port `8080`.

### 4. Default Logins
| Role | Identifier | Password | Destination Portal |
| :--- | :--- | :--- | :--- |
| **Principal (Administrator)** | `principal` | `principal123` | `principal-dashboard.html` |
| **Built-in Admin** | `admin` | `admin123` | `principal-dashboard.html` |
| **Default User** | `user` | `pwd` | `principal-dashboard.html` |
| **Faculty Member** | `teacher` | `teacher123` | `teacher-portal.html` |
| **Student** | `student` | `student123` | `student-portal.html` |

---

## Author & Project Credits

- **Developer:** Tarakeswar Reddy
- **Academic Affiliation:** B.Tech Computer Science & Engineering, Lovely Professional University
- **Location:** Phagwara, Punjab, India
- **License:** Open educational use. All rights reserved.
