# TasksFlow API

A RESTful backend API built with Spring Boot and MySQL for managing tasks and categories.
This project demonstrates layered architecture, data persistence, soft deletion logic, filtering, sorting, and structured error handling.

The API is designed to serve as the backend for a full-stack task application and follows clean separation of concerns between controllers, services, repositories, DTOs, and entities.

## Technology Stack

- Java
- Spring Boot
- Spring Data JPA
- Hibernate
- MySQL
- Maven
- Jakarta Validation

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
- Soft delete implementation for tasks

## Features

### Categories

- Create category
- Update category
- Delete category
- Retrieve all categories
- Unique category name validation
  Categories are stored in a separate table and linked to tasks via @ManyToOne.

### tasks

- Create task
- Update task (partial updates supported)
- Archive task (soft delete)
- Retrieve all active tasks
- Toggle completion
- Filter tasks by category
- Sort tasks (createdAt, title, completed)
- Excludes archived tasks from standard queries

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

### tasks

Method Endpoint Description
GET /tasks Retrieve tasks
GET /tasks?category={id} Filter by category
POST /tasks Create task
PUT /tasks/{id} Update task
POST /api/tasks/{id}/duplicate Duplicate task

## Running Locally

1. Create .env file based on .env.example

2. Ensure MySQL is running

3. Start the application:

```bash
mvn spring-boot:run
```

API base path:

```bash
http://localhost:8080/api
```

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
