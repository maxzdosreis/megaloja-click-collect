package br.com.megaloja.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record CreateOrderRequest(

        @NotNull(message = "O id do cliente é obrigatório")
        Long customerId,

        @NotNull(message = "O id da loja é obrigatório")
        Long storeId,

        @NotEmpty(message = "Os itens do pedido são obrigatórios")
        List<CreateOrderItemRequest> items,

        @NotNull(message = "O valor total é obrigatório")
        @Positive(message = "O valor total deve ser positivo")
        BigDecimal totalAmount,

        LocalDateTime pickupDeadline
){
}

