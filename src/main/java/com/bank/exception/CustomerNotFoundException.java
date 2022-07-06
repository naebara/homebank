package com.bank.exception;

public class CustomerNotFoundException extends RuntimeException {

    public CustomerNotFoundException(Integer id) {
        super("Customer with id " + id + " was not found!");
    }
}
