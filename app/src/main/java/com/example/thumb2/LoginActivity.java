package com.example.thumb2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {


    //fields
    private TextView signUpTv;
    private EditText emailEt, passwordEt;
    private ImageButton loginButton;
    private FirebaseAuth firebaseAuth;
    private String email, password;
    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        signUpTv = findViewById(R.id.sign_up_tv);
        emailEt = findViewById(R.id.insertEmailEditText);
        passwordEt = findViewById(R.id.password_et);
        loginButton = findViewById(R.id.login_button);
        firebaseAuth = FirebaseAuth.getInstance();

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

        loginButton.setOnClickListener(v -> login());
    }

    private void login() {
        if (validateEmail() != Helper.Validation.VALID
                || validatePassword() != Helper.Validation.VALID) {
            return;
        }

        firebaseAuth.signInWithEmailAndPassword(this.email, this.password).
                addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(LoginActivity.this, "מתחבר..",
                                            Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(LoginActivity.this,
                                            MainScreenActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(LoginActivity.this,
                                            "שם משתמש או סיסמא אינם נכונים",
                                            Toast.LENGTH_LONG).show();
                                    emailEt.setError("");
                                }
                            }
                        }
                ).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "SignInWithEmailAndPassword Failed! Exception: \n" + e.toString());
            }
        });

    }

    private Helper.Validation validatePassword() {
        // Just check that something was typed
        password = this.passwordEt.getText().toString();
        if (password.length() > 0) {
            return Helper.Validation.VALID;
        }
        return Helper.Validation.INVALID;
    }


    private Helper.Validation validateEmail() {
        this.email = emailEt.getText().toString();
        //check if empty
        if (TextUtils.isEmpty(email)) {
            this.emailEt.setError("לא הוזנה כתובת מייל");
            return Helper.Validation.INVALID;
        }
        //check if valid
        if (!isValidEmail(email)) {
            this.emailEt.setError("כתובת המייל שהוזנה אינה חוקית");
            return Helper.Validation.INVALID;
        }
        return Helper.Validation.VALID;
    }

    private boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

}