package ch.devprojects.orderflow.domain;

import java.math.BigDecimal;
import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * JPA entity representing an Order.
 *
 * Important: - DB table is "orders" (see Flyway V1). - "description" is the
 * canonical free-text field (replaces former "comment"). - Keep the entity
 * simple (no business logic here).
 */
@Entity
@Table(name = "orders")
public class Order {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * Business code / human friendly identifier (e.g. "ORD-1001"). Must be unique
	 * (see Flyway V1).
	 */
	@Column(nullable = false, unique = true)
	private String code;

	/**
	 * Order lifecycle status stored as a String enum name (e.g. NEW, PAID).
	 */
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private OrderStatus status;

	/**
	 * Total amount of the order.
	 */
	@Column(nullable = false)
	private BigDecimal total;

	/**
	 * Optional customer name (added by Flyway V3).
	 */
	@Column(name = "customer_name")
	private String customerName;

	/**
	 * Canonical free-text field for orders.
	 *
	 * Replaces the old "comment" naming everywhere: - DB column: orders.description
	 * (Flyway V4) - API field: description (DTOs) - Frontend field: description
	 */
	@Column(length = 2000)
	private String description;

	/**
	 * Timestamps (kept as Instant for timezone-safe persistence and JSON).
	 */
	@Column(name = "created_at")
	private Instant createdAt;

	@Column(name = "updated_at")
	private Instant updatedAt;

	public Order() {
		// JPA needs a default constructor
	}

	public Long getId() {
		return id;
	}

	public String getCode() {
		return code;
	}

	public OrderStatus getStatus() {
		return status;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public String getCustomerName() {
		return customerName;
	}

	public String getDescription() {
		return description;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public Instant getUpdatedAt() {
		return updatedAt;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setStatus(OrderStatus status) {
		this.status = status;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
	}

	public void setUpdatedAt(Instant updatedAt) {
		this.updatedAt = updatedAt;
	}
}