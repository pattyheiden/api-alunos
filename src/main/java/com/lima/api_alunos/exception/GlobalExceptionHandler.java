package com.lima.api_alunos.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.List;

@RestControllerAdvice
class GlobalExceptionHandler {

    /*
    antes do erroResponse
    @ExceptionHandler(AlunoNaoEncontradoException.class)
    * public ResponseEntity<String> handleAlunoNãoEcontradoException(AlunoNaoEncontradoException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(EmailExistenteException.class)
    public ResponseEntity<String> handleEmailExistenteException(EmailExistenteException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }

    * */

    @ExceptionHandler(AlunoNaoEncontradoException.class)
    ResponseEntity<ErroResponse> handleAlunoNãoEcontradoException(AlunoNaoEncontradoException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErroResponse(404, e.getMessage(), Instant.now()));
    }

    @ExceptionHandler(EmailExistenteException.class)
    ResponseEntity<ErroResponse> handleEmailExistenteException(EmailExistenteException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErroResponse(409, e.getMessage(), Instant.now()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ErroAtributoResponse> handleValidacaoRequest(MethodArgumentNotValidException ex) {
        List<ErroAtributo> errosAtributo = ex
                .getBindingResult()
                .getFieldErrors()
                .stream()
                .map(erro -> new ErroAtributo(erro.getField(), erro.getDefaultMessage())).toList();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErroAtributoResponse(400, "Erro de validação", Instant.now(), errosAtributo));
    }
}
