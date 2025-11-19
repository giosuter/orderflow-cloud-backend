# OrderFlow API – Canonical URLs

This document defines the **official HTTP endpoints** for the OrderFlow backend.  
It is the single source of truth for all frontend applications and external clients.

---

## 1. Base URLs

### 1.1 Localhost (dev profile)

- Base URL (context path):  
  `http://localhost:8080/orderflow-api`

All endpoints listed below are relative to this base.

Examples:

- `http://localhost:8080/orderflow-api/api/ping`
- `http://localhost:8080/orderflow-api/api/orders`
- `http://localhost:8080/orderflow-api/swagger-ui/index.html`
- `http://localhost:8080/orderflow-api/h2-console`
- `http://localhost:8080/orderflow-api/actuator/health`

### 1.2 Production (Hostpoint, MariaDB)

WAR name: `orderflow-api.war`, deployed under Tomcat context path `/orderflow-api`.  
Nginx passes `/orderflow-api` through unchanged.

- Base URL (context path):  
  `https://devprojects.ch/orderflow-api`

Examples:

- `https://devprojects.ch/orderflow-api/api/ping`
- `https://devprojects.ch/orderflow-api/api/orders`
- `https://devprojects.ch/orderflow-api/swagger-ui/index.html`
- `https://devprojects.ch/orderflow-api/actuator/health`

---

## 2. Trailing Slash Policy

**Canonical rule:**

- All REST endpoints are defined **without trailing slash**.
- Requests with a trailing slash (e.g. `/api/orders/`) are **not guaranteed to work** and may return `404` or be treated as a static resource path.

This is intentional. Frontend code must always use the canonical URLs **exactly as documented** here, without an extra `/` at the end.

**Examples (local dev):**

- ✅ Supported: `GET http://localhost:8080/orderflow-api/api/orders`
- ⛔ Not supported: `GET http://localhost:8080/orderflow-api/api/orders/`

---

## 3. Health & Utility Endpoints

### 3.1 Ping

- `GET /api/ping`  
  Returns a simple status string indicating the API is alive.

Example (local):

```bash
curl -i http://localhost:8080/orderflow-api/api/ping