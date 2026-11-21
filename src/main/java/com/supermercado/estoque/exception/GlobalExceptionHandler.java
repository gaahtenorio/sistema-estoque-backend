package com.supermercado.estoque.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Centraliza a tradução de exceções em respostas HTTP com JSON legível.
 * Assim o Angular sempre recebe um objeto com "status", "mensagem" e, se for
 * erro de formulário, "campos" campo-a-campo.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /** Regra de negócio violada -> 409 Conflict. */
    @ExceptionHandler(RegraDeNegocioException.class)
    public ResponseEntity<ErroResponse> tratarRegraDeNegocio(RegraDeNegocioException ex,
                                                             HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ErroResponse.of(HttpStatus.CONFLICT.value(), "Regra de negócio violada",
                        ex.getMessage(), request.getRequestURI()));
    }

    /** Registro inexistente -> 404 Not Found. */
    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ErroResponse> tratarNaoEncontrado(RecursoNaoEncontradoException ex,
                                                            HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErroResponse.of(HttpStatus.NOT_FOUND.value(), "Recurso não encontrado",
                        ex.getMessage(), request.getRequestURI()));
    }

    /** Erros de validação do @Valid -> 400 com o mapa de campos inválidos. */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResponse> tratarValidacao(MethodArgumentNotValidException ex,
                                                        HttpServletRequest request) {
        Map<String, String> campos = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(erro -> campos.putIfAbsent(erro.getField(), erro.getDefaultMessage()));

        return ResponseEntity.badRequest()
                .body(ErroResponse.comCampos(HttpStatus.BAD_REQUEST.value(), "Dados inválidos",
                        "Um ou mais campos estão inválidos", request.getRequestURI(), campos));
    }

    /** JSON malformado ou campo com tipo errado -> 400. */
    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ErroResponse> tratarRequisicaoInvalida(Exception ex,
                                                                HttpServletRequest request) {
        return ResponseEntity.badRequest()
                .body(ErroResponse.of(HttpStatus.BAD_REQUEST.value(), "Requisição inválida",
                        "Verifique o corpo da requisição e os tipos dos campos enviados",
                        request.getRequestURI()));
    }

    /** Violação de constraint do banco (ex.: nome único, FK) -> 409. */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErroResponse> tratarIntegridade(DataIntegrityViolationException ex,
                                                          HttpServletRequest request) {
        log.warn("Violação de integridade: {}", ex.getMostSpecificCause().getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ErroResponse.of(HttpStatus.CONFLICT.value(), "Conflito de dados",
                        "A operação viola uma regra do banco de dados (registro duplicado ou em uso)",
                        request.getRequestURI()));
    }

    /** Qualquer erro não previsto -> 500 (detalhe técnico fica só no log do servidor). */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResponse> tratarErroInesperado(Exception ex, HttpServletRequest request) {
        log.error("Erro inesperado ao processar {}", request.getRequestURI(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErroResponse.of(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Erro interno",
                        "Ocorreu um erro inesperado no servidor", request.getRequestURI()));
    }
}
