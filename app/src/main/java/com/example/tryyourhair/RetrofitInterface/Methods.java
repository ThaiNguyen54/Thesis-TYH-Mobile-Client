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

    @GET("/shair-engine/ver1/hairstyle")
    Call <HairstyleDataCallFromAPI> getAllData();

    @GET("/shair-engine/ver1/hairstyle/Male")
    Call <HairstyleDataCallFromAPI> getMaleHairStyle();

    @GET("/shair-engine/ver1/hairstyle/Female")
    Call <HairstyleDataCallFromAPI> getFemaleHairStyle();

    @POST("/shair-engine/ver1/generate_hair")
    Call<GenerationData> postGenerationData(@Body GenerationData generationData);
}
