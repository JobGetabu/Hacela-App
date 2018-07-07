package com.job.hacelaapp.dataSource;

import com.google.firebase.Timestamp;

/**
 * Created by Job on Saturday : 7/7/2018.
 */
public class GroupsTransaction {
    private String userid;
    private String groupid;
    private String cycleid;
    private double amount;
    private String username;
    private String type; //received : sent
    private Timestamp timestamp;
    private String details;
    private String transactionid;
    private String status;  //Completed : Pending : Failed

    public GroupsTransaction(String userid, String groupid,
                             String cycleid, double amount, String username, String type,
                             Timestamp timestamp, String details, String transactionid, String status) {
        this.userid = userid;
        this.groupid = groupid;
        this.cycleid = cycleid;
        this.amount = amount;
        this.username = username;
        this.type = type;
        this.timestamp = timestamp;
        this.details = details;
        this.transactionid = transactionid;
        this.status = status;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getGroupid() {
        return groupid;
    }

    public void setGroupid(String groupid) {
        this.groupid = groupid;
    }

    public String getCycleid() {
        return cycleid;
    }

    public void setCycleid(String cycleid) {
        this.cycleid = cycleid;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "GroupsTransaction{" +
                "userid='" + userid + '\'' +
                ", groupid='" + groupid + '\'' +
                ", cycleid='" + cycleid + '\'' +
                ", amount=" + amount +
                ", username='" + username + '\'' +
                ", type='" + type + '\'' +
                ", timestamp=" + timestamp +
                ", details='" + details + '\'' +
                ", transactionid='" + transactionid + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
