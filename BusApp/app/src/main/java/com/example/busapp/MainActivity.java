package com.example.busapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    Button btnDriver, btnStudent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnDriver = findViewById(R.id.btnDriver);
        btnStudent = findViewById(R.id.btnStudent);

        btnDriver.setOnClickListener(v ->
                startActivity(new Intent(this, DriverLoginActivity.class)));

        btnStudent.setOnClickListener(v ->
                startActivity(new Intent(this, StudentLoginActivity.class)));

        FirebaseAuth auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            // You can check Firestore role here
            startActivity(new Intent(this, StudentDashboardActivity.class));
            finish();
        }

        TextView tvDevelopedBy = findViewById(R.id.tvDevelopedBy);
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        tvDevelopedBy.startAnimation(fadeIn);


    }
}
