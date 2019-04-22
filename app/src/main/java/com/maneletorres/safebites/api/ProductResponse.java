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

    public String getStatus_verbose() {
        return status_verbose;
    }

    public void setStatus_verbose(String status_verbose) {
        this.status_verbose = status_verbose;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
