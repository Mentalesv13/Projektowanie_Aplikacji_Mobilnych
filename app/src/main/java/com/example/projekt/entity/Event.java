package com.example.projekt.entity;

public class Event {

private Long id_event;
private String name_event;
private String sdesc_event;
private String desc_event;

    public Long getId_event() {
        return id_event;
    }

    public void setId_event(Long id_event) {
        this.id_event = id_event;
    }

    public String getName_event() {
        return name_event;
    }

    public void setName_event(String name_event) {
        this.name_event = name_event;
    }

    public String getSdesc_event() {
        return sdesc_event;
    }

    public void setSdesc_event(String sdesc_event) {
        this.sdesc_event = sdesc_event;
    }

    public String getDesc_event() {
        return desc_event;
    }

    public void setDesc_event(String desc_event) {
        this.desc_event = desc_event;
    }

    public Event(Long id_event, String name_event, String sdesc_event, String desc_event) {
        this.id_event = id_event;
        this.name_event = name_event;
        this.sdesc_event = sdesc_event;
        this.desc_event = desc_event;
    }

    @Override
    public String toString() {
        return "Event{" +
                "id_event=" + id_event +
                ", name_event='" + name_event + '\'' +
                ", sdesc_event='" + sdesc_event + '\'' +
                ", desc_event='" + desc_event + '\'' +
                '}';
    }
}
