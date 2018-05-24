package com.job.hacelaapp.dataSource;

/**
 * Created by Job on Thursday : 5/24/2018.
 */
public class GroupMembers {
    private String userid;
    private String userrole;
    private String username;

    public GroupMembers() {
    }

    public GroupMembers(String userid, String userrole, String username) {
        this.userid = userid;
        this.userrole = userrole;
        this.username = username;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUserrole() {
        return userrole;
    }

    public void setUserrole(String userrole) {
        this.userrole = userrole;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "GroupMembers{" +
                "userid='" + userid + '\'' +
                ", userrole='" + userrole + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
