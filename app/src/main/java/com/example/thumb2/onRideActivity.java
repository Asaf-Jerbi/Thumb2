package com.example.thumb2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Timer;
import java.util.TimerTask;

public class onRideActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap googleMap;
    private TextView driverDetails_tv;
    private UserInformation driverDetails;
    private Button shareButton, endRideButton, sosButton;
    private FusedLocationProviderClient fusedLocationClient;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_ride);

        //initializations:
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // elements:
        driverDetails_tv = findViewById(R.id.on_ride_driver_details_tv);
        shareButton = (Button) findViewById(R.id.share_btn);
        endRideButton = (Button) findViewById(R.id.end_ride_btn);
        sosButton = (Button) findViewById(R.id.on_ride_sos_btn);

        // retrieve data:
        driverDetails = (UserInformation) getIntent().getSerializableExtra("driverDetails");

        //get current time
        String currentTime = new Helper().getCurrentTime();

        String newLine = "\n";

        // case driver's details wasn't recognized.
        if (driverDetails == null) {
            driverDetails_tv.setText("פרטי הנהג:" + newLine + "אירעה שגיאה בקבלת פרטי הנהג");
        }


        driverDetails_tv.setText(newLine + "שם הנהג: "
                + driverDetails.getFullName() + newLine +
                "מספר הרכב: " + driverDetails.getCarNumber() + newLine + "שעת תחילת נסיעה: "
                + currentTime + newLine);

        // add google maps support:
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.on_ride_map);
        mapFragment.getMapAsync(this);

        // define buttons:
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo: this commented code is for sharing the
                // location of the soldier via share button
                /*Double latitude = googleMap.loca.getLatitude();
                Double longitude = user_loc.getLongitude();
                String uri = "http://maps.google.com/maps?saddr=" + latitude + "," + longitude;

                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String ShareSub = "Here is my location";
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, ShareSub);
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, uri);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));*/


                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                String messageToShare = "היי!" + newLine + "הצטרפתי באמצעות אפליקציית Thumb לנסיעה עם " + driverDetails.getFullName()
                        + newLine + "שעת תחילת נסיעה: " + currentTime + newLine;

                sendIntent.putExtra(Intent.EXTRA_TEXT, messageToShare);
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            }
        });

//        endRideButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
//
//        sosButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        //todo: change the start position to be current location by phone's gps
        this.googleMap = googleMap;

        // define task that would be scheduled with timer
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                showCurrentLocationOnMap(googleMap);
            }
        };

        // update map marker according to real location every 5000 milliseconds.
        Timer timer = new Timer();
        timer.schedule(task, 0, 5000);


        //todo: remove me:
        TimerTask task2 = new TimerTask() {
            @Override
            public void run() {
                LatLng barIlan = new LatLng(32.069090, 34.843549);
                googleMap.addMarker(new MarkerOptions().position(barIlan).title("Bar-Ilan University"));
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(barIlan, 12.0f));
            }
        };

        // update map marker according to real location every 5000 milliseconds.
        Timer timer2 = new Timer();
        timer2.schedule(task2, 4000, 12000);


    }


    private void showCurrentLocationOnMap(GoogleMap googleMap) {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat
                .checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            /* TODO: Consider calling
                ActivityCompat#requestPermissions
             here to request the missing permissions, and then overriding
               public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                                      int[] grantResults)
             to handle the case where the user grants the permission. See the documentation
             for ActivityCompat#requestPermissions for more details.*/
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            LatLng current = new LatLng(location.getLatitude(), location.getLongitude());
                            googleMap.addMarker(new MarkerOptions()
                                    .position(current).title("אני כאן"));
                            googleMap.animateCamera(CameraUpdateFactory
                                    .newLatLngZoom(current, 12.0f));
                        }
                    }
                });
    }


    //TODO: consider use
/*    private void setUpMapIfNeeded(GoogleMap googleMap) {
        // Do a null check to confirm that we have not already instantiated the map.
        if (googleMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            googleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
            googleMap.setMyLocationEnabled(true);
            // Check if we were successful in obtaining the map.
            if (googleMap != null) {
                googleMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                    @Override
                    public void onMyLocationChange(Location arg0) {
                        googleMap.addMarker(new MarkerOptions().position(new LatLng(arg0.getLatitude(), arg0.getLongitude())).title("It's Me!"));
                    }
                });
            }
        }
    }*/
}