package ch.devprojects.orderflow.service;

import java.math.BigDecimal;

import org.springframework.data.jpa.domain.Specification;

import ch.devprojects.orderflow.domain.Order;
import ch.devprojects.orderflow.domain.OrderStatus;
import jakarta.persistence.criteria.Expression;

/**
 * Central place for reusable JPA {@link Specification} builders for
 * {@link Order}.
 *
 * Important design rules: - Never return null predicates for "no filter" cases;
 * return cb.conjunction() instead. This makes the specs safe to compose via
 * spec.and(...) - Always cast attribute paths to the expected type (e.g.
 * .as(String.class)), otherwise CriteriaBuilder calls like cb.lower(...) may
 * behave as Path<Object> and break Mockito stubbing + type safety. - For
 * "contains ignore case" searches, we trim the input and then lower-case it.
 */
public final class OrderSpecifications {

	private OrderSpecifications() {
		// utility class
	}

	/**
	 * Spec that always evaluates to true (safe starting point for composition).
	 */
	public static Specification<Order> alwaysTrue() {
		return (root, query, cb) -> cb.conjunction();
	}

	/**
	 * WHERE lower(code) LIKE %value%
	 */
	public static Specification<Order> codeContainsIgnoreCase(String value) {
		return (root, query, cb) -> {
			String trimmed = (value == null) ? null : value.trim();
			if (trimmed == null || trimmed.isEmpty()) {
				return cb.conjunction();
			}

			Expression<String> codeExpr = cb.lower(root.get("code").as(String.class));
			String pattern = "%" + trimmed.toLowerCase() + "%";
			return cb.like(codeExpr, pattern);
		};
	}

	/**
	 * WHERE lower(customerName) LIKE %value%
	 */
	public static Specification<Order> customerNameContainsIgnoreCase(String value) {
		return (root, query, cb) -> {
			String trimmed = (value == null) ? null : value.trim();
			if (trimmed == null || trimmed.isEmpty()) {
				return cb.conjunction();
			}

			Expression<String> customerExpr = cb.lower(root.get("customerName").as(String.class));
			String pattern = "%" + trimmed.toLowerCase() + "%";
			return cb.like(customerExpr, pattern);
		};
	}

	/**
	 * WHERE status = :status
	 */
	public static Specification<Order> statusEquals(OrderStatus status) {
		return (root, query, cb) -> {
			if (status == null) {
				return cb.conjunction();
			}
			return cb.equal(root.get("status"), status);
		};
	}

	/**
	 * Code range filter.
	 *
	 * - if both from/to are blank -> TRUE - if only from -> code >= from - if only
	 * to -> code <= to - if both -> from <= code <= to
	 */
	public static Specification<Order> codeBetween(String from, String to) {
		return (root, query, cb) -> {
			String f = (from == null) ? null : from.trim();
			String t = (to == null) ? null : to.trim();

			boolean fBlank = (f == null || f.isEmpty());
			boolean tBlank = (t == null || t.isEmpty());

			if (fBlank && tBlank) {
				return cb.conjunction();
			}

			Expression<String> codeExpr = root.get("code").as(String.class);

			if (!fBlank && tBlank) {
				return cb.greaterThanOrEqualTo(codeExpr, f);
			}
			if (fBlank && !tBlank) {
				return cb.lessThanOrEqualTo(codeExpr, t);
			}
			return cb.between(codeExpr, f, t);
		};
	}

	/**
	 * Total range filter.
	 *
	 * - if both min/max null -> TRUE - if only min -> total >= min - if only max ->
	 * total <= max - if both -> min <= total <= max
	 */
	public static Specification<Order> totalBetween(BigDecimal min, BigDecimal max) {
		return (root, query, cb) -> {
			if (min == null && max == null) {
				return cb.conjunction();
			}

			Expression<BigDecimal> totalExpr = root.get("total").as(BigDecimal.class);

			if (min != null && max == null) {
				return cb.greaterThanOrEqualTo(totalExpr, min);
			}
			if (min == null && max != null) {
				return cb.lessThanOrEqualTo(totalExpr, max);
			}
			return cb.between(totalExpr, min, max);
		};
	}
}