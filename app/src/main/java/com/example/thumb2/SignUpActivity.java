package com.example.thumb2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.rpc.Help;

public class SignUpActivity extends AppCompatActivity {
    private EditText emailEt, passwordEt1, passwordEt2;
    private TextView signInTv;
    private ImageButton signUpButton;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private String email, password, password2;
    private Helper helper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //initiate super class and define content view
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        helper = new Helper();

        //initiate views
        firebaseAuth = FirebaseAuth.getInstance();
        emailEt = findViewById(R.id.insertEmailEditText);
        passwordEt1 = findViewById(R.id.password_et);
        passwordEt2 = findViewById(R.id.insertPasswordEditText2);
        signUpButton = findViewById(R.id.next_btn);
        signInTv = findViewById(R.id.sign_in_tv);
        progressDialog = new ProgressDialog(this);


        //define text to text view:
        signInTv.setText(Html.fromHtml("<font color='#FFFFFF'>כבר יש לך חשבון? </font>"
                + "<font color='#ddff00'> היכנס </font>"));


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

        if (isValidData() == Helper.Validation.INVALID) {
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

    private Helper.Validation isValidData() {
        Helper.Validation emailValidation, passwordValidation;
        emailValidation = validateEmail();
        passwordValidation = validatePasswords();
        if (emailValidation == Helper.Validation.INVALID || passwordValidation == Helper.Validation.INVALID) {
            return Helper.Validation.INVALID;
        }
        return Helper.Validation.VALID;
    }

    private Helper.Validation validatePasswords() {
        Helper.Validation validationFlag = Helper.Validation.VALID;
        //password1:
        //check if empty
        if (TextUtils.isEmpty(password)) {
            passwordEt1.setError("יש להזין סיסמא");
            validationFlag = Helper.Validation.INVALID;
            //validate length
        } else if (password.length() < 4) {
            passwordEt1.setError("על הסיסמא להכיל 4 תווים לפחות");
            validationFlag = Helper.Validation.INVALID;
        }

        //password2:
        //check if empty
        if (TextUtils.isEmpty(password2)) {
            passwordEt2.setError("יש להזין סיסמא בשנית");
            validationFlag = Helper.Validation.INVALID;
        } else if (!password.equals(password2)) {
            passwordEt2.setError("הסיסמאות אינן זהות");
            validationFlag = Helper.Validation.INVALID;
        }
        return validationFlag;
    }

    private Helper.Validation validateEmail() {
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