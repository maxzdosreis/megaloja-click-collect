package br.com.megaloja.dtos;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"id", "name", "address", "city", "state", "active"})
public record StoreResponse(
        Long id,
        String name,
        String address,
        String city,
        String state,
        Boolean active
) {
}
