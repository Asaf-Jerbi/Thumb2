package com.example.thumb2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.thumb2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.core.utilities.Validation;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;

import java.time.LocalDate;

public class SignUpActivity extends AppCompatActivity {
    private EditText emailEt, passwordEt1, passwordEt2;
    private TextView signInTv;
    private ImageButton signUpButton;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private String email, password, password2;
    private Helper helper;
    public enum Validation {VALID, INVALID}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //initiate super class and define content view
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        helper = new Helper();

        //initiate views
        firebaseAuth = FirebaseAuth.getInstance();
        emailEt = findViewById(R.id.insertEmailEditText);
        passwordEt1 = findViewById(R.id.insertPasswordEditText);
        passwordEt2 = findViewById(R.id.insertPasswordEditText2);
        signUpButton = findViewById(R.id.next_btn);
        signInTv = findViewById(R.id.sign_in_tv);
        progressDialog = new ProgressDialog(this);


        //define logic to signup button
        signUpButton.setOnClickListener(v -> Register());

        signInTv.setOnClickListener(v -> {
            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void Register() {

        //get data:
        email = emailEt.getText().toString();
        password = passwordEt1.getText().toString();
        password2 = passwordEt2.getText().toString();

        //validate data:

        if (isValidData() == Validation.INVALID) {
            return;
        }

        //create user with email and password:
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            firebaseAuth.getCurrentUser();
                            openSoldierOrDriverActivity();
                        } else {
                            Toast.makeText(SignUpActivity.this, "אירעה שגיאה, יש לנסות שנית",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void openSoldierOrDriverActivity() {
        Intent intent = new Intent(SignUpActivity.this, SoldierOrDriverOptionsActivity.class);
        startActivity(intent);
        finish();
    }

    private Validation isValidData() {
        Validation emailValidation, passwordValidation;
        emailValidation = validateEmail();
        passwordValidation = validatePasswords();
        if (emailValidation == Validation.INVALID || passwordValidation == Validation.INVALID) {
            return Validation.INVALID;
        }
        return Validation.VALID;
    }

    private Validation validatePasswords() {
        Validation validationFlag = Validation.VALID;
        //password1:
        //check if empty
        if (TextUtils.isEmpty(password)) {
            passwordEt1.setError("יש להזין סיסמא");
            validationFlag = Validation.INVALID;
            //validate length
        } else if (password.length() < 4) {
            passwordEt1.setError("על הסיסמא להכיל 4 תווים לפחות");
            validationFlag = Validation.INVALID;
        }

        //password2:
        //check if empty
        if (TextUtils.isEmpty(password2)) {
            passwordEt2.setError("יש להזין סיסמא בשנית");
            validationFlag = Validation.INVALID;
        } else if (!password.equals(password2)) {
            passwordEt2.setError("הסיסמאות אינן זהות");
            validationFlag = Validation.INVALID;
        }
        return validationFlag;
    }

    private Validation validateEmail() {
        //check if empty
        if (TextUtils.isEmpty(email)) {
            this.emailEt.setError("לא הוזנה כתובת מייל");
            return Validation.INVALID;
        }
        //check if valid
        if (!isValidEmail(email)) {
            this.emailEt.setError("כתובת המייל שהוזנה אינה חוקית");
            return Validation.INVALID;
        }
        return Validation.VALID;
    }

    private boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
}