package com.job.hacelaapp.dataSource;

/**
 * Created by Job on Tuesday : 5/22/2018.
 */
public class Savings {
    private boolean issavinggroup;
    private double amount;

    public Savings(boolean issavinggroup, double amount) {
        this.issavinggroup = issavinggroup;
        this.amount = amount;
    }

    public boolean isIssavinggroup() {
        return issavinggroup;
    }

    public void setIssavinggroup(boolean issavinggroup) {
        this.issavinggroup = issavinggroup;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "Savings{" +
                "issavinggroup=" + issavinggroup +
                ", amount=" + amount +
                '}';
    }
}
