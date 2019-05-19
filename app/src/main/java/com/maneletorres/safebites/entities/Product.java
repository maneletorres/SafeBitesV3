package com.maneletorres.safebites.entities;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.ArrayList;

public class Product implements Parcelable {
    public static final Parcelable.Creator<Product> CREATOR = new Parcelable.Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    /**
     * UPC of the product.
     */
    private String upc;

    /**
     * Name of the product.
     */
    private String name;

    /**
     * Image resource of the product.
     */
    private String image_resource;

    /**
     * Nutrients of the product.
     */
    private ArrayList<Nutrient> nutrients;

    /**
     * Ingredients of the product.
     */
    private String ingredients;

    /**
     * Serving size of the product.
     */
    private String serving_size;

    /**
     * Allergens of the product.
     */
    private ArrayList<String> allergens;

    public Product() {
    }

    public Product(String upc, String name, String image_resource, ArrayList<Nutrient> nutrients,
                   String ingredients, String serving_size, ArrayList<String> allergens) {
        this.upc = upc;
        this.name = name;
        this.image_resource = image_resource;
        this.nutrients = nutrients;
        this.ingredients = ingredients;
        this.serving_size = serving_size;
        this.allergens = allergens;
    }

    private Product(Parcel in) {
        upc = in.readString();
        name = in.readString();
        image_resource = in.readString();
        nutrients = in.createTypedArrayList(Nutrient.CREATOR);
        ingredients = in.readString();
        serving_size = in.readString();
        //allergens = in.readArrayList(ClassLoader.getSystemClassLoader());
        allergens = in.readArrayList(null);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(upc);
        dest.writeString(name);
        dest.writeString(image_resource);
        dest.writeTypedList(nutrients);
        dest.writeString(ingredients);
        dest.writeString(serving_size);
        dest.writeList(allergens);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getUpc() {
        return upc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage_resource() {
        return image_resource;
    }

    public ArrayList<Nutrient> getNutrients() {
        return nutrients;
    }

    public String getIngredients() {
        return ingredients;
    }

    public String getServing_size() {
        return serving_size;
    }

    public ArrayList<String> getAllergens() {
        return allergens;
    }

    @NonNull
    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Product) {
            return ((Product) obj).getUpc().equals(this.getUpc());
        }

        return false;
    }
}