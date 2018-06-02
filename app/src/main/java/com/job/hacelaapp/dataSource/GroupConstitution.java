package com.job.hacelaapp.dataSource;

/**
 * Created by Job on Tuesday : 5/22/2018.
 */
public class GroupConstitution {

    private String constitutionurl;
    private String constitutiondescr;

    public GroupConstitution() { }

    public GroupConstitution(String constitutionurl, String constitutiondescr) {
        this.constitutionurl = constitutionurl;
        this.constitutiondescr = constitutiondescr;
    }

    public String getConstitutionurl() {
        return constitutionurl;
    }

    public void setConstitutionurl(String constitutionurl) {
        this.constitutionurl = constitutionurl;
    }

    public String getConstitutiondescr() {
        return constitutiondescr;
    }

    public void setConstitutiondescr(String constitutiondescr) {
        this.constitutiondescr = constitutiondescr;
    }

    @Override
    public String toString() {
        return "GroupConstitution{" +
                "constitutionurl='" + constitutionurl + '\'' +
                ", constitutiondescr='" + constitutiondescr + '\'' +
                '}';
    }
}
