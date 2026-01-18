package com.example.busapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.google.android.gms.location.*;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;

public class DriverDashboardActivity extends AppCompatActivity {

    FusedLocationProviderClient client;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_driver_dashboard);

        client = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    101
            );
            return;
        }


        client.requestLocationUpdates(
                LocationRequest.create()
                        .setInterval(3000)
                        .setPriority(Priority.PRIORITY_HIGH_ACCURACY),
                new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult result) {
                        Location l = result.getLastLocation();
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("latitude", l.getLatitude());
                        map.put("longitude", l.getLongitude());

                        FirebaseDatabase.getInstance()
                                .getReference("busLocation/BUS_101")
                                .setValue(map);
                    }
                }, Looper.getMainLooper()
        );
    }
}
