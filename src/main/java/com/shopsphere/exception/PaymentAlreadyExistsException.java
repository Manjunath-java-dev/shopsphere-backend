package com.shopsphere.exception;

public class PaymentAlreadyExistsException extends RuntimeException{
    public PaymentAlreadyExistsException(String message){
        super(message);
    }
}
