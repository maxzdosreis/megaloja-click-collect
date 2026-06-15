package br.com.megaloja.specifications;

import br.com.megaloja.filters.UserFilter;
import br.com.megaloja.models.User;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecification {

    public static Specification<User> withFilters(UserFilter filter) {
        return Specification
                .where(hasName(filter.getName()))
                .and(hasEmail(filter.getEmail()))
                .and(hasRole(filter.getRole()));
    }

    private static Specification<User> hasName(String name) {
        return (root, query, cb) -> name == null || name.isBlank()
                ? null : cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    private static Specification<User> hasEmail(String email) {
        return (root, query, cb) -> email == null || email.isBlank()
                ? null : cb.like(cb.lower(root.get("email")), "%" + email.toLowerCase() + "%");
    }

    private static Specification<User> hasRole(String role) {
        return (root, query, cb) -> role == null || role.isBlank()
                ? null : cb.equal(root.get("role"), role);
    }
}
