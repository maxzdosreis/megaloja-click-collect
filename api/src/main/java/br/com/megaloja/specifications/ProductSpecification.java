package br.com.megaloja.specifications;

import br.com.megaloja.filters.ProductFilter;
import br.com.megaloja.models.Product;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class ProductSpecification {

    public static Specification<Product> withFilters(ProductFilter filter) {
        return Specification
                .where(hasName(filter.getName()))
                .and(hasPriceBetween(filter.getMinPrice(), filter.getMaxPrice()))
                .and(isActive(filter.getActive()));
    }

    private static Specification<Product> hasName(String name) {
        return (root, query, cb) -> name == null || name.isBlank()
                ? null : cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    private static Specification<Product> hasPriceBetween(BigDecimal minPrice, BigDecimal maxPrice) {
        return (root, query, cb) -> {
            if (minPrice == null && maxPrice == null) {
                return null;
            } else if (minPrice != null && maxPrice != null) {
                return cb.between(root.get("price"), minPrice, maxPrice);
            } else if (minPrice != null) {
                return cb.greaterThanOrEqualTo(root.get("price"), minPrice);
            } else {
                return cb.lessThanOrEqualTo(root.get("price"), maxPrice);
            }
        };
    }

    private static Specification<Product> isActive(Boolean active) {
        return (root, query, cb) -> active == null || !active
                ? null : cb.greaterThan(root.get("active"), 0);
    }
}
