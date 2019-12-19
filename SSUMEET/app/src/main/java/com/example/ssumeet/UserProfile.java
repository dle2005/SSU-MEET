package com.example.ssumeet;

public class UserProfile {
    private String name;
    private String birthday;
    private String subject;

    public UserProfile(String name, String age, String subject) {
        this.name = name;
        this.birthday = birthday;
        this.subject = subject;
    }

    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getBirthday() {
        return this.birthday;
    }
    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }
    public String getSubject() {
        return this.subject;
    }
    public void setSubject(String subject) {
        this.subject = subject;
    }
}
