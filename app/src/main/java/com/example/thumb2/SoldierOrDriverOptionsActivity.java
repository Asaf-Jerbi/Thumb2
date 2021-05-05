package com.example.thumb2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.TextViewCompat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class SoldierOrDriverOptionsActivity extends AppCompatActivity {

    //fields:
    ImageView soldier_ib, driver_ib, next_btn;
    TextView soldier_tv, driver_tv;

    Helper.UserType userType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soldier_or_driver_options);
        soldier_ib = findViewById(R.id.soldier_ib);
        driver_ib = findViewById(R.id.driver_ib);
        next_btn = findViewById(R.id.next_btn);
        soldier_tv = findViewById(R.id.soldier_tv);
        driver_tv = findViewById(R.id.driver_tv);


        //"next" button:
        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userType == Helper.UserType.SOLDIER) {
                    //todo: add putExtra to save it a soldier and move next to registration page
                    Intent intent = new Intent(SoldierOrDriverOptionsActivity.this, RegistrationActivity.class);
                    intent.putExtra("userType", "soldier");
                    startActivity(intent);
                    finish();
                } else if (userType == Helper.UserType.DRIVER) {
                    Intent intent = new Intent(SoldierOrDriverOptionsActivity.this, RegistrationActivity.class);
                    intent.putExtra("userType", "driver");
                    startActivity(intent);
                    finish();
                }
            }
        });

        //soldier button:
        soldier_ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userType = Helper.UserType.SOLDIER;
                //change image from black to image with colors
                soldier_ib.setBackgroundResource(R.drawable.beret);
                driver_ib.setBackgroundResource(R.drawable.hat_unclicked);
                soldier_tv.setTypeface(null, Typeface.BOLD);
                driver_tv.setTypeface(null, Typeface.NORMAL);
            }
        });

        //driver button:
        driver_ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userType = Helper.UserType.DRIVER;
                //change image from black to image with colors
                soldier_ib.setBackgroundResource(R.drawable.beret_unclicked);
                driver_ib.setBackgroundResource(R.drawable.hat);
                soldier_tv.setTypeface(null, Typeface.NORMAL);
                driver_tv.setTypeface(null, Typeface.BOLD);
            }
        });
    }

    //todo: see if any problems occur due to this function (made on 8.4.21)
    private void openActivity(Class<?> cls) {
        Intent intent = new Intent(SoldierOrDriverOptionsActivity.this, cls);
        startActivity(intent);
        finish();
    }
}