package com.supermercado.estoque.repository;

import com.supermercado.estoque.model.Movimentacao;
import com.supermercado.estoque.model.TipoMovimentacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovimentacaoRepository extends JpaRepository<Movimentacao, Long> {

    @Query("""
            SELECT m FROM Movimentacao m
            JOIN FETCH m.produto p
            JOIN FETCH p.categoria
            ORDER BY m.dataMovimentacao DESC, m.id DESC
            """)
    List<Movimentacao> findHistoricoCompleto();

    @Query("""
            SELECT m FROM Movimentacao m
            JOIN FETCH m.produto p
            JOIN FETCH p.categoria
            WHERE p.id = :produtoId
            ORDER BY m.dataMovimentacao DESC, m.id DESC
            """)
    List<Movimentacao> findHistoricoPorProduto(@Param("produtoId") Long produtoId);

    @Query("""
            SELECT m FROM Movimentacao m
            JOIN FETCH m.produto p
            JOIN FETCH p.categoria
            WHERE m.tipo = :tipo
            ORDER BY m.dataMovimentacao DESC, m.id DESC
            """)
    List<Movimentacao> findHistoricoPorTipo(@Param("tipo") TipoMovimentacao tipo);
}
