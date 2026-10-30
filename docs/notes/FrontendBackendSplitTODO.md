# Frontend and Backend Separation TODO

## Feasibility

This project can be separated into a frontend and a backend. The backend is already a Spring Boot REST application, and the existing HTML/JavaScript pages call its REST endpoints. The current frontend is served from `src/main/resources/static`, so it is currently packaged inside the backend application rather than maintained as an independent project.

A React frontend is also feasible. It can call the existing API routes, so a complete backend rewrite is not required. The UI, login flow, and role-based navigation would need to be migrated from the existing static pages to React components and routes.

## Chosen Direction

- [x] Use two independent GitHub repositories; hosting is not part of this work.
- [x] Use React for the standalone frontend.
- [x] Keep the existing Spring Boot project as the backend repository and create a sibling frontend project, so the two repositories are not nested.
- [x] Connect the React app to the existing backend API; keep only small backend fallback/status pages.

## Backend Separation

- [ ] Keep `pom.xml`, `mvnw`, `mvnw.cmd`, and `src/main/java` together in the backend project.
- [x] Configure the local backend datasource for MySQL at `127.0.0.1:3306`, schema `paymyfee`, and username `root`; the schema and tables are confirmed present in MySQL Workbench.
- [ ] Enter the local MySQL password through the secure terminal prompt and verify the backend connects; do not commit the password or other secrets.
- [x] Inventory the existing API routes needed for authentication, student, teacher, payment, dashboard, and principal workflows.
- [x] Use the existing API operations for the initial React portals; document or add any missing API operations discovered during feature migration.
- [x] Remove the full legacy portal UI from `src/main/resources/static`; retain only the backend landing fallback and API status page.
- [x] Keep backend fallback pages separate from the React workflows and direct users to the standalone frontend.

## Frontend Separation and React Migration

- [x] Create a standalone Vite + React frontend project beside the backend repository.
- [x] Add React login and role-based views for student, teacher, and principal users, using the existing backend routes.
- [x] Route all React API requests, including login, through the shared client and configure the backend base URL through `VITE_API_BASE_URL`.
- [x] Keep backend role checks as the security boundary; Spring Security already protects the API routes.
- [x] Add loading/submission, empty, validation, and API error feedback to the migrated views.

## Cross-Origin and Authentication Security

- [x] Restrict Spring CORS to the configured frontend origin; local development defaults to `http://localhost:5173` and can be overridden with `FRONTEND_ORIGIN`.
- [x] Allow the required HTTP methods, `Authorization` and `Content-Type` headers, and browser preflight requests without enabling cookie credentials.
- [ ] Review the current HTTP Basic authentication flow before deployment. The existing UI stores encoded credentials in `sessionStorage` and sends them with requests; base64 encoding is not encryption.
- [ ] Prefer a production authentication design with secure, short-lived credentials or secure, HttpOnly cookies, and define CSRF protections if cookie-based authentication is adopted.
- [ ] Verify that endpoint authorization remains correct for student, teacher, and admin roles after the frontend origin changes.
- [ ] Keep database credentials, signing keys, and deployment secrets out of both GitHub repositories; configure them in the hosting environment.

## GitHub and Deployment

- [x] Add a frontend `.gitignore` for build outputs, dependencies, and local environment files.
- [x] Add frontend setup instructions and an environment-variable example; backend configuration remains in its existing README and `application.properties`.
- [x] Defer hosting decisions and production API URL configuration because hosting is out of scope.
- [ ] Configure CI to build/test the backend and build/test the frontend independently.
- [ ] Set up HTTPS for both applications and confirm the deployed frontend origin is the only production CORS origin.

## Verification Checklist

- [x] Backend compiles with `mvnw.cmd -q -DskipTests compile` after the CORS change.
- [ ] Start the backend and confirm it serves only fallback/status pages while all user workflows remain in React.
- [x] Frontend builds independently of the backend using `npm run build`.
- [x] Initialize a separate frontend Git repository and publish it to its own GitHub remote.
- [ ] Start both applications and verify login against a configured local database.
- [ ] Verify student profile, balance, payment, and receipt history against the running backend.
- [ ] Verify teacher assigned-student and fee-management workflows against the running backend.
- [ ] Verify principal dashboard, faculty, mentor assignment, fee adjustment, and transaction workflows against the running backend.
- [ ] Unauthorized users cannot access protected operations by calling the API directly.
- [ ] Browser CORS preflight and API requests succeed from the deployed frontend origin and fail from an unapproved origin.
- [ ] No secrets or real credentials are present in either GitHub repository or built frontend assets.

## Recommendation

The first migration slice is in place: the standalone React app builds, uses the current REST API, and covers the primary student, teacher, and principal workflows. The original HTML pages remain in the backend as a fallback. Run through the remaining local database-backed checks before removing those pages or publishing either repository. The frontend still uses the existing HTTP Basic flow and stores encoded credentials in `sessionStorage`; do not treat this as a production-ready authentication design.