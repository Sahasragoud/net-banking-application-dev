package com.optimaNet.exception;

public class InvalidKYCDetailsException extends Exception{
    private String msg;

    public InvalidKYCDetailsException(String msg){
        super(msg);
    }
}
