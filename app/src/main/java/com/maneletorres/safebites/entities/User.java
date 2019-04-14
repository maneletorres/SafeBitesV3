package com.maneletorres.safebites.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class User {
    private String email;
    private String displayName;
    private ArrayList<Product> products;
    private Map<String, Boolean> allergies;

    public User(String email, String displayName) {
        this.email = email;
        this.displayName = displayName;
        this.products = new ArrayList<>();
        this.allergies = new HashMap<>();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public ArrayList<Product> getProducts() {
        return products;
    }

    public void setProducts(ArrayList<Product> products) {
        this.products = products;
    }

    public Map<String, Boolean> getAllergies() {
        return allergies;
    }

    public void setAllergies(Map<String, Boolean> allergies) {
        this.allergies = allergies;
    }
}