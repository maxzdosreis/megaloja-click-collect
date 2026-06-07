package br.com.megaloja.dtos;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class UpdateNotificationRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    Boolean isRead;
}

