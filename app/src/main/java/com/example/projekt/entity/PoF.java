package com.example.projekt.entity;

public class PoF {

    private Long id_performance;
    private String date_performace;

    public Long getId_performance() {
        return id_performance;
    }

    public void setId_performance(Long id_performance) {
        this.id_performance = id_performance;
    }

    public String getDate_performace() {
        return date_performace;
    }

    public void setDate_performace(String date_performace) {
        this.date_performace = date_performace;
    }

    public PoF(Long id_performance, String date_performace) {
        this.id_performance = id_performance;
        this.date_performace = date_performace;
    }
}
