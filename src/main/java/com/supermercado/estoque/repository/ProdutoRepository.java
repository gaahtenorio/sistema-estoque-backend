package com.supermercado.estoque.repository;

import com.supermercado.estoque.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * ============================================================================
 * ARQUIVO PROVISÓRIO (STUB) — RESPONSÁVEL: PESSOA 5 (PRODUTOS)
 * ============================================================================
 * Aqui só existe a consulta que a regra de negócio de Categorias precisa:
 * "não permitir excluir uma categoria que ainda tem produtos".
 * A Pessoa 5 deve adicionar as demais consultas dela neste arquivo.
 * ============================================================================
 */
@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    /** Quantidade de produtos vinculados a uma categoria (usado no DTO e na exclusão). */
    long countByCategoriaId(Long categoriaId);
}
