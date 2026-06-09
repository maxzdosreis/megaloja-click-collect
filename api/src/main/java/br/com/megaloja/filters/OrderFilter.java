package br.com.megaloja.filters;

import br.com.megaloja.models.enums.OrderStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class OrderFilter {

    private Long customerId;

    private String customerName;

    private Long storeId;

    private OrderStatus status;

    private String pickupCode;

    private LocalDateTime createdAtStart;

    private LocalDateTime createdAtEnd;

    private LocalDateTime pickupDeadlineStart;

    private LocalDateTime pickupDeadlineEnd;

    private BigDecimal totalAmountMin;

    private BigDecimal totalAmountMax;

}
