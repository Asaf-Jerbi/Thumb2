package com.example.thumb2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.zxing.Result;

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
            intent.putExtra("showBarcode", "yes");
            startActivity(intent);
        });

        goUpForRide_btn.setOnClickListener(v -> {
            Intent intent = new Intent(MainScreenActivity.this, QrScannerActivity.class);
            startActivity(intent);
        });
    }
}