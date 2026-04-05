package com.hotel.model;

import java.io.Serializable;

public class Room implements Serializable, Comparable<Room> {
    private int roomNo;
    private String type; // Standard, Deluxe, Suite
    private double price;
    private String status; // Available, Occupied, Cleaning

    public Room(int roomNo, String type, double price, String status) {
        this.roomNo = roomNo;
        this.type = type;
        this.price = price;
        this.status = status;
    }

    public int getRoomNo() {
        return roomNo;
    }

    public String getType() {
        return type;
    }

    public double getPrice() {
        return price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDisplayString() {
        return "#" + roomNo + " - " + type + " (₹" + price + "/night)";
    }

    @Override
    public int compareTo(Room other) {
        return Double.compare(this.price, other.price);
    }
}
