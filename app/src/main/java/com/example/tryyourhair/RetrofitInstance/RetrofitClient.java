package com.example.tryyourhair.RetrofitInstance;

import com.example.tryyourhair.UnsafeOkHttpClient.UnsafeOkHttpClient;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit;

    // Unsafe OkHttp Client is used for accepting self-signed certificate Nodejs server
    private static OkHttpClient okHttpClient = UnsafeOkHttpClient.getUnsafeOkHttpClient();

   private static String BASE_URL = "https://192.168.1.12:7001/";

//    private static String BASE_URL = "https://geminisoftvn.ddns.net:7001/";

//    private static String BASE_URL = "https://mobile-dev-final-project-api.vercel.app/";
    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create()).build();
        }
        return retrofit;
    }
}
