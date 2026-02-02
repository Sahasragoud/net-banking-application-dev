package com.optimaNet.exception;

public class OTPBlockedException extends Exception{

    private String msg;

    public OTPBlockedException(String msg){
        super(msg);
    }
}
