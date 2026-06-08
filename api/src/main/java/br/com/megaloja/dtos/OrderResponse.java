package br.com.megaloja.dtos;

import br.com.megaloja.models.enums.OrderStatus;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@JsonPropertyOrder({"id", "customerId", "customerName", "storeId", "storeName", "status", "pickupCode", "totalAmount", "pickupDeadline", "createdAt", "updatedAt", "items"})
public record OrderResponse(
        Long id,
        Long customerId,
        String customerName,
        Long storeId,
        String storeName,
        OrderStatus status,
        String pickupCode,
        BigDecimal totalAmount,
        LocalDateTime pickupDeadline,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<OrderItemResponse> items
) {
}

