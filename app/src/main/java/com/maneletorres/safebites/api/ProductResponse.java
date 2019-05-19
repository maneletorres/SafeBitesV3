package com.maneletorres.safebites.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.maneletorres.safebites.entities.ProductNotFormatted;

public class ProductResponse {
    @SerializedName("product")
    @Expose
    private ProductNotFormatted product;

    @SerializedName("status_verbose")
    @Expose
    private String status_verbose;

    @SerializedName("code")
    @Expose
    private String code;

    @SerializedName("status")
    @Expose
    private int status;

    public ProductNotFormatted getProduct() {
        return product;
    }

    public void setProduct(ProductNotFormatted product) {
        this.product = product;
    }
}