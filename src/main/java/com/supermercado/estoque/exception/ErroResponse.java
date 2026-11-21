package com.supermercado.estoque.exception;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Formato padrão de erro devolvido pela API, para o front-end exibir mensagens.
 *
 * <pre>
 * {
 *   "dataHora": "2026-11-20T21:10:00",
 *   "status": 400,
 *   "erro": "Dados inválidos",
 *   "mensagem": "Um ou mais campos estão inválidos",
 *   "caminho": "/api/categorias",
 *   "campos": { "nome": "O nome da categoria é obrigatório" }
 * }
 * </pre>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErroResponse(
        LocalDateTime dataHora,
        int status,
        String erro,
        String mensagem,
        String caminho,
        Map<String, String> campos
) {

    public static ErroResponse of(int status, String erro, String mensagem, String caminho) {
        return new ErroResponse(LocalDateTime.now(), status, erro, mensagem, caminho, null);
    }

    public static ErroResponse comCampos(int status, String erro, String mensagem, String caminho,
                                         Map<String, String> campos) {
        return new ErroResponse(LocalDateTime.now(), status, erro, mensagem, caminho, campos);
    }
}
