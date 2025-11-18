package ch.devprojects.orderflow.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import ch.devprojects.orderflow.domain.Order;
import ch.devprojects.orderflow.domain.OrderStatus;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

/**
 * Unit tests for {@link OrderSpecifications}.
 *
 * Intent:
 *  - Treats the Specification helpers as small, pure "query builders".
 *  - It does NOT need a real database or repository here.
 *  - Instead, it is verified:
 *      * that each specification can be created (not null)
 *      * that calling toPredicate(...) uses the CriteriaBuilder and returns
 *        the Predicate provided by it (for the simple specs).
 *
 */
@ExtendWith(MockitoExtension.class)
class OrderSpecificationsTest {

    // Mocks for the JPA Criteria API types that Spring Data JPA passes in.
    @Mock
    private Root<Order> root;

    @Mock
    private CriteriaQuery<?> query;

    @Mock
    private CriteriaBuilder cb;

    // -------------------------------------------------------------------------
    // codeContainsIgnoreCase(...)
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("codeContainsIgnoreCase(term)")
    class CodeContainsIgnoreCaseTests {

        @Test
        @DisplayName("should build a LIKE predicate on the code column")
        void codeContainsIgnoreCase_shouldBuildLikePredicate() {
            // Arrange: raw Path mock for the "code" column.
            // We deliberately use raw types here to avoid Mockito + generics issues.
            @SuppressWarnings({ "rawtypes", "unchecked" })
            Path codePath = mock(Path.class);

            // When the Specification calls root.get("code"), we return our mocked path.
            when(root.get("code")).thenReturn(codePath);

            // Stub the final LIKE call. We don't care about the exact Expression type,
            // so we use any() for the first argument.
            Predicate expectedPredicate = mock(Predicate.class);
            when(cb.like(any(), eq("%ord-1%"))).thenReturn(expectedPredicate);

            // Act: build the spec and invoke toPredicate(...)
            Specification<Order> spec = OrderSpecifications.codeContainsIgnoreCase("  ORD-1  ");
            assertNotNull(spec, "Specification must not be null");

            Predicate result = spec.toPredicate(root, query, cb);

            // Assert: the predicate we get is exactly the one returned by cb.like(...)
            assertSame(expectedPredicate, result, "Should return the Predicate from cb.like(...)");

            // Verify that the 'code' column was accessed and LIKE was built.
            verify(root).get("code");
            verify(cb).like(any(), eq("%ord-1%"));
        }
    }

    // -------------------------------------------------------------------------
    // statusEquals(...)
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("statusEquals(status)")
    class StatusEqualsTests {

        @Test
        @DisplayName("should build an EQUAL predicate on the status column")
        void statusEquals_shouldBuildEqualsPredicate() {
            // Arrange: again use a raw Path to avoid generic clashes.
            @SuppressWarnings({ "rawtypes", "unchecked" })
            Path statusPath = mock(Path.class);

            when(root.get("status")).thenReturn(statusPath);

            Predicate expectedPredicate = mock(Predicate.class);
            // The combination (raw Path + concrete enum value) is fine for Mockito.
            when(cb.equal(statusPath, OrderStatus.NEW)).thenReturn(expectedPredicate);

            // Act
            Specification<Order> spec = OrderSpecifications.statusEquals(OrderStatus.NEW);
            assertNotNull(spec, "Specification must not be null");

            Predicate result = spec.toPredicate(root, query, cb);

            // Assert
            assertSame(expectedPredicate, result, "Should return the Predicate from cb.equal(...)");
            verify(root).get("status");
            verify(cb).equal(statusPath, OrderStatus.NEW);
        }
    }

    // -------------------------------------------------------------------------
    // alwaysTrue()
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("alwaysTrue()")
    class AlwaysTrueTests {

        @Test
        @DisplayName("should return cb.conjunction()")
        void alwaysTrue_shouldReturnConjunction() {
            // Arrange
            Predicate expectedConjunction = mock(Predicate.class);
            when(cb.conjunction()).thenReturn(expectedConjunction);

            // Act
            Specification<Order> spec = OrderSpecifications.alwaysTrue();
            assertNotNull(spec, "Specification must not be null");

            Predicate result = spec.toPredicate(root, query, cb);

            // Assert
            assertSame(expectedConjunction, result, "Should return the Predicate from cb.conjunction()");
            verify(cb).conjunction();
        }
    }

    // -------------------------------------------------------------------------
    // Combined specs
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("combined specifications")
    class CombinedSpecsTests {

        @Test
        @DisplayName("alwaysTrue().and(statusEquals(...)) should be invokable without errors")
        void combinedSpecs_shouldFilterByStatus() {
            // Arrange: raw Path again
            @SuppressWarnings({ "rawtypes", "unchecked" })
            Path statusPath = mock(Path.class);

            when(root.get("status")).thenReturn(statusPath);

            Predicate statusPredicate = mock(Predicate.class);
            when(cb.equal(statusPath, OrderStatus.PAID)).thenReturn(statusPredicate);

            Predicate conjunction = mock(Predicate.class);
            when(cb.conjunction()).thenReturn(conjunction);

            // Intentionally do NOT stub cb.and(...), because the actual value
            // is not important for this test. We only care that no exception is thrown
            // and that the underlying specs are exercised.
            // In production, a real CriteriaBuilder will return a proper Predicate here.

            // Act: combine "alwaysTrue" with "statusEquals"
            Specification<Order> spec = OrderSpecifications.alwaysTrue()
                    .and(OrderSpecifications.statusEquals(OrderStatus.PAID));

            assertNotNull(spec, "Combined Specification must not be null");

            // This must not throw; result may be null because cb.and(...) is not stubbed.
            spec.toPredicate(root, query, cb);

            // Verify that at least it interacts with the criteria objects as expected.
            verify(root).get("status");
            verify(cb).equal(statusPath, OrderStatus.PAID);
            verify(cb).conjunction();
        }
    }
}