package com.example.thumb2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class ExceptionDebug extends AppCompatActivity {

    private TextView exceptionText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exception_debug);
        exceptionText = findViewById(R.id.exception_tv);
        exceptionText.setText(getIntent().getStringExtra("Exception"));
    }
}