package com.supermercado.estoque.repository;

import com.supermercado.estoque.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    /** Lista todas as categorias em ordem alfabética (usado no combo do front-end). */
    List<Categoria> findAllByOrderByNomeAsc();

    /** Busca parcial pelo nome, ignorando maiúsculas/minúsculas. */
    List<Categoria> findByNomeContainingIgnoreCaseOrderByNomeAsc(String nome);

    /** Usado para impedir duas categorias com o mesmo nome no cadastro. */
    boolean existsByNomeIgnoreCase(String nome);

    /** Usado para impedir nome duplicado na edição (ignora o próprio registro). */
    boolean existsByNomeIgnoreCaseAndIdNot(String nome, Long id);
}
