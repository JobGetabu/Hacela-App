package com.job.hacelaapp.dataSource;

/**
 * Created by Job on Sunday : 6/24/2018.
 */
public class UserTransaction {

    private String userid;
    private String transactionid;
    private String type; //  withdraw | deposit
    private String status; // pending | failed | completed
    private String timestamp;
    private double amount;

    public UserTransaction(String userid, String transactionid, String type, String status, String timestamp, double amount) {
        this.userid = userid;
        this.transactionid = transactionid;
        this.type = type;
        this.status = status;
        this.timestamp = timestamp;
        this.amount = amount;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getTransactionid() {
        return transactionid;
    }

    public void setTransactionid(String transactionid) {
        this.transactionid = transactionid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "UserTransaction{" +
                "userid='" + userid + '\'' +
                ", transactionid='" + transactionid + '\'' +
                ", type='" + type + '\'' +
                ", status='" + status + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", amount=" + amount +
                '}';
    }
}
