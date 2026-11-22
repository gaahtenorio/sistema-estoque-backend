package com.supermercado.estoque.service;

import com.supermercado.estoque.dto.MovimentacaoRequestDTO;
import com.supermercado.estoque.dto.MovimentacaoResponseDTO;
import com.supermercado.estoque.dto.ProdutoEstoqueBaixoDTO;
import com.supermercado.estoque.exception.RecursoNaoEncontradoException;
import com.supermercado.estoque.exception.RegraDeNegocioException;
import com.supermercado.estoque.model.Movimentacao;
import com.supermercado.estoque.model.Produto;
import com.supermercado.estoque.model.TipoMovimentacao;
import com.supermercado.estoque.repository.MovimentacaoRepository;
import com.supermercado.estoque.repository.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MovimentacaoService {

    private final MovimentacaoRepository movimentacaoRepository;
    private final ProdutoRepository produtoRepository;

    public MovimentacaoService(MovimentacaoRepository movimentacaoRepository,
                               ProdutoRepository produtoRepository) {
        this.movimentacaoRepository = movimentacaoRepository;
        this.produtoRepository = produtoRepository;
    }

    @Transactional
    public MovimentacaoResponseDTO registrarEntrada(MovimentacaoRequestDTO dto) {
        validarQuantidade(dto.quantidade());

        Produto produto = produtoRepository.findById(dto.produtoId())
                .orElseThrow(() -> RecursoNaoEncontradoException.produto(dto.produtoId()));

        int saldoAtual = produto.getQuantidadeEmEstoque() != null ? produto.getQuantidadeEmEstoque() : 0;
        produto.setQuantidadeEmEstoque(saldoAtual + dto.quantidade());
        produtoRepository.save(produto);

        Movimentacao movimentacao = new Movimentacao(
                produto,
                TipoMovimentacao.ENTRADA,
                dto.quantidade(),
                normalizarObservacao(dto.observacao())
        );

        Movimentacao salva = movimentacaoRepository.save(movimentacao);
        return MovimentacaoResponseDTO.fromEntity(salva);
    }

    @Transactional
    public MovimentacaoResponseDTO registrarSaida(MovimentacaoRequestDTO dto) {
        validarQuantidade(dto.quantidade());

        Produto produto = produtoRepository.findById(dto.produtoId())
                .orElseThrow(() -> RecursoNaoEncontradoException.produto(dto.produtoId()));

        int saldoAtual = produto.getQuantidadeEmEstoque() != null ? produto.getQuantidadeEmEstoque() : 0;

        if (dto.quantidade() > saldoAtual) {
            throw new RegraDeNegocioException(
                    "Quantidade solicitada (" + dto.quantidade()
                            + ") maior que o estoque disponível (" + saldoAtual
                            + ") para o produto '" + produto.getNome() + "'."
            );
        }

        produto.setQuantidadeEmEstoque(saldoAtual - dto.quantidade());
        produtoRepository.save(produto);

        Movimentacao movimentacao = new Movimentacao(
                produto,
                TipoMovimentacao.SAIDA,
                dto.quantidade(),
                normalizarObservacao(dto.observacao())
        );

        Movimentacao salva = movimentacaoRepository.save(movimentacao);
        return MovimentacaoResponseDTO.fromEntity(salva);
    }

    @Transactional(readOnly = true)
    public List<MovimentacaoResponseDTO> listarHistorico(Long produtoId, TipoMovimentacao tipo) {
        List<Movimentacao> movimentacoes;

        if (produtoId != null) {
            if (!produtoRepository.existsById(produtoId)) {
                throw RecursoNaoEncontradoException.produto(produtoId);
            }
            movimentacoes = movimentacaoRepository.findHistoricoPorProduto(produtoId);
        } else if (tipo != null) {
            movimentacoes = movimentacaoRepository.findHistoricoPorTipo(tipo);
        } else {
            movimentacoes = movimentacaoRepository.findHistoricoCompleto();
        }

        return movimentacoes.stream()
                .map(MovimentacaoResponseDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProdutoEstoqueBaixoDTO> listarProdutosComEstoqueBaixo() {
        return produtoRepository.findProdutosComEstoqueBaixo()
                .stream()
                .map(ProdutoEstoqueBaixoDTO::fromEntity)
                .toList();
    }

    private void validarQuantidade(Integer quantidade) {
        if (quantidade == null || quantidade <= 0) {
            throw new RegraDeNegocioException("A quantidade da movimentação deve ser maior que zero.");
        }
    }

    private String normalizarObservacao(String observacao) {
        if (observacao == null) {
            return null;
        }
        String limpo = observacao.trim().replaceAll("\\s+", " ");
        return limpo.isEmpty() ? null : limpo;
    }
}
