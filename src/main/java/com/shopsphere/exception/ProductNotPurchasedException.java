package com.shopsphere.exception;

public class ProductNotPurchasedException extends  RuntimeException{

    public ProductNotPurchasedException(String message){
        super(message);
    }
}
