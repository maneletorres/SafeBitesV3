package com.maneletorres.safebites.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.maneletorres.safebites.entities.Product;

import java.util.List;

public class ResponseBody {
    /**
     * Product list in JSON format.
     */
    @SerializedName("products")
    @Expose
    private List<Product> products;

    /**
     * Number of products per page.
     */
    @SerializedName("page_size")
    @Expose
    private int pageSize;

    /**
     *
     */
    @SerializedName("count")
    @Expose
    private int count;

    /**
     * Current page.
     */
    @SerializedName("page")
    @Expose
    private int page;

    public List<Product> getProducts() {
        return products;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getCount() {
        return count;
    }
}