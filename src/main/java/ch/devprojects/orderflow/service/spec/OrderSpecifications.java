package ch.devprojects.orderflow.service.spec;

import java.math.BigDecimal;

import org.springframework.data.jpa.domain.Specification;

import ch.devprojects.orderflow.domain.Order;
import ch.devprojects.orderflow.domain.OrderStatus;
import jakarta.persistence.criteria.Expression;

/**
 * Central place for reusable JPA Specifications for Order queries.
 *
 * All specs are null-safe in the sense that they can be composed freely;
 * however, do NOT call them with null values unless documented.
 */
public final class OrderSpecifications {

	private OrderSpecifications() {
		// utility class
	}

	/**
	 * Returns a spec that always evaluates to true. Useful as a neutral element for
	 * .and() chaining.
	 */
	public static Specification<Order> alwaysTrue() {
		return (root, query, cb) -> cb.conjunction();
	}

	public static Specification<Order> statusEquals(OrderStatus status) {
		return (root, query, cb) -> cb.equal(root.get("status"), status);
	}

	/**
	 * Case-insensitive "contains" on code.
	 */
	public static Specification<Order> codeContainsIgnoreCase(String term) {
		return (root, query, cb) -> {
			Expression<String> code = cb.lower(root.get("code"));
			return cb.like(code, "%" + term.toLowerCase() + "%");
		};
	}

	/**
	 * Case-insensitive "contains" on customerName.
	 */
	public static Specification<Order> customerNameContainsIgnoreCase(String term) {
		return (root, query, cb) -> {
			Expression<String> customer = cb.lower(root.get("customerName"));
			return cb.like(customer, "%" + term.toLowerCase() + "%");
		};
	}

	/**
	 * Lexicographic lower bound (inclusive) for code.
	 */
	public static Specification<Order> codeGreaterThanOrEqualTo(String from) {
		return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("code"), from);
	}

	/**
	 * Lexicographic upper bound (inclusive) for code.
	 */
	public static Specification<Order> codeLessThanOrEqualTo(String to) {
		return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("code"), to);
	}

	public static Specification<Order> totalGreaterThanOrEqualTo(BigDecimal min) {
		return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("total"), min);
	}

	public static Specification<Order> totalLessThanOrEqualTo(BigDecimal max) {
		return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("total"), max);
	}
}