package com.job.hacelaapp.dataSource;

import java.util.Date;

/**
 * Created by Job on Sunday : 5/13/2018.
 */
public class GroupDescription {

    private String typeofgroup;
    private String description;
    private Date createdate;
    private String createdby;

    public GroupDescription(String typeofgroup, String description, Date createdate, String createdby) {
        this.typeofgroup = typeofgroup;
        this.description = description;
        this.createdate = createdate;
        this.createdby = createdby;
    }

    public GroupDescription() {
    }

    public String getTypeofgroup() {
        return typeofgroup;
    }

    public void setTypeofgroup(String typeofgroup) {
        this.typeofgroup = typeofgroup;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreatedate() {
        return createdate;
    }

    public void setCreatedate(Date createdate) {
        this.createdate = createdate;
    }

    public String getCreatedby() {
        return createdby;
    }

    public void setCreatedby(String createdby) {
        this.createdby = createdby;
    }

    @Override
    public String toString() {
        return "GroupDescription{" +
                "typeofgroup='" + typeofgroup + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
