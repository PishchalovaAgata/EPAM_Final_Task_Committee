package com.pishchalova.committee.exception;

public class DAOException extends Exception {
    public DAOException(String message){
        super(message);
    }
    public DAOException(){
        super();
    }
    public DAOException(Exception e){
        super(e);
    }
    public DAOException(String message, Exception e){
        super(message,e);
    }

}
