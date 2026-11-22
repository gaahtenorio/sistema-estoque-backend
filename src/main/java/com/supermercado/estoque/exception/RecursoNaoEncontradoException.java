package com.supermercado.estoque.exception;

/** Lançada quando um registro pedido pela URL não existe no banco (HTTP 404). */
public class RecursoNaoEncontradoException extends RuntimeException {

    public RecursoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }

    public static RecursoNaoEncontradoException categoria(Long id) {
        return new RecursoNaoEncontradoException("Categoria não encontrada com o id " + id);
    }

    public static RecursoNaoEncontradoException produto(Long id) {
        return new RecursoNaoEncontradoException("Produto não encontrado com o id " + id);
    }
}