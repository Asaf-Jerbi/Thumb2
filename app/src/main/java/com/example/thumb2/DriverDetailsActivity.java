package com.example.thumb2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class DriverDetailsActivity extends AppCompatActivity {

    private UserInformation userInformation;
    private String userToShowInformationOn = FirebaseAuth.getInstance().getUid();
    public static final String TAG = "DriverDetailsActivity";
    private boolean isImageFitToScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_details);

        // user wants to take a traveler, so the barcode need to be displayed:
        if (getIntent().getStringExtra("showBarcode").toLowerCase().equals("yes")) {
            // create barcode of current user id:
            View v = findViewById(R.id.recognization_approval);
            v.setVisibility(View.INVISIBLE);
            displayUserBarcode();
        } else if (getIntent().getStringExtra("showBarcode").toLowerCase().equals("no")) {
            // get user id from scanned barcode:
            this.userToShowInformationOn = getIntent().getStringExtra("stringFromQrScanner");
        }
        TextView firstName_tv = findViewById(R.id.driverDetails_firstName);
        TextView lastName_tv = findViewById(R.id.driverDetails_lastName);
        TextView carNumber_tv = findViewById(R.id.driverDetails_carNumber_et);
        TextView carDesc_tv = findViewById(R.id.driverDetails_carDescription_et);
        ImageView id_iv = (ImageView) findViewById(R.id.driverDetails_idCard_iv);
        ImageView armyId_iv = findViewById(R.id.driverDetails_armyIdCard_iv);
        Button approveDriver_btn = (Button) findViewById(R.id.yes_btn);
        Button sos_btn = (Button) findViewById(R.id.sos_btn);


        // Load user details from firebase:
        DatabaseReference dbRefToUsers = FirebaseDatabase.getInstance().getReference().child("users");

        dbRefToUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // If user doesn't exist in system (means the qr is fake) - open alert:
                try {
                    if (!snapshot.hasChild(userToShowInformationOn)) {
                        showUnrecognizedBarcodeAlert();
                    } else {
                        userInformation = snapshot.child(userToShowInformationOn).getValue(UserInformation.class);
                        firstName_tv.setText("שם פרטי: " + userInformation.getFirstName());
                        lastName_tv.setText("שם משפחה: " + userInformation.getLastName());
                        carNumber_tv.setText("מספר רכב: " + userInformation.getCarNumber());
                        carDesc_tv.setText("תיאור הרכב: " + userInformation.getCarDescription());

                        // Load images from storage: user_id + army_id:
                        String url = "gs://thumb2.appspot.com/users/" + userToShowInformationOn + "/images/";
                        StorageReference armyIdCardRef = FirebaseStorage.getInstance().
                                getReferenceFromUrl(url + "armyIdCard.jpg");

                        StorageReference idCardRef = FirebaseStorage.getInstance().
                                getReferenceFromUrl(url + "idCard.jpg");

                        loadImageToImageView(armyIdCardRef, armyId_iv);
                        loadImageToImageView(idCardRef, id_iv);
                    }
                } catch (Exception e) {
                    showUnrecognizedBarcodeAlert();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }


        });

        // Define buttons behaviour
        approveDriver_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DriverDetailsActivity.this, onRideActivity.class);
                intent.putExtra("driverDetails", userInformation);
                startActivity(intent);
                finish();
            }
        });

    }

    private void showUnrecognizedBarcodeAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(DriverDetailsActivity.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(DriverDetailsActivity.this).inflate(
                R.layout.layout_unrecognized_barcode_dialog,
                (ConstraintLayout) findViewById(R.id.layoutDialogContainer)
        );
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.try_again_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // go back to qr scanner that was open earlier
                finish();
            }
        });


        view.findViewById(R.id.sos_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: what happened when clicking this button?
                //I think should be:
                // 1. alert that asks for additional approval
                // 2. sending message with: location, driver details, driver image.
                // 3. sos contact was
            }
        });

        //if back button is pressed, go back to main screen (because driver details is empty)
        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                finish();
            }
        });

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        alertDialog.show();
    }


    private void loadImageToImageView(StorageReference storageReference, ImageView imageView) {
        final long ONE_MEGABYTE = 1024 * 1024;
        storageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                imageView.setImageBitmap(bmp);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(getApplicationContext(), "אירעה בעיה בטעינת התמונה",
                        Toast.LENGTH_LONG).show();
            }
        });
    }


    public void displayUserBarcode() {
        ImageView image = new ImageView(this);
        Bitmap barcode = stringToBitmapBarcode(this.userToShowInformationOn);

        if (barcode != null) {
            image.setImageBitmap(barcode);
            new AlertDialog.Builder(DriverDetailsActivity.this)
                    .setTitle("הצג מסך זה לחייל")
                    .setView(image)
                    .setPositiveButton("הצגתי, אפשר להמשיך", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //something to do when clicking
                        }
                    }).show();
        } else {
            new AlertDialog.Builder(DriverDetailsActivity.this)
                    .setTitle("משהו השתבש :/ ")
                    .setPositiveButton("סגור", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Continue with delete operation
                        }
                    }).setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }


    }

    MultiFormatWriter multiFormatWriter = new MultiFormatWriter();

    private Bitmap stringToBitmapBarcode(String toEncode) {
        Bitmap bitmap = null;
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(toEncode, BarcodeFormat.QR_CODE,
                    550, 550);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            bitmap = barcodeEncoder.createBitmap(bitMatrix);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}