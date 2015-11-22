package com.dtf.daanx;

import com.google.gson.annotations.SerializedName;

/**
 * Created by yoyo930021 on 2015/11/19.
 */
public class Post {

    @SerializedName("title")
    private String title;

    @SerializedName("content")
    private String content;

    @SerializedName("writer")
    private String writer;

    @SerializedName("file")
    private String file;

    @SerializedName("image")
    private String image;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getWriter() {
        return writer.substring(0,0);
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

}
