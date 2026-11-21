package com.supermercado.estoque.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Objects;

/**
 * ============================================================================
 * ATENÇÃO — ARQUIVO PROVISÓRIO (STUB) — RESPONSÁVEL: PESSOA 5 (PRODUTOS)
 * ============================================================================
 * Este arquivo existe só para o relacionamento Categoria 1:N Produto compilar,
 * porque a Categoria precisa da classe Produto para o {@code mappedBy}.
 *
 * A Pessoa 5 deve COMPLETAR esta entidade com os campos do projeto
 * (codigoBarras, preco, quantidadeEmEstoque, estoqueMinimo etc.), mantendo:
 *   - a tabela "produtos";
 *   - o {@code @ManyToOne} para Categoria com a coluna "categoria_id";
 *   - a coluna "categoria_id" como NOT NULL (todo produto tem uma categoria).
 *
 * Contrato detalhado em: docs/API-CATEGORIAS.md
 * ============================================================================
 */
@Entity
@Table(name = "produtos")
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome do produto é obrigatório")
    @Size(max = 120, message = "O nome do produto deve ter no máximo 120 caracteres")
    @Column(name = "nome", nullable = false, length = 120)
    private String nome;

    /** Lado dono do relacionamento: gera a coluna categoria_id na tabela produtos. */
    @NotNull(message = "A categoria do produto é obrigatória")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "categoria_id", nullable = false,
            foreignKey = @jakarta.persistence.ForeignKey(name = "fk_produto_categoria"))
    private Categoria categoria;

    public Produto() {
    }

    public Produto(String nome, Categoria categoria) {
        this.nome = nome;
        this.categoria = categoria;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Produto outro)) {
            return false;
        }
        return id != null && Objects.equals(id, outro.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Produto.class);
    }
}
