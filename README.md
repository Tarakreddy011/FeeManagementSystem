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
| Onboard New Students | No (Blocked 403) | Yes (assigned to the authenticated teacher) | Yes (mentor optional) |
| Onboard New Teachers | No (Blocked 403) | No (Blocked 403) | Yes |
| Delete Student or Teacher Records | No (Blocked 403) | No (Blocked 403) | Yes |
| Access Principal Command Center | No (Blocked 403) | No (Blocked 403) | Yes (React frontend) |

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
- **Strict Two-Decimal Rounding:** Payment amounts and resulting balances are rounded to cents using `Math.round(val * 100.0) / 100.0`; amounts that round to zero are rejected.
- **Atomic Database Commits:** `@Transactional` annotations on `FeeRecipt` and `FeeUpdateService` ensure that student account balances and `FeeHistory` payment slips commit or roll back together atomically.
- **Null-Safe Fee Adjustments:** In `FeeUpdateService`, when adjusting fees without specifying a new teacher ID, the student's existing teacher is safely preserved rather than throwing `The given id must not be null`.
- **Overpayment Safeguards:** The payment engine prevents transactions where `paymentAmount > currentBalance`, returning a descriptive validation error instead of negative balances.
- **Automatic Faculty Linking:** When a student settles a fee, the system credits their assigned mentor from the student record, ignoring any conflicting mentor ID in the payment request.

### 2. Spring Security Without Browser Dialog Hijacking
Standard HTTP Basic Authentication triggers the browser's native credential modal prompt when a 401 response contains `WWW-Authenticate: Basic`. This creates an intrusive user experience where users are prompted twice.

**The Solution:**
- Configured a custom `AuthenticationEntryPoint` in `SecurityConfig.java` that returns clean JSON responses (`{"status":401,"error":"Unauthorized"}`) without the challenge header.
- Configured a dedicated `AccessDeniedHandler` returning clean JSON HTTP 403 Forbidden responses when users attempt actions outside their role permissions.

### 3. Sanitized Date of Birth Credentials
Newly enrolled students and faculty have default passwords generated from their birth date (`ddMMyyyy`). In real-world data entry, dates often contain separators (e.g., `15-08-2005` or `15/08/2005`).
- The enrollment services sanitize stored birthdate passwords using `replaceAll("[^0-9]", "")`.
- A resilient `PasswordEncoder` strips non-alphanumeric characters during credential matching, allowing users to log in smoothly regardless of separator formatting.

### 4. Student Enrollment and Initial Fees
- Teachers can enroll students from their student roster. The backend assigns each new student to the authenticated teacher, regardless of any teacher ID in the request.
- Administrators can enroll students and optionally select a mentor.
- Both enrollment forms require an initial fee balance of at least one cent. It is rounded to two decimals and recorded as outstanding due, not as a payment or receipt; dashboard totals and mentor cohort balances include it automatically.

---

## Application Architecture

- The complete user interface is maintained in the separate [PayMyFee React frontend](https://github.com/Tarakreddy011/FMS).
- The Spring Boot project in this repository is the API backend. React sends authentication, student, teacher, payment, and principal requests to the backend at `http://localhost:8080` by default.
- The backend no longer serves the legacy portal UI. Its `src/main/resources/static` folder contains only small fallback/status pages for local troubleshooting; they are not the application frontend.
- Backend authorization remains the security boundary. React route guards only control navigation and do not replace Spring Security.

---

## Run Locally

Start MySQL, then start the Spring Boot backend and React frontend in separate terminals. The detailed Windows/PowerShell instructions are in [docs/RunApplication.md](docs/RunApplication.md).

- Backend API: `http://localhost:8080`
- React frontend: `http://localhost:5173`
- Backend fallback/API status page: `http://localhost:8080/api-status.html`

The backend defaults to MySQL at `127.0.0.1:3306`, schema `paymyfee`, user `root`. Set `DB_PASSWORD` in the backend terminal when starting; do not put it in tracked files.

### Default Logins
| Role | Identifier | Password | Destination Portal |
| :--- | :--- | :--- | :--- |
| **Principal (Administrator)** | `principal` | `principal123` | Principal React portal |
| **Built-in Admin** | `admin` | `admin123` | Principal React portal |
| **Default User** | `user` | `pwd` | Principal React portal |
| **Faculty Member** | `teacher` | `teacher123` | Teacher React portal |
| **Student** | `student` | `student123` | Student React portal |

---

## Author & Project Credits

- **Developer:** Tarakeswar Reddy
- **Academic Affiliation:** B.Tech Computer Science & Engineering, Lovely Professional University
- **Location:** Phagwara, Punjab, India
- **License:** Open educational use. All rights reserved.
