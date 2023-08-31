package com.example.tryyourhair;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.tryyourhair.Singleton.Singleton;

import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class GeneratedHair extends AppCompatActivity {
    ConstraintLayout constraintLayout_loading;
    ConstraintLayout constraintLayout_generated_hair;
    Singleton singleton;
    ImageView img_generated_hair;
    Button btn_home;
    Button btn_download;
    Button btn_share;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generated_hair);

        singleton = Singleton.getInstance();
        constraintLayout_loading = findViewById(R.id.constraint_layout_loading);
        constraintLayout_generated_hair = findViewById(R.id.constraint_layout_generated_hair);
        img_generated_hair = findViewById(R.id.img_generated_hair);
        btn_download = findViewById(R.id.btn_download);
        btn_home = findViewById(R.id.btn_home);
        btn_share = findViewById(R.id.btn_share);

        if (!singleton.getReceivedGeneratedHair()) {
            constraintLayout_generated_hair.setVisibility(View.INVISIBLE);
            constraintLayout_loading.setVisibility(View.VISIBLE);
        }
        if (singleton.getReceivedGeneratedHair()) {
            singleton.setReceivedGeneratedHair(false);
            constraintLayout_loading.setVisibility(View.INVISIBLE);
            constraintLayout_generated_hair.setVisibility(View.VISIBLE);
            Log.d("URL", singleton.getGeneratedURL());
            Glide.with(this).load(singleton.getGeneratedURL()).into(img_generated_hair);
        }

        btn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent HomeIntent = new Intent(GeneratedHair.this, HomeScreen.class);
                startActivity(HomeIntent);
            }
        });

        btn_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create unique filename for the captured image
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyy_HH-mm-ss");
                String currentDateTime = dateFormat.format(new Date());

                String fileName = getApplicationContext().getFilesDir().getPath()
                        + "/TYH-Generated-YourPhoto"
                        + currentDateTime
                        + ".jpg";

                String fileNameForSave = "THY_" + Calendar.getInstance().getTime();

                // Save the bitmap to the gallery
                OutputStream fileOutputStream;

                img_generated_hair.setDrawingCacheEnabled(true);
                img_generated_hair.buildDrawingCache();
                Bitmap savingBitmap = img_generated_hair.getDrawingCache();
                try{
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        ContentResolver resolver = getContentResolver();
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileNameForSave);
                        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/png");
                        Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                        fileOutputStream = resolver.openOutputStream(Objects.requireNonNull(imageUri));
                        savingBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                        Objects.requireNonNull(fileOutputStream);
                    }
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
                img_generated_hair.setDrawingCacheEnabled(false);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(GeneratedHair.this, "Generated Image Saved", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    public void onBackPressed() {
        super.onBackPressed();
        Intent HomeScreenIntent = new Intent(this, HomeScreen.class);
        startActivity(HomeScreenIntent);
    }
}