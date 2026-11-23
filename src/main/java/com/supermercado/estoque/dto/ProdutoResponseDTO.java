
package com.supermercado.estoque.dto;

import com.supermercado.estoque.model.Produto;

import java.math.BigDecimal;

public record ProdutoResponseDTO(

        Long id,
        String nome,
        String codigoBarras,
        BigDecimal preco,
        Integer quantidadeEmEstoque,
        Integer estoqueMinimo,
        Long categoriaId,
        String categoriaNome

) {

    public static ProdutoResponseDTO fromEntity(Produto produto) {

        return new ProdutoResponseDTO(
                produto.getId(),
                produto.getNome(),
                produto.getCodigoBarras(),
                produto.getPreco(),
                produto.getQuantidadeEmEstoque(),
                produto.getEstoqueMinimo(),
                produto.getCategoria().getId(),
                produto.getCategoria().getNome()
        );
    }
}
