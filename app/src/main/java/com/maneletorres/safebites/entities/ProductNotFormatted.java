package com.maneletorres.safebites.entities;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProductNotFormatted {
    @SerializedName("product_name")
    @Expose
    private String product_name;

    @SerializedName("code")
    @Expose
    private String code;

    @SerializedName("nutriments")
    @Expose
    private JsonObject nutriments;

    @SerializedName("image_small_url")
    @Expose
    private String image_small_url;

    @SerializedName("ingredients_text")
    @Expose
    private String ingredients_text;

    @SerializedName("serving_size")
    @Expose
    private String serving_size;

    @SerializedName("allergens_hierarchy")
    @Expose
    private JsonArray allergens_hierarchy;

    @SerializedName("traces_hierarchy")
    @Expose
    private JsonArray traces_hierarchy;

    public ProductNotFormatted(String product_name, String code, JsonObject nutriments, String image_small_url, String ingredients_text, String serving_size, JsonArray allergens_hierarchy, JsonArray traces_hierarchy) {
        this.product_name = product_name;
        this.code = code;
        this.nutriments = nutriments;
        this.image_small_url = image_small_url;
        this.ingredients_text = ingredients_text;
        this.serving_size = serving_size;
        this.allergens_hierarchy = allergens_hierarchy;
        this.traces_hierarchy = traces_hierarchy;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public JsonObject getNutriments() {
        return nutriments;
    }

    public void setNutriments(JsonObject nutriments) {
        this.nutriments = nutriments;
    }

    public String getImage_small_url() {
        return image_small_url;
    }

    public void setImage_small_url(String image_small_url) {
        this.image_small_url = image_small_url;
    }

    public String getIngredients_text() {
        return ingredients_text;
    }

    public void setIngredients_text(String ingredients_text) {
        this.ingredients_text = ingredients_text;
    }

    public String getServing_size() {
        return serving_size;
    }

    public void setServing_size(String serving_size) {
        this.serving_size = serving_size;
    }

    public JsonArray getAllergens_hierarchy() {
        return allergens_hierarchy;
    }

    public void setAllergens_hierarchy(JsonArray allergens_hierarchy) {
        this.allergens_hierarchy = allergens_hierarchy;
    }

    public JsonArray getTraces_hierarchy() {
        return traces_hierarchy;
    }

    public void setTraces_hierarchy(JsonArray traces_hierarchy) {
        this.traces_hierarchy = traces_hierarchy;
    }
}