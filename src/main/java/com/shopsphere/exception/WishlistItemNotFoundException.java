package com.shopsphere.exception;

public class WishlistItemNotFoundException extends RuntimeException{
    public WishlistItemNotFoundException(String message){
        super(message);
    }
}
