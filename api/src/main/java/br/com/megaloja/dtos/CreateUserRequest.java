package br.com.megaloja.dtos;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(

        @NotBlank(message = "O nome é obrigatório")
        String name,

        @Email(message = "O email deve ser válido")
        @NotBlank(message = "O email é obrigatório")
        @Pattern(
                regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
                message = "O email deve ter um formato válido"
        )
        @Size(max = 100, message = "O email não pode exceder o limite de 100 caracteres")
        String email,

        @NotBlank(message = "A senha é obrigatória")
        @Size(min = 6, message = "A senha deve conter pelo menos 6 caracteres")
        String password
) {
}
