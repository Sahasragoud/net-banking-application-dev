package com.optimaNet.exception;

public class KycDocumentNotFoundException extends Exception{
    private String msg;

    public KycDocumentNotFoundException(String msg){
        super(msg);
    }


}
