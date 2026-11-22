package com.supermercado.estoque.controller;

import com.supermercado.estoque.dto.MovimentacaoRequestDTO;
import com.supermercado.estoque.dto.MovimentacaoResponseDTO;
import com.supermercado.estoque.dto.ProdutoEstoqueBaixoDTO;
import com.supermercado.estoque.model.TipoMovimentacao;
import com.supermercado.estoque.service.MovimentacaoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api")
public class MovimentacaoController {

    private final MovimentacaoService movimentacaoService;

    public MovimentacaoController(MovimentacaoService movimentacaoService) {
        this.movimentacaoService = movimentacaoService;
    }

    @PostMapping("/movimentacoes/entrada")
    public ResponseEntity<MovimentacaoResponseDTO> registrarEntrada(
            @Valid @RequestBody MovimentacaoRequestDTO dto) {
        MovimentacaoResponseDTO criada = movimentacaoService.registrarEntrada(dto);

        URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/movimentacoes/{id}")
                .buildAndExpand(criada.id())
                .toUri();

        return ResponseEntity.created(location).body(criada);
    }

    @PostMapping("/movimentacoes/saida")
    public ResponseEntity<MovimentacaoResponseDTO> registrarSaida(
            @Valid @RequestBody MovimentacaoRequestDTO dto) {
        MovimentacaoResponseDTO criada = movimentacaoService.registrarSaida(dto);

        URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/movimentacoes/{id}")
                .buildAndExpand(criada.id())
                .toUri();

        return ResponseEntity.created(location).body(criada);
    }

    @GetMapping("/movimentacoes")
    public ResponseEntity<List<MovimentacaoResponseDTO>> listarHistorico(
            @RequestParam(name = "produtoId", required = false) Long produtoId,
            @RequestParam(name = "tipo", required = false) TipoMovimentacao tipo) {
        return ResponseEntity.ok(movimentacaoService.listarHistorico(produtoId, tipo));
    }

    @GetMapping("/movimentacoes/produto/{produtoId}")
    public ResponseEntity<List<MovimentacaoResponseDTO>> listarPorProduto(
            @PathVariable Long produtoId) {
        return ResponseEntity.ok(movimentacaoService.listarHistorico(produtoId, null));
    }

    @GetMapping("/produtos/estoque-baixo")
    public ResponseEntity<List<ProdutoEstoqueBaixoDTO>> listarEstoqueBaixo() {
        return ResponseEntity.ok(movimentacaoService.listarProdutosComEstoqueBaixo());
    }
}
