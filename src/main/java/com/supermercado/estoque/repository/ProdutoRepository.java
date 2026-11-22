package com.supermercado.estoque.repository;

import com.supermercado.estoque.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    long countByCategoriaId(Long categoriaId);

    @Query("""
            SELECT p FROM Produto p
            JOIN FETCH p.categoria
            WHERE p.quantidadeEmEstoque <= p.estoqueMinimo
            ORDER BY p.quantidadeEmEstoque ASC, p.nome ASC
            """)
    List<Produto> findProdutosComEstoqueBaixo();
}