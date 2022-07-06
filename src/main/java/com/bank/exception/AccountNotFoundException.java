package com.bank.exception;

public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(Integer id) {
        super("Account with id " + id + " was not found!");
    }
}
