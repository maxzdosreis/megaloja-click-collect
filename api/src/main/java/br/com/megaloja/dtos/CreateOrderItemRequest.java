package br.com.megaloja.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CreateOrderItemRequest(

        @NotNull(message = "O ID do produto é obrigatório")
        Long productId,

        @NotNull(message = "A quantidade é obrigatória")
        @Positive(message = "A quantidade deve ser positiva")
        Integer quantity,

        @NotNull(message = "O preço unitário é obrigatório")
        BigDecimal unitPrice
) {
}
