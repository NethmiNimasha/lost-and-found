# Lost and Found System (Backend)

This is the backend API for the Lost and Found system, built with Spring Boot and Java 17. 
It provides the core functionality to manage lost and found items, user authentication, and persistent data storage.

## Tech Stack

- **Java 17**
- **Spring Boot 4.1.0**
  - Spring Web MVC
  - Spring Data JPA
  - Spring Security
  - Spring Validation
- **Database**
  - H2 Database (for development/testing)
  - MySQL (Production configuration available)
- **Security**
  - JWT (JSON Web Tokens) via `jjwt`
- **Build Tool**: Gradle

## Getting Started

### Prerequisites

- Java 17 or higher
- Gradle (the project includes the Gradle wrapper, so a local installation is not strictly required)

### Running the Application Locally

1. Open your terminal and navigate to the project root directory (`d:\coursework03\lost-and-found`).
2. Run the application using the Gradle wrapper:

```bash
# On Windows
gradlew bootRun

# On macOS/Linux
./gradlew bootRun
```

By default, the Spring Boot application will start on port `8080`.

### Database Configuration

The application is configured to connect to an H2 database or MySQL depending on your `application.properties` settings. 
- You can access the H2 console at `/h2-console` (if enabled in properties).
- Ensure your MySQL service is running and credentials match the properties file when switching to production.

### Authentication

The API is secured using JWT. To access protected endpoints, you must obtain a JWT token by logging in or registering, and then include the token in the `Authorization` header of subsequent requests:

