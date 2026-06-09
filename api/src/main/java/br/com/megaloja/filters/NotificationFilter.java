package br.com.megaloja.filters;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class NotificationFilter {

    private Long orderId;

    private LocalDateTime sentAtFrom;

    private LocalDateTime sentAtTo;

    private Boolean isRead;

}
