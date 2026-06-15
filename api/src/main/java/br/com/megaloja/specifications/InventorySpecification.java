package br.com.megaloja.specifications;

import br.com.megaloja.filters.InventoryFilter;
import br.com.megaloja.models.Inventory;
import org.springframework.data.jpa.domain.Specification;

public class InventorySpecification {

    public static Specification<Inventory> withFilters(InventoryFilter filter) {
        return Specification
                .where(hasStoreId(filter.getStoreId()))
                .and(hasProductId(filter.getProductId()))
                .and(hasProductName(filter.getProductName()))
                .and(hasQuantityBetween(filter.getMinQuantity(), filter.getMaxQuantity()))
                .and(hasReservedQuantityBetween(filter.getReservedQuantityMin(), filter.getReservedQuantityMax()))
                .and(hasStock(filter.getHasStock()));
    }

    private static Specification<Inventory> hasStoreId(Long storeId) {
        return (root, query, cb) -> storeId == null
                ? null : cb.equal(root.get("store").get("id"), storeId);
    }

    private static Specification<Inventory> hasProductId(Long productId) {
        return (root, query, cb) -> productId == null
                ? null : cb.equal(root.get("product").get("id"), productId);
    }

    private static Specification<Inventory> hasProductName(String productName) {
        return (root, query, cb) -> productName == null || productName.isBlank()
                ? null : cb.like(cb.lower(root.get("product").get("name")), "%" + productName.toLowerCase() + "%");
    }

    private static Specification<Inventory> hasQuantityBetween(Integer minQuantity, Integer maxQuantity) {
        return (root, query, cb) -> {
            if (minQuantity == null && maxQuantity == null) {
                return null;
            } else if (minQuantity != null && maxQuantity != null) {
                return cb.between(root.get("quantity"), minQuantity, maxQuantity);
            } else if (minQuantity != null) {
                return cb.greaterThanOrEqualTo(root.get("quantity"), minQuantity);
            } else {
                return cb.lessThanOrEqualTo(root.get("quantity"), maxQuantity);
            }
        };
    }

    private static Specification<Inventory> hasReservedQuantityBetween(Integer reservedQuantityMin, Integer reservedQuantityMax) {
        return (root, query, cb) -> {
            if (reservedQuantityMin == null && reservedQuantityMax == null) {
                return null;
            } else if (reservedQuantityMin != null && reservedQuantityMax != null) {
                return cb.between(root.get("reservedQuantity"), reservedQuantityMin, reservedQuantityMax);
            } else if (reservedQuantityMin != null) {
                return cb.greaterThanOrEqualTo(root.get("reservedQuantity"), reservedQuantityMin);
            } else {
                return cb.lessThanOrEqualTo(root.get("reservedQuantity"), reservedQuantityMax);
            }
        };
    }

    private static Specification<Inventory> hasStock(Boolean hasStock) {
        return (root, query, cb) -> hasStock == null || !hasStock
                ? null : cb.greaterThan(root.get("quantity"), 0);
    }
}
