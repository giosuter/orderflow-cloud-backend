package ch.devprojects.orderflow.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO returned to the frontend for order list / search results.
 *
 * Design choices (matching current tests + typical Angular expectations): -
 * status is a String (e.g. "NEW") rather than an enum - createdAt is
 * LocalDateTime (simple JSON representation)
 *
 * This avoids leaking JPA entities to the frontend.
 */
public class OrderResponseDto {

	private Long id;
	private String code;
	private String status;

	private String customerName;
	private String assignedTo;

	private BigDecimal total;
	private LocalDateTime createdAt;

	public OrderResponseDto() {
		// default constructor for JSON serialization
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getAssignedTo() {
		return assignedTo;
	}

	public void setAssignedTo(String assignedTo) {
		this.assignedTo = assignedTo;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
}