package com.mariasher.freemobility;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.mariasher.freemobility.databinding.ActivityAfterAdminLoginBinding;

public class AfterAdminLogin extends AppCompatActivity {

    private ActivityAfterAdminLoginBinding binding;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAfterAdminLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.after_admin_login_options_menu,menu);
        return true;
    }


    public void profileOptionClicked(@NonNull MenuItem item){
        Intent intent = new Intent(this, AdminProfile.class);
        startActivity(intent);
    }

    public void logoutOptionClicked(@NonNull MenuItem item){
        mAuth.signOut();
        Intent intent = new Intent(this, AdminPortal.class);
        startActivity(intent);
    }

    public void startButtonClicked(View view) {
    }

    public void nextButtonClicked(View view) {
    }

    public void pauseButtonClicked(View view) {
    }

    public void resetButtonClicked(View view) {
    }


}