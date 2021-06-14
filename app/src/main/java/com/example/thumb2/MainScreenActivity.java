package com.example.thumb2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

public class MainScreenActivity extends AppCompatActivity implements OnMapReadyCallback {

    private Button getSoldierUpForRide_btn, goUpForRide_btn;
    private GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        // request all permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getApplicationContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        1);
            }
        }

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