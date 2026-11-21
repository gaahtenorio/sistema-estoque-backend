package com.supermercado.estoque.dto;

import com.supermercado.estoque.model.Categoria;

/**
 * Dados que a API devolve por categoria.
 *
 * <pre>
 * {
 *   "id": 1,
 *   "nome": "Bebidas",
 *   "descricao": "Sucos, refrigerantes e águas",
 *   "quantidadeProdutos": 12
 * }
 * </pre>
 */
public record CategoriaResponseDTO(
        Long id,
        String nome,
        String descricao,
        long quantidadeProdutos
) {

    public static CategoriaResponseDTO fromEntity(Categoria categoria) {
        return fromEntity(categoria, 0L);
    }

    public static CategoriaResponseDTO fromEntity(Categoria categoria, long quantidadeProdutos) {
        return new CategoriaResponseDTO(
                categoria.getId(),
                categoria.getNome(),
                categoria.getDescricao(),
                quantidadeProdutos
        );
    }
}
