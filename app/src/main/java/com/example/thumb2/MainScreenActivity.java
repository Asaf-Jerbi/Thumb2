package com.example.thumb2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainScreenActivity extends AppCompatActivity {

    private Button getSoldierUpForRide_btn, goUpForRide_btn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        getSoldierUpForRide_btn = findViewById(R.id.getSoldierUpForRide_btn);
        goUpForRide_btn = findViewById(R.id.goUpForRide_btn);

        getSoldierUpForRide_btn.setOnClickListener(v -> {
            Intent intent = new Intent(MainScreenActivity.this, DriverDetailsActivity.class);
            startActivity(intent);
        });

        goUpForRide_btn.setOnClickListener(v -> {
//            Intent intent = new Intent(MainScreenActivity.this, BarcodeScannerActivity.class);
//            startActivity(intent);
        });
    }
}