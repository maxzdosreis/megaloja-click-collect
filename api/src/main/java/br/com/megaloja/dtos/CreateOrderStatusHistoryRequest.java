package br.com.megaloja.dtos;

import br.com.megaloja.models.enums.OrderStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateOrderStatusHistoryRequest(

        @NotNull(message = "O id do pedido é obrigatório")
        Long orderId,

        @NotNull(message = "O status é obrigatório")
        OrderStatus status,

        @NotBlank(message = "A descrição é obrigatória")
        String description
){
}

