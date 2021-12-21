package com.mariasher.freemobility;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mariasher.freemobility.databinding.ActivityAdminProfileBinding;

public class AdminProfile extends AppCompatActivity {

    private ActivityAdminProfileBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mReal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        mReal = FirebaseDatabase.getInstance();

        mReal.getReference("Admins").child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Admin admin = snapshot.getValue(Admin.class);
                if(admin != null){
                    binding.fullNameTextView.setText(admin.fullName);
                    binding.emailTextView.setText(admin.email);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminProfile.this, "Something went wrong! Please try again!", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.admin_profile_options_menu,menu);
        return true;
    }

    public void logoutOptionClicked(@NonNull MenuItem item){
        mAuth.signOut();
        Intent intent = new Intent(this, AdminPortal.class);
        startActivity(intent);
    }
}