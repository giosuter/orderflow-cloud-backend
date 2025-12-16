package ch.devprojects.orderflow.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

/**
 * Unit tests for OrderSpecifications.
 *
 * We don't execute SQL here. We just verify the CriteriaBuilder interactions.
 */
class OrderSpecificationsTest {

	@Nested
	@DisplayName("codeContainsIgnoreCase(...)")
	class CodeContainsIgnoreCaseTests {

		@Test
		@DisplayName("should build lower(code) LIKE %value% (trimmed + lowercased)")
		void codeContainsIgnoreCase_shouldBuildLikePredicate() {
			@SuppressWarnings("unchecked")
			Root<ch.devprojects.orderflow.domain.Order> root = Mockito.mock(Root.class);
			@SuppressWarnings("unchecked")
			CriteriaQuery<Object> query = Mockito.mock(CriteriaQuery.class);

			CriteriaBuilder cb = Mockito.mock(CriteriaBuilder.class);

			// root.get("code") is a Path<Object> at compile-time; we treat it as raw and
			// then stub .as(String.class)
			@SuppressWarnings("unchecked")
			Path<Object> rawCodePath = Mockito.mock(Path.class);

			@SuppressWarnings("unchecked")
			Expression<String> codeAsString = Mockito.mock(Expression.class);

			@SuppressWarnings("unchecked")
			Expression<String> lowered = Mockito.mock(Expression.class);

			Predicate predicate = Mockito.mock(Predicate.class);

			when(root.get("code")).thenReturn(rawCodePath);
			when(rawCodePath.as(String.class)).thenReturn(codeAsString);
			when(cb.lower(codeAsString)).thenReturn(lowered);

			// stub like(...) with any pattern
			when(cb.like(Mockito.eq(lowered), anyString())).thenReturn(predicate);

			Predicate result = OrderSpecifications.codeContainsIgnoreCase("  ORD-1  ").toPredicate(root, query, cb);

			assertThat(result).isSameAs(predicate);

			ArgumentCaptor<String> patternCaptor = ArgumentCaptor.forClass(String.class);
			verify(cb).like(Mockito.eq(lowered), patternCaptor.capture());

			assertThat(patternCaptor.getValue()).isEqualTo("%ord-1%");
		}
	}
}