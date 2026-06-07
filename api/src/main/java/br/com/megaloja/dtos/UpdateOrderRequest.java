package br.com.megaloja.dtos;

import br.com.megaloja.models.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;
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

    @NotNull(message = "O status é obrigatório")
    OrderStatus status;

    LocalDateTime pickupDeadline;
}

