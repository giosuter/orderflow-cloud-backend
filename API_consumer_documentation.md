# Orderflow API – Consumer Documentation

This document explains how to consume the Orderflow REST API: base URLs, endpoints, payloads, error model, and examples.

## 1. Base URLs

### Local (development)
- Root: `http://localhost:8080/orderflow-api`
- Swagger UI: `http://localhost:8080/orderflow-api/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/orderflow-api/v3/api-docs`

### Production (example)
- Root: `https://devprojects.ch/orderflow-api`

All endpoints below assume the `/orderflow-api` context path.

## 2. Conventions

- Content type: `application/json`
- Authentication: none (current phase)
- Dates: ISO-8601 with trailing Z
- Errors: Unified error model (see section 5)

## 3. Health Endpoints

### GET /api/ping
Returns service liveness text.

### GET /api/ping/time
Returns an ISO timestamp for debugging.

## 4. Orders API

### Schema: OrderDto
```
id: number (server-assigned)
code: string (required)
status: string (enum)
total: number (required)
createdAt: ISO timestamp
updatedAt: ISO timestamp
```

---

## POST /api/orders

Request:
```json
{
  "code": "IT-1001",
  "status": "NEW",
  "total": 15.25
}
```

Response 200:
```json
{
  "id": 1,
  "code": "IT-1001",
  "status": "NEW",
  "total": 15.25,
  "createdAt": "2025-11-11T16:17:29.550592Z",
  "updatedAt": "2025-11-11T16:17:29.550616Z"
}
```

Validation error → 422 Unprocessable Entity.

---

## GET /api/orders/{id}

Response 200:
```json
{
  "id": 1,
  "code": "IT-1001",
  "status": "NEW",
  "total": 15.25
}
```

Nonexistent ID → 404 Not Found.

---

## GET /api/orders

Example:
```json
[
  {
    "id": 1,
    "code": "IT-1001",
    "status": "NEW",
    "total": 15.25
  }
]
```

---

## PUT /api/orders/{id}

Request:
```json
{
  "code": "IT-1001",
  "status": "NEW",
  "total": 20.00
}
```

---

## DELETE /api/orders/{id}

Returns:
```
204 No Content
```

---

## 5. Error Model

All API errors follow:

```json
{
  "status": 422,
  "error": "Unprocessable Entity",
  "message": "total: must not be null",
  "path": "/api/orders",
  "timestamp": "2025-11-11T16:12:08.575111Z"
}
```

---

## 6. Examples

### Curl
```bash
curl -X POST http://localhost:8080/orderflow-api/api/orders   -H "Content-Type: application/json"   -d '{"code":"IT-1001","status":"NEW","total":15.25}'
```

### JavaScript fetch
```javascript
const res = await fetch('/orderflow-api/api/orders', {
  method: "POST",
  headers: { "Content-Type": "application/json" },
  body: JSON.stringify({ code: "IT-1001", status: "NEW", total: 15.25 })
});
```

### Angular HttpClient
```ts
@Injectable({ providedIn: 'root' })
export class OrderApi {
  private base = '/orderflow-api/api/orders';
  constructor(private http: HttpClient) {}

  list() { return this.http.get(this.base); }
  get(id: number) { return this.http.get(`${this.base}/${id}`); }
  create(o: any) { return this.http.post(this.base, o); }
  update(id: number, o: any) { return this.http.put(`${this.base}/${id}`, o); }
  delete(id: number) { return this.http.delete(`${this.base}/${id}`); }
}
```

---

## 7. Swagger Endpoints

- Swagger UI: `/orderflow-api/swagger-ui/index.html`
- API Docs JSON: `/orderflow-api/v3/api-docs`
