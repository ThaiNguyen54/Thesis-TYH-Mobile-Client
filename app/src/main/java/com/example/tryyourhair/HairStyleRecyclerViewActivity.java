package com.example.tryyourhair;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.channels.Pipe;
import java.util.ArrayList;
import java.util.List;

import com.bumptech.glide.Glide;
import com.example.tryyourhair.CustomAdapter.HairStyleAdapter;
import com.example.tryyourhair.Interface.RecyclerViewInterFace;
import com.example.tryyourhair.Models.HairStyle;
import com.example.tryyourhair.Models.HairstyleDataCallFromAPI;
import com.example.tryyourhair.RetrofitInstance.RetrofitClient;
import com.example.tryyourhair.RetrofitInterface.Methods;
import com.example.tryyourhair.Singleton.Singleton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.search.SearchBar;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HairStyleRecyclerViewActivity extends AppCompatActivity implements RecyclerViewInterFace {

    private RecyclerView rvHairStyle;
    private HairStyleAdapter hairStyleAdapter;
    private List<HairStyle> listHairStyle;
    private ImageView img_home;
    private androidx.appcompat.widget.SearchView searchView;
    Singleton singleton;

    Dialog dialog;
    TextView txt_title;
    TextView txt_des;
    TextView txt_celeb;
    ImageView detail_img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hairstyle_list_recycler_view);

        img_home = findViewById(R.id.img_home);
        singleton = Singleton.getInstance();
        searchView = findViewById(R.id.search_view);
        searchView.clearFocus();

        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterList(newText);
                return false;
            }
        });

        dialog = new Dialog(HairStyleRecyclerViewActivity.this);


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
                            Log.d("CELEB", Hairstyles.get(i).getCelebrity());

                            listHairStyle.add(new HairStyle(
                                    Hairstyles.get(i).getName(),
                                    Hairstyles.get(i).get_id(),
                                    Hairstyles.get(i).getUrl(),
                                    Hairstyles.get(i).getDes(),
                                    Hairstyles.get(i).getTrending(),
                                    Hairstyles.get(i).getCelebrity(),
                                    Hairstyles.get(i).getCategory()
                            ));

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

    }

    @Override
    public void onImageItemClick(int position) {
        Intent intent = new Intent(HairStyleRecyclerViewActivity.this, HomeScreen.class);
        String ChoseHairURL = hairStyleAdapter.getListHairStyle().get(position).getUrl();
        String ChoseHairName = hairStyleAdapter.getListHairStyle().get(position).getName();

        singleton.setChoseHair(true);
        singleton.setChoseHairURL(ChoseHairURL);
        singleton.setChoseHairstyleName(ChoseHairName);
        startActivity(intent);
    }

    @Override
    public void onDetailClick(int position) {
        dialog.setContentView(R.layout.dialog_hairstyle_detail);
        txt_title = (TextView) dialog.findViewById(R.id.dialog_txt_title);
        txt_des = (TextView) dialog.findViewById(R.id.dialog_txt_description);
        txt_celeb = (TextView) dialog.findViewById(R.id.dialog_txt_celeb);
        detail_img = (ImageView) dialog.findViewById(R.id.dialog_img);
        txt_title.setText(hairStyleAdapter.getListHairStyle().get(position).getName());
        txt_des.setText(hairStyleAdapter.getListHairStyle().get(position).getDes());
        txt_celeb.setText("");
        Glide.with(HairStyleRecyclerViewActivity.this).load(hairStyleAdapter.getListHairStyle().get(position).getUrl()).into(detail_img);
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return super.onCreateOptionsMenu(menu);
    }

    private void filterList(String text) {
        List<HairStyle> filteredHairstyle = new ArrayList<>();
        for (HairStyle hairStyle : listHairStyle) {
            if (hairStyle.getCelebrity().toLowerCase().contains(text.toLowerCase())) {
                filteredHairstyle.add(hairStyle);
            }
        }

        if (filteredHairstyle.isEmpty()) {
            Toast.makeText(this, "No hairstyle found", Toast.LENGTH_SHORT).show();
        }
        else {
            hairStyleAdapter.setListHairStyle(filteredHairstyle);
            rvHairStyle.setAdapter(hairStyleAdapter);
        }
    }
}