package com.lima.api_alunos.exception;

public class EmailExistenteException extends RuntimeException{

    public EmailExistenteException(String mensagem) {
        super(mensagem);
    }

}
