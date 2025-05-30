package com.example.chilipestdetection.services;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final String BASE_URL = "https://script.google.com/macros/s/AKfycby8Lt57Dl_oADPLNQk-NsWgVqW_qcNMk2ouAUaiqh89JqFJJ0x2bzz40rwaxyE2HE1KtA/";
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static ApiService getApiService() {
        return getClient().create(ApiService.class);
    }
}

