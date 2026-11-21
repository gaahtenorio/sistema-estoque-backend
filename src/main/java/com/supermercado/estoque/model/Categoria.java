package com.supermercado.estoque.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Categoria de produtos do supermercado (ex.: Bebidas, Limpeza, Hortifruti).
 *
 * <p>Tabela: {@code categorias}. O nome é único para evitar duplicidade
 * (ex.: duas categorias "Bebidas").</p>
 */
@Entity
@Table(
        name = "categorias",
        uniqueConstraints = @UniqueConstraint(name = "uk_categoria_nome", columnNames = "nome")
)
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome da categoria é obrigatório")
    @Size(min = 2, max = 80, message = "O nome deve ter entre 2 e 80 caracteres")
    @Column(name = "nome", nullable = false, length = 80)
    private String nome;

    @Size(max = 255, message = "A descrição deve ter no máximo 255 caracteres")
    @Column(name = "descricao", length = 255)
    private String descricao;

    /**
     * Relacionamento 1:N com Produto.
     *
     * <p>O lado "dono" do relacionamento é o Produto (coluna {@code categoria_id} na tabela
     * {@code produtos}), por isso usamos {@code mappedBy}. A lista não é serializada no JSON:
     * a API devolve apenas a quantidade de produtos da categoria.</p>
     */
    @OneToMany(mappedBy = "categoria", fetch = FetchType.LAZY)
    private List<Produto> produtos = new ArrayList<>();

    public Categoria() {
    }

    public Categoria(String nome, String descricao) {
        this.nome = nome;
        this.descricao = descricao;
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

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public List<Produto> getProdutos() {
        return produtos;
    }

    public void setProdutos(List<Produto> produtos) {
        this.produtos = produtos;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Categoria outra)) {
            return false;
        }
        return id != null && Objects.equals(id, outra.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Categoria.class);
    }

    @Override
    public String toString() {
        return "Categoria{id=" + id + ", nome='" + nome + "'}";
    }
}
