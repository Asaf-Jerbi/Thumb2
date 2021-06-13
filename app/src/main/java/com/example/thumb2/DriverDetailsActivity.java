package com.example.thumb2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_details);

        // user wants to take a traveler, so the barcode need to be displayed:
        if (getIntent().getStringExtra("showBarcode").toLowerCase().equals("yes")) {
            displayUserBarcode();
        } else if (getIntent().getStringExtra("showBarcode").toLowerCase().equals("no")) {
            // user scanned qr code, and want to get user details page:
            this.userToShowInformationOn = getIntent().getStringExtra("stringFromQrScanner");
        }
        TextView firstName_tv = findViewById(R.id.driverDetails_firstName);
        TextView lastName_tv = findViewById(R.id.driverDetails_lastName);
        TextView carNumber_tv = findViewById(R.id.driverDetails_carNumber_et);
        TextView carDesc_tv = findViewById(R.id.driverDetails_carDescription_et);
        ImageView id_iv = findViewById(R.id.driverDetails_idCard_iv);
        ImageView armyId_iv = findViewById(R.id.driverDetails_armyIdCard_iv);

        // Load user details from firebase:
        DatabaseReference dbRefToUsers = FirebaseDatabase.getInstance().getReference().child("users");

        dbRefToUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //TODO: ADD ALERT IF  BARCODE WASN'T RECOGNIZE AS A VALID USER!

                // If user doesn't exist in system (means the qr is fake) - open alert:
                try {
                    if (!snapshot.hasChild(userToShowInformationOn)) {
                        new AlertDialog.Builder(DriverDetailsActivity.this)
                                .setTitle("חירום חירום חירום")
                                .setPositiveButton("הצילו!", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        //something to do when clicking
                                    }
                                }).show();
                    } else {
                        userInformation = snapshot.getValue(UserInformation.class);
                        firstName_tv.setText("שם פרטי: " + userInformation.getFirstName());
                        lastName_tv.setText("שם משפחה: " + userInformation.getLastName());
                        carNumber_tv.setText("מספר רכב: " + userInformation.getCarNumber());
                        carDesc_tv.setText("תיאור הרכב: " + userInformation.getCarDescription());
                    }
                } catch (Exception e) {
                    Log.e(TAG, "onDataChange: " + e.toString());
                    new AlertDialog.Builder(DriverDetailsActivity.this)
                            .setTitle("חירום חירום חירום")
                            .setPositiveButton("הצילו!", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    //something to do when clicking
                                }
                            }).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }


        });

        // Load user id and army id.

        String url = "gs://thumb2.appspot.com/users/" + userToShowInformationOn + "/images/";
        StorageReference armyIdCardRef = FirebaseStorage.getInstance().
                getReferenceFromUrl(url + "armyIdCard.jpg");

        StorageReference idCardRef = FirebaseStorage.getInstance().
                getReferenceFromUrl(url + "idCard.jpg");

        loadImageToImageView(armyIdCardRef, armyId_iv);
        loadImageToImageView(idCardRef, id_iv);

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