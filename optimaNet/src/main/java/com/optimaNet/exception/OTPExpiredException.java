package com.optimaNet.exception;

public class OTPExpiredException extends Exception{
    private String msg;

    public OTPExpiredException(String msg){
        super(msg);
    }
}
