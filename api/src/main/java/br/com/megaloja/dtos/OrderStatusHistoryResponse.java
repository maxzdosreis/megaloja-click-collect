package br.com.megaloja.dtos;

import br.com.megaloja.models.enums.OrderStatus;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.time.LocalDateTime;

@JsonPropertyOrder({"id", "orderId", "status", "description", "createdAt"})
public record OrderStatusHistoryResponse(
        Long id,
        Long orderId,
        OrderStatus status,
        String description,
        LocalDateTime createdAt
) {
}

