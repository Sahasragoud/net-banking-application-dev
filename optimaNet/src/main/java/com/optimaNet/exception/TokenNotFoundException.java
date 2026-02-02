package com.optimaNet.exception;

public class TokenNotFoundException extends Exception {
    private String msg;

    public TokenNotFoundException(String msg){
        super(msg);
    }
}

