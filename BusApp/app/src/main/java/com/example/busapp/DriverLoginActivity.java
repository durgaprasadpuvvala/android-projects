package com.example.busapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

public class DriverLoginActivity extends AppCompatActivity {

    EditText email, password;
    Button login;
    TextView register;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_driver_login);

        email = findViewById(R.id.etEmail);
        password = findViewById(R.id.etPassword);
        login = findViewById(R.id.btnLogin);
        register = findViewById(R.id.tvRegister);

        auth = FirebaseAuth.getInstance();

        login.setOnClickListener(v ->
                auth.signInWithEmailAndPassword(
                        email.getText().toString(),
                        password.getText().toString()
                ).addOnSuccessListener(res -> {
                    startActivity(new Intent(this, DriverDashboardActivity.class));
                    finish();
                }).addOnFailureListener(e ->
                        Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show())
        );

        register.setOnClickListener(v ->
                startActivity(new Intent(this, DriverRegistrationActivity.class)));
    }
}
