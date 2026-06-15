package br.com.megaloja.specifications;

import br.com.megaloja.filters.NotificationFilter;
import br.com.megaloja.models.Notification;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class NotificationSpecification {

    public static Specification<Notification> withFilters(NotificationFilter filter) {
        return Specification
                .where(hasOrderId(filter.getOrderId()))
                .and(hasSentAtBetween(filter.getSentAtFrom(), filter.getSentAtTo()))
                .and(isRead(filter.getIsRead()));
    }

    private static Specification<Notification> hasOrderId(Long orderId) {
        return (root, query, cb) -> orderId == null
                ? null : cb.equal(root.get("order").get("id"), orderId);
    }

    private static Specification<Notification> hasSentAtBetween(LocalDateTime sentAtFrom, LocalDateTime sentAtTo) {
        return (root, query, cb) -> {
            if (sentAtFrom == null && sentAtTo == null) {
                return null;
            } else if (sentAtFrom != null && sentAtTo != null) {
                return cb.between(root.get("sentAt"), sentAtFrom, sentAtTo);
            } else if (sentAtFrom != null) {
                return cb.greaterThanOrEqualTo(root.get("sentAt"), sentAtFrom);
            } else {
                return cb.lessThanOrEqualTo(root.get("sentAt"), sentAtTo);
            }
        };
    }

    private static Specification<Notification> isRead(Boolean isRead) {
        return (root, query, cb) -> isRead == null
                ? null : cb.equal(root.get("isRead"), isRead);
    }
}
