package com.example.ssumeet.model;

public class UserModel {
    private String userid;
    private String uid;
    private String name;
    private String age;
    private String major;
    private String token;
    private String photoUrl;
    private String statusMsg;
    //private boolean chat_permission;
    //private boolean ranchat_permission;

    private String interest;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() { return this.age; }

    public void setAge(String age) { this.age = age; }

    public String getMajor() { return this.major; }

    public  void setMajor(String major) { this.major = major; }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getStatusMsg() { return statusMsg; }

    public void setStatusMsg(String statusMsg) { this.statusMsg = statusMsg; }

    /*public boolean getChat_permission() { return chat_permission; }

    public void setChat_permission(boolean chat_permission) { this.chat_permission = chat_permission; }

    public boolean getRanchat_permission() { return ranchat_permission; }

    public void setRanchat_permission(boolean ranchat_permission) { this.ranchat_permission = ranchat_permission; }*/

    public String getInterest() {
        return interest;
    }

    public void setInterest(String interest) {
        this.interest = interest;
    }
}
