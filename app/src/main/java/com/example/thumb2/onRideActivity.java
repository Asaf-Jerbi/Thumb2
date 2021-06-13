package com.example.thumb2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class onRideActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap map;
    private TextView driverDetails_tv;
    private UserInformation driverDetails;
    private Button share_btn, endRide_btn, sos_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_ride);

        // elements:
        driverDetails_tv = findViewById(R.id.driver_details_tv);

        // retrieve data:
        driverDetails = (UserInformation) getIntent().getSerializableExtra("driverDetails");
        String newLine = "\n";
        driverDetails_tv.setText(newLine + "שם הנהג: "
                + driverDetails.getFirstName() + " " + driverDetails.getLastName() + newLine +
                "מספר הרכב: " + driverDetails.getCarNumber() + newLine);

        // add google maps support:
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

//        // define buttons:
//        share_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent sendIntent = new Intent();
//                sendIntent.setAction(Intent.ACTION_SEND);
//                sendIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");
//                sendIntent.setType("text/plain");
//                startActivity(sendIntent);
//            }
//        });

        endRide_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        sos_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
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