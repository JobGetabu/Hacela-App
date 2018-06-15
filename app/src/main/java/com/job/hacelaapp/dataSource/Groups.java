package com.job.hacelaapp.dataSource;

/**
 * Created by Job on Tuesday : 5/22/2018.
 */
public class Groups {

    private String groupname;
    private String displayname;
    private String photourl;
    private String groupid;
    private GroupDescription description;
    private GroupConstitution constitution;

    public Groups() {
    }

    public Groups(String groupname, String displayname, String photourl, String groupid, GroupDescription description, GroupConstitution constitution) {
        this.groupname = groupname;
        this.displayname = displayname;
        this.photourl = photourl;
        this.groupid = groupid;
        this.description = description;
        this.constitution = constitution;
    }

    public String getGroupname() {
        return groupname;
    }

    public void setGroupname(String groupname) {
        this.groupname = groupname;
    }

    public String getDisplayname() {
        return displayname;
    }

    public void setDisplayname(String displayname) {
        this.displayname = displayname;
    }

    public String getPhotourl() {
        return photourl;
    }

    public void setPhotourl(String photourl) {
        this.photourl = photourl;
    }

    public GroupDescription getDescription() {
        return description;
    }

    public void setDescription(GroupDescription description) {
        this.description = description;
    }

    public GroupConstitution getConstitution() {
        return constitution;
    }

    public void setConstitution(GroupConstitution constitution) {
        this.constitution = constitution;
    }

    public String getGroupid() {
        return groupid;
    }

    public void setGroupid(String groupid) {
        this.groupid = groupid;
    }

    @Override
    public String toString() {
        return "Groups{" +
                "groupname='" + groupname + '\'' +
                ", displayname='" + displayname + '\'' +
                ", photourl='" + photourl + '\'' +
                ", description=" + description +
                ", constitution=" + constitution +
                '}';
    }
}
