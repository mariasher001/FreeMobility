package com.mariasher.freemobility;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.mariasher.freemobility.databinding.ActivityMain2Binding;

public class MainActivity2 extends AppCompatActivity {

    private ActivityMain2Binding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMain2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    public void adminPortalClicked(View view) {
        Intent intent = new Intent(this, AdminPortal.class);
        startActivity(intent);
    }

    public void customerPortalClicked(View view) {
        //Intent intent = new Intent(this, CustomerPortal.class);
        //startActivity(intent);
    }
}