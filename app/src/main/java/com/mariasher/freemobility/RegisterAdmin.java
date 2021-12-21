package com.mariasher.freemobility;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.mariasher.freemobility.databinding.ActivityRegisterAdminBinding;

public class RegisterAdmin extends AppCompatActivity {


    private ActivityRegisterAdminBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mReal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        mReal = FirebaseDatabase.getInstance();
    }

    public void registerButtonClicked(View view) {
        String fullName = binding.fullName.getText().toString();
        String email = binding.emailAddress.getText().toString();
        String password = binding.password.getText().toString();

        if (fullName.isEmpty()) {
            binding.fullName.setError("Full-Name is Required!");
            binding.fullName.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            binding.emailAddress.setError("Email-Address is Required!");
            binding.emailAddress.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailAddress.setError("Please Enter correct Email-Address");
            binding.emailAddress.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            binding.password.setError("Password is Required!");
            binding.password.requestFocus();
            return;
        }

        if (password.length() < 6) {
            binding.password.setError("Password should be min 6 characters");
            binding.password.requestFocus();
            return;
        }

        //register in database
        binding.progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Admin admin = new Admin(fullName, email);
                            realTime(admin);
                            Intent intent = new Intent(RegisterAdmin.this, AdminPortal.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(RegisterAdmin.this, "Failed to Register,Try Again!!", Toast.LENGTH_SHORT).show();
                            binding.progressBar.setVisibility(View.GONE);
                        }
                    }
                });

    }

    private void realTime(Admin admin) {
        mReal.getReference("Admins")      //Table-Name
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())   //Primary key from Authentication Database as RowNumber
                .setValue(admin)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(RegisterAdmin.this, "Admin has been Registered Successfully :)", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(RegisterAdmin.this, "Failed to Register!!Try Again!!", Toast.LENGTH_SHORT).show();
                    }
                    binding.progressBar.setVisibility(View.GONE);
                });
    }
}