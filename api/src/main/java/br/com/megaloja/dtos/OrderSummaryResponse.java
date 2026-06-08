package br.com.megaloja.dtos;

import br.com.megaloja.models.enums.OrderStatus;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@JsonPropertyOrder({"id", "customerName", "storeName", "status", "totalAmount", "createdAt"})
public record OrderSummaryResponse(
        Long id,
        String customerName,
        String storeName,
        OrderStatus status,
        BigDecimal totalAmount,
        LocalDateTime createdAt
) {
}
