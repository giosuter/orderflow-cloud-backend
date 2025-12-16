package ch.devprojects.orderflow.dto;

import java.math.BigDecimal;
import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonAlias;

/**
 * Read-only response DTO.
 *
 * Kept separate from OrderDto in case you want to: - hide fields - add derived
 * fields - prevent certain fields from being accepted on create/update
 *
 * For now it mirrors OrderDto in a safe, explicit way.
 */
public class OrderResponseDto {

	private Long id;
	private String code;
	private String status;
	private BigDecimal total;
	private String customerName;

	/**
	 * Canonical free-text field. JsonAlias is kept for tolerance if this DTO is
	 * ever reused as input.
	 */
	@JsonAlias("comment")
	private String description;

	private Instant createdAt;
	private Instant updatedAt;

	public OrderResponseDto() {
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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