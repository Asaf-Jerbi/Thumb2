package com.example.thumb2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {


    //fields
    private TextView signUpTv;
    private EditText emailEt, passwordEt;
    private ImageButton loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        signUpTv = findViewById(R.id.sign_up_tv);
        emailEt = findViewById(R.id.insertEmailEditText);
        passwordEt = findViewById(R.id.password_et);
        loginButton = findViewById(R.id.login_button);

        signUpTv.setText(Html.fromHtml("<font color='#FFFFFF'>עוד אין לך חשבון?</font>"
                + "<font color='#ddff00'> הרשם </font>"));

        signUpTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}