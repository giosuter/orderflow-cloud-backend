package ch.devprojects.orderflow.web;

import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;

/**
 * Global exception handler. Covers: bad JSON (400), bean validation (422), not
 * found (404), DB unique conflicts (409), illegal args (400), and a safe 500
 * fallback.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

	// 400 – bad client input (e.g., illegal enum value)
	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest req) {
		return ResponseEntity.badRequest()
				.body(new ErrorResponse(400, "Bad Request", ex.getMessage(), req.getRequestURI()));
	}

	// 422 – @Valid body errors
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
		String msg = ex.getBindingResult().getFieldErrors().stream()
				.map(fe -> fe.getField() + ": " + fe.getDefaultMessage()).collect(Collectors.joining("; "));
		return ResponseEntity.unprocessableEntity().body(new ErrorResponse(422, "Unprocessable Entity",
				msg.isBlank() ? "Validation failed" : msg, req.getRequestURI()));
	}

	// 422 – @Validated on parameters (e.g., query/path) violations
	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex,
			HttpServletRequest req) {
		String msg = ex.getConstraintViolations().stream().map(v -> v.getPropertyPath() + ": " + v.getMessage())
				.collect(Collectors.joining("; "));
		return ResponseEntity.unprocessableEntity().body(new ErrorResponse(422, "Unprocessable Entity",
				msg.isBlank() ? "Constraint violation" : msg, req.getRequestURI()));
	}

	// 404 – entity not found in service layer lookups
	@ExceptionHandler(NoSuchElementException.class)
	public ResponseEntity<ErrorResponse> handleNotFound(NoSuchElementException ex, HttpServletRequest req) {
		return ResponseEntity.status(404).body(new ErrorResponse(404, "Not Found",
				ex.getMessage() == null ? "Resource not found" : ex.getMessage(), req.getRequestURI()));
	}

	// 409 – unique keys / FK problems, etc.
	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<ErrorResponse> handleDataIntegrity(DataIntegrityViolationException ex,
			HttpServletRequest req) {
		return ResponseEntity.status(409).body(new ErrorResponse(409, "Conflict", "Data integrity violation: "
				+ (ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getMessage() : ex.getMessage()),
				req.getRequestURI()));
	}

	// 500 – last resort
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleAny(Exception ex, HttpServletRequest req) {
		return ResponseEntity.status(500)
				.body(new ErrorResponse(500, "Internal Server Error", "Unexpected error", req.getRequestURI()));
	}
}