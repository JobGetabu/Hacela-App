package com.job.hacelaapp.dataSource;

import io.reactivex.annotations.Nullable;

/**
 * Created by Job on Tuesday : 5/1/2018.
 */

//Maps UsersProfile
public class UsersProfile {

    private String profession;
    @Nullable
    private String typeOfBusiness;
    private String income;
    private int profileCompletion;
    private String fullname;
    private UsersGroups groups;

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setIdnumber(String idnumber) {
        this.idnumber = idnumber;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    private String gender;
    private String idnumber;
    private Location location;

    public String getGender() {
        return gender;
    }

    public String getIdnumber() {
        return idnumber;
    }

    public Location getLocation() {
        return location;
    }

    public UsersProfile() {
    }

    public UsersProfile(String profession, @Nullable String typeOfBusiness, String income,
                        int profileCompletion, String fullname, UsersGroups groups) {
        this.profession = profession;
        this.typeOfBusiness = typeOfBusiness;
        this.income = income;
        this.profileCompletion = profileCompletion;
        this.fullname = fullname;
        this.groups = groups;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getTypeOfBusiness() {
        return typeOfBusiness;
    }

    public void setTypeOfBusiness(String typeOfBusiness) {
        this.typeOfBusiness = typeOfBusiness;
    }

    public String getIncome() {
        return income;
    }

    public void setIncome(String income) {
        this.income = income;
    }

    public int getProfileCompletion() {
        return profileCompletion;
    }

    public void setProfileCompletion(int profileCompletion) {
        this.profileCompletion = profileCompletion;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public UsersGroups getGroups() {
        return groups;
    }

    public void setGroups(UsersGroups groups) {
        this.groups = groups;
    }

    @Override
    public String toString() {
        return "UsersProfile{" +
                "profession='" + profession + '\'' +
                ", typeOfBusiness='" + typeOfBusiness + '\'' +
                ", income='" + income + '\'' +
                ", profileCompletion=" + profileCompletion +
                ", fullname='" + fullname + '\'' +
                ", groups=" + groups +
                '}';
    }
}
