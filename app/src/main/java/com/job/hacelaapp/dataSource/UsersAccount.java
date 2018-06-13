package com.job.hacelaapp.dataSource;

/**
 * Created by Job on Wednesday : 6/13/2018.
 */
public class UsersAccount {
    private Double balance;
    private String status;

    public UsersAccount() {
    }

    public UsersAccount(Double balance, String status) {
        this.balance = balance;
        this.status = status;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
