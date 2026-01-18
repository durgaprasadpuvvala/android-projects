package com.example.busapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class DriverDashboardActivity extends AppCompatActivity {

    FusedLocationProviderClient client;
    LocationCallback locationCallback;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_driver_dashboard);

        client = LocationServices.getFusedLocationProviderClient(this);

        TextView tvLiveStatus = findViewById(R.id.tvLiveStatus);
        Button btnLogout = findViewById(R.id.btnLogout);

        btnLogout.setOnClickListener(v -> {
            client.removeLocationUpdates(locationCallback);
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

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

        LocationRequest request = LocationRequest.create();
        request.setInterval(3000);
        request.setPriority(Priority.PRIORITY_HIGH_ACCURACY);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult result) {

                Location l = result.getLastLocation();
                if (l == null) return;

                Map<String, Object> map = new HashMap<>();
                map.put("latitude", l.getLatitude());
                map.put("longitude", l.getLongitude());
                map.put("lastUpdate", System.currentTimeMillis());

                FirebaseDatabase.getInstance()
                        .getReference("busLocation/BUS_101")
                        .setValue(map);

                tvLiveStatus.setText("🟢 Live Location Active");
            }
        };

        client.requestLocationUpdates(
                request,
                locationCallback,
                Looper.getMainLooper()
        );
    }
}
