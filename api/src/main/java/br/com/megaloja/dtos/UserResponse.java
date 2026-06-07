package br.com.megaloja.dtos;

import br.com.megaloja.models.enums.UserRole;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.time.LocalDateTime;

@JsonPropertyOrder({"id", "name", "email", "role", "createdAt"})
public record UserResponse(
        Long id,
        String name,
        String email,
        UserRole role,
        LocalDateTime createdAt
) {
}
