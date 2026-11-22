package com.supermercado.estoque.dto;

import com.supermercado.estoque.model.Movimentacao;
import com.supermercado.estoque.model.TipoMovimentacao;

import java.time.LocalDateTime;

public record MovimentacaoResponseDTO(
        Long id,
        Long produtoId,
        String produtoNome,
        TipoMovimentacao tipo,
        Integer quantidade,
        Integer estoqueAtual,
        LocalDateTime dataMovimentacao,
        String observacao
) {
    public static MovimentacaoResponseDTO fromEntity(Movimentacao movimentacao) {
        return new MovimentacaoResponseDTO(
                movimentacao.getId(),
                movimentacao.getProduto().getId(),
                movimentacao.getProduto().getNome(),
                movimentacao.getTipo(),
                movimentacao.getQuantidade(),
                movimentacao.getProduto().getQuantidadeEmEstoque(),
                movimentacao.getDataMovimentacao(),
                movimentacao.getObservacao()
        );
    }
}