package com.optimaNet.exception;


public class EmployeeDeviceNotFoundException extends Exception{
    private String msg;

    public EmployeeDeviceNotFoundException(String msg){
        super(msg);
    }


}
