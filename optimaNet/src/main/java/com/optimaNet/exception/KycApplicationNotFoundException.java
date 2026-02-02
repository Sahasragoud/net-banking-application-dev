package com.optimaNet.exception;

public class KycApplicationNotFoundException extends Exception{
    private String msg;

    public KycApplicationNotFoundException(String msg){
        super(msg);
    }
}

