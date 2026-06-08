package br.com.megaloja.dtos;

import br.com.megaloja.models.enums.OrderStatus;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class UpdateOrderRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    OrderStatus status;

    LocalDateTime pickupDeadline;
}

