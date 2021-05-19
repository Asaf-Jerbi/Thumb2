package com.example.thumb2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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
import com.google.rpc.Help;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.Objects;

import id.privy.livenessfirebasesdk.LivenessApp;
import id.privy.livenessfirebasesdk.entity.LivenessItem;
import id.privy.livenessfirebasesdk.listener.PrivyCameraLivenessCallBackListener;

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


        ///////////////////   BUTTONS   ///////////////////

        //upload image button
        /*uploadImageImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePicture();
            }
        });*/

        // get selfie and check liveness
        takeSelfieImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
            }
        });

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

//    public Bitmap getCroppedBitmap(Bitmap bitmap) {
//        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
//                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(output);
//
//        final int color = 0xff424242;
//        final Paint paint = new Paint();
//        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
//        paint.setAntiAlias(true);
//        canvas.drawARGB(0, 0, 0, 0);
//        paint.setColor(color);
//        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
//        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
//                bitmap.getWidth() / 2, paint);
//        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
//        canvas.drawBitmap(bitmap, rect, rect, paint);
//        //Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
//        //return _bmp;
//        return output;
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //uploading id
        if (requestCode == 100) {
            // Get Capture Image
            Bitmap captureImage = (Bitmap) data.getExtras().get("data");
            //Set Capture Image
            uploadImage(captureImage, idCardImageView, "idCard.jpg");
            idCardImageView.setImageBitmap(captureImage);
        } else if (requestCode == 101) {
            //uploading army id
            Bitmap captureImage = (Bitmap) data.getExtras().get("data");
            uploadImage(captureImage, armyIdCardImageView, "armyIdCard.jpg");
            armyIdCardImageView.setImageBitmap(captureImage);
        }
    }

    private void Register() {

        //validate data:
        if (isValidData() == Helper.Validation.INVALID) {
            //todo: remove me
            Toast.makeText(RegistrationActivity.this, "משהו בשדות לא עובר ואלידציה", Toast.LENGTH_LONG).show();
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

    public void uploadImage(Bitmap bitmap, ImageView imageView, String imageName) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
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
                carNumberValidation, phoneNumberValidation, idCapture, armyIdCapture;

        firstNameValidation = validateFirstName();
        lastNameValidation = validateLastName();
        personalNumberValidation = validatePersonalNumber();
        idNumberValidation = validateId();
        releaseDateValidation = validateReleaseDate();
        carNumberValidation = validateCarNumber();
        phoneNumberValidation = validatePhoneNumber();
        //idCapture = validateIdCaptureUpload();
        //armyIdCapture = validateArmyIdCaptureUpload();

        if (firstNameValidation == Helper.Validation.INVALID
                || lastNameValidation == Helper.Validation.INVALID
                || personalNumberValidation == Helper.Validation.INVALID
                || idNumberValidation == Helper.Validation.INVALID
                || releaseDateValidation == Helper.Validation.INVALID
                || carNumberValidation == Helper.Validation.INVALID
                || phoneNumberValidation == Helper.Validation.INVALID) {
            return Helper.Validation.INVALID;
        } else {
            return Helper.Validation.VALID;
        }
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
            //todo: remove me
            Toast.makeText(RegistrationActivity.this, "התאריך נאל", Toast.LENGTH_LONG).show();

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