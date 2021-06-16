package com.example.thumb2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.Objects;

import id.privy.livenessfirebasesdk.LivenessApp;

public class RegistrationActivity extends AppCompatActivity {

    public static final String TAG = "RegistrationActivity";

    //Fields
    private ImageView selfieImageView, idCardImageView, armyIdCardImageView;
    private ImageButton takeSelfieImageButton;
    private EditText firstName_et, lastNmae_et, idNumber_et, personalNumber_et,
            phoneNumber_et, carNumber_et, carDescription_et;
    private TextView releaseDateTextView;
    private Button nextButton;
    private Uri imageUri;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private Helper.UserType userType;
    private String firstName, lastName, releaseDateString, phoneNumber, carNumber, carDescription;
    private String personalNumber, idNumber;
    private DatabaseReference mDatabase;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private static final String $SUCCESSTEXT = "מעולה!";
    private static final String $INSTRUCTIONS = "הוראות";
    private static final String $LEFT_MOTION_INSTRUCTION = "הבט/י שמאלה";
    private static final String $RIGHT_MOTION_INSTRUCTION = "הבט/י ימינה";
    private LivenessApp livenessApp;
    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //init views
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        selfieImageView = findViewById(R.id.selfie_iv);
        takeSelfieImageButton = findViewById(R.id.take_selfie_IB);
        firstName_et = findViewById(R.id.insertFirstName);
        lastNmae_et = findViewById(R.id.insertLastName);
        idNumber_et = findViewById(R.id.insertIdNumber);
        personalNumber_et = findViewById(R.id.insertPersonalNumber);
        nextButton = (Button) findViewById(R.id.next_btn);
        releaseDateTextView = findViewById(R.id.releaseDate_tv);
        idCardImageView = findViewById(R.id.idCard_iv);
        armyIdCardImageView = findViewById(R.id.armyIdCard_iv);
        phoneNumber_et = findViewById(R.id.phoneNumber_et);
        carNumber_et = findViewById(R.id.carNumber_et);
        carDescription_et = findViewById(R.id.carDescription_et);

        //init firebase services
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //get data from previous activity
        userType = ((getIntent().getStringExtra("userType").toLowerCase().equals("soldier")
                ? Helper.UserType.SOLDIER : Helper.UserType.DRIVER));

        //Request for camera permissions:
        if (ContextCompat.checkSelfPermission(RegistrationActivity.this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(RegistrationActivity.this,
                    new String[]{
                            Manifest.permission.CAMERA
                    },
                    100);
        }


        // buttons configuration
        takeSelfieImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                    } else {
                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cameraIntent, CAMERA_REQUEST);
                    }
                }
            }
        });

//todo: to bting this back later. for now, I have major problems of versions conflicts.
/*        takeSelfieImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    livenessApp = new LivenessApp.Builder(RegistrationActivity.this)
                            .setDebugMode(false) //to enable face landmark detection
                            .setSuccessText($SUCCESSTEXT)
                            .setInstructions($INSTRUCTIONS)
                            .setMotionInstruction($LEFT_MOTION_INSTRUCTION, $RIGHT_MOTION_INSTRUCTION).build();
                    livenessApp.start(new PrivyCameraLivenessCallBackListener() {
                        @Override
                        public void success(LivenessItem livenessItem) {
                            //set image in imageView
                            selfieImageView.setImageBitmap(livenessItem.getImageBitmap());
                        }

                        @Override
                        public void failed(Throwable t) {
                            t.printStackTrace();
                        }
                    });
                } catch (Exception e) {
                    Log.e(TAG, "onClick: " + e.toString());
                }
            }
        });*/

        // Pic id card
        idCardImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Open Camera
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 100);
            }
        });

        // Pic army id card
        armyIdCardImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Open Camera
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 101);
            }
        });


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
                month += 1;
                String date = dayOfMonth + "/" + month + "/" + year;
                releaseDateTextView.setText(date);
                releaseDateString = date;
            }
        };
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //uploading id
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            // Get Capture Image
            Bitmap captureImage = (Bitmap) data.getExtras().get("data");
            //Set Capture Image
            idCardImageView.setImageBitmap(captureImage);
            //upload to firebase
            uploadImage(captureImage, idCardImageView, "idCard.png");
        } else if (requestCode == 101 && resultCode == Activity.RESULT_OK) {
            //uploading army id
            Bitmap captureImage = (Bitmap) data.getExtras().get("data");
            armyIdCardImageView.setImageBitmap(captureImage);
            uploadImage(captureImage, armyIdCardImageView, "armyIdCard.png");
        } else if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap captureImage = (Bitmap) data.getExtras().get("data");
            selfieImageView.setImageBitmap(captureImage);
            uploadImage(captureImage, selfieImageView, "selfie.png");
        }
    }

    private void Register() {

        // collect data from fields (I took other fields' data in their validation methods)
        if (carDescription_et.getText().toString().equals("")) {
            carDescription = "לא הוזן תיאור";
        } else {
            carDescription = carDescription_et.getText().toString();
        }

        //validate data:
        if (isValidData() == Helper.Validation.INVALID) {
            Toast.makeText(RegistrationActivity.this,
                    "הפרטים אינם תקינים", Toast.LENGTH_LONG).show();
            return;
        }

        //save user's data to firebase:
        UserInformation newUser = new UserInformation(firstName, lastName, personalNumber, idNumber,
                releaseDateString, carNumber, phoneNumber, carDescription, userType);
        mDatabase.child("users").child(Objects.requireNonNull(firebaseAuth.getUid()))
                .setValue(newUser).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(RegistrationActivity.this, "נתוני המשתמש נשמרו בהצלחה!",
                        Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RegistrationActivity.this, "אירעה שגיאה במהלך שמירת הנתונים",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    public void uploadImage(Bitmap bitmap, ImageView imageView, String imageName) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        // Create path
        StorageReference imagesRef = storage.getReference().
                child("users").child(FirebaseAuth.getInstance().getUid()).
                child("images").child(imageName);
        UploadTask uploadTask = imagesRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                imageView.setBackgroundResource(R.drawable.red_frame_image_view);
                Toast.makeText(RegistrationActivity.this, "העלאה נכשלה",
                        Toast.LENGTH_LONG).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Handle successful uploads
                Toast.makeText(RegistrationActivity.this, "התמונה הועלתה בהצלחה",
                        Toast.LENGTH_LONG).show();
                imageView.setBackgroundResource(R.drawable.green_frame_image_view);
            }
        });
    }

    private Helper.Validation isValidData() {
        Helper.Validation firstNameValidation, lastNameValidation,
                personalNumberValidation, idNumberValidation, releaseDateValidation,
                carNumberValidation, phoneNumberValidation, idCaptureValidation,
                armyIdCaptureValidation;

        firstNameValidation = validateFirstName();
        lastNameValidation = validateLastName();
        personalNumberValidation = validatePersonalNumber();
        idNumberValidation = validateId();
        releaseDateValidation = validateReleaseDate();
        carNumberValidation = validateCarNumber();
        phoneNumberValidation = validatePhoneNumber();
        idCaptureValidation = validateImageUploaded(idCardImageView);
        armyIdCaptureValidation = validateImageUploaded(armyIdCardImageView);

        if (firstNameValidation == Helper.Validation.INVALID
                || lastNameValidation == Helper.Validation.INVALID
                || personalNumberValidation == Helper.Validation.INVALID
                || idNumberValidation == Helper.Validation.INVALID
                || releaseDateValidation == Helper.Validation.INVALID
                || carNumberValidation == Helper.Validation.INVALID
                || phoneNumberValidation == Helper.Validation.INVALID
                || idCaptureValidation == Helper.Validation.INVALID
                || armyIdCaptureValidation == Helper.Validation.INVALID) {
            return Helper.Validation.INVALID;
        } else {
            return Helper.Validation.VALID;
        }
    }


    // this method gets image view and if it's frame is green - returns Valid. Otherwise - Invalid.
    // (sure this method relevant to image views for uploaded images that get diiferent
    // frame for different upload situations. (successful upload --> green frame)
    private Helper.Validation validateImageUploaded(ImageView imageView) {
        if (imageView.getBackground().getConstantState()
                == getResources().getDrawable(R.drawable.green_frame_image_view).getConstantState()) {
            // if image frame is green (means image uploaded successfully)
            return Helper.Validation.VALID;
        }

        // otherwise, change frame to red and return invalid
        imageView.setBackgroundResource(R.drawable.red_frame_image_view);
        return Helper.Validation.INVALID;
    }

    private Helper.Validation validatePhoneNumber() {
        this.phoneNumber = phoneNumber_et.getText().toString();
        if (this.phoneNumber.length() == 10
                && this.phoneNumber.matches("^05\\d([-]{0,1})\\d{7}$")) {
            return Helper.Validation.VALID;
        }
        phoneNumber_et.setError("מספר הטלפון שהוזן אינו תקין");
        return Helper.Validation.INVALID;
    }

    private Helper.Validation validateCarNumber() {
        this.carNumber = carNumber_et.getText().toString();
        // The user is a Driver
        if (this.userType == Helper.UserType.DRIVER) {
            if (this.carNumber.length() == 0) {
                this.carNumber_et.setError("שדה זה הינו חובה");
                return Helper.Validation.INVALID;
            } else if (!this.carNumber.matches("\\d{7,8}")) {
                this.carNumber_et.setError("מספר הרכב אינו באורך תקין");
                return Helper.Validation.INVALID;
            }
            return Helper.Validation.VALID;
        }

        // The user is a Soldier
        if (this.carNumber.length() > 0 && !this.carNumber.matches("\\d{7,8}")) {
            this.carNumber_et.setError("מספר הרכב אינו באורך תקין");
            return Helper.Validation.INVALID;
        }

        return Helper.Validation.VALID;
    }

    private Helper.Validation validateReleaseDate() {

        if (this.releaseDateString == null) {
            this.releaseDateTextView.setError("יש לבחור תאריך");
            return Helper.Validation.INVALID;
        }
        return Helper.Validation.VALID;
    }

    //todo: QA
    private Helper.Validation validateFirstName() {
        this.firstName = firstName_et.getText().toString();
        if (this.firstName.length() > 1 && this.firstName.matches("^([\\u0590-\\u05fe|'])+$")) {
            return Helper.Validation.VALID;
        }
        firstName_et.setError("נא השתמשו באותיות עברית בלבד ובאורך תקין");
        return Helper.Validation.INVALID;
    }

    //todo: QA
    private Helper.Validation validateLastName() {
        this.lastName = lastNmae_et.getText().toString();
        if (this.lastName.length() > 2 && this.lastName.matches("^([\\u0590-\\u05fe|'])+$")) {
            return Helper.Validation.VALID;
        }
        lastNmae_et.setError("נא השתמשו באותיות עברית ובאורך תקין");
        return Helper.Validation.INVALID;
    }

    private Helper.Validation validatePersonalNumber() {
        personalNumber = personalNumber_et.getText().toString();

        if (personalNumber.matches("\\d{4,10}")) {
            // todo: if(personalNumber == personalNumberTakenFromArmyIdPicture){return Helper.Validation.VALID}
            return Helper.Validation.VALID;
        }
        this.personalNumber_et.setError("נראה כי האורך אינו תקין");
        return Helper.Validation.INVALID;
    }

    //todo:
    // 1. create method to validate id
    // 2.  compare it to the data from the military id (choger / teodat shihrur)
    private Helper.Validation validateId() {
        idNumber = idNumber_et.getText().toString();
        if (!idNumber.matches("\\d{9}")) {
            idNumber_et.setError("יש להזין 9 ספרות, כולל ספרת ביקורת");
            return Helper.Validation.INVALID;
        }
        return Helper.Validation.VALID;
    }
}