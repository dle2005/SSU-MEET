package com.example.ssumeet;

public class WritePostInfo {
    private String title;
    private String content;
    private String publisher;

    public WritePostInfo(String title, String content, String publisher) {
        this.title = title;
        this.content = content;
        this.publisher = publisher;
    }

    public String getTitle() { return this.title ;}
    public void setTitle() { this.title = title; }
    public String getContent() { return this.content ;}
    public void setContent() { this.content = content; }
    public String getPublisher() { return this.publisher ;}
    public void setPublisher() { this.title = publisher; }
}
