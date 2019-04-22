package com.maneletorres.safebites.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ProductService {
    @GET("search.pl")
    Call<ProductsResponse> getProducts(
            @Query("search_terms") String search_terms,
            @Query("search_simple") int search_simple,
            @Query("action") String action,
            @Query("json") int json,
            @Query("page_size") int page_size,
            @Query("page") int page
    );

    @GET("{upc}")
    Call<ProductResponse> getProduct(@Path("upc") String upc);
}

// ProductsResponse:
// https://world.openfoodfacts.org/cgi/search.pl?search_terms=banania&search_simple=1&action=process&json=1&page_size=10&page=1

// ProductResponse:
// https://world.openfoodfacts.org/api/v0/product/5053827150316.json