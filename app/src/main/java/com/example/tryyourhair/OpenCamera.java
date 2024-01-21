package com.example.tryyourhair;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Guideline;

import android.Manifest;
import android.animation.Animator;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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

import org.opencv.android.CameraActivity;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class OpenCamera extends CameraActivity {
    Singleton singleton;
    CameraBridgeViewBase cameraBridgeViewBase;
    ImageView btn_taking_picture;
    ImageView btn_cancel_take_image;
    View face_roi;
    int take_image = 0;
    FaceDetector faceDetector;
    private static final int SCALING_FACTOR = 10;
    ImageView img_processing_face;
    boolean isTakeImage = false;
    LottieAnimationView animationViewCountDown;
    Guideline roi_vertical1;
    Guideline roi_vertical2;
    Guideline roi_horizontal1;
    Guideline roi_horizontal2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_camera);

        getPermission();

//        face_roi = findViewById(R.id.face_roi);
        img_processing_face = findViewById(R.id.img_processing_face);

        roi_vertical1 = findViewById(R.id.roi_guideline1);
        roi_vertical2 = findViewById(R.id.roi_guideline2);
        roi_horizontal1 = findViewById(R.id.roi_horizontal_guideline1);
        roi_horizontal2 = findViewById(R.id.roi_horizontal_guideline2);


        // Set camera fullscreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        animationViewCountDown = findViewById(R.id.animation_countdown);
        singleton = Singleton.getInstance();
        cameraBridgeViewBase = findViewById(R.id.camera_view);
        btn_taking_picture = findViewById(R.id.btn_take_picture);
        btn_cancel_take_image = findViewById(R.id.btn_cancel);
        cameraBridgeViewBase.setCameraIndex(1); // Use front camera



        // Init FaceDetector Object
        FaceDetectorOptions realTimeFdo = new FaceDetectorOptions.Builder()
                .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
                .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                .build();

        faceDetector = FaceDetection.getClient(realTimeFdo);

        cameraBridgeViewBase.setCvCameraViewListener(new CameraBridgeViewBase.CvCameraViewListener2() {
            @Override
            public void onCameraViewStarted(int width, int height) {

            }

            @Override
            public void onCameraViewStopped() {

            }

            @Override
            public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
                take_image = TakePicture(take_image, inputFrame.rgba());

                // Create a thread for processing image real-time
                Thread FaceDetectionRealTime = new Thread(new Runnable() {
                    @Override
                    public void run() {

//                        // get location of the face_roi
//                        int top_roi = face_roi.getTop();
//                        int bottom_roi = face_roi.getBottom();
//                        int right_roi = face_roi.getRight();
//                        int left_roi = face_roi.getLeft();

                        // Create a new mat
                        Mat processingMat = new Mat();

                        // Rotate the original mat 90 degrees counter-clockwise
                        Core.rotate(inputFrame.rgba(), processingMat, 2);

                        // Covert the rotated mat into Bitmap
                        Bitmap bitmap_processing = Bitmap.createBitmap(processingMat.cols(),
                                processingMat.rows(),
                                Bitmap.Config.ARGB_8888);
                        Utils.matToBitmap(processingMat, bitmap_processing);

                        // Create a bitmap to store processed image
                        Bitmap processed_bitmap = bitmap_processing.copy(
                                Bitmap.Config.ARGB_8888,
                                true);

                        // Create a smaller bitmap for faster processing
                        Bitmap smaller_processingBitmap = Bitmap.createScaledBitmap(
                                bitmap_processing,
                                bitmap_processing.getWidth() / SCALING_FACTOR,
                                bitmap_processing.getHeight() / SCALING_FACTOR,
                                false);

                        // Get size of cameraBridgeViewBase
                        int cameraH = cameraBridgeViewBase.getHeight();
                        int cameraW = cameraBridgeViewBase.getWidth();
                        int camera_size = cameraH * cameraW;

                        // Get size of processing_face_view
                        int processing_face_H = img_processing_face.getHeight();
                        int processing_face_W = img_processing_face.getWidth();
                        int processing_face_size = processing_face_H * processing_face_W;
//
                        // Calculate scale factor
                        int x_scale_factor = cameraW / processing_face_W;
                        int y_scale_factor = cameraW / processing_face_H;
                        int size_scale_factor = camera_size / processing_face_size;



                        // Create an InputImage object for face detection process
                        InputImage inputImage = InputImage.fromBitmap(smaller_processingBitmap, 0);

                        // Start the detection process
                        faceDetector.process(inputImage)
                                .addOnSuccessListener(new OnSuccessListener<List<Face>>() {
                                    @Override
                                    public void onSuccess(List<Face> faces) {
//                                        int top = top_roi / y_scale_factor;
//                                        int bottom = bottom_roi / y_scale_factor;
//                                        int left = left_roi / x_scale_factor;
//                                        int right = right_roi / x_scale_factor;

//                                        int top = processing_face_H - 650;
//                                        int bottom = processing_face_H - 10;
//                                        int left = processing_face_W - 400;
//                                        int right = processing_face_W + 100;

                                        int left = (int) ((int) roi_vertical1.getX() / x_scale_factor);
                                        int top = (int) (roi_horizontal1.getY() / size_scale_factor) + 150;
                                        int bottom = (int) ((int) roi_horizontal2.getY() / y_scale_factor) - 600;
                                        int right = (int) ((int) roi_vertical2.getX() / x_scale_factor) + 200;

                                        Log.d("ROI L", String.valueOf(roi_vertical1.getX()));
                                        Log.d("ROI R", String.valueOf(roi_vertical2.getX()));
//

                                        Rect roi_rect = new Rect(left, top, right, bottom);
                                        Rect rect = null;
                                        if (faces.size() == 0) {
//                                            runOnUiThread(new Runnable() {
//                                                @Override
//                                                public void run() {
//                                                    Toast.makeText(OpenCamera.this, "Oops! No face detected", Toast.LENGTH_SHORT).show();
//                                                }
//                                            });
                                        }
                                        else if (faces.size() > 1) {
//                                            runOnUiThread(new Runnable() {
//                                                @Override
//                                                public void run() {
//                                                    Toast.makeText(OpenCamera.this, "More than one face detected", Toast.LENGTH_SHORT).show();
//                                                }
//                                            });
                                        }
                                        else {
                                            for(Face face : faces) {
                                                rect = face.getBoundingBox();
                                                rect.set(rect.left * SCALING_FACTOR,
                                                        rect.top * (SCALING_FACTOR - 4),
                                                        rect.right * SCALING_FACTOR,
                                                        (rect.bottom * SCALING_FACTOR) + 50);
                                            }

                                            // Draw bounding box for detected face
                                            Mat mat_processing_face = new Mat();
                                            Utils.bitmapToMat(bitmap_processing, mat_processing_face);
                                            Imgproc.rectangle(
                                                    mat_processing_face,
                                                    new Point((float) rect.left, (float) rect.bottom),
                                                    new Point((float) rect.right, (float) rect.top),
                                                    new Scalar(255, 0, 0),
                                                    5);
//
                                            // Define boundaries of a roi
                                            Imgproc.rectangle(
                                                    mat_processing_face,
                                                    new Point((float) roi_rect.left, (float) roi_rect.bottom),
                                                    new Point((float) roi_rect.right, (float) roi_rect.top),
                                                    new Scalar(255, 0, 0),
                                                    5);

                                            String msg;
                                            if ((rect.top < roi_rect.top)
                                                    || (rect.bottom > roi_rect.bottom)
                                                    || (rect.right > roi_rect.right)
                                                    || (rect.left < roi_rect.left)) {
                                                isTakeImage = false;
                                                msg = "Please put your face into the frame";
                                                animationViewCountDown.cancelAnimation();
                                                animationViewCountDown.setProgress(0.0f);
//                                                runOnUiThread(new Runnable() {
//                                                    @Override
//                                                    public void run() {
//                                                        Toast.makeText(OpenCamera.this, msg, Toast.LENGTH_SHORT).show();
//                                                    }
//                                                });
                                            }
                                            if ((rect.top >= roi_rect.top) && (rect.bottom <= roi_rect.bottom) && (rect.left >= roi_rect.left) && (rect.right <= roi_rect.right) && (!isTakeImage)) {
                                                isTakeImage = true;
                                                animationViewCountDown.playAnimation();
                                                animationViewCountDown.addAnimatorListener(new Animator.AnimatorListener() {
                                                    @Override
                                                    public void onAnimationStart(@NonNull Animator animation) {

                                                    }

                                                    @Override
                                                    public void onAnimationEnd(@NonNull Animator animation) {
                                                        if (isTakeImage) {
                                                            btn_taking_picture.performClick();
                                                            isTakeImage = false;
                                                        }
                                                        animationViewCountDown.pauseAnimation();
                                                    }

                                                    @Override
                                                    public void onAnimationCancel(@NonNull Animator animation) {

                                                    }

                                                    @Override
                                                    public void onAnimationRepeat(@NonNull Animator animation) {

                                                    }
                                                });

                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Toast.makeText(OpenCamera.this, "Keep your face still", Toast.LENGTH_SHORT).show();
                                                    }
                                                });

                                            }
//                                            Utils.matToBitmap(mat_processing_face, processed_bitmap);
//                                            runOnUiThread(new Runnable() {
//                                                @Override
//                                                public void run() {
//                                                    try{
//                                                        img_processing_face.setImageBitmap(processed_bitmap);
//                                                    } catch (Exception e) {
//                                                        e.printStackTrace();
//                                                    }
//                                                }
//                                            });
                                        }
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(OpenCamera.this, "Detection failed due to " + e.getMessage(), Toast.LENGTH_SHORT).show();

                                    }
                                });
                    }
                });
                FaceDetectionRealTime.start();

                return inputFrame.rgba();
            }
        });

        if(OpenCVLoader.initDebug()) {
//            Log.d("OPENCV", "success");
            cameraBridgeViewBase.enableView();
        }

        btn_taking_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (take_image == 0) {
                    take_image = 1;
                }
                else {
                    take_image = 0;
                }
            }
        });

        btn_cancel_take_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CancelTakeImage();
            }
        });
    }

    private int TakePicture(int take_image, Mat rgba) {
       if (take_image == 1) {

           // add permission for writing in local storage first
           // create new mat that you want to save
           Mat matForSave = new Mat();

           // rotate image by 90 degrees
           Core.flip(rgba.t(), matForSave, 1);

           // Convert image from RGBA to BGRA
           Imgproc.cvtColor(matForSave, matForSave, Imgproc.COLOR_RGBA2BGRA);

           // Create a folder TYH-YourPhoto
           // and save image into that folder
           File folder = new File(getApplicationContext().getFilesDir().getPath() + "/TYH");

           // Check if the folder is exist
           boolean isExist = true;
           if(folder.exists() == false) {
               isExist = folder.mkdirs();
           }

           // Create unique filename for the captured image
           SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyy_HH-mm-ss");
           String currentDateTime = dateFormat.format(new Date());

           // String fileName =  currentDateTime + ".jpg";
           String fileName = getApplicationContext().getFilesDir().getPath()
                   + "/TYH-YourPhoto"
                   + currentDateTime
                   + ".jpg";

//           String fileNameForSave = "/TYH-YourPhoto" + currentDateTime + ".jpg";
//           String fileNameForSave = "/TYH-YourPhoto/" + "THY-" + Calendar.getInstance().getTime();
           String fileNameForSave = "TYH_" + Calendar.getInstance().getTime();
           UUID uuid = UUID.randomUUID();
           String uniqueFileName = uuid.toString();
           singleton.setConfirmedFaceName(uniqueFileName);
           Log.d("FILE", fileNameForSave);

           // Write the mat to the storage
           Imgcodecs.imwrite(fileName, matForSave);
           take_image = 0;

           // Convert the color space from BGR to RGB
           Imgproc.cvtColor(matForSave, matForSave, Imgproc.COLOR_BGR2RGB);

           // Convert the Mat to Bimap to store into the gallery of the phone
           Bitmap bitmapForSave = Bitmap.createBitmap(matForSave.cols(), matForSave.rows(), Bitmap.Config.ARGB_8888);
           Utils.matToBitmap(matForSave, bitmapForSave);

           // Flip bitmap
           Matrix matrix = new Matrix();
           matrix.postRotate(180);

           Bitmap rotatedBimap = Bitmap.createBitmap(bitmapForSave,
                   0,
                   0,
                   bitmapForSave.getWidth(),
                   bitmapForSave.getHeight(),
                   matrix,
                   true);



//           // Save the bitmap to the gallery
//           OutputStream fileOutputStream;
//           try{
//               if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                   ContentResolver resolver = getContentResolver();
//                   ContentValues contentValues = new ContentValues();
//                   contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileNameForSave);
//                   contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/png");
//                   Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
//                   fileOutputStream = resolver.openOutputStream(Objects.requireNonNull(imageUri));
//                   rotatedBimap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
//                   Objects.requireNonNull(fileOutputStream);
//               }
//           } catch (FileNotFoundException e) {
//               throw new RuntimeException(e);
//           }

           // Convert bitmap to array
           ByteArrayOutputStream stream = new ByteArrayOutputStream();
           rotatedBimap.compress(Bitmap.CompressFormat.JPEG, 95, stream);
           byte[] byteArray = stream.toByteArray();

//            Send the converted bitmap to another activity
//           cameraBridgeViewBase.disableView();
           try {
               Intent FaceReusltintent = new Intent(this, FaceResult.class);
               FaceReusltintent.putExtra("userface", byteArray);
               startActivity(FaceReusltintent);

           } catch (Exception e) {
               Log.d("ERROR IMAGE", e.toString());
           }


       }

        return take_image;
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraBridgeViewBase.enableView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraBridgeViewBase.disableView();
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraBridgeViewBase.enableView();
    }



    @Override
    protected List<? extends CameraBridgeViewBase> getCameraViewList() {
        return Collections.singletonList(cameraBridgeViewBase);
    }

    void getPermission() {
        if(checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 101);
        }

        if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            getPermission();
        }
    }

    public void CancelTakeImage() {
        Intent HomeScreenIntent = new Intent(this, HomeScreen.class);
        startActivity(HomeScreenIntent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        CancelTakeImage();
    }

}