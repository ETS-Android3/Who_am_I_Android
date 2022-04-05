package com.lhs.project.model;

public class DetectionReq {
    private String img_url;

    public DetectionReq(String img_url) {
        this.img_url = img_url;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }
}
