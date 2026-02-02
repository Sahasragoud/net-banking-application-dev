package com.optimaNet.exception;

public class AccessDeniedException extends Exception{
    private String msg;

    public AccessDeniedException(String msg){
        super(msg);
    }
}

