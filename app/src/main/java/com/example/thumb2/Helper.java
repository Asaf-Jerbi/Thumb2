package com.example.thumb2;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class Helper extends AppCompatActivity {
    private final int MY_SMS_SENDING_PERMISSION_CODE = 0;

    // Fields

    public enum Validation {VALID, INVALID}

    enum UserType {SOLDIER, DRIVER}

    private String smsMessageText;
    private String phoneNumber;
    private final SmsManager smsManager;
    private Location userCurrentLocation;
    private Context smsContext;

    public Helper() {
        this.smsManager = SmsManager.getDefault();
    }
    // Methods


    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setSmsMessageText(String smsMessageText) {
        this.smsMessageText = smsMessageText;
    }

    public String getSmsMessageText() {
        return smsMessageText;
    }

    // This method returns the current time according to user's phone
    public String getCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        String hour = Integer.toString(calendar.get(Calendar.HOUR_OF_DAY));
        String minute = Integer.toString(calendar.get(Calendar.MINUTE));

        return hour + ":" + minute;
    }


    // This method sends sms message to pre defined SOS contact
    public void sendSosMessage(Context context) {

        // get emergency contact phone number for local saved data:
        SharedPreferences localData = context
                .getSharedPreferences(context.getString(R.string.preference_file_key), 0);
        String emergencyPhoneNumber = localData.getString(context.getString(R.string.emergencyContact),
                null);

        // get user's current location:
        if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

            //todo: what to do if doesn't have permissions to location?

        }

        LocationServices.getFusedLocationProviderClient(context).getLastLocation()
                .addOnSuccessListener(location -> {
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        String newLine = "\n";
                        String locationOfUser = "https://www.google.com/maps/search/?api=1&query=" +
                                location.getLatitude() + "," + location.getLongitude();
                        String emergencyText = "הצילו! אני במצב חירום." + newLine
                                + "המיקום שלי:" + newLine + locationOfUser
                                + newLine
                                + "הודעה זו נשלחה בעקבות לחיצה על לחצן חירום באפליקציית Thumb";

                        sendSmsMessage(context, emergencyPhoneNumber, emergencyText);
                    }
                });
    }


    // This method sends sms by phone number and text to send
    public void sendSmsMessage(Context context, String phoneNumber, String message) {
        this.smsContext = context;
        this.setSmsMessageText(message);
        this.setPhoneNumber(phoneNumber);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context.checkSelfPermission(Manifest.permission.SEND_SMS)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.SEND_SMS},
                        MY_SMS_SENDING_PERMISSION_CODE);
            } else {
                SMSUtils.sendSMS(context, this.getPhoneNumber(), this.getSmsMessageText());
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[],
                                           int[] grantResults) {
        if (requestCode == MY_SMS_SENDING_PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (this.smsContext == null) return;
                SMSUtils.sendSMS(this.smsContext, this.getPhoneNumber(), this.getSmsMessageText());
            } else {
                Toast.makeText(getApplicationContext(),
                        "לא הצלחנו לשלוח את ההודעה", Toast.LENGTH_LONG).show();
            }
        }
    }
}
