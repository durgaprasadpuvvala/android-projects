package com.example.busapp;

import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.google.firebase.database.*;

public class StudentDashboardActivity extends FragmentActivity
        implements OnMapReadyCallback {

    GoogleMap map;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_student_dashboard);

        SupportMapFragment frag =
                (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);

        frag.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        FirebaseDatabase.getInstance()
                .getReference("busLocation/BUS_101")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot s) {
                        if (!s.exists()) return;

                        double lat = s.child("latitude").getValue(Double.class);
                        double lng = s.child("longitude").getValue(Double.class);

                        LatLng bus = new LatLng(lat, lng);
                        map.clear();
                        map.addMarker(new MarkerOptions().position(bus).title("Bus Location"));
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(bus, 15));
                    }

                    @Override public void onCancelled(DatabaseError e) {}
                });
    }
}
