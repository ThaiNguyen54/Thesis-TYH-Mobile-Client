package com.example.tryyourhair;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import com.example.tryyourhair.CustomAdapter.HairStyleAdapter;
import com.example.tryyourhair.Interface.RecyclerViewInterFace;
import com.example.tryyourhair.Models.HairStyle;
import com.example.tryyourhair.Models.HairstyleDataCallFromAPI;
import com.example.tryyourhair.RetrofitInstance.RetrofitClient;
import com.example.tryyourhair.RetrofitInterface.Methods;
import com.example.tryyourhair.Singleton.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HairStyleRecyclerViewActivity extends AppCompatActivity implements RecyclerViewInterFace {

    private RecyclerView rvHairStyle;
    private HairStyleAdapter hairStyleAdapter;
    private List<HairStyle> listHairStyle;
    private ImageView img_home;
    Singleton singleton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hairstyle_list_recycler_view);

        img_home = findViewById(R.id.img_home);
        singleton = Singleton.getInstance();

        img_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent HomeScreenIntent = new Intent(HairStyleRecyclerViewActivity.this, HomeScreen.class);
                startActivity(HomeScreenIntent);
            }
        });


        Thread GetAllHairStyleThread = new Thread(new Runnable() {
            @Override
            public void run() {
                rvHairStyle = (RecyclerView) findViewById(R.id.rv_hairstyle);
                listHairStyle = new ArrayList<>();

                // Call API get Hairstyle
                Methods methods = RetrofitClient.getRetrofitInstance().create(Methods.class);
                Call<HairstyleDataCallFromAPI> call = methods.getAllData();
                call.enqueue(new Callback<HairstyleDataCallFromAPI>() {
                    @Override
                    public void onResponse(@NonNull Call<HairstyleDataCallFromAPI> call, @NonNull Response<HairstyleDataCallFromAPI> response) {
                        assert response.body() != null;
                        ArrayList<HairstyleDataCallFromAPI.data> Hairstyles = response.body().getHairstyles();
                        Log.d("LENGTH", String.valueOf(Hairstyles.size()));
                        for (int i = 0; i < Hairstyles.size(); i++) {
                            Log.d("TEST",  Hairstyles.get(i).get_id());
                            Log.d("NAME", Hairstyles.get(i).getName());

                            listHairStyle.add(new HairStyle(
                                    Hairstyles.get(i).getName(),
                                    Hairstyles.get(i).get_id(),
                                    Hairstyles.get(i).getUrl(),
                                    Hairstyles.get(i).getDes()));

                            hairStyleAdapter = new HairStyleAdapter(
                                    HairStyleRecyclerViewActivity.this,
                                    listHairStyle,
                                    HairStyleRecyclerViewActivity.this);

                            rvHairStyle.setAdapter(hairStyleAdapter);

                    // Recycler view scroll vertical
//                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(HairStyleRecyclerViewActivity.this, LinearLayoutManager.VERTICAL, false);

                            // Use GridLayout
                            GridLayoutManager gridLayoutManager = new GridLayoutManager(HairStyleRecyclerViewActivity.this, 2);
                            rvHairStyle.setLayoutManager(gridLayoutManager);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<HairstyleDataCallFromAPI> call, @NonNull Throwable t) {

                    }
                });
            }
        });
        GetAllHairStyleThread.start();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent HomeScreenIntent = new Intent(this, HomeScreen.class);
        startActivity(HomeScreenIntent);
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(HairStyleRecyclerViewActivity.this, HomeScreen.class);
        String ChoseHairURL = listHairStyle.get(position).getUrl();
        String ChoseHairName = listHairStyle.get(position).getName();

        singleton.setChoseHair(true);
        singleton.setChoseHairURL(ChoseHairURL);
        singleton.setChoseHairstyleName(ChoseHairName);
        startActivity(intent);
    }
}