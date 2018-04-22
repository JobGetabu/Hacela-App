package com.job.hacelaapp.dataSource;

/**
 * Created by Job on Sunday : 4/22/2018.
 */
public class UserBasicInfo {
    private String devicetoken;
    private String username;
    private String photourl;

    public UserBasicInfo() {
    }

    public UserBasicInfo(String devicetoken, String username, String photourl) {
        this.devicetoken = devicetoken;
        this.username = username;
        this.photourl = photourl;
    }

    public String getDevicetoken() {
        return devicetoken;
    }

    public void setDevicetoken(String devicetoken) {
        this.devicetoken = devicetoken;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhotourl() {
        return photourl;
    }

    public void setPhotourl(String photourl) {
        this.photourl = photourl;
    }
}
