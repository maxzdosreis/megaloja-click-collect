package br.com.megaloja.dtos;

import jakarta.validation.constraints.NotBlank;
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
public class UpdatePickupAuthorizationRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "O nome do autorizado é obrigatório")
    String authorizedName;

    @NotBlank(message = "O documento do autorizado é obrigatório")
    String authorizedDocument;
}

