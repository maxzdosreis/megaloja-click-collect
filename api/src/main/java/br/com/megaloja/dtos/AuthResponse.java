package br.com.megaloja.dtos;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"token", "refreshToken", "tokenType", "expiresIn", "user"})
public record AuthResponse(
        String token,
        String refreshToken,
        String tokenType,
        long expiresIn,
        UserResponse user
) {
}
