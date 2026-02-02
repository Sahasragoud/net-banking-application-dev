package com.optimaNet.exception;

public class SessionNotFoundException extends Exception {
    private String msg;

    public SessionNotFoundException(String msg){
        super(msg);
    }
}
