package com.supermercado.estoque.service;

import com.supermercado.estoque.dto.CategoriaRequestDTO;
import com.supermercado.estoque.dto.CategoriaResponseDTO;
import com.supermercado.estoque.exception.RecursoNaoEncontradoException;
import com.supermercado.estoque.exception.RegraDeNegocioException;
import com.supermercado.estoque.model.Categoria;
import com.supermercado.estoque.repository.CategoriaRepository;
import com.supermercado.estoque.repository.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Regras de negócio das categorias.
 *
 * <p>Regras implementadas:</p>
 * <ul>
 *   <li>nome obrigatório (validado também no DTO) e único, sem diferenciar maiúsculas;</li>
 *   <li>não é possível excluir categoria que ainda possui produtos;</li>
 *   <li>toda operação que não encontra o id devolve 404.</li>
 * </ul>
 */
@Service
@Transactional(readOnly = true)
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final ProdutoRepository produtoRepository;

    public CategoriaService(CategoriaRepository categoriaRepository,
                            ProdutoRepository produtoRepository) {
        this.categoriaRepository = categoriaRepository;
        this.produtoRepository = produtoRepository;
    }

    /** Lista categorias; se vier "busca", filtra por parte do nome. */
    public List<CategoriaResponseDTO> listar(String busca) {
        List<Categoria> categorias = StringUtils.hasText(busca)
                ? categoriaRepository.findByNomeContainingIgnoreCaseOrderByNomeAsc(busca.trim())
                : categoriaRepository.findAllByOrderByNomeAsc();

        return categorias.stream()
                .map(categoria -> CategoriaResponseDTO.fromEntity(
                        categoria, produtoRepository.countByCategoriaId(categoria.getId())))
                .toList();
    }

    public CategoriaResponseDTO buscarPorId(Long id) {
        Categoria categoria = buscarEntidadePorId(id);
        return CategoriaResponseDTO.fromEntity(categoria,
                produtoRepository.countByCategoriaId(categoria.getId()));
    }

    @Transactional
    public CategoriaResponseDTO criar(CategoriaRequestDTO dto) {
        String nome = normalizar(dto.nome());

        if (categoriaRepository.existsByNomeIgnoreCase(nome)) {
            throw new RegraDeNegocioException("Já existe uma categoria com o nome '" + nome + "'");
        }

        Categoria categoria = new Categoria(nome, normalizarDescricao(dto.descricao()));
        Categoria salva = categoriaRepository.save(categoria);
        return CategoriaResponseDTO.fromEntity(salva, 0L);
    }

    @Transactional
    public CategoriaResponseDTO atualizar(Long id, CategoriaRequestDTO dto) {
        Categoria categoria = buscarEntidadePorId(id);
        String nome = normalizar(dto.nome());

        if (categoriaRepository.existsByNomeIgnoreCaseAndIdNot(nome, id)) {
            throw new RegraDeNegocioException("Já existe outra categoria com o nome '" + nome + "'");
        }

        categoria.setNome(nome);
        categoria.setDescricao(normalizarDescricao(dto.descricao()));

        Categoria atualizada = categoriaRepository.save(categoria);
        return CategoriaResponseDTO.fromEntity(atualizada,
                produtoRepository.countByCategoriaId(id));
    }

    @Transactional
    public void excluir(Long id) {
        Categoria categoria = buscarEntidadePorId(id);

        long produtosVinculados = produtoRepository.countByCategoriaId(id);
        if (produtosVinculados > 0) {
            throw new RegraDeNegocioException(
                    "Não é possível excluir a categoria '" + categoria.getNome()
                            + "' porque ela possui " + produtosVinculados + " produto(s) vinculado(s). "
                            + "Mova os produtos para outra categoria antes de excluir.");
        }

        categoriaRepository.delete(categoria);
    }

    private Categoria buscarEntidadePorId(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> RecursoNaoEncontradoException.categoria(id));
    }

    /** Remove espaços nas pontas e evita nomes com espaços repetidos no meio. */
    private String normalizar(String texto) {
        return texto == null ? null : texto.trim().replaceAll("\\s+", " ");
    }

    private String normalizarDescricao(String descricao) {
        return StringUtils.hasText(descricao) ? descricao.trim() : null;
    }
}
