package com.optimaNet.exception;

public class UserDeviceNotFoundException extends Exception{
    private String msg;

    public UserDeviceNotFoundException(String msg){
        super(msg);
    }


}
