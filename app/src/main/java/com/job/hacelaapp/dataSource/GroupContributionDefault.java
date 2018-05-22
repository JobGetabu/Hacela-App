package com.job.hacelaapp.dataSource;

/**
 * Created by Job on Tuesday : 5/22/2018.
 */
public class GroupContributionDefault {
    private double cycleamount;
    private String cycleinterval;
    private int cycleperiod;
    private Penalty penalty;
    private Savings savings;

    public GroupContributionDefault(double cycleamount, String cycleinterval, int cycleperiod, Penalty penalty, Savings savings) {
        this.cycleamount = cycleamount;
        this.cycleinterval = cycleinterval;
        this.cycleperiod = cycleperiod;
        this.penalty = penalty;
        this.savings = savings;
    }

    public GroupContributionDefault() {
    }

    public double getCycleamount() {
        return cycleamount;
    }

    public void setCycleamount(double cycleamount) {
        this.cycleamount = cycleamount;
    }

    public String getCycleinterval() {
        return cycleinterval;
    }

    public void setCycleinterval(String cycleinterval) {
        this.cycleinterval = cycleinterval;
    }

    public int getCycleperiod() {
        return cycleperiod;
    }

    public void setCycleperiod(int cycleperiod) {
        this.cycleperiod = cycleperiod;
    }

    public Penalty getPenalty() {
        return penalty;
    }

    public void setPenalty(Penalty penalty) {
        this.penalty = penalty;
    }

    public Savings getSavings() {
        return savings;
    }

    public void setSavings(Savings savings) {
        this.savings = savings;
    }

    @Override
    public String toString() {
        return "GroupContributionDefault{" +
                "cycleamount=" + cycleamount +
                ", cycleinterval='" + cycleinterval + '\'' +
                ", cycleperiod=" + cycleperiod +
                ", penalty=" + penalty +
                ", savings=" + savings +
                '}';
    }
}
