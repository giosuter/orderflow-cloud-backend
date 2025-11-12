package ch.devprojects.orderflow.dto;

import java.math.BigDecimal;
import java.time.Instant;

import ch.devprojects.orderflow.domain.OrderStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * OrderDto is the request/response contract for the Order API. Validation runs
 * here so invalid requests fail with HTTP 422. 'status' is an OrderStatus enum,
 * not a String.
 */
public class OrderDto {

	private Long id;

	@NotBlank(message = "code must not be blank")
	private String code;

	@NotNull(message = "status must not be null")
	private OrderStatus status; // <-- use enum type (fix)

	@NotNull(message = "total must not be null")
	@DecimalMin(value = "0.00", inclusive = false, message = "total must be > 0")
	private BigDecimal total;

	// Optional API-only field (not stored in DB/entity yet)
	private String customerName;

	private Instant createdAt;
	private Instant updatedAt;

	public OrderDto() {
	}

	// --- Getters / Setters ---
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

	public OrderStatus getStatus() {
		return status;
	} // <-- enum getter

	public void setStatus(OrderStatus status) {
		this.status = status;
	} // <-- enum setter

	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
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
}