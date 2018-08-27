package com.pishchalova.committee.exception;

public class PoolException extends Exception {
    public PoolException(String message){
        super(message);
    }
    public PoolException(){
        super();
    }
    public PoolException(Exception e){
        super(e);
    }
    public PoolException(String message, Exception e){
        super(message,e);
    }
}
