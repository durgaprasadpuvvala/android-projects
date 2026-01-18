package com.example.busapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class DriverRegistrationActivity extends AppCompatActivity {

    EditText name, email, password, bus;
    Button register;
    FirebaseAuth auth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_driver_registration);

        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        bus = findViewById(R.id.bus);
        register = findViewById(R.id.register);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        register.setOnClickListener(v -> {

            String sName = name.getText().toString().trim();
            String sEmail = email.getText().toString().trim();
            String sPassword = password.getText().toString().trim();
            String sBus = bus.getText().toString().trim();

            if (sName.isEmpty() || sEmail.isEmpty() || sPassword.isEmpty() || sBus.isEmpty()) {
                Toast.makeText(this, "All fields required", Toast.LENGTH_SHORT).show();
                return;
            }

            auth.createUserWithEmailAndPassword(sEmail, sPassword)
                    .addOnSuccessListener(result -> {

                        String uid = auth.getCurrentUser().getUid();

                        Map<String, Object> map = new HashMap<>();
                        map.put("name", sName);
                        map.put("email", sEmail);
                        map.put("bus", sBus);
                        map.put("role", "driver");

                        db.collection("drivers").document(uid)
                                .set(map)
                                .addOnSuccessListener(unused -> {
                                    Toast.makeText(this, "Driver Registered", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(this, DriverLoginActivity.class));
                                    finish();
                                });
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show()
                    );
        });
    }
}
