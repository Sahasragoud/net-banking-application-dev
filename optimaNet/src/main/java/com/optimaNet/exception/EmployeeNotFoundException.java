package com.optimaNet.exception;

public class EmployeeNotFoundException extends Exception{
    private String msg;

    public EmployeeNotFoundException(String msg){
        super(msg);
    }
}
