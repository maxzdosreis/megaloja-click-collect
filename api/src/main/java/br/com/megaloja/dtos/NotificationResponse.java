package br.com.megaloja.dtos;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.time.LocalDateTime;

@JsonPropertyOrder({"id", "orderId", "title", "message", "sentAt", "isRead"})
public record NotificationResponse(
        Long id,
        Long orderId,
        String title,
        String message,
        LocalDateTime sentAt,
        Boolean isRead
) {
}

