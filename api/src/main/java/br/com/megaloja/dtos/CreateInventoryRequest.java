package br.com.megaloja.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record CreateInventoryRequest(

        @NotNull(message = "O id da loja é obrigatório")
        Long storeId,

        @NotNull(message = "O id do produto é obrigatório")
        Long productId,

        @NotNull(message = "A quantidade é obrigatória")
        @PositiveOrZero(message = "A quantidade não pode ser negativa")
        Integer quantity
){
}

