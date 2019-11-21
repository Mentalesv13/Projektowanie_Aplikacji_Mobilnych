package com.example.projekt.entity;

public class Repertoire {

    private Long id_repertoire;
    private String name_repertoire;
    private String time_repertoire;
    private String tag_repertoire;

    public Long getId_repertoire() {
        return id_repertoire;
    }

    public void setId_repertoire(Long id_repertoire) {
        this.id_repertoire = id_repertoire;
    }

    public String getName_repertoire() {
        return name_repertoire;
    }

    public void setName_repertoire(String name_repertoire) {
        this.name_repertoire = name_repertoire;
    }

    public String getTime_repertoire() {
        return time_repertoire;
    }

    public void setTime_repertoire(String time_repertoire) {
        this.time_repertoire = time_repertoire;
    }

    public String getTag_repertoire() {
        return tag_repertoire;
    }

    public void setTag_repertoire(String tag_repertoire) {
        this.tag_repertoire = tag_repertoire;
    }

    public Repertoire(Long id_repertoire, String name_repertoire, String time_repertoire, String tag_repertoire) {
        this.id_repertoire = id_repertoire;
        this.name_repertoire = name_repertoire;
        this.time_repertoire = time_repertoire;
        this.tag_repertoire = tag_repertoire;
    }
}
