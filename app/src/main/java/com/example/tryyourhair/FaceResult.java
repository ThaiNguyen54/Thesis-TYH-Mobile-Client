package com.example.tryyourhair;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.tryyourhair.Singleton.Singleton;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.nio.ByteBuffer;
import java.util.List;

import com.example.tryyourhair.ErrorDialog.NoFaceDialog;



public class FaceResult extends AppCompatActivity {

    ImageView FaceResultView;

    FaceDetector faceDetector;

    LottieAnimationView ScanEffectView;

    Button BtnRetake;
    Button BtnConfirm;
    Singleton singleton;


    //This factor is used to make the detecting image smaller, to make the process faster
    private static final int SCALING_FACTOR = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_result);

        OpenCVLoader.initDebug();
        singleton = Singleton.getInstance();

        FaceResultView = findViewById(R.id.face_img_view);
        ScanEffectView = findViewById(R.id.scan_effect);
        BtnRetake = findViewById(R.id.btn_retake);
        BtnConfirm = findViewById(R.id.btn_confirm);

        BtnRetake.setVisibility(View.INVISIBLE);
        BtnConfirm.setVisibility(View.INVISIBLE);


        // Receive bitmap from another activity
        byte[] byteArray = getIntent().getByteArrayExtra("userface");
        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

        FaceResultView.setImageBitmap(bitmap);

        // Display the bitmap
        FaceResultView.setImageBitmap(bitmap);

        Thread DetectFaceThread = new Thread(new Runnable() {
            @Override
            public void run() {

                // Init FaceDetector Object
                FaceDetectorOptions realTimeFdo = new FaceDetectorOptions.Builder()
                        .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
                        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                        .build();

                faceDetector = FaceDetection.getClient(realTimeFdo);

                // Create a smaller bitmap to process faster
                Bitmap smallerBitmap = Bitmap.createScaledBitmap(
                        bitmap,
                        bitmap.getWidth() / SCALING_FACTOR,
                        bitmap.getHeight() / SCALING_FACTOR,
                        false);


                // Create an input image for detecting face process from the smaller bitmap
                InputImage inputImage = InputImage.fromBitmap(smallerBitmap, 0);

                // Start the detection process
                faceDetector.process(inputImage)
                        .addOnSuccessListener(new OnSuccessListener<List<Face>>() {
                            @Override
                            public void onSuccess(List<Face> faces) {
                                // After detecting successfully, we need to disable the animation view
                                ScanEffectView.setVisibility(View.GONE);
                                BtnConfirm.setVisibility(View.VISIBLE);
                                BtnRetake.setVisibility(View.VISIBLE);
                                Rect rect = null;
                                float eulerX = 0;
                                float eulerY = 0;
                                float eulerZ = 0;
                                if (faces.size() == 0 ) {
//                                    Toast.makeText(FaceResult.this, "Oops! No face detected", Toast.LENGTH_LONG).show();
                                    OpenDialog();
                                }
                                else if (faces.size() > 1) {
                                    Toast.makeText(FaceResult.this, "More than one face detected", Toast.LENGTH_LONG).show();
                                }
                                else {
                                    // There can be multiple faces detected from an image,
                                    // manage them using loop from List<Face> faces
                                    for(Face face: faces) {
                                        // Get detected faces as rectangle
                                        rect = face.getBoundingBox();
                                        rect.set(rect.left * SCALING_FACTOR,
                                                rect.top * (SCALING_FACTOR - 4),
                                                rect.right * SCALING_FACTOR,
                                                (rect.bottom * SCALING_FACTOR) + 50);
                                        eulerX = face.getHeadEulerAngleX();
                                        eulerY = face.getHeadEulerAngleY();
                                        eulerZ = face.getHeadEulerAngleZ();
                                    }


                                    // Draw a bounding box for detected face
                                    Mat ProcessingMat = new Mat(); // Create a new mat
                                    Utils.bitmapToMat(bitmap, ProcessingMat); // Convert the Bitmap image to Mat image
                                    Imgproc.rectangle(
                                            ProcessingMat,
                                            new Point((float) rect.left, (float) rect.bottom),
                                            new Point((float) rect.right, (float) rect.top),
                                            new Scalar(255, 0, 0),
                                            5
                                    );

                                    Bitmap ProcessedBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true); // Copy the original Bitmap to a mutable bitmap
                                    Utils.matToBitmap(ProcessingMat, ProcessedBitmap); // Convert the Mat image to Bitmap image to display on image view

                                    // Display the processed Bitmap
                                    float finalEulerX = eulerX;
                                    float finalEulerY = eulerY;
                                    float finalEulerZ = eulerZ;
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            FaceResultView.setImageBitmap(ProcessedBitmap);
//                                            Toast.makeText(getApplicationContext(),"euler X: " + finalEulerX, Toast.LENGTH_SHORT).show();
//                                            Toast.makeText(getApplicationContext(),"euler Y: " + finalEulerY, Toast.LENGTH_SHORT).show();
//                                            Toast.makeText(getApplicationContext(),"euler Z: " + finalEulerZ, Toast.LENGTH_SHORT).show();

                                        }
                                    });
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Detection failed
                                Toast.makeText(FaceResult.this, "Detection failed due to " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });


        Thread ConfirmAndRetakeThread = new Thread(new Runnable() {
            @Override
            public void run() {
                BtnConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent HomeIntent = new Intent(FaceResultView.getContext(), HomeScreen.class);
                        singleton.setConfirmedFaceImage(byteArray);
                        singleton.setConfirmedFace(true);
                        startActivity(HomeIntent);}
                });

                BtnRetake.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        OpenCameraScreen();
                    }
                });
            }
        });

        new CountDownTimer(3000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Toast.makeText(FaceResult.this, "We are detecting your face", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish() {
                DetectFaceThread.start();
                ConfirmAndRetakeThread.start();
            }
        }.start();
    }

    public void OpenHomeScreen() {
        Intent HomeIntent = new Intent(this, HomeScreen.class);
        startActivity(HomeIntent);
    }

    public void OpenCameraScreen() {
        Intent OpenCameraIntent = new Intent(this, OpenCamera.class);
        startActivity(OpenCameraIntent);
    }

    public void OpenDialog() {
        NoFaceDialog noFaceDialog = new NoFaceDialog();
        noFaceDialog.show(getSupportFragmentManager(), "no face");
    }
}