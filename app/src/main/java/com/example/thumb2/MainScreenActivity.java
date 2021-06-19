package com.example.thumb2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.Result;

import java.util.ArrayList;
import java.util.List;

public class MainScreenActivity extends AppCompatActivity implements OnMapReadyCallback {

    private Button getSoldierUpForRide_btn, goUpForRide_btn;
    private GoogleMap map;
    private final int MY_SMS_SENDING_PERMISSION_CODE = 0;
    private final int MY_LOCATION_PERMISSION_CODE = 1;
    private final int MY_CAMERA_PERMISSION_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        getSoldierUpForRide_btn = findViewById(R.id.getSoldierUpForRide_btn);
        goUpForRide_btn = findViewById(R.id.goUpForRide_btn);

        getSoldierUpForRide_btn.setOnClickListener(v -> {
            Intent intent = new Intent(MainScreenActivity.this, DriverDetailsActivity.class);
            intent.putExtra("showBarcode", "yes");
            startActivity(intent);
        });

        goUpForRide_btn.setOnClickListener(v -> {
            Intent intent = new Intent(MainScreenActivity.this, QrScannerActivity.class);
            startActivity(intent);
        });
    }




    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        //todo: change the start position to be current location by phone's gps
        map = googleMap;
        LatLng barIlan = new LatLng(32.069090, 34.843549);
        map.addMarker(new MarkerOptions().position(barIlan).title("Bar-Ilan University"));
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(barIlan, 12.0f));
    }
}