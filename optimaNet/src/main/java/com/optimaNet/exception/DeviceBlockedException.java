package com.optimaNet.exception;

public class DeviceBlockedException extends Exception{
    private String msg;

    public DeviceBlockedException(String msg){
        super(msg);
    }

}
