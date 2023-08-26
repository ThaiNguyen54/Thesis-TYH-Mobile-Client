package com.example.tryyourhair;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import com.example.tryyourhair.Models.HairstyleDataCallFromAPI;
import com.example.tryyourhair.RetrofitInstance.RetrofitClient;
import com.example.tryyourhair.RetrofitInterface.Methods;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TestAPI extends AppCompatActivity {

    private Button btnGetData;
    private ListView listView;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_api);

        btnGetData = findViewById(R.id.btnGetData);
        listView = findViewById(R.id.listviewData);
        imageView = findViewById(R.id.img_hairstyle);
//        Glide.with(this).load("https://res.cloudinary.com/dkwihofta/image/upload/v1683795300/StyleYourHair/toc-nu-7_yintwl.png").into(imageView);

        btnGetData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Methods methods = RetrofitClient.getRetrofitInstance().create(Methods.class);
                Call <HairstyleDataCallFromAPI> call = methods.getAllData();
                call.enqueue(new Callback<HairstyleDataCallFromAPI>() {
                    @Override
                    public void onResponse(Call<HairstyleDataCallFromAPI> call, Response<HairstyleDataCallFromAPI> response) {
                        ArrayList<HairstyleDataCallFromAPI.data> Hairstyles  = response.body().getHairstyles();
                        String[] hairstyle = new String[Hairstyles.size()];
                        for (int i = 0; i < Hairstyles.size(); i++) {
                            hairstyle[i] = "Id : " + Hairstyles.get(i).get_id() + "\nName : " + Hairstyles.get(i).getUrl() + " " + Hairstyles.get(i).getDes() ;
                            Glide.with(TestAPI.this).load(Hairstyles.get(i).getUrl()).into(imageView);
                        }

                        listView.setAdapter(new ArrayAdapter< String >(getApplicationContext(), android.R.layout.simple_list_item_1, hairstyle));
                    }

                    @Override
                    public void onFailure(Call<HairstyleDataCallFromAPI> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), "An error has occured", Toast.LENGTH_LONG).show();
                        Log.d("ERROR", t.toString());
                    }
                });

            }
        });
    }
}