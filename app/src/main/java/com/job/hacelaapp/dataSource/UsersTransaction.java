package com.job.hacelaapp.dataSource;

import com.google.firebase.Timestamp;

/**
 * Created by Job on Saturday : 6/16/2018.
 */
public class UsersTransaction {
    private String username;
    private String userid;
    private String transactionid;
    private String type;
    private String status;
    private Timestamp timestamp;
    private double amount;
    private String paymentsystem;
    private String details;

    public UsersTransaction() {
    }

    public UsersTransaction(String username, String userid, String transactionid,
                            String type, String status, Timestamp timestamp, double amount, String paymentsystem, String details) {
        this.username = username;
        this.userid = userid;
        this.transactionid = transactionid;
        this.type = type;
        this.status = status;
        this.timestamp = timestamp;
        this.amount = amount;
        this.paymentsystem = paymentsystem;
        this.details = details;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getPaymentsystem() {
        return paymentsystem;
    }

    public void setPaymentsystem(String paymentsystem) {
        this.paymentsystem = paymentsystem;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }


    @Override
    public String toString() {
        return "UsersTransaction{" +
                "username='" + username + '\'' +
                ", userid='" + userid + '\'' +
                ", transactionid='" + transactionid + '\'' +
                ", type='" + type + '\'' +
                ", status='" + status + '\'' +
                ", timestamp=" + timestamp +
                ", amount=" + amount +
                ", paymentsystem='" + paymentsystem + '\'' +
                ", details='" + details + '\'' +
                '}';
    }
}
