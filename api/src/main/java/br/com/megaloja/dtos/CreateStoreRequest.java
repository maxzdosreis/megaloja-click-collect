package br.com.megaloja.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateStoreRequest(

        @NotBlank(message = "O nome é obrigatório")
        String name,

        @NotBlank(message = "O endereço é obrigatório")
        String address,

        @NotBlank(message = "A cidade é obrigatório")
        String city,

        @NotBlank(message = "O estado é obrigatório")
        @Size(max = 100, message = "O estado não pode exceder o limite de 100 caracteres")
        String state
) {
}
