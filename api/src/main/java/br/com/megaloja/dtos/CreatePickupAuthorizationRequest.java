package br.com.megaloja.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreatePickupAuthorizationRequest(

        @NotNull(message = "O id do pedido é obrigatório")
        Long orderId,

        @NotBlank(message = "O nome do autorizado é obrigatório")
        String authorizedName,

        @NotBlank(message = "O documento do autorizado é obrigatório")
        String authorizedDocument
){
}

