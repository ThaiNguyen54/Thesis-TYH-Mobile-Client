package com.example.tryyourhair.RetrofitInstance;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit;

//   private static String BASE_URL = "http://192.168.1.2:7001/";

    private static String BASE_URL = "https://geminisoftvn.ddns.net:7001/";

//    private static String BASE_URL = "https://mobile-dev-final-project-api.vercel.app/";
    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder().baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create()).build();
        }
        return retrofit;
    }
}
