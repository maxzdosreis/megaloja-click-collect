package br.com.megaloja.dtos;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.math.BigDecimal;

@JsonPropertyOrder({"id", "productId", "orderId", "quantity", "unitPrice"})
public record OrderItemResponse(
        Long id,
        Long productId,
        Long orderId,
        Integer quantity,
        BigDecimal unitPrice
) {
}

