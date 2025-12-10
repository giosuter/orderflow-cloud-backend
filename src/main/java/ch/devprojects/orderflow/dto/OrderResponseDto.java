package ch.devprojects.orderflow.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO used to expose Order data via the REST API.
 *
 * For now this is independent from any JPA entity. Later we can map from the
 * real Order entity to this DTO without breaking the API contract or the
 * Angular frontend.
 */
public class OrderResponseDto {

	// Unique identifier of the order
	private Long id;

	// Human-readable order code, e.g. "ORD-2025-0001"
	private String code;

	// Current lifecycle status, e.g. NEW, PROCESSING, PAID, SHIPPED, CANCELLED
	private String status;

	// Business actor: the customer that placed the order
	private String customerName;

	// System actor: person responsible for this order (can be null)
	private String assignedTo;

	// Monetary total of the order
	private BigDecimal total;

	// When the order was created; later this can be taken from the DB
	private LocalDateTime createdAt;	

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