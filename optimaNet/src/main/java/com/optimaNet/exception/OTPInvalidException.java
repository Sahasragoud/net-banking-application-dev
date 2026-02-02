package com.optimaNet.exception;

public class OTPInvalidException extends Exception {
    private String msg;

    public OTPInvalidException(String msg){
        super(msg);
    }
}
