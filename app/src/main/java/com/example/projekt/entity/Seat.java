package com.example.projekt.entity;

public class Seat {
    private int row_id;
    private int seat_id;
    private int seatNumber;
    private int seatString;
    private int seatTag;
    private double seatPrice;
    private String seatType;
    private boolean seatReduced;

    public Seat(int row_id, int seat_id, int seatNumber, int seatString, int seatTag, double seatPrice, String seatType, boolean seatReduced) {
        this.row_id = row_id;
        this.seat_id = seat_id;
        this.seatNumber = seatNumber;
        this.seatString = seatString;
        this.seatTag = seatTag;
        this.seatPrice = seatPrice;
        this.seatType = seatType;
        this.seatReduced = seatReduced;
    }

    public double getSeatPrice() {
        return seatPrice;
    }

    public void setSeatPrice(double seatPrice) {
        this.seatPrice = seatPrice;
    }

    public String getSeatType() {
        return seatType;
    }

    public void setSeatType(String seatType) {
        this.seatType = seatType;
    }

    public int getSeatString() {
        return seatString;
    }

    public void setSeatString(int seatString) {
        this.seatString = seatString;
    }

    public boolean isSeatReduced() {
        return seatReduced;
    }

    public void setSeatReduced(boolean seatReduced) {
        this.seatReduced = seatReduced;
    }

    public int getSeatTag() {
        return seatTag;
    }

    public void setSeatTag(int seatTag) {
        this.seatTag = seatTag;
    }

    public int getRow_id() {
        return row_id;
    }

    public void setRow_id(int row_id) {
        this.row_id = row_id;
    }

    public int getSeat_id() {
        return seat_id;
    }

    public void setSeat_id(int seat_id) {
        this.seat_id = seat_id;
    }

    public int getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(int seatNumber) {
        this.seatNumber = seatNumber;
    }
}
