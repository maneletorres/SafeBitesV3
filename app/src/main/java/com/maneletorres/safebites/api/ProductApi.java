package com.maneletorres.safebites.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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
}