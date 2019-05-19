package com.maneletorres.safebites.api;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public class ProductApi {
    private static Retrofit sRetrofit = null;
    private static Retrofit sRetrofit2 = null;

    public static Retrofit getClient() {
        if (sRetrofit == null) {
            sRetrofit = new Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl("https://world.openfoodfacts.org/cgi/")
                    .build();
        }
        return sRetrofit;
    }

    public static Retrofit getProduct() {
        if (sRetrofit2 == null) {
            sRetrofit2 = new Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl("https://world.openfoodfacts.org/api/v0/product/")
                    .build();
        }
        return sRetrofit2;
    }

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
}