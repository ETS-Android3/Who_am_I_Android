package com.lhs.project.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Item implements Serializable {
    private String kind;
    private String etag;
    private Id id;
    private Snippet snippet;

    public class Id {
        public String kind;
        public String videoId;
    }

    public class Snippet {
        public String publishedAt;
        public String channelId;
        public String title;
        public String description;
        public Thumbnails thumbnails;
        public String channelTitle;
        public String liveBroadcastContent;
        public String publishTime;

        public class Thumbnails {

            //            @JsonProperty("default")
            @SerializedName("default")
            public ImageSize def;
            public ImageSize medium;
            public ImageSize high;

            public class ImageSize {
                public String url;
                public int width;
                public int height;
            }
        }
    }

    public String getKind() {
        return kind;
    }

    public String getEtag() {
        return etag;
    }

    public Id getId() {
        return id;
    }

    public Snippet getSnippet() {
        return snippet;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    public void setId(Id id) {
        this.id = id;
    }

    public void setSnippet(Snippet snippet) {
        this.snippet = snippet;
    }
}

