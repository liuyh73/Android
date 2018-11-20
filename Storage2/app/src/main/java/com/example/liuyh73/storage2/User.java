package com.example.liuyh73.storage2;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String username;
    private String password;
    private Bitmap portrait;
    private List<Comment>comments;
    private List<Comment>thumbsUpComments;

    public User(String _username, String _password, Bitmap _portrait){
        this.username = _username;
        this.password = _password;
        this.portrait = _portrait;
        this.comments = new ArrayList<>();
        this.thumbsUpComments = new ArrayList<>();
    }

    public User(String _username, String _password, Bitmap _portrait, List<Comment>_comments, List<Comment>_thumbsUpComments){
        this.username = _username;
        this.password = _password;
        this.portrait = _portrait;
        this.comments = new ArrayList<>(_comments);
        this.thumbsUpComments = new ArrayList<>(_thumbsUpComments);
    }

    public String getUsername() { return this.username; }

    public String getPassword() {
        return this.password;
    }

    public Bitmap getPortrait() { return this.portrait; }

    public void setUsername(String _username) {
        this.username = _username;
    }

    public void setPassowrd (String _password) {
        this.password = _password;
    }

    public void setPortrait (Bitmap _portrait) { this.portrait = _portrait; }

    public List<Comment> getComments() { return this.comments; }
    public List<Comment> getThumbsUpComments() {return this.thumbsUpComments; }

    public void setComments(List<Comment>_comments) {
        this.comments.clear();
        this.comments.addAll(_comments);
    }

    public void setThumbsUpComments(List<Comment>_thumbsUpComments){
        this.thumbsUpComments.clear();
        this.thumbsUpComments.addAll(_thumbsUpComments);
    }
}
