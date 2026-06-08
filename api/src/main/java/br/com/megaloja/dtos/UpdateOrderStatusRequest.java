package br.com.megaloja.dtos;

import br.com.megaloja.models.enums.OrderStatus;

public record UpdateOrderStatusRequest (

        OrderStatus status

) {
}


