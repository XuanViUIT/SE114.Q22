package com.example.myapplication;
public class Post {
    private String name;
    private String date;
    private String content;

    public Post(String name, String date, String content) {
        this.name = name;
        this.date = date;
        this.content = content;
    }

    public String getName() { return name; }
    public String getDate() { return date; }
    public String getContent() { return content; }
}
