# Postman Collection for OrderFlow Cloud API
This Markdown file contains a ready-to-import Postman collection (v2.1).
Copy the JSON below into a file named `orderflow-cloud-api.postman_collection.json` and import it into Postman.
Generated: 2025-11-11T17:13:43

## Base URL
- Local: `http://localhost:8080/orderflow-api`
- Production (example): `https://devprojects.ch/orderflow-api`

You can adjust the `baseUrl` collection variable after importing.

## How to import
1. Save the JSON block into `orderflow-cloud-api.postman_collection.json`.
2. In Postman: Import → File → select the JSON file.
3. Run requests in order: Create → Get → Update → List → Delete.

## Notes
- Create/Update use fields that match your DTO: `code` (string), `status` (NEW/PROCESSING/COMPLETED), `total` (number).
- Tests store `orderId` in collection variables after Create and reuse it later.
- Swagger/OpenAPI endpoints are included for convenience.

---

```json
{
  "info": {
    "name": "OrderFlow Cloud API",
    "_postman_id": "6e5f2c1f-2a3b-4f0f-9a41-0c7c9c1f0a11",
    "description": "Postman collection for OrderFlow Cloud API (Spring Boot 3.5.x). Includes ping and Order CRUD endpoints.",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "Ping",
      "request": {
        "method": "GET",
        "header": [],
        "url": {
          "raw": "{{baseUrl}}/api/ping",
          "host": [
            "{{baseUrl}}"
          ],
          "path": [
            "api",
            "ping"
          ]
        }
      },
      "event": [
        {
          "listen": "test",
          "script": {
            "type": "text/javascript",
            "exec": [
              "pm.test('Status is 200', function () {",
              "  pm.response.to.have.status(200);",
              "});",
              "pm.test('Body contains alive', function () {",
              "  pm.expect(pm.response.text()).to.include('alive');",
              "});"
            ]
          }
        }
      ]
    },
    {
      "name": "Orders",
      "item": [
        {
          "name": "Create Order",
          "request": {
            "method": "POST",
            "header": [
              {
                "key": "Content-Type",
                "value": "application/json"
              }
            ],
            "body": {
              "mode": "raw",
              "raw": "{\"code\":\"IT-1001\",\"status\":\"NEW\",\"total\":15.25}"
            },
            "url": {
              "raw": "{{baseUrl}}/api/orders",
              "host": [
                "{{baseUrl}}"
              ],
              "path": [
                "api",
                "orders"
              ]
            }
          },
          "event": [
            {
              "listen": "test",
              "script": {
                "type": "text/javascript",
                "exec": [
                  "pm.test('Status is 200', function () {",
                  "  pm.response.to.have.status(200);",
                  "});",
                  "pm.test('Returns JSON', function () {",
                  "  pm.response.to.be.withBody;",
                  "  pm.response.to.be.json;",
                  "});",
                  "const json = pm.response.json();",
                  "pm.test('Has id', function () {",
                  "  pm.expect(json.id).to.be.a('number');",
                  "});",
                  "// Save id for later requests",
                  "pm.collectionVariables.set('orderId', json.id);"
                ]
              }
            }
          ]
        },
        {
          "name": "Get Order by ID",
          "request": {
            "method": "GET",
            "header": [],
            "url": {
              "raw": "{{baseUrl}}/api/orders/{{orderId}}",
              "host": [
                "{{baseUrl}}"
              ],
              "path": [
                "api",
                "orders",
                "{{orderId}}"
              ]
            }
          },
          "event": [
            {
              "listen": "test",
              "script": {
                "type": "text/javascript",
                "exec": [
                  "pm.test('Status is 200', function () {",
                  "  pm.response.to.have.status(200);",
                  "});",
                  "pm.test('Order id matches', function () {",
                  "  const json = pm.response.json();",
                  "  pm.expect(json.id).to.eql(Number(pm.collectionVariables.get('orderId')));",
                  "});"
                ]
              }
            }
          ]
        },
        {
          "name": "Update Order",
          "request": {
            "method": "PUT",
            "header": [
              {
                "key": "Content-Type",
                "value": "application/json"
              }
            ],
            "body": {
              "mode": "raw",
              "raw": "{\"code\":\"IT-1001\",\"status\":\"PROCESSING\",\"total\":20.5}"
            },
            "url": {
              "raw": "{{baseUrl}}/api/orders/{{orderId}}",
              "host": [
                "{{baseUrl}}"
              ],
              "path": [
                "api",
                "orders",
                "{{orderId}}"
              ]
            }
          },
          "event": [
            {
              "listen": "test",
              "script": {
                "type": "text/javascript",
                "exec": [
                  "pm.test('Status is 200', function () {",
                  "  pm.response.to.have.status(200);",
                  "});",
                  "pm.test('Status updated to PROCESSING', function () {",
                  "  pm.expect(pm.response.json().status).to.eql('PROCESSING');",
                  "});"
                ]
              }
            }
          ]
        },
        {
          "name": "List Orders",
          "request": {
            "method": "GET",
            "header": [],
            "url": {
              "raw": "{{baseUrl}}/api/orders",
              "host": [
                "{{baseUrl}}"
              ],
              "path": [
                "api",
                "orders"
              ]
            }
          },
          "event": [
            {
              "listen": "test",
              "script": {
                "type": "text/javascript",
                "exec": [
                  "pm.test('Status is 200', function () {",
                  "  pm.response.to.have.status(200);",
                  "});",
                  "pm.test('Returns array', function () {",
                  "  const arr = pm.response.json();",
                  "  pm.expect(Array.isArray(arr)).to.be.true;",
                  "});"
                ]
              }
            }
          ]
        },
        {
          "name": "Delete Order",
          "request": {
            "method": "DELETE",
            "header": [],
            "url": {
              "raw": "{{baseUrl}}/api/orders/{{orderId}}",
              "host": [
                "{{baseUrl}}"
              ],
              "path": [
                "api",
                "orders",
                "{{orderId}}"
              ]
            }
          },
          "event": [
            {
              "listen": "test",
              "script": {
                "type": "text/javascript",
                "exec": [
                  "pm.test('Status is 204', function () {",
                  "  pm.response.to.have.status(204);",
                  "});"
                ]
              }
            }
          ]
        }
      ]
    },
    {
      "name": "OpenAPI JSON",
      "request": {
        "method": "GET",
        "header": [],
        "url": {
          "raw": "{{baseUrl}}/v3/api-docs",
          "host": [
            "{{baseUrl}}"
          ],
          "path": [
            "v3",
            "api-docs"
          ]
        }
      }
    },
    {
      "name": "Swagger UI (for reference)",
      "request": {
        "method": "GET",
        "header": [],
        "url": {
          "raw": "{{baseUrl}}/swagger-ui/index.html",
          "host": [
            "{{baseUrl}}"
          ],
          "path": [
            "swagger-ui",
            "index.html"
          ]
        }
      }
    }
  ],
  "variable": [
    {
      "key": "baseUrl",
      "value": "http://localhost:8080/orderflow-api"
    },
    {
      "key": "orderId",
      "value": "1"
    }
  ]
}
```
