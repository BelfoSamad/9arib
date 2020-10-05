package com.belfoapps.qarib.pojo;

import java.util.Date;

public class User {

    private String username;
    private String first_name;
    private String last_name;
    private Date birthdate;
    private String gender;

    public User(String username, String first_name, String last_name, Date birthdate, String gender) {
        this.username = username;
        this.first_name = first_name;
        this.last_name = last_name;
        this.birthdate = birthdate;
        this.gender = gender;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public Date getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
