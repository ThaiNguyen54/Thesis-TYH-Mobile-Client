package com.example.tryyourhair;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cloudinary.android.MediaManager;
import com.example.tryyourhair.MQTT.MQTTHandler;
import com.example.tryyourhair.Models.GenerationData;
import com.example.tryyourhair.RetrofitInstance.RetrofitClient;
import com.example.tryyourhair.RetrofitInterface.Methods;
import com.example.tryyourhair.Singleton.Singleton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeScreen extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    ImageView chose_hairstyle_img;
    ImageView confirmed_face_img;
    Singleton singleton;
    Button btn_generate;
    final String SERVER_IP = "192.168.1.5";
    final String BROKER_URL = "tcp://192.168.1.3:1883";
    final String CLIENT_ID = "android";
    final int SERVER_PORT = 9000;
    final int CLIENT_PORT = 9999;
    final int BUFFER_SIZE = 65536;
    Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        chose_hairstyle_img = findViewById(R.id.img_chose_hairstyle);
        confirmed_face_img = findViewById(R.id.img_confirmed_face);
//        bottomNavigationView = findViewById(R.id.bottom_navigation);
//        bottomNavigationView.setSelectedItemId(R.id.item_home);
        btn_generate = findViewById(R.id.btn_generate);
        singleton = Singleton.getInstance();

        String TAG = "TOKEN";
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();
                        singleton.setRegistrationToken(token);
                    }
                });


        confirmed_face_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenCameraActivity();
            }
        });

        chose_hairstyle_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenListHairActivity();
            }
        });

        btn_generate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!singleton.getConfirmedFace() && !singleton.getChoseHair()) {
                    Toast.makeText(
                            HomeScreen.this,
                            "Please provide a photo of you and choose a hairstyle",
                            Toast.LENGTH_SHORT).show();
                }
                else if (!singleton.getChoseHair()) {
                    Toast.makeText(
                            HomeScreen.this,
                            "Please choose a hairstyle",
                            Toast.LENGTH_SHORT).show();
                }
                else if (!singleton.getConfirmedFace()) {
                    Toast.makeText(
                            HomeScreen.this,
                            "Please provide a photo of you",
                            Toast.LENGTH_SHORT).show();
                }
                else if (singleton.getConfirmedFace() && singleton.getChoseHair()) {
                    Thread SendGenerationData = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
//                                // Initiate a socket instance
//                                Socket socket = new Socket(SERVER_IP, SERVER_PORT);
//
//
//                                OutputStream outputStream = socket.getOutputStream();


                                // Encode image for sending to server
                                byte[] Confirmed_Face_ByteArray = singleton.getConfirmedFaceImage();
                                Bitmap Confirmed_Face_Bitmap = BitmapFactory.decodeByteArray(Confirmed_Face_ByteArray,0, Confirmed_Face_ByteArray.length);

                                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                Confirmed_Face_Bitmap.compress(Bitmap.CompressFormat.JPEG, 100 ,byteArrayOutputStream);

                                byte[] Compressed_Image = byteArrayOutputStream.toByteArray();
                                String base64Image = Base64.encodeToString(Compressed_Image, Base64.DEFAULT);
                                String processedBase64ImageString = base64Image.replaceAll("\n", "");
                                processedBase64ImageString = processedBase64ImageString.replaceAll("\r", "");

                                // Convert the generation data to JSON string
                                GenerationData generationData = new GenerationData(
                                        processedBase64ImageString,
                                        singleton.getConfirmedFaceName(),
                                        singleton.getChoseHairstyleName(),
                                        singleton.getRegistrationToken());

                                String GenerationJSON = gson.toJson(generationData);

                                Methods methods = RetrofitClient.getRetrofitInstance().create(Methods.class);
                                Call<GenerationData> call = methods.postGenerationData(generationData);
                                call.enqueue(new Callback<GenerationData>() {
                                    @Override
                                    public void onResponse(Call<GenerationData> call, Response<GenerationData> response) {
                                        GenerationData responseFromAPI = response.body();
                                        Toast.makeText(HomeScreen.this,"generated hair: " + response.code(), Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onFailure(Call<GenerationData> call, Throwable throwable) {

                                    }
                                });
//                                // Establish a connection to the server
//                                outputStream.write(GenerationJSON.getBytes());
//                                outputStream.flush();
//                                outputStream.close();
//                                socket.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    });
                    SendGenerationData.start();
                    OpenGeneratedHair();
                }
            }
        });
//        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                switch (item.getItemId()) {
//                    case R.id.item_camera:
//                        OpenCameraActivity();
//                        break;
//
//                    case R.id.item_hairstyle:
//                        OpenListHairActivity();
//                        break;
//                }
//                return true;
//            }
//        });

        if(singleton.getChoseHair() == false) {
            chose_hairstyle_img.setImageResource(R.drawable.img_ex2);
        }
        else if (singleton.getChoseHair() == true) {
            Glide.with(this).load(singleton.getChoseHairURL()).into(chose_hairstyle_img);
        }

        if(singleton.getConfirmedFace() == false) {
            confirmed_face_img.setImageResource(R.drawable.img_ex1);
        }
        else if (singleton.getConfirmedFace() == true) {
            byte[] Confirmed_Face_ByteArray = singleton.getConfirmedFaceImage();
            Bitmap Confirmed_Face_Bitmap = BitmapFactory.decodeByteArray(Confirmed_Face_ByteArray,0, Confirmed_Face_ByteArray.length);
            confirmed_face_img.setImageBitmap(Confirmed_Face_Bitmap);
        }
    }

    public void OpenCameraActivity() {
        Intent CameraIntent = new Intent(this, OpenCamera.class);
        startActivity(CameraIntent);
    }

    public void OpenGallery(){}

    public void OpenHomeActivity(){
        Intent HomeIntent = new Intent(this, HomeScreen.class);
        startActivity(HomeIntent);
    }

    public void OpenListHairActivity() {
        Intent ListHairIntent = new Intent(this, HairStyleRecyclerViewActivity.class);
        startActivity(ListHairIntent);
    }

    public void OpenGeneratedHair() {
        Intent GeneratedHairIntent = new Intent(this, GeneratedHair.class);
        startActivity(GeneratedHairIntent);
    }
}