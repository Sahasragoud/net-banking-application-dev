package com.optimaNet.exception;


public class UserNotFoundException extends Exception{
    private String msg;

    public UserNotFoundException(String msg){
        super(msg);
    }

}
