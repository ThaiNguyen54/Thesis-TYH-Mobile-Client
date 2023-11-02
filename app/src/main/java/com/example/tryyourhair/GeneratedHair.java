package com.example.tryyourhair;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Dialog;
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
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

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
    Button btn_share;
    Dialog qrDialog;
    ImageView generated_hair_qr_code;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generated_hair);
        qrDialog = new Dialog(GeneratedHair.this);

        singleton = Singleton.getInstance();
        constraintLayout_loading = findViewById(R.id.constraint_layout_loading);
        constraintLayout_generated_hair = findViewById(R.id.constraint_layout_generated_hair);
        img_generated_hair = findViewById(R.id.img_generated_hair);
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

        btn_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    qrDialog.setContentView(R.layout.dialog_qrcode);
                generated_hair_qr_code = (ImageView) qrDialog.findViewById(R.id.qrcode_generated_hair);
                // Generate qr code
                String text = singleton.getGeneratedURL().trim();
                MultiFormatWriter writer = new MultiFormatWriter();
                try {
                    BitMatrix matrix = writer.encode(text, BarcodeFormat.QR_CODE, 300, 300);
                    BarcodeEncoder encoder = new BarcodeEncoder();
                    Bitmap bitmap = encoder.createBitmap(matrix);
                    generated_hair_qr_code.setImageBitmap(bitmap);
                } catch (WriterException e) {
                    e.printStackTrace();
                }
                    qrDialog.show();

                } catch (Exception e) {
                    Log.d("ERROR", e.toString());
                }


            }
        });
    }

    public void onBackPressed() {
        super.onBackPressed();
        Intent HomeScreenIntent = new Intent(this, HomeScreen.class);
        startActivity(HomeScreenIntent);
    }
}