package br.com.megaloja.dtos;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"id", "storeId", "productId", "quantity", "reservedQuantity"})
public record InventoryResponse(
        Long id,
        Long storeId,
        Long productId,
        Integer quantity,
        Integer reservedQuantity
) {
}

