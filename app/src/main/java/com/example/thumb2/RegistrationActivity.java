package com.example.thumb2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;
import java.util.Objects;

public class RegistrationActivity extends AppCompatActivity {

    public static final String TAG = "RegistrationActivity";

    //Fields
    private ImageView armyIdCardImageView, uploadImageImageView;
    private EditText firstName_et, lastNmae_et, idNumber_et, personalNumber_et,
    phoneNumber_et, carNumber_et, carType_et;
    private TextView releaseDateTextView;
    private Button nextButton;
    private Uri imageUri;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private Helper.UserType userType;
    private String firstName, lastName, releaseDateString, phoneNumber, carNumber, carDescription;
    private int personalNumber, idNumber;
    private DatabaseReference mDatabase;
    private DatePickerDialog.OnDateSetListener mDateSetListener;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //init views
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        armyIdCardImageView = findViewById(R.id.selfie_iv);
        uploadImageImageView = findViewById(R.id.upload_image_button);
        firstName_et = findViewById(R.id.insertFirstName);
        lastNmae_et = findViewById(R.id.insertLastName);
        idNumber_et = findViewById(R.id.insertIdNumber);
        personalNumber_et = findViewById(R.id.insertPersonalNumber);
        nextButton = findViewById(R.id.next_btn);
        releaseDateTextView = findViewById(R.id.releaseDate_tv);

        //init firebase services
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //get data from previous activity
        userType = ((getIntent().getStringExtra("userType").toLowerCase().equals("soldier")
                ? Helper.UserType.SOLDIER : Helper.UserType.DRIVER));


        ///////////////////   BUTTONS   ///////////////////

        //upload image button
        /*uploadImageImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePicture();
            }
        });*/

        //next button
        nextButton.setOnClickListener(v -> Register());

        //releaseDate tv
        releaseDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        RegistrationActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String date = dayOfMonth + "/" + month + "/" + year;
                releaseDateTextView.setText(date);
            }
        };
    }


    private void Register() {

        //validate data:
        if (isValidData() == Helper.Validation.INVALID) {
            return;
        }

        //save user's data to firebase:
        UserInformation newUser = new UserInformation(firstName, lastName, personalNumber, idNumber,
                null, null, null, userType);
        mDatabase.child("users").child(Objects.requireNonNull(firebaseAuth.getUid()))
                .setValue(newUser).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(RegistrationActivity.this, "נתוני המשתמש נשמרו בהצלחה!",
                        Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RegistrationActivity.this, "אירעה שגיאה במהלך שמירת הנתונים",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private Helper.Validation isValidData() {
        Helper.Validation firstNameValidation, lastNameValidation,
                personalNumberValidation, idNumberValidation;

        firstNameValidation = validateFirstName(firstName_et);
        lastNameValidation = validateLastName(lastNmae_et);
        personalNumberValidation = validatePersonalNumber();
        idNumberValidation = validateId();

        if (firstNameValidation == Helper.Validation.INVALID
                || lastNameValidation == Helper.Validation.INVALID
                || personalNumberValidation == Helper.Validation.INVALID
                || idNumberValidation == Helper.Validation.INVALID) {
            return Helper.Validation.INVALID;
        } else {
            return Helper.Validation.VALID;
        }
    }


    //todo: test me
    private Helper.Validation validateFirstName(EditText theName_et) {
        this.firstName = theName_et.getText().toString();
        if (this.firstName.length() > 2 && this.firstName.matches("^([\\u0590-\\u05fe|'])+$")) {
            return Helper.Validation.VALID;
        }
        theName_et.setError("נא השתמשו באותיות עברית ובאורך תקין");
        return Helper.Validation.INVALID;
    }

    //todo: test me
    private Helper.Validation validateLastName(EditText theName_et) {
        this.lastName = theName_et.getText().toString();
        if (this.lastName.length() > 2 && this.lastName.matches("^([\\u0590-\\u05fe|'])+$")) {
            return Helper.Validation.VALID;
        }
        theName_et.setError("נא השתמשו באותיות עברית ובאורך תקין");
        return Helper.Validation.INVALID;
    }

    //todo: check if can get letters?!
    private Helper.Validation validatePersonalNumber() {
        personalNumber = Integer.parseInt(personalNumber_et.getText().toString());
        //todo: compare it to the data from the military id (choger / teodat shihrur)

        if (personalNumber > 999999) {
            return Helper.Validation.VALID;
        }
        return Helper.Validation.INVALID;
    }

    //todo: check if can get letters?!
    //todo: create method to validate id
    private Helper.Validation validateId() {
        idNumber = Integer.parseInt(idNumber_et.getText().toString());
        //todo: compare it to the data from the military id (choger / teodat shihrur)

        if (idNumber > 999999) {
            return Helper.Validation.VALID;
        }
        return Helper.Validation.INVALID;
    }


    //Code for uploading image from gallery! not from
    /*    private void choosePicture() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
        uploadImage();
    }

    private void uploadImage() {
        // Create a reference to 'armyIds/userId.jpg'
        StorageReference armyIdsImagesRef = storageReference.child("armyIds/" +
                firebaseAuth.getCurrentUser().getUid() + ".jpg");
        armyIdsImagesRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            armyIdCardImageView.setImageURI(imageUri);
        }

    }*/
}