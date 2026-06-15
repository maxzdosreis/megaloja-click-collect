package br.com.megaloja.specifications;

import br.com.megaloja.filters.StoreFilter;
import br.com.megaloja.models.Store;
import org.springframework.data.jpa.domain.Specification;

public class StoreSpecification {

    public static Specification<Store> withFilters(StoreFilter filter) {
        return Specification
                .where(hasName(filter.getName()))
                .and(hasCity(filter.getCity()))
                .and(hasState(filter.getState()))
                .and(isActive(filter.getActive()));
    }

    private static Specification<Store> hasName(String name) {
        return (root, query, cb) -> name == null || name.isBlank()
                ? null : cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    private static Specification<Store> hasCity(String city) {
        return (root, query, cb) -> city == null || city.isBlank()
                ? null : cb.equal(root.get("city"), city);
    }

    private static Specification<Store> hasState(String state) {
        return (root, query, cb) -> state == null || state.isBlank()
                ? null : cb.equal(root.get("state"), state);
    }

    private static Specification<Store> isActive(Boolean active) {
        return (root, query, cb) -> active == null
                ? null : cb.equal(root.get("active"), active);
    }
}
