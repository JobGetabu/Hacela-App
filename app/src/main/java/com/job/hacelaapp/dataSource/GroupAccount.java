package com.job.hacelaapp.dataSource;

/**
 * Created by Job on Tuesday : 5/22/2018.
 */
public class GroupAccount {
    private double balance;

    public GroupAccount() {
    }

    public GroupAccount(double balance) {
        this.balance = balance;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "GroupAccount{" +
                "balance=" + balance +
                '}';
    }
}
