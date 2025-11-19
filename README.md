# OrderFlow Cloud – Enterprise-Grade Order Management API  
### Spring Boot 3.5 · Java 21 · Tomcat 10.1 · Flyway · Swagger UI · Jenkins Ready

OrderFlow Cloud is a **clean, production-ready**, backend service that manages orders with full CRUD, search, validation, and test coverage.  
It is designed as a **professional showcase project** demonstrating enterprise engineering skills:

- modern Spring Boot 3 architecture  
- layered domain/service/controller structure  
- clean mapper and specification patterns  
- strong testing discipline (unit + integration)  
- WAR deployment on Tomcat  
- Flyway-based database versioning  
- OpenAPI/Swagger contract  
- CI/CD-ready structure (Jenkins pipelines)

This project is deployed and running on a **real production server**, publicly accessible:

**🔗 Production API:** https://devprojects.ch/orderflow-api/api/ping  
**🔗 Swagger UI:** https://devprojects.ch/orderflow-api/swagger-ui/index.html  
**🔗 Actuator Health:** https://devprojects.ch/orderflow-api/actuator/health  
**🔗 JaCoCo Report:** https://devprojects.ch/orderflow-api/jacoco/index.html  

---

# 1. Features

### ✔ Complete CRUD for Orders  
- Create, read, update, delete  
- Validation (code, status, totals)  
- Automatic timestamping (createdAt, updatedAt)

### ✔ Advanced Search  
- Search by code  
- Search by status  
- Combined filters  
- Implementation uses Spring Data JPA **Specification Pattern**

### ✔ Clean Architecture  
- Domain model: `Order`  
- DTO: `OrderDto`  
- Repository: `OrderRepository`  
- Service layer with business logic  
- Manual `OrderMapper` for deterministic mapping  
- Thin REST controller (no business logic)

### ✔ Database Ready  
- Flyway migrations (V1 and ready for V2+)  
- MariaDB (production)  
- H2 (development/testing)

### ✔ Professional Deployment  
- WAR packaged  
- Hosted on Tomcat 10.1  
- Reverse-proxy-ready via Hostpoint (FreeBSD-based Nginx + Tomcat)

### ✔ Documentation  
- Swagger UI OpenAPI contract  
- Fully documented endpoints  
- Comments and explanations inside code

---

# 2. Technology Stack

| Layer | Technology |
|------|------------|
| Language | **Java 21** |
| Framework | **Spring Boot 3.5.x** |
| Build | **Maven**, Surefire, JaCoCo |
| Database | **MariaDB 10.6** / H2 |
| Migrations | **Flyway** |
| API Docs | **Springdoc OpenAPI 2.6.0 (Swagger UI)** |
| Server | **Tomcat 10.1 (Jakarta EE)** |
| CI/CD (planned) | **Jenkins pipelines** |
| Hosting | **Hostpoint FreeBSD / Nginx reverse proxy** |

---

# 3. Architecture Overview

OrderFlow uses a **layered architecture**:

```
orderflow-api
 ├── domain/        → JPA entities (Order)
 ├── dto/           → API contract objects
 ├── mapper/        → Manual mappers (OrderMapper)
 ├── repository/    → Spring Data repositories
 ├── service/       → Business logic (OrderServiceImpl)
 ├── web/           → REST controllers (OrderController)
 ├── config/        → Global config, CORS, OpenAPI, context-path
 ├── specs/         → Specifications for dynamic search
 └── resources/     → Flyway, YAML configs, H2, SQL
```

### Key Design Patterns Used
- **Mapper Pattern** (OrderMapper)  
- **Specification Pattern** (OrderSpecifications)  
- **DTO Pattern**  
- **Service Layer Pattern**  
- **Repository Pattern**  
- (Planned) Strategy, Builder, Template Method, Factory, Assembler

---

# 4. Testing Strategy

This project demonstrates **professional testing discipline**.

### ✔ Unit tests (Mockito + JUnit 5)
- OrderServiceImplTest (full coverage)  
- OrderMapperTest  
- OrderSpecificationsTest

### ✔ Integration tests (H2)
- Basic persistence tests successful  
- Flyway migrations validated

### Planned
- Testcontainers for MariaDB  
- Repository integration tests  
- REST Controller slice tests  
- Jenkins CI pipeline running all suites  
- Code coverage thresholds enforced

JaCoCo reports are published online:  
**https://devprojects.ch/orderflow-api/jacoco/index.html**

---

# 5. Deployment

### Local
Run with:

```bash
mvn clean test
mvn spring-boot:run
```

### Production
Build WAR:

```bash
mvn clean package -DskipTests
```

Deploy to Tomcat (Hostpoint):

```
/home/zitatusi/app/tools/tomcat/apache-tomcat-10.1.33/webapps/orderflow-api.war
```

Tomcat context path is fixed:

```yaml
server:
  servlet:
    context-path: /orderflow-api
```

---

# 6. API Endpoints (Summary)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/ping` | Health ping |
| GET | `/api/orders` | List all orders |
| GET | `/api/orders/{id}` | Get order by ID |
| GET | `/api/orders/search` | Search by code/status |
| POST | `/api/orders` | Create order |
| PUT | `/api/orders/{id}` | Update order |
| DELETE | `/api/orders/{id}` | Delete order |

Full documentation:  
https://devprojects.ch/orderflow-api/swagger-ui/index.html

---

# 7. Roadmap (Planned Enhancements)

### Phase 1 — API
- Pagination + sorting  
- Error handling using RFC 7807 (Problem Details)  
- Add Customer / Invoice entities  
- Multi-criteria search with joins

### Phase 2 — Persistence
- Testcontainers with MariaDB  
- Repository integration tests  
- Additional Flyway migrations (V2, V3…)

### Phase 3 — DevOps
- orderflow-ci (build, test, JaCoCo)  
- orderflow-deploy-local  
- orderflow-deploy-prod  
- orderflow-rollback  
- orderflow-static-analysis (SpotBugs, PMD, Checkstyle)

### Phase 4 — Cloud Readiness
- Actuator metrics  
- OpenTelemetry tracing  
- Correlation ID (MDC) filters  
- Structured JSON logging

---

# 8. Purpose of the Project

OrderFlow API is a **professional portfolio backend** demonstrating:

- clean engineering  
- production-ready Spring Boot expertise  
- deep testing skills  
- software architecture understanding  
- DevOps readiness  
- cloud deployment  
- real-world design patterns

It is explicitly built to show recruiters and companies:

> “This is how I structure, test, document, and deploy enterprise-grade backend services.”

---

# 9. Contact

**Giovanni Suter**  
Email: *giovanni.suter@me.com*  
GitHub: https://github.com/giosuter  
Portfolio: https://devprojects.ch  
