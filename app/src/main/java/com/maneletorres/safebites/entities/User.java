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
        this.products = null;
        this.allergies = new HashMap<>();
    }

    public String getEmail() {
        return email;
    }

    public String getDisplayName() {
        return displayName;
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

    public void addProduct(Product product) {
        int position = checkProduct(product);
        if (position == -1) {
            products.add(product);
        }
    }

    public void removeProduct(Product product) {
        int positionToDelete = checkProduct(product);
        if (positionToDelete >= 0 && positionToDelete < products.size()) {
            products.remove(positionToDelete);
        }
    }

    private int checkProduct(Product product) {
        boolean condition = false;
        int position = -1;

        for (int i = 0; i < products.size() && !condition; i++) {
            Product currentProduct = products.get(i);
            if (currentProduct.getUpc().equals(product.getUpc())) {
                condition = true;
                position = i;
            }
        }

        return position;
    }
}