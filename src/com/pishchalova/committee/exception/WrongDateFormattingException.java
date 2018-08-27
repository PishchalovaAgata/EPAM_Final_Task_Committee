package com.pishchalova.committee.exception;

public class WrongDateFormattingException extends Exception {
    public WrongDateFormattingException(String message){
        super(message);
    }
    public WrongDateFormattingException(){
        super();
    }
    public WrongDateFormattingException(Exception e){
        super(e);
    }
    public WrongDateFormattingException(String message, Exception e){
        super(message,e);
    }
}
