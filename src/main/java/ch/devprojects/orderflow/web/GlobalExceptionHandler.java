package ch.devprojects.orderflow.web;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * Global exception handler.
 * Covers: bad JSON (400), bean validation (422), not found (404), DB unique conflicts (409), 
 * illegal args (400), and a safe 500 fallback.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

	private ResponseEntity<ErrorResponse> build(HttpStatus status, String message, HttpServletRequest req) {
		ErrorResponse body = new ErrorResponse(status.value(), status.getReasonPhrase(), message, req.getRequestURI());
		return ResponseEntity.status(status).body(body);
	}

	/** 400 Bad Request – malformed JSON, wrong enum, etc. */
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ErrorResponse> badJson(HttpMessageNotReadableException ex, HttpServletRequest req) {
		return build(HttpStatus.BAD_REQUEST, rootMsg(ex), req);
	}

	/** 422 Unprocessable Entity – bean validation errors from @Valid */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> validation(MethodArgumentNotValidException ex, HttpServletRequest req) {
		String msg = ex.getBindingResult().getFieldErrors().stream().map(this::formatFieldError)
				.collect(Collectors.joining("; "));
		if (msg.isBlank())
			msg = "Validation failed";
		return build(HttpStatus.UNPROCESSABLE_ENTITY, msg, req);
	}

	private String formatFieldError(FieldError fe) {
		String field = fe.getField();
		String code = fe.getCode();
		String def = fe.getDefaultMessage();
		return field + ": " + (def != null ? def : code);
	}

	/** 404 Not Found – when service throws EntityNotFoundException */
	@ExceptionHandler(EntityNotFoundException.class)
	public ResponseEntity<ErrorResponse> notFound(EntityNotFoundException ex, HttpServletRequest req) {
		return build(HttpStatus.NOT_FOUND, rootMsg(ex), req);
	}

	/** 409 Conflict – unique constraint violations (e.g., duplicate order code) */
	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<ErrorResponse> conflict(DataIntegrityViolationException ex, HttpServletRequest req) {
		return build(HttpStatus.CONFLICT, friendlyConstraintMessage(ex), req);
	}

	/** 400 Bad Request – generic illegal arguments in service layer */
	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ErrorResponse> illegalArg(IllegalArgumentException ex, HttpServletRequest req) {
		return build(HttpStatus.BAD_REQUEST, rootMsg(ex), req);
	}

	/** 500 – catch-all */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> generic(Exception ex, HttpServletRequest req) {
		return build(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error", req);
	}

	private String rootMsg(Throwable t) {
		Throwable r = t;
		while (r.getCause() != null)
			r = r.getCause();
		return r.getMessage() != null ? r.getMessage() : t.getClass().getSimpleName();
	}

	private String friendlyConstraintMessage(DataIntegrityViolationException ex) {
		String m = rootMsg(ex);
		// Soft parsing for common messages; adjust to your DB messages if needed
		if (m != null && m.toLowerCase().contains("unique")) {
			return "Duplicate value violates unique constraint";
		}
		return "Data integrity violation";
	}
}