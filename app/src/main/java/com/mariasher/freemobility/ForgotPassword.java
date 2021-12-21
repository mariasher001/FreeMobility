package com.mariasher.freemobility;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.mariasher.freemobility.databinding.ActivityForgotPasswordBinding;

public class ForgotPassword extends AppCompatActivity {

    private ActivityForgotPasswordBinding binding;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
    }

    public void ResetLinkClicked(View view) {
        String resetEmail = binding.ResetEmail.getText().toString();

        if (resetEmail.isEmpty()) {
            binding.ResetEmail.setError("Email-Address is Required!");
            binding.ResetEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(resetEmail).matches()) {
            binding.ResetEmail.setError("Please Enter correct Email-Address");
            binding.ResetEmail.requestFocus();
            return;
        }

        binding.progressBar.setVisibility(View.VISIBLE);
        mAuth.sendPasswordResetEmail(resetEmail).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(ForgotPassword.this, "The Link is successfully sent to the Email", Toast.LENGTH_LONG).show();

                /*
                Edit here to go back to admin portal
                 */
            } else {
                Toast.makeText(ForgotPassword.this, "Sorry, Email not found! Please try again!", Toast.LENGTH_LONG).show();
            }
            binding.progressBar.setVisibility(View.GONE);
        });
    }
}