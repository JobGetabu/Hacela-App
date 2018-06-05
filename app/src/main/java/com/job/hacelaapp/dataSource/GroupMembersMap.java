package com.job.hacelaapp.dataSource;

import java.util.Map;

/**
 * Created by Job on Sunday : 6/3/2018.
 */
public class GroupMembersMap {
    private Map<String,GroupMembers> stringGroupMembersMap;

    public GroupMembersMap() {
    }

    public GroupMembersMap(Map<String, GroupMembers> stringGroupMembersMap) {
        this.stringGroupMembersMap = stringGroupMembersMap;
    }

    public Map<String, GroupMembers> getStringGroupMembersMap() {
        return stringGroupMembersMap;
    }

    public void setStringGroupMembersMap(Map<String, GroupMembers> stringGroupMembersMap) {
        this.stringGroupMembersMap = stringGroupMembersMap;
    }

    @Override
    public String toString() {
        return "GroupMembersMap{" +
                "stringGroupMembersMap=" + stringGroupMembersMap +
                '}';
    }
}
