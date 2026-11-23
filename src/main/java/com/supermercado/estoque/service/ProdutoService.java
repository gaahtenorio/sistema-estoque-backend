
package com.supermercado.estoque.service;

import com.supermercado.estoque.dto.ProdutoRequestDTO;
import com.supermercado.estoque.dto.ProdutoResponseDTO;
import com.supermercado.estoque.exception.RecursoNaoEncontradoException;
import com.supermercado.estoque.exception.RegraDeNegocioException;
import com.supermercado.estoque.model.Categoria;
import com.supermercado.estoque.model.Produto;
import com.supermercado.estoque.repository.CategoriaRepository;
import com.supermercado.estoque.repository.ProdutoRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Regras de negócio relacionadas aos produtos.
 *
 * Responsabilidades:
 * - Cadastrar produtos.
 * - Listar produtos.
 * - Consultar produtos por ID.
 * - Atualizar produtos.
 * - Excluir produtos.
 * - Buscar produtos pelo nome.
 * - Validar categoria e código de barras.
 *
 * A quantidade em estoque é controlada pelo serviço de movimentações.
 */
@Service
@Transactional(readOnly = true)
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final CategoriaRepository categoriaRepository;

    public ProdutoService(ProdutoRepository produtoRepository,
                          CategoriaRepository categoriaRepository) {
        this.produtoRepository = produtoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    /**
     * Lista todos os produtos ou filtra pelo nome.
     */
    public List<ProdutoResponseDTO> listar(String busca) {

        List<Produto> produtos = StringUtils.hasText(busca)
                ? produtoRepository.findByNomeContainingIgnoreCase(busca.trim())
                : produtoRepository.findAll();

        return produtos.stream()
                .map(ProdutoResponseDTO::fromEntity)
                .toList();
    }

    /**
     * Consulta um produto pelo seu ID.
     */
    public ProdutoResponseDTO buscarPorId(Long id) {

        Produto produto = buscarEntidadePorId(id);

        return ProdutoResponseDTO.fromEntity(produto);
    }

    /**
     * Cadastra um novo produto.
     */
    @Transactional
    public ProdutoResponseDTO criar(ProdutoRequestDTO dto) {

        String codigoBarras = normalizarCodigoBarras(dto.codigoBarras());

        validarCodigoBarras(codigoBarras);

        Categoria categoria = buscarCategoriaPorId(dto.categoriaId());

        Produto produto = new Produto();

        produto.setNome(normalizarNome(dto.nome()));
        produto.setCodigoBarras(codigoBarras);
        produto.setPreco(dto.preco());
        produto.setEstoqueMinimo(dto.estoqueMinimo());
        produto.setCategoria(categoria);

        // O estoque inicial permanece em zero.
        produto.setQuantidadeEmEstoque(0);

        Produto salvo = produtoRepository.save(produto);

        return ProdutoResponseDTO.fromEntity(salvo);
    }

    /**
     * Atualiza os dados de um produto existente.
     */
    @Transactional
    public ProdutoResponseDTO atualizar(Long id, ProdutoRequestDTO dto) {

        Produto produto = buscarEntidadePorId(id);

        String codigoBarras = normalizarCodigoBarras(dto.codigoBarras());

        validarCodigoBarrasNaEdicao(codigoBarras, id);

        Categoria categoria = buscarCategoriaPorId(dto.categoriaId());

        produto.setNome(normalizarNome(dto.nome()));
        produto.setCodigoBarras(codigoBarras);
        produto.setPreco(dto.preco());
        produto.setEstoqueMinimo(dto.estoqueMinimo());
        produto.setCategoria(categoria);

        // Não alterar quantidadeEmEstoque.
        // As alterações de estoque pertencem ao serviço de movimentações.

        Produto atualizado = produtoRepository.save(produto);

        return ProdutoResponseDTO.fromEntity(atualizado);
    }

    /**
     * Exclui um produto pelo ID.
     */
    @Transactional
    public void excluir(Long id) {

        Produto produto = buscarEntidadePorId(id);

        produtoRepository.delete(produto);
    }

    /**
     * Verifica se o código de barras já está cadastrado.
     */
    private void validarCodigoBarras(String codigoBarras) {

        if (codigoBarras != null
                && produtoRepository.existsByCodigoBarras(codigoBarras)) {

            throw new RegraDeNegocioException(
                    "Já existe um produto cadastrado com este código de barras."
            );
        }
    }

    /**
     * Verifica duplicidade de código de barras durante a edição,
     * desconsiderando o próprio produto.
     */
    private void validarCodigoBarrasNaEdicao(String codigoBarras, Long id) {

        if (codigoBarras != null
                && produtoRepository.existsByCodigoBarrasAndIdNot(codigoBarras, id)) {

            throw new RegraDeNegocioException(
                    "Este código de barras já pertence a outro produto."
            );
        }
    }

    /**
     * Busca uma categoria existente pelo ID.
     */
    private Categoria buscarCategoriaPorId(Long categoriaId) {

        return categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Categoria não encontrada com o ID " + categoriaId
                ));
    }

    /**
     * Busca um produto existente pelo ID.
     */
    private Produto buscarEntidadePorId(Long id) {

        return produtoRepository.findById(id)
                .orElseThrow(() -> RecursoNaoEncontradoException.produto(id));
    }

    /**
     * Remove espaços desnecessários do nome.
     */
    private String normalizarNome(String nome) {

        return nome.trim().replaceAll("\\s+", " ");
    }

    /**
     * Converte código de barras vazio em null.
     */
    private String normalizarCodigoBarras(String codigoBarras) {

        return StringUtils.hasText(codigoBarras)
                ? codigoBarras.trim()
                : null;
    }
}
