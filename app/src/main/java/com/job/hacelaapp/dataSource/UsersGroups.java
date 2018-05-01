package com.job.hacelaapp.dataSource;

import java.util.Date;
import java.util.UUID;

import io.reactivex.annotations.Nullable;

/**
 * Created by Job on Tuesday : 5/1/2018.
 */
public class UsersGroups {

    private UUID groupId;
    private boolean isMember;
    private Date startDate;
    @Nullable
    private Date endDate;


    public UsersGroups(UUID groupId, boolean isMember, Date startDate, @Nullable Date endDate) {
        this.groupId = groupId;
        this.isMember = isMember;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public UUID getGroupId() {
        return groupId;
    }

    public void setGroupId(UUID groupId) {
        this.groupId = groupId;
    }

    public boolean isMember() {
        return isMember;
    }

    public void setMember(boolean member) {
        isMember = member;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return "UsersGroups{" +
                "groupId=" + groupId +
                ", isMember=" + isMember +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }
}
