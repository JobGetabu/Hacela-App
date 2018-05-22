package com.job.hacelaapp.dataSource;

import java.util.Date;

/**
 * Created by Job on Tuesday : 5/22/2018.
 */
public class GroupAdmins {
    private String userid;
    private String position;
    private Date fromdate;  //timestamp
    private String status;  // active / (inactive|date)

    public GroupAdmins(String userid, String position, Date fromdate, String status) {
        this.userid = userid;
        this.position = position;
        this.fromdate = fromdate;
        this.status = status;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public Date getFromdate() {
        return fromdate;
    }

    public void setFromdate(Date fromdate) {
        this.fromdate = fromdate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "GroupAdmins{" +
                "userid='" + userid + '\'' +
                ", position='" + position + '\'' +
                ", fromdate=" + fromdate +
                ", status='" + status + '\'' +
                '}';
    }
}
