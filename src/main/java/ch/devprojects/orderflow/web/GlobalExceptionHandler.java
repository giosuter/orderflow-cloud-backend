package ch.devprojects.orderflow.web;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;

/**
 * Centralized REST exception mapping. IMPORTANT: Only one generic handler for
 * Exception to avoid ambiguity.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

	// 404 – entity not found
	@ExceptionHandler(EntityNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleEntityNotFound(EntityNotFoundException ex, HttpServletRequest req) {
		return buildError(HttpStatus.NOT_FOUND, "Not Found", ex.getMessage(), req.getRequestURI());
	}

	// 400 – unreadable/malformed JSON, enum parse errors, etc.
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ErrorResponse> handleNotReadable(HttpMessageNotReadableException ex, HttpServletRequest req) {
		return buildError(HttpStatus.BAD_REQUEST, "Bad Request", rootMessage(ex), req.getRequestURI());
	}

	// 400 – bean validation on @RequestBody
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpServletRequest req) {
		List<Map<String, String>> errors = ex.getBindingResult().getFieldErrors().stream().map(this::fieldErrorToMap)
				.collect(Collectors.toList());

		Map<String, Object> body = new LinkedHashMap<>();
		body.put("timestamp", Instant.now());
		body.put("status", HttpStatus.BAD_REQUEST.value());
		body.put("error", "Validation Failed");
		body.put("message", "Request validation failed");
		body.put("path", req.getRequestURI());
		body.put("errors", errors);

		return new ResponseEntity<>(body, new HttpHeaders(), HttpStatus.BAD_REQUEST);
	}

	// 400 – constraint violations on params/path/query (e.g., @Validated on
	// controllers)
	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex,
			HttpServletRequest req) {
		return buildError(HttpStatus.BAD_REQUEST, "Validation Failed", ex.getMessage(), req.getRequestURI());
	}

	// 409 – unique constraints, FK constraints, etc.
	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<ErrorResponse> handleDataIntegrity(DataIntegrityViolationException ex,
			HttpServletRequest req) {
		return buildError(HttpStatus.CONFLICT, "Data Integrity Violation", rootMessage(ex), req.getRequestURI());
	}

	// 400 – illegal arguments from service layer
	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest req) {
		return buildError(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage(), req.getRequestURI());
	}

	// 500 – FINAL CATCH-ALL (single method -> no ambiguity)
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleGeneric(Exception ex, HttpServletRequest req) {
		// During stabilization, keep this log; remove later if too noisy.
		ex.printStackTrace();
		return buildError(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", "Unexpected error",
				req.getRequestURI());
	}

	// ----------------- helpers -----------------

	private ResponseEntity<ErrorResponse> buildError(HttpStatus status, String error, String message, String path) {
		ErrorResponse body = new ErrorResponse(Instant.now(), status.value(), error, message, path);
		return ResponseEntity.status(status).body(body);
	}

	private String rootMessage(Throwable ex) {
		Throwable t = ex;
		while (t.getCause() != null)
			t = t.getCause();
		return t.getMessage() != null ? t.getMessage() : ex.getMessage();
	}

	private Map<String, String> fieldErrorToMap(FieldError fe) {
		Map<String, String> m = new LinkedHashMap<>();
		m.put("field", fe.getField());
		m.put("rejectedValue", fe.getRejectedValue() == null ? "null" : String.valueOf(fe.getRejectedValue()));
		m.put("message", fe.getDefaultMessage());
		return m;
	}
}