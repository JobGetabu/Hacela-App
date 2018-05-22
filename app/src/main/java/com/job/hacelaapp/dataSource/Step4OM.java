package com.job.hacelaapp.dataSource;

/**
 * Created by Job on Tuesday : 5/22/2018.
 */
public class Step4OM {
    private double amount;
    private double payout;
    private double savings;
    private String intervalPeriod;
    private int contPeriod;

    public Step4OM() {
    }

    public Step4OM(double amount, double payout, double savings, String intervalPeriod, int contPeriod) {
        this.amount = amount;
        this.payout = payout;
        this.savings = savings;
        this.intervalPeriod = intervalPeriod;
        this.contPeriod = contPeriod;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getPayout() {
        return payout;
    }

    public void setPayout(double payout) {
        this.payout = payout;
    }

    public double getSavings() {
        return savings;
    }

    public void setSavings(double savings) {
        this.savings = savings;
    }

    public String getIntervalPeriod() {
        return intervalPeriod;
    }

    public void setIntervalPeriod(String intervalPeriod) {
        this.intervalPeriod = intervalPeriod;
    }

    public int getContPeriod() {
        return contPeriod;
    }

    public void setContPeriod(int contPeriod) {
        this.contPeriod = contPeriod;
    }

    @Override
    public String toString() {
        return "Step4OM{" +
                "amount=" + amount +
                ", payout=" + payout +
                ", savings=" + savings +
                ", intervalPeriod='" + intervalPeriod + '\'' +
                ", contPeriod=" + contPeriod +
                '}';
    }
}
