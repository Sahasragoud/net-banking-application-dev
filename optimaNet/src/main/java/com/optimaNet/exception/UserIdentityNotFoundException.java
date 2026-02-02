package com.optimaNet.exception;

public class UserIdentityNotFoundException extends Exception{
    private String msg;

    public UserIdentityNotFoundException(String msg){
        super(msg);
    }
}
