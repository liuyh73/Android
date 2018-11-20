package com.example.liuyh73.storage2;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Comment implements Serializable {
    private String commentUsername;
    private String commentTime;
    private String commentContent;
    private int thumbsUpCount;
    private static final long serialVersionUID = 8711368828010083044L;
    public Comment(String _commentUsername, String _commentContent, int _thumbsUpCount) {
        commentUsername = _commentUsername;
        commentContent = _commentContent;
        thumbsUpCount = _thumbsUpCount;
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        commentTime = df.format(new Date());// new Date()为获取当前系统时间
    }

    public Comment(String _commentUsername, String _commentTime, String _commentContent, int _thumbsUpCount) {
        commentUsername = _commentUsername;
        commentContent = _commentContent;
        thumbsUpCount = _thumbsUpCount;
        commentTime = _commentTime;
    }

    public String getCommentUsername(){
        return commentUsername;
    }
    public void setCommentUsername(String _commentUsername) {
        commentUsername = _commentUsername;
    }

    public String getCommentTime(){
        return commentTime;
    }
    public void setCommentTime(String _commentTime) {
        commentTime = _commentTime;
    }

    public String getCommentContent() {
        return commentContent;
    }
    public void setCommentContent(String _commentContent) {
        commentContent = _commentContent;
    }

    public int getThumbsUpCount(){
        return thumbsUpCount;
    }
    public void setThumbsUpCount(int _thumbsUpCount){
        thumbsUpCount = _thumbsUpCount;
    }

    @Override
    public String toString() {
        return commentUsername+" "+commentTime+" "+commentContent+" "+thumbsUpCount;
    }

    @Override
    public boolean equals(Object obj){
        if(obj == null) {
            return false;
        }
        if(this == obj){
            return true;
        }
        if(obj instanceof Comment) {
            Comment comment = (Comment)obj;
            if(this.commentUsername.equals(comment.commentUsername) && this.commentTime.equals(comment.commentTime)){
                return true;
            } else {
                return false;
            }
        }
        return false;
    }
}
