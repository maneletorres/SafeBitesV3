package com.maneletorres.safebites.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.maneletorres.safebites.data.ProductNotFormatted;

import java.util.List;

public class ProductsResponse {
    @SerializedName("products")
    @Expose
    private List<ProductNotFormatted> products;

    @SerializedName("page_size")
    @Expose
    private int pageSize;

    @SerializedName("count")
    @Expose
    private int count;

    @SerializedName("page")
    @Expose
    private int page;

    public List<ProductNotFormatted> getProducts() {
        return products;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getCount() {
        return count;
    }
}