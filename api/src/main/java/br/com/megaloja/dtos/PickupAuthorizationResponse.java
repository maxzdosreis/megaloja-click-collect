package br.com.megaloja.dtos;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.time.LocalDateTime;

@JsonPropertyOrder({"id", "orderId", "authorizedName", "authorizedDocument", "createdAt"})
public record PickupAuthorizationResponse(
        Long id,
        Long orderId,
        String authorizedName,
        String authorizedDocument,
        LocalDateTime createdAt
) {
}

