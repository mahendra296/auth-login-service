# Auth Login Service

A Spring Boot authentication service that provides user registration, login, OAuth2 (Google & GitHub), and user management APIs.

## Tech Stack

- Java 21
- Spring Boot 4.0.1
- Spring Data JPA
- Spring WebFlux (WebClient for OAuth)
- PostgreSQL
- Liquibase (database migrations)
- MapStruct (DTO mapping)
- Lombok

## Prerequisites

- JDK 21
- Maven
- PostgreSQL database
- Google OAuth credentials (for Google login)
- GitHub OAuth credentials (for GitHub login)

## Database Setup

1. Create a PostgreSQL database:
```sql
CREATE DATABASE authdb;
```

2. Update `src/main/resources/application.yaml` with your database credentials if needed:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/authdb
    username: postgres
    password: root
```

## OAuth Configuration

### Google OAuth Setup

1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select existing one
3. Navigate to APIs & Services > Credentials
4. Create OAuth 2.0 Client ID
5. Add authorized redirect URI: `http://localhost:8080/auth/google/callback`
6. Set environment variables:
```bash
export GOOGLE_CLIENT_ID=your-google-client-id
export GOOGLE_CLIENT_SECRET=your-google-client-secret
```

### GitHub OAuth Setup

1. Go to [GitHub Developer Settings](https://github.com/settings/developers)
2. Create a new OAuth App
3. Set Authorization callback URL: `http://localhost:8080/auth/github/callback`
4. Set environment variables:
```bash
export GITHUB_CLIENT_ID=your-github-client-id
export GITHUB_CLIENT_SECRET=your-github-client-secret
```

### Application Configuration

Update `application.yaml` or use environment variables:
```yaml
oauth:
  google:
    client-id: ${GOOGLE_CLIENT_ID}
    client-secret: ${GOOGLE_CLIENT_SECRET}
    redirect-uri: http://localhost:8080/auth/google/callback
  github:
    client-id: ${GITHUB_CLIENT_ID}
    client-secret: ${GITHUB_CLIENT_SECRET}
    redirect-uri: http://localhost:8080/auth/github/callback
  frontend-redirect-url: http://localhost:3000
```

## Running the Application

```bash
mvn spring-boot:run
```

The service will start on `http://localhost:8080`

## API Endpoints

### User Management APIs

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/users/register` | Register a new user |
| POST | `/api/users/login` | Login with email and password |
| GET | `/api/users` | Get all users |
| GET | `/api/users/{id}` | Get user by ID |
| GET | `/api/users/email?email={email}` | Get user by email |

### OAuth APIs

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/auth/google` | Initiate Google OAuth login (redirects to Google) |
| GET | `/auth/google/callback` | Handle Google OAuth callback |
| GET | `/auth/google/url` | Get Google authorization URL (for SPA/mobile) |
| GET | `/auth/github` | Initiate GitHub OAuth login (redirects to GitHub) |
| GET | `/auth/github/callback` | Handle GitHub OAuth callback |
| GET | `/auth/github/url` | Get GitHub authorization URL (for SPA/mobile) |

## cURL Examples

### Register User

```bash
curl -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "gender": "Male",
    "email": "john.doe@example.com",
    "countryCode": "+1",
    "phone": "1234567890",
    "password": "password123"
  }'
```

**Response:**
```json
{
  "success": true,
  "message": "User registered successfully",
  "data": {
    "id": 1,
    "firstName": "John",
    "lastName": "Doe",
    "gender": "Male",
    "email": "john.doe@example.com",
    "countryCode": "+1",
    "phone": "1234567890",
    "createdAt": "2025-01-20T10:30:00",
    "updatedAt": "2025-01-20T10:30:00"
  },
  "error": null
}
```

### Login

```bash
curl -X POST http://localhost:8080/api/users/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john.doe@example.com",
    "password": "password123"
  }'
```

**Response:**
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "id": 1,
    "firstName": "John",
    "lastName": "Doe",
    "gender": "Male",
    "email": "john.doe@example.com",
    "countryCode": "+1",
    "phone": "1234567890",
    "createdAt": "2025-01-20T10:30:00",
    "updatedAt": "2025-01-20T10:30:00"
  },
  "error": null
}
```

### Get All Users

```bash
curl -X GET http://localhost:8080/api/users
```

**Response:**
```json
{
  "success": true,
  "message": "Users retrieved successfully",
  "data": [
    {
      "id": 1,
      "firstName": "John",
      "lastName": "Doe",
      "gender": "Male",
      "email": "john.doe@example.com",
      "countryCode": "+1",
      "phone": "1234567890",
      "createdAt": "2025-01-20T10:30:00",
      "updatedAt": "2025-01-20T10:30:00"
    }
  ],
  "error": null
}
```

### Get User by ID

```bash
curl -X GET http://localhost:8080/api/users/1
```

**Response:**
```json
{
  "success": true,
  "message": "User retrieved successfully",
  "data": {
    "id": 1,
    "firstName": "John",
    "lastName": "Doe",
    "gender": "Male",
    "email": "john.doe@example.com",
    "countryCode": "+1",
    "phone": "1234567890",
    "createdAt": "2025-01-20T10:30:00",
    "updatedAt": "2025-01-20T10:30:00"
  },
  "error": null
}
```

### Get User by Email

```bash
curl -X GET "http://localhost:8080/api/users/email?email=john.doe@example.com"
```

**Response:**
```json
{
  "success": true,
  "message": "User retrieved successfully",
  "data": {
    "id": 1,
    "firstName": "John",
    "lastName": "Doe",
    "gender": "Male",
    "email": "john.doe@example.com",
    "countryCode": "+1",
    "phone": "1234567890",
    "createdAt": "2025-01-20T10:30:00",
    "updatedAt": "2025-01-20T10:30:00"
  },
  "error": null
}
```

---

## OAuth API cURL Examples

### Get Google Auth URL (for SPA/Mobile)

Returns the Google OAuth authorization URL. Use this when building SPAs or mobile apps.

```bash
curl -X GET http://localhost:8080/auth/google/url \
  -H "Accept: application/json"
```

**Response:**
```json
{
  "success": true,
  "message": "Google authorization URL generated",
  "data": {
    "authorizationUrl": "https://accounts.google.com/o/oauth2/v2/auth?client_id=your-client-id.apps.googleusercontent.com&redirect_uri=http://localhost:8080/auth/google/callback&response_type=code&scope=openid%20profile%20email&state=abc123xyz&code_challenge=E9Melhoa2OwvFrEMTJguCHaoeK1t8URWbuGJSstw-cM&code_challenge_method=S256&access_type=offline&prompt=consent"
  },
  "error": null
}
```

> **Note:** State is stored server-side. You can call this from Postman and paste the URL in a browser.

---

### Get GitHub Auth URL (for SPA/Mobile)

Returns the GitHub OAuth authorization URL. Use this when building SPAs or mobile apps.

```bash
curl -X GET http://localhost:8080/auth/github/url \
  -H "Accept: application/json"
```

**Response:**
```json
{
  "success": true,
  "message": "GitHub authorization URL generated",
  "data": {
    "authorizationUrl": "https://github.com/login/oauth/authorize?client_id=your-github-client-id&redirect_uri=http://localhost:8080/auth/github/callback&scope=read:user%20user:email&state=xyz789abc"
  },
  "error": null
}
```

> **Note:** State is stored server-side. You can call this from Postman and paste the URL in a browser.

---

### Initiate Google Login (Browser Redirect)

Redirects the user directly to Google's login page. Use this for server-side rendered apps.

```bash
curl -X GET http://localhost:8080/auth/google -L
```

Or open directly in browser:
```
http://localhost:8080/auth/google
```

**Flow:**
1. Redirects to Google login page
2. User authenticates with Google
3. Google redirects to `/auth/google/callback`
4. Server validates state, processes user, and redirects to `/api/users/{userId}`

---

### Initiate GitHub Login (Browser Redirect)

Redirects the user directly to GitHub's login page. Use this for server-side rendered apps.

```bash
curl -X GET http://localhost:8080/auth/github -L
```

Or open directly in browser:
```
http://localhost:8080/auth/github
```

**Flow:**
1. Redirects to GitHub login page
2. User authenticates with GitHub
3. GitHub redirects to `/auth/github/callback`
4. Server validates state, processes user, and redirects to `/api/users/{userId}`

---

### Google OAuth Callback (Internal - Called by Google)

This endpoint is called by Google after user authentication. Not typically called directly.

```bash
curl -X GET "http://localhost:8080/auth/google/callback?code=4/0AX4XfWh...&state=abc123xyz" \
  -L
```

**Parameters:**
- `code` - Authorization code from Google
- `state` - State parameter for CSRF protection (validated against server-side store)

**Success:** Redirects to `{frontend-redirect-url}/api/users/{userId}`
**Error:** Redirects to `{frontend-redirect-url}/login?error=google_auth_failed`

---

### GitHub OAuth Callback (Internal - Called by GitHub)

This endpoint is called by GitHub after user authentication. Not typically called directly.

```bash
curl -X GET "http://localhost:8080/auth/github/callback?code=abc123def456&state=xyz789abc" \
  -L
```

**Parameters:**
- `code` - Authorization code from GitHub
- `state` - State parameter for CSRF protection (validated against server-side store)

**Success:** Redirects to `{frontend-redirect-url}/api/users/{userId}`
**Error:** Redirects to `{frontend-redirect-url}/login?error=github_auth_failed`

---

## Postman Collection

### Import these cURL commands into Postman:

#### 1. User Registration
```
POST http://localhost:8080/api/users/register
Content-Type: application/json

{
  "firstName": "John",
  "lastName": "Doe",
  "gender": "Male",
  "email": "john.doe@example.com",
  "countryCode": "+1",
  "phone": "1234567890",
  "password": "password123"
}
```

#### 2. User Login
```
POST http://localhost:8080/api/users/login
Content-Type: application/json

{
  "email": "john.doe@example.com",
  "password": "password123"
}
```

#### 3. Get All Users
```
GET http://localhost:8080/api/users
```

#### 4. Get User by ID
```
GET http://localhost:8080/api/users/1
```

#### 5. Get User by Email
```
GET http://localhost:8080/api/users/email?email=john.doe@example.com
```

#### 6. Get Google Auth URL
```
GET http://localhost:8080/auth/google/url
Accept: application/json
```

#### 7. Get GitHub Auth URL
```
GET http://localhost:8080/auth/github/url
Accept: application/json
```

#### 8. Initiate Google Login (Open in Browser)
```
GET http://localhost:8080/auth/google
```

#### 9. Initiate GitHub Login (Open in Browser)
```
GET http://localhost:8080/auth/github
```

## Error Responses

All error responses include an `error` object with `errorCode` and `errorMessage`:

### User Already Exists (409)
```json
{
  "success": false,
  "message": "User with email john.doe@example.com already exists",
  "data": null,
  "error": {
    "errorCode": "USER_ALREADY_EXISTS",
    "errorMessage": "User with email john.doe@example.com already exists"
  }
}
```

### Invalid Credentials (401)
```json
{
  "success": false,
  "message": "Invalid email or password",
  "data": null,
  "error": {
    "errorCode": "INVALID_CREDENTIALS",
    "errorMessage": "Invalid email or password"
  }
}
```

### User Not Found (404)
```json
{
  "success": false,
  "message": "User not found with id: 999",
  "data": null,
  "error": {
    "errorCode": "USER_NOT_FOUND",
    "errorMessage": "User not found with id: 999"
  }
}
```

### OAuth Error (400)
```json
{
  "success": false,
  "message": "Invalid OAuth state",
  "data": null,
  "error": {
    "errorCode": "OAUTH_STATE_MISMATCH",
    "errorMessage": "Invalid OAuth state"
  }
}
```

## Error Codes

| Error Code | Description |
|------------|-------------|
| `USER_NOT_FOUND` | User does not exist |
| `USER_ALREADY_EXISTS` | Email already registered |
| `INVALID_CREDENTIALS` | Wrong email or password |
| `VALIDATION_ERROR` | Request validation failed |
| `OAUTH_STATE_MISMATCH` | OAuth state parameter mismatch |
| `OAUTH_TOKEN_EXCHANGE_FAILED` | Failed to exchange OAuth code for tokens |
| `OAUTH_USER_INFO_FAILED` | Failed to fetch user info from OAuth provider |
| `INTERNAL_SERVER_ERROR` | Unexpected server error |

## Project Structure

```
src/main/java/com/auth/
├── AuthLoginServiceApplication.java
├── config/
│   ├── OAuthProperties.java
│   └── WebClientConfig.java
├── controller/
│   ├── AuthController.java
│   └── UserController.java
├── dto/
│   ├── ApiResponse.java
│   ├── ErrorDetails.java
│   ├── LoginRequest.java
│   ├── UserDto.java
│   ├── UserResponse.java
│   └── oauth/
│       ├── GitHubEmail.java
│       ├── GitHubUserInfo.java
│       ├── GoogleUserInfo.java
│       ├── OAuthTokenResponse.java
│       └── OAuthUrlResponse.java
├── enums/
│   ├── AuthProvider.java
│   └── ErrorCode.java
├── exception/
│   ├── GlobalExceptionHandler.java
│   ├── InvalidCredentialsException.java
│   ├── OAuthException.java
│   ├── ResourceNotFoundException.java
│   └── UserAlreadyExistsException.java
├── mapper/
│   └── UserMapper.java
├── model/
│   ├── AuthAccount.java
│   └── User.java
├── repository/
│   ├── AuthAccountRepository.java
│   └── UserRepository.java
├── service/
│   ├── UserService.java
│   └── oauth/
│       ├── GitHubOAuthService.java
│       ├── GoogleOAuthService.java
│       └── OAuthStateStore.java
└── util/
    └── OAuthUtils.java

src/main/resources/
├── application.yaml
└── db/
    └── changelog/
        ├── db.changelog-master.xml
        └── changes/
            ├── 001-create-users-table.xml
            └── 002-create-auth-accounts-table.xml
```

## OAuth Flow

### State Management

OAuth state and code verifier are stored in a **server-side in-memory store** (`OAuthStateStore`) keyed by the state parameter. This allows:
- API calls from Postman to generate auth URLs
- Browser to complete the OAuth flow
- State validation without requiring cookies/sessions

States automatically expire after 10 minutes.

### Server-Side Flow (Traditional Web Apps)

1. User clicks "Login with Google/GitHub"
2. Browser redirects to `/auth/google` or `/auth/github`
3. Server generates state (and code verifier for Google PKCE)
4. State is stored in server-side memory store
5. User authenticates with OAuth provider
6. Provider redirects to callback URL with authorization code and state
7. Server validates state against memory store
8. Server exchanges code for tokens
9. Server fetches user info and creates/updates user
10. Server redirects to `{frontend-redirect-url}/api/users/{userId}`

### Client-Side Flow (SPA/Mobile Apps)

1. Frontend calls `/auth/google/url` or `/auth/github/url`
2. Server stores state in memory and returns authorization URL
3. Frontend opens URL (popup or redirect)
4. User authenticates with OAuth provider
5. Provider redirects to callback URL
6. Server validates state, processes user, and redirects to frontend

### OAuth User Creation

When a user logs in via OAuth for the first time:
- A new user is created with email, first name, and last name from the provider
- A random 8-character password is generated (user can reset it later)
- An auth account record is created linking the user to the OAuth provider

If a user with the same email already exists:
- The existing user is linked to the OAuth provider
- No duplicate user is created
