package com.job.hacelaapp.dataSource;

/**
 * Created by Job on Tuesday : 5/22/2018.
 */
public class Penalty {

    private String condition;
    private double amount;

    public Penalty(String condition, double amount) {
        this.condition = condition;
        this.amount = amount;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "Penalty{" +
                "condition='" + condition + '\'' +
                ", amount=" + amount +
                '}';
    }
}
