package ch.devprojects.orderflow.service;

import ch.devprojects.orderflow.domain.Order;
import ch.devprojects.orderflow.domain.OrderStatus;
import org.springframework.data.jpa.domain.Specification;

/**
 * Specification helpers for building type-safe JPA queries.
 */
public final class OrderSpecifications {

	private OrderSpecifications() {
	}

	public static Specification<Order> codeContainsIgnoreCase(String term) {
		final String like = "%" + term.toLowerCase().trim() + "%";
		return (root, query, cb) -> cb.like(cb.lower(root.get("code")), like);
	}

	public static Specification<Order> statusEquals(OrderStatus status) {
		return (root, query, cb) -> cb.equal(root.get("status"), status);
	}

	public static Specification<Order> alwaysTrue() {
		return (root, query, cb) -> cb.conjunction();
	}
}