package com.example.tryyourhair.RetrofitInterface;

import com.example.tryyourhair.Models.GenerationData;
import com.example.tryyourhair.Models.HairstyleDataCallFromAPI;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface Methods {
//    @GET("ver1/hairstyle")
//    Call <TestAPIHairstyle> getAllData();

    @GET("ver1/hairstyle")
    Call <HairstyleDataCallFromAPI> getAllData();

    @POST("ver1/generate_hair")
    Call<GenerationData> postGenerationData(@Body GenerationData generationData);
}
