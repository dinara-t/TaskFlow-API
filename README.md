# Todos API

A RESTful backend API built with Spring Boot and MySQL for managing todos and categories.
This project demonstrates layered architecture, data persistence, soft deletion logic, filtering, sorting, and structured error handling.

The API is designed to serve as the backend for a full-stack Todo application and follows clean separation of concerns between controllers, services, repositories, DTOs, and entities.

## Technology Stack

- Java 17
- Spring Boot
- Spring Data JPA (Hibernate)
- MySQL
- Maven
- Bean Validation (Jakarta Validation)

## Project Architecture

The application follows a standard layered architecture:

Controller → Service → Repository → Database

- Controllers handle HTTP requests and responses
- Services contain business logic and validation
- Repositories interact with the database via JPA
- DTOs define request/response contracts
- Entities map to database tables

The project also includes:

- Global exception handling
- Custom error response structure
- Timestamp handling via entity listeners
- Soft delete implementation for todos

## Features

### Categories

- Create category
- Update category
- Delete category (hard delete with cascade)
- Retrieve all categories
- Unique category name validation

### Todos

- Create todo
- Update todo (partial updates supported)
- Archive todo (soft delete)
- Retrieve all active todos
- Filter todos by category
- Sort todos (createdAt, title, completed)
- Excludes archived todos from standard queries

### Additional Behaviour

- Automatic createdAt and updatedAt timestamps
- Validation with meaningful error responses
- Clean 400 / 404 error handling

## API Base URL

http://localhost:8080/api

## Endpoints

### Categories

Method Endpoint Description
GET /categories Retrieve all categories
POST /categories Create new category
PUT /categories/{id} Update category
DELETE /categories/{id} Delete category

### Todos

Method Endpoint Description
GET /todos Retrieve todos
GET /todos?category={id} Filter by category
POST /todos Create todo
PUT /todos/{id} Update todo

## Request Examples

### Create Category

{
"name": "Work"
}

### Create Todo

{
"title": "Finish Spring project",
"completed": false,
"categoryId": 1
}

## Configuration

The application uses environment variables loaded via .env:

DB_HOST=
DB_PORT=
DB_NAME=
DB_USER=
DB_PASSWORD=
JWT_SECRET=
JWT_TOKEN_EXPIRY=

Database schema is automatically created/updated in development mode.

## Learning Experience

This project was built as part of a structured software development program to deepen understanding of backend architecture and RESTful API design.

Key areas of learning include:

- Designing clean layered architecture in Spring Boot
- Implementing DTO-driven request/response mapping
- Handling relational data with JPA
- Implementing soft delete patterns
- Building robust validation and error handling
- Applying sorting and filtering logic
- Managing environment configuration securely
- Understanding development vs production configuration strategies

The project emphasises maintainable structure, separation of concerns, and production-style coding practices rather than minimal CRUD implementation.
