package ch.devprojects.orderflow.dto;

import java.math.BigDecimal;
import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonAlias;

/**
 * DTO used for create/update and also for list/search responses.
 *
 * Contract (stable for REST + Angular): - status is a String enum name ("NEW",
 * "PAID", ...) - description is the canonical free-text field
 *
 * Backward compatibility: - older clients might still send "comment" -> we
 * accept it via @JsonAlias
 */
public class OrderDto {

	private Long id;
	private String code;
	private String customerName;
	private BigDecimal total;

	private Instant createdAt;
	private Instant updatedAt;

	/**
	 * Enum name as String to keep the API simple for frontend clients.
	 */
	private String status;

	/**
	 * Canonical name (replaces comment). JsonAlias allows accepting "comment" from
	 * older payloads.
	 */
	@JsonAlias("comment")
	private String description;

	public OrderDto() {
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

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
	}

	public Instant getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Instant updatedAt) {
		this.updatedAt = updatedAt;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}