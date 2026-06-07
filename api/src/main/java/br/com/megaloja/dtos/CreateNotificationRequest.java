package br.com.megaloja.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateNotificationRequest(

        @NotNull(message = "O id do pedido é obrigatório")
        Long orderId,

        @NotBlank(message = "O título é obrigatório")
        String title,

        @NotBlank(message = "A mensagem é obrigatória")
        String message
){
}

