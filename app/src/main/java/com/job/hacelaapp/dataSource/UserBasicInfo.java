package com.job.hacelaapp.dataSource;

/**
 * Created by Job on Sunday : 4/22/2018.
 */
public class UserBasicInfo {
    private String devicetoken;
    private String displayname;
    private String photourl;

    public UserBasicInfo() {
    }

    public UserBasicInfo(String devicetoken, String displayname, String photourl) {
        this.devicetoken = devicetoken;
        this.displayname = displayname;
        this.photourl = photourl;
    }

    public String getDevicetoken() {
        return devicetoken;
    }

    public void setDevicetoken(String devicetoken) {
        this.devicetoken = devicetoken;
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
}
