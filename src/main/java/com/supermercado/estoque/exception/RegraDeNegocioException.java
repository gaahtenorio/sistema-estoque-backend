package com.supermercado.estoque.exception;

/**
 * Lançada quando o pedido é válido no formato, mas fere uma regra do sistema
 * (HTTP 409), por exemplo: nome de categoria repetido ou exclusão de categoria
 * que ainda possui produtos.
 */
public class RegraDeNegocioException extends RuntimeException {

    public RegraDeNegocioException(String mensagem) {
        super(mensagem);
    }
}
