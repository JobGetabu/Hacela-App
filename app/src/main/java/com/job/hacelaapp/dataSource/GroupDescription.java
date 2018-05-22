package com.job.hacelaapp.dataSource;

/**
 * Created by Job on Sunday : 5/13/2018.
 */
public class GroupDescription {

    private String typeofgroup;
    private String description;

    public GroupDescription(String typeofgroup, String description) {
        this.typeofgroup = typeofgroup;
        this.description = description;
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

    @Override
    public String toString() {
        return "GroupDescription{" +
                "typeofgroup='" + typeofgroup + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
