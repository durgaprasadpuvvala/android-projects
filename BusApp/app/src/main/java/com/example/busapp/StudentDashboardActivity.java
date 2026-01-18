package com.example.busapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.*;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Scanner;

public class StudentDashboardActivity extends AppCompatActivity
        implements OnMapReadyCallback {

    GoogleMap map;
    TextView tvStatus, tvDistance;
    Button btnLogout;

    DatabaseReference busRef;
    FusedLocationProviderClient locationClient;

    LatLng studentLocation;
    Marker busMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_dashboard);

        tvStatus = findViewById(R.id.tvStatus);
        tvDistance = findViewById(R.id.tvDistance);
        btnLogout = findViewById(R.id.btnLogout);

        locationClient = LocationServices.getFusedLocationProviderClient(this);

        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent i = new Intent(this, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
        });

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        busRef = FirebaseDatabase.getInstance()
                .getReference("busLocation/BUS_101");

        fetchStudentLocation();
    }

    // 📍 REAL STUDENT LOCATION
    private void fetchStudentLocation() {

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 200);
            return;
        }

        locationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        studentLocation = new LatLng(
                                location.getLatitude(),
                                location.getLongitude()
                        );
                    }
                });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        map.getUiSettings().setZoomControlsEnabled(true);
        trackBus();
    }

    private void trackBus() {
        busRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snap) {

                if (!snap.exists() || studentLocation == null) {
                    tvStatus.setText("🔴 Bus OFFLINE");
                    return;
                }

                Double lat = snap.child("latitude").getValue(Double.class);
                Double lng = snap.child("longitude").getValue(Double.class);
                Long lastUpdate = snap.child("lastUpdate").getValue(Long.class);

                if (lat == null || lng == null || lastUpdate == null) {
                    tvStatus.setText("🔴 Data Missing");
                    return;
                }

                if (System.currentTimeMillis() - lastUpdate > 15000) {
                    tvStatus.setText("🔴 Driver OFFLINE");
                    return;
                }

                LatLng busLocation = new LatLng(lat, lng);

                map.clear();

                map.addMarker(new MarkerOptions()
                        .position(studentLocation)
                        .title("You"));

                busMarker = map.addMarker(new MarkerOptions()
                        .position(busLocation)
                        .title("Bus"));

                map.moveCamera(CameraUpdateFactory
                        .newLatLngZoom(busLocation, 14));

                calculateDistance(studentLocation, busLocation);
                drawRoute(studentLocation, busLocation);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    // 📏 DISTANCE FIX
    private void calculateDistance(LatLng s, LatLng b) {
        float[] res = new float[1];
        Location.distanceBetween(
                s.latitude, s.longitude,
                b.latitude, b.longitude,
                res
        );
        float km = res[0] / 1000;
        tvDistance.setText("🚌 Distance: " + String.format("%.2f km", km));
    }

    // 🛣 ROAD ROUTE + ETA
    private void drawRoute(LatLng origin, LatLng dest) {

        String url = "https://maps.googleapis.com/maps/api/directions/json?"
                + "origin=" + origin.latitude + "," + origin.longitude
                + "&destination=" + dest.latitude + "," + dest.longitude
                + "&key=YOUR_API_KEY";

        new Thread(() -> {
            try {
                HttpURLConnection conn =
                        (HttpURLConnection) new URL(url).openConnection();
                conn.connect();

                Scanner sc = new Scanner(conn.getInputStream());
                StringBuilder sb = new StringBuilder();
                while (sc.hasNext()) sb.append(sc.next());
                sc.close();

                JSONObject json = new JSONObject(sb.toString());
                JSONArray routes = json.getJSONArray("routes");
                if (routes.length() == 0) return;

                JSONObject leg = routes.getJSONObject(0)
                        .getJSONArray("legs").getJSONObject(0);

                String eta = leg.getJSONObject("duration").getString("text");
                String poly = routes.getJSONObject(0)
                        .getJSONObject("overview_polyline")
                        .getString("points");

                List<LatLng> points = PolyUtil.decode(poly);

                runOnUiThread(() -> {
                    map.addPolyline(new PolylineOptions()
                            .addAll(points)
                            .width(10f)
                            .color(0xFF2196F3));

                    tvStatus.setText("🟢 Bus ON THE WAY | ⏱ ETA: " + eta);
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
