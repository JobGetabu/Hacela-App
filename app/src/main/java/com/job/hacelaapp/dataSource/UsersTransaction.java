package com.job.hacelaapp.dataSource;

import com.google.firebase.Timestamp;

/**
 * Created by Job on Saturday : 6/16/2018.
 */
public class UsersTransaction {
    private String username;
    private String userid;
    private String type;
    private Timestamp timestamp;
    private String details;
    private String transactionid;

    //TODO: Add safaricom callback data

    public UsersTransaction(String username, String userid, String type, Timestamp timestamp, String details, String transactionid) {
        this.username = username;
        this.userid = userid;
        this.type = type;
        this.timestamp = timestamp;
        this.details = details;
        this.transactionid = transactionid;
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

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getTransactionid() {
        return transactionid;
    }

    public void setTransactionid(String transactionid) {
        this.transactionid = transactionid;
    }

    @Override
    public String toString() {
        return "UsersTransaction{" +
                "username='" + username + '\'' +
                ", userid='" + userid + '\'' +
                ", type='" + type + '\'' +
                ", timestamp=" + timestamp +
                ", details='" + details + '\'' +
                ", transactionid='" + transactionid + '\'' +
                '}';
    }
}
