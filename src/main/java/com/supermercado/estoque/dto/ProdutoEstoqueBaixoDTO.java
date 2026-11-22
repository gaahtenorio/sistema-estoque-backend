package com.supermercado.estoque.dto;

import com.supermercado.estoque.model.Produto;

import java.math.BigDecimal;

public record ProdutoEstoqueBaixoDTO(
        Long id,
        String nome,
        String codigoBarras,
        BigDecimal preco,
        Integer quantidadeEmEstoque,
        Integer estoqueMinimo,
        Long categoriaId,
        String categoriaNome
) {
    public static ProdutoEstoqueBaixoDTO fromEntity(Produto produto) {
        return new ProdutoEstoqueBaixoDTO(
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