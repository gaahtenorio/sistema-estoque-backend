package com.supermercado.estoque.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Dados que o front-end envia para cadastrar ou editar uma categoria.
 *
 * <p>Exemplo de corpo (POST /api/categorias):</p>
 * <pre>
 * {
 *   "nome": "Bebidas",
 *   "descricao": "Sucos, refrigerantes e águas"
 * }
 * </pre>
 */
public record CategoriaRequestDTO(

        @NotBlank(message = "O nome da categoria é obrigatório")
        @Size(min = 2, max = 80, message = "O nome deve ter entre 2 e 80 caracteres")
        String nome,

        @Size(max = 255, message = "A descrição deve ter no máximo 255 caracteres")
        String descricao
) {
}
