# Auth Login Service

A Spring Boot authentication service that provides user registration, login, and user management APIs.

## Tech Stack

- Java 21
- Spring Boot 4.0.1
- Spring Data JPA
- PostgreSQL
- Liquibase (database migrations)
- MapStruct (DTO mapping)
- Lombok

## Prerequisites

- JDK 21
- Maven
- PostgreSQL database

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

## Running the Application

```bash
mvn spring-boot:run
```

The service will start on `http://localhost:8080`

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/users/register` | Register a new user |
| POST | `/api/users/login` | Login with email and password |
| GET | `/api/users` | Get all users |
| GET | `/api/users/{id}` | Get user by ID |
| GET | `/api/users/email?email={email}` | Get user by email |

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
  }
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
  }
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
  ]
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
  }
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
  }
}
```

## Error Responses

### User Already Exists (409)
```json
{
  "success": false,
  "message": "User with email john.doe@example.com already exists",
  "data": null
}
```

### Invalid Credentials (401)
```json
{
  "success": false,
  "message": "Invalid email or password",
  "data": null
}
```

### User Not Found (404)
```json
{
  "success": false,
  "message": "User not found with id: 999",
  "data": null
}
```

## Project Structure

```
src/main/java/com/grpc/
├── AuthLoginServiceApplication.java
├── controller/
│   └── UserController.java
├── dto/
│   ├── ApiResponse.java
│   ├── LoginRequest.java
│   ├── UserDto.java
│   └── UserResponse.java
├── enums/
│   └── AuthProvider.java
├── exception/
│   ├── GlobalExceptionHandler.java
│   ├── InvalidCredentialsException.java
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
└── service/
    ├── UserService.java
    └── impl/
        └── UserServiceImpl.java
```