package com.job.hacelaapp.dataSource;

/**
 * Created by Job on Sunday : 4/22/2018.
 */
//maps db UsersAuth

public class UserAuthInfo {
    private Boolean googleconnected;
    private Boolean fbconnected;
    private String phonenumber;

    public UserAuthInfo() {
    }

    public UserAuthInfo(Boolean googleconnected, Boolean fbconnected, String phonenumber) {
        this.googleconnected = googleconnected;
        this.fbconnected = fbconnected;
        this.phonenumber = phonenumber;
    }

    public Boolean getGoogleconnected() {
        return googleconnected;
    }

    public void setGoogleconnected(Boolean googleconnected) {
        this.googleconnected = googleconnected;
    }

    public Boolean getFbconnected() {
        return fbconnected;
    }

    public void setFbconnected(Boolean fbconnected) {
        this.fbconnected = fbconnected;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    @Override
    public String toString() {
        return "UserAuthInfo{" +
                "googleconnected=" + googleconnected +
                ", fbconnected=" + fbconnected +
                ", phonenumber='" + phonenumber + '\'' +
                '}';
    }
}
