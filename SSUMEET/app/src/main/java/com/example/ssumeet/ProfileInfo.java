package com.example.ssumeet;

public class ProfileInfo {
    private String name;
    private String age;
    private String subject;
    private String interest;
    private String photoUrl;

    public ProfileInfo(String name, String age, String subject, String interest) {
        this.name = name;
        this.age = age;
        this.subject = subject;
        this.interest = interest;
    }
    public ProfileInfo(String name, String age, String subject, String interest, String photoUrl) {
        this.name = name;
        this.age = age;
        this.subject = subject;
        this.interest = interest;
        this.photoUrl = photoUrl;
    }

    public String getName() { return this.name; }
    public void setName(String name) { this.name = name; }

    public String getAge() { return this.age; }
    public void setAge(String age) { this.age = age; }

    public String getSubject() { return this.subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getInterest() { return this.interest; }
    public void setInterest(String interest) { this.interest = interest; }

    public String getPhotoUrl() { return this.photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

}
