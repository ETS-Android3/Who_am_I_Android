package com.lhs.project.model;

public class nuPostingReq {
    private int id;
    private String img_url;
    private String content;
    private String created_at;

    public int getId() {
        return id;
    }

    public String getImg_url() {
        return img_url;
    }

    public String getContent() {
        return content;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
}
