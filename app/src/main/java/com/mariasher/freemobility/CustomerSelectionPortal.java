package com.mariasher.freemobility;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mariasher.freemobility.databinding.ActivityCustomerSelectionPortalBinding;

import java.time.LocalTime;

public class CustomerSelectionPortal extends AppCompatActivity {

    ActivityCustomerSelectionPortalBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mReal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCustomerSelectionPortalBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        mReal = FirebaseDatabase.getInstance();
        getDatabase();
    }

    private void getDatabase() {
        mReal.getReference("CustomerPortalAccess")
                .child("CustomerAccessKey")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        CustomerAccessKey accessKey = snapshot.getValue(CustomerAccessKey.class);
                        if(accessKey!=null){
                            deciderFunction(accessKey);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void deciderFunction(CustomerAccessKey accessKey) {
        if(accessKey.queueStart == true){
            binding.progressBar.setVisibility(View.VISIBLE);
            mReal.getReference("QueueTable")
                    .child(accessKey.adminIdKey)
                    .addValueEventListener(new ValueEventListener() {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Queue queue = snapshot.getValue(Queue.class);
                            if(queue!=null){
                                setScreenValues(queue);
                                binding.enterQueueButton.setEnabled(true);
                                binding.queueDetailsTextView.setVisibility(View.GONE);
                            }
                            else{
                                binding.enterQueueButton.setEnabled(false);
                                binding.queueDetailsTextView.setVisibility(View.VISIBLE);
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

            binding.progressBar.setVisibility(View.GONE);
        }
        else{
            binding.enterQueueButton.setEnabled(false);
            binding.queueDetailsTextView.setVisibility(View.VISIBLE);
        }


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setScreenValues(Queue queue) {
        binding.currentNumberOnCall.setText("" + queue.currentNumber);
        binding.yourExpectedNumber.setText("" + (queue.lastNumber+1));

        long ETAinNano = LocalTime.parse(queue.averageTime).toNanoOfDay() * queue.customerLeft;
        LocalTime ETA = LocalTime.ofNanoOfDay(ETAinNano);
        int hour = ETA.getHour();
        int min = ETA.getMinute();
        int sec = ETA.getSecond();
        binding.ETA.setText("" + hour + " h, " + min + " m, " + sec + " s");
    }

    public void enterQueueClicked(View view) {
        Intent intent = new Intent(this,CustomerScreenPortal.class);
        startActivity(intent);

    }
}
