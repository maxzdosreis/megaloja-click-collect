package br.com.megaloja.specifications;

import br.com.megaloja.filters.OrderFilter;
import br.com.megaloja.models.Order;
import br.com.megaloja.models.enums.OrderStatus;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OrderSpecification {

    public static Specification<Order> withFilters(OrderFilter filter) {
        return Specification
                .where(hasCustomerId(filter.getCustomerId()))
                .and(hasCustomerName(filter.getCustomerName()))
                .and(hasStoreId(filter.getStoreId()))
                .and(hasStatus(filter.getStatus()))
                .and(hasPickupCode(filter.getPickupCode()))
                .and(hasCreatedAtBetween(filter.getCreatedAtStart(), filter.getCreatedAtEnd()))
                .and(hasPickupDeadlineBetween(filter.getPickupDeadlineStart(), filter.getPickupDeadlineEnd()))
                .and(hasTotalAmountBetween(filter.getTotalAmountMin(), filter.getTotalAmountMax()));
    }

    private static Specification<Order> hasCustomerId(Long customerId) {
        return (root, query, cb) -> customerId == null
                ? null : cb.equal(root.get("customer").get("id"), customerId);
    }

    private static Specification<Order> hasCustomerName(String customerName) {
        return (root, query, cb) -> customerName == null || customerName.isBlank()
                ? null : cb.like(cb.lower(root.get("customer").get("name")), "%" + customerName.toLowerCase() + "%");
    }

    private static Specification<Order> hasStoreId(Long storeId) {
        return (root, query, cb) -> storeId == null
                ? null : cb.equal(root.get("store").get("id"), storeId);
    }

    private static Specification<Order> hasStatus(OrderStatus status) {
        return (root, query, cb) -> status == null
                ? null : cb.equal(root.get("status"), status);
    }

    private static Specification<Order> hasPickupCode(String pickupCode) {
        return (root, query, cb) -> pickupCode == null || pickupCode.isBlank()
                ? null : cb.equal(root.get("pickupCode"), pickupCode);
    }

    private static Specification<Order> hasCreatedAtBetween(LocalDateTime createdAtStart, LocalDateTime createdAtEnd) {
        return (root, query, cb) -> {
            if (createdAtStart == null && createdAtEnd == null) {
                return null;
            } else if (createdAtStart != null && createdAtEnd != null) {
                return cb.between(root.get("createdAt"), createdAtStart, createdAtEnd);
            } else if (createdAtStart != null) {
                return cb.greaterThanOrEqualTo(root.get("createdAt"), createdAtStart);
            } else {
                return cb.lessThanOrEqualTo(root.get("createdAt"), createdAtEnd);
            }
        };
    }

    private static Specification<Order> hasPickupDeadlineBetween(LocalDateTime pickupDeadlineStart, LocalDateTime pickupDeadlineEnd) {
        return (root, query, cb) -> {
            if (pickupDeadlineStart == null && pickupDeadlineEnd == null) {
                return null;
            } else if (pickupDeadlineStart != null && pickupDeadlineEnd != null) {
                return cb.between(root.get("pickupDeadline"), pickupDeadlineStart, pickupDeadlineEnd);
            } else if (pickupDeadlineStart != null) {
                return cb.greaterThanOrEqualTo(root.get("pickupDeadline"), pickupDeadlineStart);
            } else {
                return cb.lessThanOrEqualTo(root.get("pickupDeadline"), pickupDeadlineEnd);
            }
        };
    }

    private static Specification<Order> hasTotalAmountBetween(BigDecimal totalAmountMin, BigDecimal totalAmountMax) {
        return (root, query, cb) -> {
            if (totalAmountMin == null && totalAmountMax == null) {
                return null;
            } else if (totalAmountMin != null && totalAmountMax != null) {
                return cb.between(root.get("totalAmount"), totalAmountMin, totalAmountMax);
            } else if (totalAmountMin != null) {
                return cb.greaterThanOrEqualTo(root.get("totalAmount"), totalAmountMin);
            } else {
                return cb.lessThanOrEqualTo(root.get("totalAmount"), totalAmountMax);
            }
        };
    }
}
