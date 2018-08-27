package com.pishchalova.committee.exception;

public class ServletCustomException extends Exception {
    public ServletCustomException(String message){
        super(message);
    }
    public ServletCustomException(){
        super();
    }
    public ServletCustomException(Exception e){
        super(e);
    }
    public ServletCustomException(String message, Exception e){
        super(message,e);
    }
}
