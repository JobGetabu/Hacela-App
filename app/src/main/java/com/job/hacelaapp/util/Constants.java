package com.job.hacelaapp.util;

/**
 * Created by Job on Wednesday : 5/2/2018.
 */
public class Constants {

    public static final int LOCATION_INTERVAL = 10000;
    public static final int FASTEST_LOCATION_INTERVAL = 5000;

    //for future  when we will access even finer location details
    public static final int SUCCESS_RESULT = 0;
    public static final int FAILURE_RESULT = 1;
    public static final String PACKAGE_NAME = "com.job.hacelaapp.util";
    public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";
    public static final String RESULT_DATA_KEY = PACKAGE_NAME +
            ".RESULT_DATA_KEY";
    public static final String LOCATION_DATA_EXTRA = PACKAGE_NAME +
            ".LOCATION_DATA_EXTRA";

    public static final String PHONEAUTH_DETAILS = PACKAGE_NAME +
            ".PHONEAUTH_DETAILS";

    public static final String GROUP_UID ="GROUP_UID";

    // This are hacelas collection names
    public static final String GROUPCOL = "Groups";
    public static final String GROUPCONTRIBUTIONCOL = "GroupsContributionDefault";
    public static final String GROUPACCOUNTCOL = "GroupsAccount";
    public static final String GROUPADMINCOL = "GroupsAdmin";
    public static final String GROUPMEMBERCOL = "GroupsMembers";
    public static final String USERSPROFILE = "UsersProfile";

}
