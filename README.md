OrderFlow Cloud – Backend

Spring Boot 3.5.x · Java 21 · Maven · H2 (file-based) · Springdoc OpenAPI · Jenkins CI
Context path: /orderflow-api

⸻

1. Project Overview

OrderFlow Cloud Backend provides a clean REST API for managing Orders, including:
	•	CRUD operations
	•	DTO + Mapper
	•	Service layer
	•	Global exception handling
	•	Integration tests using MockMvc (OrderControllerIT)
	•	Automatic OpenAPI 3 documentation (Swagger UI)

⸻

2. Technologies

Technology	Version	Notes
Java	21	Required
Spring Boot	3.5.x	Using Boot parent
Maven	3.9+	Build + tests
H2	file-based	Stored under ~/.orderflow/data
Flyway	v1 migration	Creates ORDER table
Springdoc	2.6.0	OpenAPI 3 documentation
Jenkins	configured	Builds + runs tests


⸻

3. Local Run

Start with:

./mvnw clean install
./mvnw spring-boot:run

Server starts at:

http://localhost:8080/orderflow-api


⸻

4. Swagger / OpenAPI URLs

Local
	•	Swagger UI
http://localhost:8080/orderflow-api/swagger-ui.html
	•	Raw OpenAPI JSON
http://localhost:8080/orderflow-api/v3/api-docs

Production (Hostpoint)

Replace domain:
	•	Swagger UI
https://devprojects.ch/orderflow-api/swagger-ui.html
	•	Raw OpenAPI
https://devprojects.ch/orderflow-api/v3/api-docs

⸻

5. H2 Database

H2 is persisted on disk.

Path:

~/.orderflow/data.mv.db

URL:

jdbc:h2:file:~/.orderflow/data

Console:

http://localhost:8080/orderflow-api/h2-console


⸻

6. REST Endpoints

Base:

/orderflow-api/api/orders

Create Order (POST)

{
  "code": "A-100",
  "status": "NEW",
  "total": 15.25
}

Example statuses:

NEW
PROCESSING
COMPLETED
CANCELLED

Get all

GET /api/orders

Update

PUT /api/orders/{id}

Delete

DELETE /api/orders/{id}


⸻

7. Tests

Unit tests

./mvnw test

Integration tests (MockMvc)

OrderControllerIT runs:
	•	create
	•	get
	•	update
	•	list
	•	delete
	•	get-after-delete → 404
	•	validation → 422

Run with:

./mvnw verify


⸻

8. Jenkins Pipeline

Your Jenkins job now:
	1.	Checks out code
	2.	Runs mvn clean verify
	3.	Publishes test results
	4.	Publishes JaCoCo report
	5.	Fails build if tests fail

Current build: GREEN

⸻

9. Next Steps

You should check in your changes now:

git add .
git commit -m "Order CRUD stable + Swagger fixed + IT green"
git push

After commit/push:

Jenkins will run
Your project remains stable
Swagger works
Tests pass

⸻

provide API consumer documentation
provide Postman collection
provide Front-end Angular client for Orders
provide Deployment scripts
