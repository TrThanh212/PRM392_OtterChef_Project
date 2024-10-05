package com.app.cookbook.model;

import java.io.Serializable;

public class Rating implements Serializable {

    private String userEmail;
    private String review;
    private double rate;
    private long timestamp;

    public Rating() {}

    public Rating(String userEmail, String review, double rate, long timestamp) {
        this.userEmail = userEmail;
        this.review = review;
        this.rate = rate;
        this.timestamp = timestamp;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}