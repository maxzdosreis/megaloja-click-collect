package br.com.megaloja.dtos;

import br.com.megaloja.models.enums.OrderStatus;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.time.LocalDateTime;
import java.util.List;

@JsonPropertyOrder({"orderId", "status", "pickupCode", "pickupDeadline", "history"})
public record OrderTrackingResponse(
        Long orderId,
        OrderStatus status,
        String pickupCode,
        LocalDateTime pickupDeadline,
        List<OrderStatusHistoryResponse> history
) {
}
