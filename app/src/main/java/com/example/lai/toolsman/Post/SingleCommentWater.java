package com.example.lai.toolsman.Post;

public class SingleCommentWater {
    private String Comment;
    private String Email;
    private String Profile;

    public SingleCommentWater() {

    }
    public SingleCommentWater(String comment) {
        this.Comment = comment;
    }

    public String getComment() {
        return Comment;
    }

    public void setComment(String comment) {
        Comment = comment;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getProfile() {
        return Profile;
    }

    public void setProfile(String profile) {
        Profile = profile;
    }
}
