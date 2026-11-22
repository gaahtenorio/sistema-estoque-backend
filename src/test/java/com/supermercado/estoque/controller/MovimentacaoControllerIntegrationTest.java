package com.supermercado.estoque.controller;

import com.supermercado.estoque.model.Categoria;
import com.supermercado.estoque.model.Produto;
import com.supermercado.estoque.repository.CategoriaRepository;
import com.supermercado.estoque.repository.MovimentacaoRepository;
import com.supermercado.estoque.repository.ProdutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MovimentacaoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MovimentacaoRepository movimentacaoRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    private Produto produtoSalvo;

    @BeforeEach
    void prepararBase() {
        movimentacaoRepository.deleteAll();
        produtoRepository.deleteAll();
        categoriaRepository.deleteAll();

        Categoria categoria = categoriaRepository.save(new Categoria("Bebidas", "Sucos e refrigerantes"));
        produtoSalvo = produtoRepository.save(new Produto("Refrigerante Cola 2L", categoria, 10, 5));
    }

    @Test
    @DisplayName("POST /api/movimentacoes/entrada deve somar ao estoque e registrar ENTRADA")
    void deveRegistrarEntradaEAtualizarEstoque() throws Exception {
        mockMvc.perform(post("/api/movimentacoes/entrada")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"produtoId": %d, "quantidade": 15, "observacao": "Compra fornecedor"}
                                """.formatted(produtoSalvo.getId())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tipo").value("ENTRADA"))
                .andExpect(jsonPath("$.quantidade").value(15))
                .andExpect(jsonPath("$.estoqueAtual").value(25))
                .andExpect(jsonPath("$.dataMovimentacao").isNotEmpty());
    }

    @Test
    @DisplayName("POST /api/movimentacoes/saida deve subtrair do estoque e registrar SAIDA")
    void deveRegistrarSaidaEAtualizarEstoque() throws Exception {
        mockMvc.perform(post("/api/movimentacoes/saida")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"produtoId": %d, "quantidade": 4, "observacao": "Venda caixa"}
                                """.formatted(produtoSalvo.getId())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tipo").value("SAIDA"))
                .andExpect(jsonPath("$.quantidade").value(4))
                .andExpect(jsonPath("$.estoqueAtual").value(6));
    }

    @Test
    @DisplayName("POST /api/movimentacoes/saida com quantidade maior que o estoque deve retornar 409")
    void deveBloquearSaidaMaiorQueEstoque() throws Exception {
        mockMvc.perform(post("/api/movimentacoes/saida")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"produtoId": %d, "quantidade": 50}
                                """.formatted(produtoSalvo.getId())))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    @DisplayName("POST com quantidade negativa ou zero deve retornar 400")
    void deveRecusarQuantidadeInvalida() throws Exception {
        mockMvc.perform(post("/api/movimentacoes/entrada")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"produtoId": %d, "quantidade": -3}
                                """.formatted(produtoSalvo.getId())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("GET /api/movimentacoes e GET /api/produtos/estoque-baixo devem retornar dados")
    void deveListarHistoricoEEstoqueBaixo() throws Exception {
        mockMvc.perform(post("/api/movimentacoes/saida")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"produtoId": %d, "quantidade": 7}
                                """.formatted(produtoSalvo.getId())))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/movimentacoes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].tipo").value("SAIDA"));

        mockMvc.perform(get("/api/produtos/estoque-baixo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].quantidadeEmEstoque").value(3));
    }
}
