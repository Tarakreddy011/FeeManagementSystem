# Run PayMyFee Locally

This guide starts the React frontend and Spring Boot API as two separate applications. Keep each server running in its own PowerShell terminal.

## Prerequisites

- MySQL is running on `127.0.0.1:3306`.
- The MySQL schema `paymyfee` exists. Create it only if it is missing:

```sql
CREATE DATABASE paymyfee;
```

- The backend repository is `FeeManagementSystem` and the separate React repository is its sibling folder `FeeManagementFrontend`.
- Java and Node.js are installed. The backend uses the included Maven wrapper.

## 1. Start the Backend

Open PowerShell terminal 1 in the backend folder:

```powershell
Set-Location C:\Users\hp\Desktop\feemanagementapp\FeeManagementSystem
```

Enter the MySQL password at the secure prompt and start Spring Boot. The password is not written to a project file or command argument:

```powershell
$secure = Read-Host 'MySQL root password' -AsSecureString
$pointer = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($secure)
try {
    $env:DB_PASSWORD = [Runtime.InteropServices.Marshal]::PtrToStringBSTR($pointer)
    .\mvnw.cmd spring-boot:run
} finally {
    [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($pointer)
    Remove-Item Env:DB_PASSWORD -ErrorAction SilentlyContinue
}
```

Wait until the log says Tomcat started on port `8080`. Leave this terminal running. To stop the backend, press `Ctrl+C` in this terminal.

The local datasource defaults are:

- MySQL URL: `jdbc:mysql://127.0.0.1:3306/paymyfee`
- Database user: `root`
- Password: provided through `DB_PASSWORD`

Optional `DB_URL` and `DB_USERNAME` environment variables override the URL and username. The local CORS origin defaults to `http://localhost:5173`; override it with `FRONTEND_ORIGIN` only when using a different frontend origin.

## 2. Start the React Frontend

Open a second PowerShell terminal in the frontend folder:

```powershell
Set-Location C:\Users\hp\Desktop\feemanagementapp\FeeManagementFrontend
```

Create the local environment file if this is the first run, install dependencies, and start Vite:

```powershell
if (-not (Test-Path .env)) { Copy-Item .env.example .env }
npm install
npm run dev
```

Leave this terminal running. To stop the frontend, press `Ctrl+C` in this terminal.

## 3. Open and Verify

- React application: <http://localhost:5173>
- Spring Boot fallback page: <http://localhost:8080>
- Backend/API reachability check: <http://localhost:8080/api-status.html>

The fallback/status pages are diagnostics only. Student, teacher, and principal workflows are in the React frontend. All React API requests, including login, go through its shared API client and default to `http://localhost:8080`.

## Optional Build Checks

In the backend folder:

```powershell
.\mvnw.cmd clean compile
```

In the frontend folder:

```powershell
npm run build
```

## Troubleshooting

- **Frontend says it cannot reach the backend:** confirm terminal 1 is still running and that `http://localhost:8080/api-status.html` opens.
- **MySQL access denied:** verify the MySQL account is `root` and re-enter its password at the backend prompt. The schema name is `paymyfee`; it is not the database username.
- **CORS error:** use the frontend's default `http://localhost:5173` origin, or set backend `FRONTEND_ORIGIN` to exactly match the frontend URL and restart the backend.
- **Port already in use:** stop the process using port `8080` or `5173`, or update the backend/frontend port and corresponding URL/origin configuration together.
- **Login rejected:** verify the account exists in the `paymyfee` schema and use that account's application password. The database password is separate from the application login password.
