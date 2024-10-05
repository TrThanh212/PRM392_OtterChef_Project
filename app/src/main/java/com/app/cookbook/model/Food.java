package com.app.cookbook.model;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;

public class Food implements Serializable {
    private long id;
    private String name;
    private String image;
    private String url;
    private boolean featured;
    private int count;
    private HashMap<String, UserInfo> favorite;
    private HashMap<String, UserInfo> history;
    private HashMap<String, UserInfo> mydish;
    private HashMap<String, Rating> rating;
    private long categoryId;
    private String categoryName;
    private double calories;

    public Food() {
    }

    public Food(long id, String name, String image, String url, boolean featured, long categoryId, String categoryName, double calories) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.url = url;
        this.featured = featured;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.calories = calories; // Initialize new field
    }
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isFeatured() {
        return featured;
    }

    public void setFeatured(boolean featured) {
        this.featured = featured;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public HashMap<String, UserInfo> getFavorite() {
        return favorite;
    }

    public void setFavorite(HashMap<String, UserInfo> favorite) {
        this.favorite = favorite;
    }

    public String countFavorites() {
        if (favorite == null || favorite.isEmpty()) {
            return "0";
        } else {
            return String.valueOf(favorite.size());
        }
    }

    public HashMap<String, UserInfo> getHistory() {
        return history;
    }

    public void setHistory(HashMap<String, UserInfo> history) {
        this.history = history;
    }

    public HashMap<String, Rating> getRating() {
        return rating;
    }

    public void setRating(HashMap<String, Rating> rating) {
        this.rating = rating;
    }

    public double getRate() {
        if (rating == null || rating.isEmpty()) return 0;
        double sum = 0;
        for (Rating ratingEntity : rating.values()) {
            sum += ratingEntity.getRate();
        }
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        DecimalFormat formatter = new DecimalFormat("#.#");
        formatter.setDecimalFormatSymbols(symbols);
        return Double.parseDouble(formatter.format(sum / rating.size()));
    }

    public int getCountReviews() {
        if (rating == null || rating.isEmpty()) return 0;
        return rating.size();
    }

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public HashMap<String, UserInfo> getMydish() {
        return mydish;
    }

    public void setMydish(HashMap<String, UserInfo> mydish) {
        this.mydish = mydish;
    }

    public String countMydishes() {
        if (mydish == null || mydish.isEmpty()) {
            return "0";
        } else {
            return String.valueOf(mydish.size());
        }
    }

    public double getCalories() { // Getter for calories
        return calories;
    }

    public void setCalories(double calories) { // Setter for calories
        this.calories = calories;
    }
}