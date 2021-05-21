package com.example.thumb2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
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

import java.util.concurrent.TimeUnit;

public class DriverDetailsActivity extends AppCompatActivity {

    private UserInformation userInformation;
    private String userId = FirebaseAuth.getInstance().getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_details);

        TextView firstName_tv = findViewById(R.id.driverDetails_firstName);
        TextView lastName_tv = findViewById(R.id.driverDetails_lastName);
        TextView carNumber_tv = findViewById(R.id.driverDetails_carNumber_et);
        TextView carDesc_tv = findViewById(R.id.driverDetails_carDescription_et);
        ImageView id_iv = findViewById(R.id.driverDetails_idCard_iv);
        ImageView armyId_iv = findViewById(R.id.driverDetails_armyIdCard_iv);

        // Load user details
        DatabaseReference dbRefToUsers = FirebaseDatabase.getInstance().getReference().child("users")
                .child(FirebaseAuth.getInstance().getUid());

        dbRefToUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userInformation = snapshot.getValue(UserInformation.class);

                //I STOPPED HERE:
                // i need to finish to display te user's data AS I WANT IT TO BE DISPLAYED! NICE
                // AND ELEGANT ! ! !
                // to create barcode
                // to create barcode scanner
                // to create google map

                firstName_tv.setText(userInformation.getFirstName());
                lastName_tv.setText(userInformation.getLastName());
                carNumber_tv.setText(userInformation.getCarNumber());
                carDesc_tv.setText("תיאור הרכב: " + userInformation.getCarDescription());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Load user id and army id.

        String url = "gs://thumb2.appspot.com/users/" + userId + "/images/";
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
                Toast.makeText(getApplicationContext(), "No Such file or Path found!!", Toast.LENGTH_LONG).show();
            }
        });
    }

}