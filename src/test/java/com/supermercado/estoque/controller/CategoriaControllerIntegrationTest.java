package com.supermercado.estoque.controller;

import com.supermercado.estoque.model.Categoria;
import com.supermercado.estoque.model.Produto;
import com.supermercado.estoque.repository.CategoriaRepository;
import com.supermercado.estoque.repository.ProdutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Testes de integração da API de categorias.
 * Rodam com H2 em memória: não precisam do PostgreSQL ligado.
 *
 * <p>Execute com: {@code ./mvnw test}</p>
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CategoriaControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @BeforeEach
    void limparBase() {
        produtoRepository.deleteAll();
        categoriaRepository.deleteAll();
    }

    @Test
    @DisplayName("POST /api/categorias deve cadastrar e devolver 201 com Location")
    void deveCadastrarCategoria() throws Exception {
        mockMvc.perform(post("/api/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nome": "  Bebidas  ", "descricao": "Sucos e refrigerantes"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/categorias/")))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.nome").value("Bebidas"))
                .andExpect(jsonPath("$.descricao").value("Sucos e refrigerantes"))
                .andExpect(jsonPath("$.quantidadeProdutos").value(0));
    }

    @Test
    @DisplayName("POST /api/categorias sem nome deve devolver 400 com mensagem por campo")
    void deveRecusarCategoriaSemNome() throws Exception {
        mockMvc.perform(post("/api/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nome": "  ", "descricao": "sem nome"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.campos.nome").value("O nome da categoria é obrigatório"));
    }

    @Test
    @DisplayName("POST /api/categorias com nome repetido deve devolver 409")
    void deveRecusarNomeDuplicado() throws Exception {
        categoriaRepository.save(new Categoria("Limpeza", null));

        mockMvc.perform(post("/api/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nome": "limpeza"}
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.mensagem").value(containsString("Já existe uma categoria")));
    }

    @Test
    @DisplayName("GET /api/categorias deve listar em ordem alfabética e filtrar por busca")
    void deveListarEBuscar() throws Exception {
        categoriaRepository.save(new Categoria("Hortifruti", null));
        categoriaRepository.save(new Categoria("Bebidas", null));
        categoriaRepository.save(new Categoria("Padaria", null));

        mockMvc.perform(get("/api/categorias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].nome").value("Bebidas"))
                .andExpect(jsonPath("$[1].nome").value("Hortifruti"));

        mockMvc.perform(get("/api/categorias").param("busca", "pa"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nome").value("Padaria"));
    }

    @Test
    @DisplayName("GET /api/categorias/{id} inexistente deve devolver 404")
    void deveDevolver404QuandoNaoExiste() throws Exception {
        mockMvc.perform(get("/api/categorias/9999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("PUT /api/categorias/{id} deve atualizar os dados")
    void deveAtualizarCategoria() throws Exception {
        Categoria salva = categoriaRepository.save(new Categoria("Bebida", null));

        mockMvc.perform(put("/api/categorias/" + salva.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nome": "Bebidas", "descricao": "Atualizada"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Bebidas"))
                .andExpect(jsonPath("$.descricao").value("Atualizada"));
    }

    @Test
    @DisplayName("DELETE /api/categorias/{id} deve excluir e depois devolver 404")
    void deveExcluirCategoria() throws Exception {
        Categoria salva = categoriaRepository.save(new Categoria("Congelados", null));

        mockMvc.perform(delete("/api/categorias/" + salva.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/categorias/" + salva.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE de categoria com produtos vinculados deve devolver 409")
    void naoDeveExcluirCategoriaComProdutos() throws Exception {
        Categoria salva = categoriaRepository.save(new Categoria("Bebidas", null));
        produtoRepository.save(new Produto("Refrigerante 2L", salva));

        mockMvc.perform(delete("/api/categorias/" + salva.getId()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.mensagem").value(
                        containsString("possui 1 produto(s) vinculado(s)")));

        mockMvc.perform(get("/api/categorias/" + salva.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantidadeProdutos").value(1));
    }
}
