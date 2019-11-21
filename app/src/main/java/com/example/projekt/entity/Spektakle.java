package com.example.projekt.entity;

public class Spektakle {
    private long SpektaklId;
    private String desc;
    private String name;
    private String date;
    private String imgUrl;

    public Spektakle(long spektaklId, String name, String desc, String date, String imgUrl) {
        SpektaklId = spektaklId;
        this.desc = desc;
        this.name = name;
        this.date = date;
        this.imgUrl = imgUrl;
    }

    public long getSpektaklId() {
        return SpektaklId;
    }

    public void setSpektaklId(long spektaklId) {
        SpektaklId = spektaklId;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
