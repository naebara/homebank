package com.bank.exception;

public class CustomerHasAssociatedAccountsException extends RuntimeException {
    public CustomerHasAssociatedAccountsException(Integer id) {
        super("Customer with id " + id + " has associated accounts. Delete accounts before deleting customer.");
    }
}
