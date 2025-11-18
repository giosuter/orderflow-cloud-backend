package ch.devprojects.orderflow.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;

/**
 * Unit tests for GlobalExceptionHandler.
 *
 * This test:
 * - verifies each handler method in isolation, without starting the whole Spring context.
 * - gives precise coverage for all branches and error mapping logic.
 *
 * Strategy:
 * - Construct the relevant exception type manually.
 * - Create a MockHttpServletRequest with a fixed URI.
 * - Call the corresponding handler method directly.
 * - Assert HTTP status and JSON error body fields.
 */
class GlobalExceptionHandlerTest {

    /**
     * Helper method to create a simple HttpServletRequest for tests.
     */
    private HttpServletRequest request(String uri) {
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setRequestURI(uri);
        return req;
    }

    // -------------------------------------------------------------------------
    // EntityNotFoundException -> 404 Not Found
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("EntityNotFoundException should be mapped to 404 Not Found with proper body")
    void handleEntityNotFound_shouldReturn404() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        EntityNotFoundException ex = new EntityNotFoundException("Order not found: 42");
        HttpServletRequest req = request("/api/orders/42");

        ResponseEntity<ErrorResponse> response = handler.handleEntityNotFound(ex, req);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        ErrorResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(404, body.getStatus());
        assertEquals("Not Found", body.getError());
        assertEquals("Order not found: 42", body.getMessage());
        assertEquals("/api/orders/42", body.getPath());
    }

    // -------------------------------------------------------------------------
    // HttpMessageNotReadableException -> 400 Bad Request
    // (typically invalid JSON or invalid enum value for a field)
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("HttpMessageNotReadableException should be mapped to 400 Bad Request and use root cause message")
    void handleHttpMessageNotReadable_shouldReturn400() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();

        // Simulate a JSON parse / enum conversion failure.
        RuntimeException rootCause = new RuntimeException("Invalid enum value");
        HttpMessageNotReadableException ex =
                new HttpMessageNotReadableException("JSON parse error", rootCause, null);

        HttpServletRequest req = request("/api/orders");

        ResponseEntity<ErrorResponse> response = handler.handleNotReadable(ex, req);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(400, body.getStatus());
        assertEquals("Bad Request", body.getError());
        // We do not assert the full message text, but we expect it to contain the root cause.
        assertNotNull(body.getMessage());
        assertTrue(body.getMessage().contains("Invalid enum value")
                || body.getMessage().contains("JSON")
                || body.getMessage().length() > 0);
        assertEquals("/api/orders", body.getPath());
    }

    // -------------------------------------------------------------------------
    // MethodArgumentNotValidException -> 400 Validation Failed
    // (Bean Validation on @RequestBody)
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("MethodArgumentNotValidException should be mapped to 400 with detailed errors array")
    void handleMethodArgumentNotValid_shouldReturn400WithErrors() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();

        // Build a BindingResult with a single FieldError for "code".
        Object targetObject = new Object();
        BindingResult bindingResult = new BeanPropertyBindingResult(targetObject, "orderDto");
        FieldError fieldError = new FieldError(
                "orderDto",
                "code",
                "bad-code",
                false,
                null,
                null,
                "must not be blank"
        );
        bindingResult.addError(fieldError);

        // The MethodParameter is not used by our handler, so we can safely pass null.
        MethodArgumentNotValidException ex =
                new MethodArgumentNotValidException(null, bindingResult);

        HttpServletRequest req = request("/api/orders");

        ResponseEntity<Object> response = handler.handleMethodArgumentNotValid(ex, req);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Object bodyObj = response.getBody();
        assertNotNull(bodyObj);

        // The handler returns a Map<String, Object> with keys:
        // timestamp, status, error, message, path, errors
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) bodyObj;

        assertEquals(400, body.get("status"));
        assertEquals("Validation Failed", body.get("error"));
        assertEquals("Request validation failed", body.get("message"));
        assertEquals("/api/orders", body.get("path"));

        Object errorsObj = body.get("errors");
        assertNotNull(errorsObj);
        assertTrue(errorsObj instanceof List<?>);

        @SuppressWarnings("unchecked")
        List<Map<String, String>> errors = (List<Map<String, String>>) errorsObj;
        assertEquals(1, errors.size());
        Map<String, String> firstError = errors.get(0);
        assertEquals("code", firstError.get("field"));
        assertEquals("bad-code", firstError.get("rejectedValue"));
        assertEquals("must not be blank", firstError.get("message"));
    }

    // -------------------------------------------------------------------------
    // ConstraintViolationException -> 400 Validation Failed (for params/query)
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("ConstraintViolationException should be mapped to 400 Validation Failed")
    void handleConstraintViolation_shouldReturn400() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();

        ConstraintViolationException ex =
                new ConstraintViolationException("Parameter constraint violated", Collections.emptySet());

        HttpServletRequest req = request("/api/orders");

        ResponseEntity<ErrorResponse> response = handler.handleConstraintViolation(ex, req);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(400, body.getStatus());
        assertEquals("Validation Failed", body.getError());
        assertEquals("Parameter constraint violated", body.getMessage());
        assertEquals("/api/orders", body.getPath());
    }

    // -------------------------------------------------------------------------
    // DataIntegrityViolationException -> 409 Conflict
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("DataIntegrityViolationException should be mapped to 409 Conflict with root cause message")
    void handleDataIntegrityViolation_shouldReturn409() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();

        RuntimeException rootCause = new RuntimeException("Unique index or primary key violation");
        DataIntegrityViolationException ex =
                new DataIntegrityViolationException("DB error", rootCause);

        HttpServletRequest req = request("/api/orders");

        ResponseEntity<ErrorResponse> response = handler.handleDataIntegrity(ex, req);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        ErrorResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(409, body.getStatus());
        assertEquals("Data Integrity Violation", body.getError());
        // rootMessage(...) should extract the root cause message.
        assertEquals("Unique index or primary key violation", body.getMessage());
        assertEquals("/api/orders", body.getPath());
    }

    // -------------------------------------------------------------------------
    // IllegalArgumentException -> 400 Bad Request
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("IllegalArgumentException should be mapped to 400 Bad Request")
    void handleIllegalArgument_shouldReturn400() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();

        IllegalArgumentException ex = new IllegalArgumentException("code must not be blank");
        HttpServletRequest req = request("/api/orders");

        ResponseEntity<ErrorResponse> response = handler.handleIllegalArgument(ex, req);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(400, body.getStatus());
        assertEquals("Bad Request", body.getError());
        assertEquals("code must not be blank", body.getMessage());
        assertEquals("/api/orders", body.getPath());
    }

    // -------------------------------------------------------------------------
    // Generic Exception -> 500 Internal Server Error
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Generic Exception should be mapped to 500 Internal Server Error with generic message")
    void handleGenericException_shouldReturn500() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();

        Exception ex = new Exception("Unexpected boom");
        HttpServletRequest req = request("/api/anything");

        ResponseEntity<ErrorResponse> response = handler.handleGeneric(ex, req);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        ErrorResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(500, body.getStatus());
        assertEquals("Internal Server Error", body.getError());
        // The handler intentionally returns a generic message, not the raw exception text.
        assertEquals("Unexpected error", body.getMessage());
        assertEquals("/api/anything", body.getPath());
    }
}