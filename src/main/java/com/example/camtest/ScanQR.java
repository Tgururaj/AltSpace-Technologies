package com.example.camtest;

import android.app.Activity;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Size;
import android.widget.Toast;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;
public class ScanQR extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CAMERA = 0;

    private PreviewView previewView;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        previewView = findViewById(R.id.activity_main_previewView);

        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        requestCamera();
    }

    private void requestCamera() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                ActivityCompat.requestPermissions(ScanQR.this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CAMERA) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            } else {
                Toast.makeText(this, "Camera Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startCamera() {
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindCameraPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                Toast.makeText(this, "Error starting camera " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void bindCameraPreview(@NonNull ProcessCameraProvider cameraProvider) {
        previewView.setPreferredImplementationMode(PreviewView.ImplementationMode.SURFACE_VIEW);

        Preview preview = new Preview.Builder()
                .build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        preview.setSurfaceProvider(previewView.createSurfaceProvider());

        ImageAnalysis imageAnalysis =
                new ImageAnalysis.Builder()
                        .setTargetResolution(new Size(1280, 720))
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

        final Activity thisActivity = this;
        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(this), new QRCodeImageAnalyser(new QRCodeFoundListener() {
            @Override
            public void onQRCodeFound(String qrCode) {

                String prodID = "";
                String prodName = "";
                try{
                    String[] entries = qrCode.split(";");
                    if (entries.length == 2)
                    {
                        // read product ID
                        String[] keyVal = entries[0].split("=");
                        if (keyVal.length != 2) {
                            throw new Exception("Invalid QR code!");
                        }
                        String key = keyVal[0];
                        String val = keyVal[1];
                        if (key.equals("productID"))
                        {
                            prodID = val;
                        }
                        else
                        {
                            throw new Exception("Invalid QR code!");
                        }

                        // read product Name
                        keyVal = entries[1].split("=");
                        if (keyVal.length != 2) {
                            throw new Exception("Invalid QR code!");
                        }

                        key = keyVal[0];
                        val = keyVal[1];
                        if (key.equals("productName"))
                        {
                            prodName = val;
                        }
                        else
                        {
                            throw new Exception("Invalid QR code!");
                        }
                    }
                    Intent productDetails = new Intent(ScanQR.this, UpdateStock.class);
                    productDetails.putExtra("productID", prodID);
                    productDetails.putExtra("productName", prodName);
                    startActivity(productDetails);
                }
                catch (Exception e){
                    // show the error message in toast
                    Toast.makeText(thisActivity, "Invalid QR code!", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void qrCodeNotFound() {
            }
        }));

        Camera camera = cameraProvider.bindToLifecycle((LifecycleOwner)this, cameraSelector, imageAnalysis, preview);
    }

    }

