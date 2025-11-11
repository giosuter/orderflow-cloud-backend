package ch.devprojects.orderflow.web;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.Instant;

/**
 * Standard error payload
 */
public class ErrorResponse {
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	private Instant timestamp = Instant.now();
	private int status;
	private String error; // e.g. "Bad Request"
	private String message; // human message, safe to show
	private String path; // request path

	public ErrorResponse() {
	}

	public ErrorResponse(int status, String error, String message, String path) {
		this.status = status;
		this.error = error;
		this.message = message;
		this.path = path;
	}

	public Instant getTimestamp() {
		return timestamp;
	}

	public int getStatus() {
		return status;
	}

	public String getError() {
		return error;
	}

	public String getMessage() {
		return message;
	}

	public String getPath() {
		return path;
	}

	public void setTimestamp(Instant timestamp) {
		this.timestamp = timestamp;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public void setError(String error) {
		this.error = error;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setPath(String path) {
		this.path = path;
	}
}