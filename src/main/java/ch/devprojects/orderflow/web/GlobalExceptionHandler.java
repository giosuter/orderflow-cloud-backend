package ch.devprojects.orderflow.web;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * IMPORTANT: limit scope to our own web package so we don't catch Springdoc endpoints.
 */
@RestControllerAdvice(basePackages = "ch.devprojects.orderflow.web")
public class GlobalExceptionHandler {

	@ExceptionHandler(EntityNotFoundException.class)
	public ResponseEntity<Map<String, Object>> handleNotFound(EntityNotFoundException ex) {
		return build(HttpStatus.NOT_FOUND, ex.getMessage());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
		String msg = ex.getBindingResult().getFieldErrors().stream().findFirst()
				.map(fe -> fe.getField() + ": " + fe.getDefaultMessage()).orElse("Validation error");
		return build(HttpStatus.UNPROCESSABLE_ENTITY, msg);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
		// Generic 500 for our controllers only (springdoc endpoints are excluded by
		// basePackages)
		return build(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error");
	}

	private ResponseEntity<Map<String, Object>> build(HttpStatus status, String message) {
		Map<String, Object> body = new HashMap<>();
		body.put("status", status.value());
		body.put("error", status.getReasonPhrase());
		body.put("message", message);
		body.put("path", ""); // optionally filled by a filter if you like
		body.put("timestamp", Instant.now().toString());
		return ResponseEntity.status(status).body(body);
	}
}