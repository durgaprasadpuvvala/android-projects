package com.example.busapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

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
    }
}
