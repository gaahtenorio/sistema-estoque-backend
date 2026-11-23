
package com.supermercado.estoque.controller;

import com.supermercado.estoque.dto.ProdutoRequestDTO;
import com.supermercado.estoque.dto.ProdutoResponseDTO;
import com.supermercado.estoque.service.ProdutoService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

/**
 * Controller responsável pelo gerenciamento de produtos.
 *
 * Pessoa 5, Gerenciamento de Produtos.
 *
 * Endpoints:
 * GET    /api/produtos
 * GET    /api/produtos/{id}
 * POST   /api/produtos
 * PUT    /api/produtos/{id}
 * DELETE /api/produtos/{id}
 */
@RestController
@RequestMapping("/api/produtos")
public class ProdutoController {

    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    /**
     * Lista todos os produtos ou realiza busca pelo nome.
     */
    @GetMapping
    public ResponseEntity<List<ProdutoResponseDTO>> listar(
            @RequestParam(name = "busca", required = false) String busca) {

        return ResponseEntity.ok(produtoService.listar(busca));
    }

    /**
     * Consulta um produto pelo ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProdutoResponseDTO> buscarPorId(
            @PathVariable Long id) {

        return ResponseEntity.ok(produtoService.buscarPorId(id));
    }

    /**
     * Cadastra um novo produto.
     */
    @PostMapping
    public ResponseEntity<ProdutoResponseDTO> criar(
            @Valid @RequestBody ProdutoRequestDTO dto) {

        ProdutoResponseDTO criado = produtoService.criar(dto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(criado.id())
                .toUri();

        return ResponseEntity.created(location).body(criado);
    }

    /**
     * Atualiza os dados de um produto existente.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProdutoResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody ProdutoRequestDTO dto) {

        return ResponseEntity.ok(produtoService.atualizar(id, dto));
    }

    /**
     * Exclui um produto pelo ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(
            @PathVariable Long id) {

        produtoService.excluir(id);

        return ResponseEntity.noContent().build();
    }
}
