package com.alunoonline.api.service.exceptions;

public class IntegrityException extends RuntimeException{

    public IntegrityException(Object id){
        super("Deletion violates foreign key constraint. Id " + id);
    }
}
