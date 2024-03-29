package com.example.ssumeet;

public class ProfileInfo {
    private String name;
    private String age;
    private String subject;
    private String interest;
    private String photoUrl;
    private String chat_permission;
    private String ranchat_permission;

    public ProfileInfo(String name, String age, String subject, String interest,
                       String chat_permission, String ranchat_permission) {
        this.name = name;
        this.age = age;
        this.subject = subject;
        this.interest = interest;
        this.chat_permission = chat_permission;
        this.ranchat_permission = ranchat_permission;
    }
    public ProfileInfo(String name, String age, String subject, String interest, String photoUrl,
                       String chat_permission, String ranchat_permission) {
        this.name = name;
        this.age = age;
        this.subject = subject;
        this.interest = interest;
        this.photoUrl = photoUrl;
        this.chat_permission = chat_permission;
        this.ranchat_permission = ranchat_permission;
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

    public String getChat_permission() { return this.chat_permission; }
    public void setChat_permission(String chat_permission) { this.chat_permission = chat_permission; }

    public String getRanchat_permission() { return this.ranchat_permission; }
    public void setRanchat_permission(String ranchat_permission) { this.ranchat_permission = ranchat_permission; }

}
