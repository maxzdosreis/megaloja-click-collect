package br.com.megaloja.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CreateOrderItemRequest(

        @NotNull(message = "O ID do produto é obrigatório")
        Long productId,

        @NotNull(message = "O ID de order é obrigatório")
        Long orderId,

        @NotNull(message = "A quantidade é obrigatória")
        @Positive(message = "A quantidade deve ser positiva")
        @Min(value = 1, message = "A quantidade mínima é 1")
        Integer quantity,

        @NotNull(message = "O preço unitário é obrigatório")
        BigDecimal unitPrice
) {
}
