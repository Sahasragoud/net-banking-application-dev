package com.optimaNet.exception;

public class DuplicateResourceException  extends Exception{
    private String msg;

    public DuplicateResourceException(String msg){
        super(msg);
    }
}

