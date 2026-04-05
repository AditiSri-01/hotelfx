package com.hotel.model;

import java.io.Serializable;

public class Booking implements Serializable {
    private Customer customer;
    private int roomNo;
    private int nights;
    private double totalBill;

    public Booking(Customer customer, int roomNo, int nights, double totalBill) {
        this.customer = customer;
        this.roomNo = roomNo;
        this.nights = nights;
        this.totalBill = totalBill;
    }

    public Customer getCustomer() {
        return customer;
    }

    public String getCustomerName() {
        return customer.getName();
    } // For TableView

    public String getPhone() {
        return customer.getPhone();
    } // For TableView

    public int getRoomNo() {
        return roomNo;
    }

    public int getNights() {
        return nights;
    }

    public double getTotalBill() {
        return totalBill;
    }
}
