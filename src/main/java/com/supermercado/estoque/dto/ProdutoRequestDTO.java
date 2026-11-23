
package com.supermercado.estoque.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ProdutoRequestDTO(

        @NotBlank(message = "O nome do produto é obrigatório")
        @Size(max = 120, message = "O nome deve ter no máximo 120 caracteres")
        String nome,

        @Size(max = 50, message = "O código de barras deve ter no máximo 50 caracteres")
        String codigoBarras,

        @DecimalMin(value = "0.0", message = "O preço não pode ser negativo")
        BigDecimal preco,

        @NotNull(message = "O estoque mínimo é obrigatório")
        @PositiveOrZero(message = "O estoque mínimo não pode ser negativo")
        Integer estoqueMinimo,

        @NotNull(message = "A categoria é obrigatória")
        @Positive(message = "O ID da categoria deve ser positivo")
        Long categoriaId

) {
}
