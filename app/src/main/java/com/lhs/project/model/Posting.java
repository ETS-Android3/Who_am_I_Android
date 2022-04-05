package com.lhs.project.model;

import java.io.Serializable;

public class Posting implements Serializable {
    private int posting_id;
    private String img_url;
    private String content;
    private int user_id;
    private String username;
    private String created_at;
    private String like_cut;

    public int getPosting_id() {
        return posting_id;
    }

    public String getImg_url() {
        return img_url;
    }

    public String getContent() {
        return content;
    }

    public int getUser_id() {
        return user_id;
    }

    public String getUsername() {
        return username;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getLike_cut() {
        return like_cut;
    }

    public void setPosting_id(int posting_id) {
        this.posting_id = posting_id;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public void setLike_cut(String like_cut) {
        this.like_cut = like_cut;
    }
}
