package com.job.hacelaapp.dataSource;

/**
 * Created by Job on Thursday : 5/24/2018.
 */
public class GroupMembers {
    private String groupid;
    private String userid;
    private String userrole;
    private String username;
    private Boolean ismember;

    public GroupMembers() {
    }

    public GroupMembers(String groupid, String userid, String userrole, String username, Boolean ismember) {
        this.groupid = groupid;
        this.userid = userid;
        this.userrole = userrole;
        this.username = username;
        this.ismember = ismember;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public Boolean getIsmember() {
        return ismember;
    }

    public void setIsmember(Boolean ismember) {
        this.ismember = ismember;
    }

    public String getGroupid() {
        return groupid;
    }

    public void setGroupid(String groupid) {
        this.groupid = groupid;
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
                ", userrole='" + userrole + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
