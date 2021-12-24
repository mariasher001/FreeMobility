package com.mariasher.freemobility;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mariasher.freemobility.databinding.ActivityAfterAdminLoginBinding;

import java.time.LocalTime;

public class AfterAdminLogin extends AppCompatActivity {

    private ActivityAfterAdminLoginBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mReal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAfterAdminLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        mReal = FirebaseDatabase.getInstance();

        mReal.getReference("QueueTable")
                .child(mAuth.getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Queue queue = snapshot.getValue(Queue.class);
                        if(queue!=null){
                            updateScreen();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.after_admin_login_options_menu, menu);
        return true;
    }


    public void profileOptionClicked(@NonNull MenuItem item) {
        Intent intent = new Intent(this, AdminProfile.class);
        startActivity(intent);
    }

    public void logoutOptionClicked(@NonNull MenuItem item) {
        mAuth.signOut();
        Intent intent = new Intent(this, AdminPortal.class);
        startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void startButtonClicked(View view) {

        customerKeyGeneration(true);

        binding.progressBar.setVisibility(View.VISIBLE);

        String timeOfStart = LocalTime.now().toString();
        long t = LocalTime.now().minusNanos(LocalTime.parse(timeOfStart).toNanoOfDay()).toNanoOfDay();
        String avgTime = LocalTime.ofNanoOfDay(t).toString();

        Queue queue = new Queue(0, 2, 2, avgTime, timeOfStart, timeOfStart);
        mReal.getReference("QueueTable")
                .child(mAuth.getCurrentUser().getUid())
                .setValue(queue)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(AfterAdminLogin.this, "Queue Successfully Created!", Toast.LENGTH_LONG).show();
                            updateScreen();
                        } else {
                            Toast.makeText(AfterAdminLogin.this, "Unsuccessful!Please Try again!", Toast.LENGTH_LONG).show();
                        }
                        binding.progressBar.setVisibility(View.GONE);
                    }
                });
    }

    private void customerKeyGeneration(boolean queueStart) {
        String AdminID = mAuth.getCurrentUser().getUid();

        binding.progressBar.setVisibility(View.VISIBLE);
        CustomerAccessKey accessKey = new CustomerAccessKey(queueStart,AdminID,0);
        mReal.getReference("CustomerPortalAccess")
                .child("CustomerAccessKey")
                .setValue(accessKey)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(AfterAdminLogin.this,"Customer Key Set!", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(AfterAdminLogin.this,"Can't create customer key!",Toast.LENGTH_SHORT).show();
                        }
                        binding.progressBar.setVisibility(View.GONE);
                    }
                });
    }

    private void updateScreen() {
        mReal.getReference("QueueTable")
                .child(mAuth.getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Queue queue = snapshot.getValue(Queue.class);
                        if (queue != null) {
                            binding.currentNumberData.setText("" + queue.currentNumber);
                            binding.lastNumberData.setText("" + queue.lastNumber);
                            mReal.getReference("QueueTable")
                                    .child(mAuth.getCurrentUser().getUid())
                                    .child("lastNumber")
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            int lastNum = snapshot.getValue(Integer.class);
                                            binding.customersLeftData.setText("" + (lastNum-queue.currentNumber));
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });


                            LocalTime avgTime = LocalTime.parse(queue.averageTime);
                            int hour = avgTime.getHour();
                            int min = avgTime.getMinute();
                            int sec = avgTime.getSecond();
                            binding.averageTimeData.setText("" + hour + " h, " + min + " m, " + sec + " s");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public void nextButtonClicked(View view) {
        binding.progressBar.setVisibility(View.VISIBLE);

        mReal.getReference("QueueTable")
                .child(mAuth.getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Queue queue = snapshot.getValue(Queue.class);
                        if (queue != null) {
                            updateQueue(queue);
                        }
                        binding.progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void updateQueue(Queue queue) {
        if (queue.currentNumber < queue.lastNumber) {
            queue.currentNumber++;
        } else {
            Toast.makeText(this, "This was the last customer!", Toast.LENGTH_LONG).show();
            return;
        }

        queue.customerLeft = queue.lastNumber - queue.currentNumber;

        queue.timeOfNext = LocalTime.now().toString();

        //queue.timeOfNext-queue.timeOfStart/queue.currentNumber;

        long avgNano = LocalTime.parse(queue.timeOfNext).minusNanos(LocalTime.parse(queue.timeOfStart).toNanoOfDay()).toNanoOfDay();
        if (queue.currentNumber != 0) {
            avgNano = avgNano / queue.currentNumber;
        }
        queue.averageTime = LocalTime.ofNanoOfDay(avgNano).toString();

        mReal.getReference("QueueTable")
                .child(mAuth.getCurrentUser().getUid())
                .setValue(queue)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(AfterAdminLogin.this, "Queue is updated", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(AfterAdminLogin.this, "Can't update, PLease try again!", Toast.LENGTH_LONG).show();
                        }
                    }
                });

        updateScreen();

    }

    public void pauseButtonClicked(View view) {
        //Stop assigning Numbers
        mReal.getReference("CustomerPortalAccess")
                .child("CustomerAccessKey")
                .child("queueStart")
                .setValue(false).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(AfterAdminLogin.this,"Customer Entry is Paused",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void resetButtonClicked(View view) {
        String timeOfStart = LocalTime.now().toString();
        long t = LocalTime.now().minusNanos(LocalTime.parse(timeOfStart).toNanoOfDay()).toNanoOfDay();
        String avgTime = LocalTime.ofNanoOfDay(t).toString();

        Queue queue = new Queue(0,0,0,avgTime,timeOfStart,timeOfStart);
        binding.progressBar.setVisibility(View.VISIBLE);
        mReal.getReference("QueueTable")
                .child(mAuth.getCurrentUser().getUid())
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            resetDisplay();
                            Toast.makeText(AfterAdminLogin.this, "Reset Success!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(AfterAdminLogin.this, "Can't reset, Please Try again!", Toast.LENGTH_SHORT).show();
                        }
                        binding.progressBar.setVisibility(View.GONE);
                    }
                });

        pauseButtonClicked(view);
    }

    private void resetDisplay() {
        binding.currentNumberData.setText("0");
        binding.lastNumberData.setText("0");
        binding.customersLeftData.setText("0");
        binding.averageTimeData.setText("0 h, 0 m, 0 s");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity2.class);
        startActivity(intent);

    }

}