# Billing PreProcessor

_The service provides functionality for storing invoicing records and associated accounting information, as well as their respective status (created, attested, invoiced or rejected). The records that have status attested will be included in a generated file that is sent to the Raindance system for invoicing and accounting. The sent invoices will then be changed to status invoiced._

_The service contains two periodic jobs that are executed at certain intervals, one to create the invoice files to be sent and one to send files to the Raindance system. These jobs are executed once per day._

## Getting Started

### Prerequisites

- **Java 25 or higher**
- **Maven**
- **MariaDB**
- **Git**
- **[Dependent Microservices](#dependencies)**

### Installation

1. **Clone the repository:**

```bash
git clone https://github.com/Sundsvallskommun/api-service-billing-preprocessor.git
cd api-service-billing-preprocessor
```

2. **Configure the application:**

   Before running the application, you need to set up configuration settings.
   See [Configuration](#Configuration)

   **Note:** Ensure all required configurations are set; otherwise, the application may fail to start.

3. **Ensure dependent services are running:**

   If this microservice depends on other services, make sure they are up and accessible. See [Dependencies](#dependencies) for more details.

4. **Build and run the application:**

   - Using Maven:

```bash
mvn spring-boot:run
```

- Using Gradle:

```bash
gradle bootRun
```

## Dependencies

This microservice depends on the following services:

- **Party**
  - **Purpose:** Used for translating between party id and legal id.
  - **Repository:** [https://github.com/Sundsvallskommun/api-service-party](https://github.com/Sundsvallskommun/api-service-party)
  - **Setup Instructions:** See documentation in repository above for installation and configuration steps.
- **Messaging**
  - **Purpose:** Used for sending report emails.
  - **Repository:** [https://github.com/Sundsvallskommun/api-service-messaging](https://github.com/Sundsvallskommun/api-service-messaging)
  - **Setup Instructions:** See documentation in repository above for installation and configuration steps.

Ensure that these services are running and properly configured before starting this microservice.

## API Documentation

Access the API documentation via Swagger UI:

- **Swagger UI:** [http://localhost:8080/api-docs](http://localhost:8080/api-docs)

## Usage

### API Endpoints

See the [API Documentation](#api-documentation) for detailed information on available endpoints.

### Example Request

```bash
curl -X GET http://localhost:8080/2281/billingrecords?filter=category : 'SOME-CATEGORY' and status : 'APPROVED'
```

## Configuration

Configuration is crucial for the application to run successfully. Ensure all necessary settings are configured in `application.yml`.

### Key Configuration Parameters

- **Server Port:**

```yaml
server:
  port: 8080
```

- **Database Settings**

```yaml
spring:
  datasource:
    url: jdbc:mysql://<server>:<port>/<database-name>
    username: <username>
    password: <password>
```

- **External Service URLs**

```yaml
spring:
  security:
    oauth2:
      client:
        registration:
          messaging:
            client-id: <client-id>
            client-secret: <client-secret>
          party:
            client-id: <client-id>
            client-secret: <client-secret>
        provider:
          messaging:
            token-uri: <token-url>
          party:
            token-uri: <token-url>

integration:
  messaging:
    url: <service_url>
  party:
    url: <service_url>
  sftp:
    municipalityIds:
      <municipality-id>:
        host: <host-name>
        user: <user>
        port: <port>
        remoteDir: <remote-dir>
```

- **Recipients of reports**

```yaml
errorreport:
  recipients:
    - <email-to-recipient>
```

### Database Initialization

The project is set up with [Flyway](https://github.com/flyway/flyway) for database migrations. Flyway is disabled by default so you will have to enable it to automatically populate the database schema upon application startup.

```yaml
spring:
  flyway:
    enabled: true
```

- **No additional setup is required** for database initialization, as long as the database connection settings are correctly configured.

### Additional Notes

- **Application Profiles:**

  Use Spring profiles (`dev`, `prod`, etc.) to manage different configurations for different environments.

- **Logging Configuration:**

  Adjust logging levels if necessary.

## Contributing

Contributions are welcome! Please see [CONTRIBUTING.md](https://github.com/Sundsvallskommun/.github/blob/main/.github/CONTRIBUTING.md) for guidelines.

## License

This project is licensed under the [MIT License](LICENSE).

## Status

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=Sundsvallskommun_api-service-billing-preprocessor&metric=alert_status)](https://sonarcloud.io/summary/overall?id=Sundsvallskommun_api-service-billing-preprocessor)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=Sundsvallskommun_api-service-billing-preprocessor&metric=reliability_rating)](https://sonarcloud.io/summary/overall?id=Sundsvallskommun_api-service-billing-preprocessor)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=Sundsvallskommun_api-service-billing-preprocessor&metric=security_rating)](https://sonarcloud.io/summary/overall?id=Sundsvallskommun_api-service-billing-preprocessor)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=Sundsvallskommun_api-service-billing-preprocessor&metric=sqale_rating)](https://sonarcloud.io/summary/overall?id=Sundsvallskommun_api-service-billing-preprocessor)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=Sundsvallskommun_api-service-billing-preprocessor&metric=vulnerabilities)](https://sonarcloud.io/summary/overall?id=Sundsvallskommun_api-service-billing-preprocessor)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=Sundsvallskommun_api-service-billing-preprocessor&metric=bugs)](https://sonarcloud.io/summary/overall?id=Sundsvallskommun_api-service-billing-preprocessor)

---

&copy; 2023 Sundsvalls kommun
