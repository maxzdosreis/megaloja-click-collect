package br.com.megaloja.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;

public record CreateProductRequest(

        @NotBlank(message = "O nome é obrigatório")
        String name,

        String description,

        @NotNull(message = "O preço é obrigatório")
        @Positive(message = "O preço deve ser um valor positivo")
        BigDecimal price,

        @Length(max = 500, message = "A URL da imagem não pode exceder o limite de 500 caracteres")
        String imageUrl
) {
}
