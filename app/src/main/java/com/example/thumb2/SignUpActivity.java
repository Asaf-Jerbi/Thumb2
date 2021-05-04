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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;

public class SignUpActivity extends AppCompatActivity {
    private EditText emailEt, passwordEt1, passwordEt2;
    private TextView signInTv;
    private ImageButton signUpButton;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private String email, password, password2;

    enum Validation {VALID, INVALID}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //initiate super class and define content view
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

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
        //get data from text boxes:
        email = emailEt.getText().toString();
        password = passwordEt1.getText().toString();
        password2 = passwordEt2.getText().toString();

        if (isValidData() == Validation.INVALID) {
            return;
        }

        // #$#$#$#$#$#$#$#$ I STOPPED HERE LAST TIME: 25/04/2021 23:02 #$#$#$#$#$#$#$#$#$# //
        // #$#$#$#$#$#$#$#$ I STOPPED HERE LAST TIME: 25/04/2021 23:02 #$#$#$#$#$#$#$#$#$# //
        // #$#$#$#$#$#$#$#$ I STOPPED HERE LAST TIME: 25/04/2021 23:02 #$#$#$#$#$#$#$#$#$# //
        //1. user data was created on firebase, but one time it didn't (don't know why)
        //2. now I need to move all the information taking to another activity, here I only should
        //get the email and the password (if they valid)
        //3. after all that, I should create a page for displaying all the data for specific user.
        // #$#$#$#$#$#$#$#$ I STOPPED HERE LAST TIME: 25/04/2021 23:02 #$#$#$#$#$#$#$#$#$# //
        // #$#$#$#$#$#$#$#$ I STOPPED HERE LAST TIME: 25/04/2021 23:02 #$#$#$#$#$#$#$#$#$# //
        // #$#$#$#$#$#$#$#$ I STOPPED HERE LAST TIME: 25/04/2021 23:02 #$#$#$#$#$#$#$#$#$# //

        //todo: move the creation to the end of the registration process.
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            openSoldierOrDriverActivity();
                        } else {
                            Toast.makeText(SignUpActivity.this, "אירעה שגיאה, יש לנסות שנית",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });


        //todo: add below line back

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
        //check if empty
        if (TextUtils.isEmpty(password)) {
            passwordEt1.setError("יש להזין סיסמא");
            validationFlag = Validation.INVALID;
            //validate length
        } else if (password.length() < 4) {
            passwordEt1.setError("על הסיסמא להכיל 4 תווים לפחות");
            validationFlag = Validation.INVALID;
        }


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