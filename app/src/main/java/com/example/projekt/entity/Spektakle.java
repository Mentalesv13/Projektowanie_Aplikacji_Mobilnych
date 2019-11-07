package com.example.projekt.entity;

public class Spektakle {
    private int SpektaklId;
    private String desc;
    private String name;
    private String date;

    public int getSpektaklId() {
        return SpektaklId;
    }

    public void setSpektaklId(int spektaklId) {
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
}
