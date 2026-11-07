# Skill Mastery AI Lesson Planner — React frontend

A professional React + Vite + Material UI frontend for the existing Spring Boot 4, Gemini and MySQL backend.

## 1. Requirements
- Node.js 20.19+ (or a current supported LTS release)
- Your Spring Boot API running locally (default `http://localhost:8080`)
- A valid teacher account and JWT authentication endpoints

## 2. Install and run
```bash
npm install
cp .env.example .env
npm run dev
```
Open `http://localhost:5173`.

For Windows PowerShell, copy the environment file with:
```powershell
Copy-Item .env.example .env
```

## 3. API contract used by this frontend
The lesson API is configured to call:

| Method | Endpoint | Purpose |
|---|---|---|
| POST | `/api/lessons/generate` | Generate a lesson (does not save it) |
| POST | `/api/lessons` | Save the generated lesson |
| GET | `/api/lessons` | List the logged-in teacher's lessons |
| GET | `/api/lessons/{id}` | Read one lesson |
| PUT | `/api/lessons/{id}` | Update a lesson (available for future edit UI) |
| DELETE | `/api/lessons/{id}` | Delete a lesson |

The frontend sends `Authorization: Bearer <JWT>` on API calls when a token is stored.

### Authentication paths
Defaults are `/auth/login` and `/auth/register` relative to `VITE_API_BASE_URL`. Set the actual paths in `.env`:
```env
VITE_API_BASE_URL=http://localhost:8080/api
VITE_LOGIN_PATH=/auth/login
VITE_REGISTER_PATH=/auth/register
VITE_TOKEN_KEY=accessToken
```
The login request body is `{ "email": "...", "password": "..." }`. The registration body includes `name`, `fullName`, `email`, `password`, and `role: "TEACHER"`.

The login response must include a JWT under one of these keys: `token`, `accessToken`, `jwt`, `data.token`, or `data.accessToken`. If your backend uses a different shape, update `extractToken()` in `src/api/authApi.js` and the request DTO mappings in that same file. The backend may return the teacher object as `user` or `teacher`.

## 4. CORS
Allow the Vite origin in your Spring Boot backend during development:
```java
@CrossOrigin(origins = "http://localhost:5173")
```
Prefer a global CORS configuration for all `/api/**` routes, and ensure Spring Security permits your login and registration endpoints while requiring authentication for lesson endpoints.

## 5. Important integration notes
- Gemini API keys stay in the Spring Boot backend. Never put them in React `.env` or browser code.
- Generate and save are separate: the generator calls `/lessons/generate`, then the Save button calls `POST /lessons`.
- A generated lesson has no database ID until it is saved.
- `visualAids.imagePrompt` is prompt text, not an image URL. This UI displays it as a prompt rather than incorrectly using it as an `<img src>`.
- The lesson viewer handles optional/missing `detailedContent` fields gracefully.
- This first frontend version includes viewing and deleting saved lessons. The `PUT /lessons/{id}` API is wired in `src/api/lessonApi.js`; a dedicated edit form can be added once the exact update DTO is confirmed.

## 6. Build for deployment
```bash
npm run build
```
Deploy the generated `dist/` directory to your static host. Set `VITE_API_BASE_URL` to your deployed Spring Boot API URL at build time. Configure production CORS on the backend for your real frontend domain.
