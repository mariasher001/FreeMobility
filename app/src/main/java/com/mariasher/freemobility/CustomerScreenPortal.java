package com.mariasher.freemobility;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mariasher.freemobility.databinding.ActivityCustomerScreenPortalBinding;

import java.time.LocalTime;

public class CustomerScreenPortal extends AppCompatActivity {
    ActivityCustomerScreenPortalBinding binding;
    FirebaseAuth mAuth;
    FirebaseDatabase mReal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCustomerScreenPortalBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        mReal = FirebaseDatabase.getInstance();
        getDatabase();
        updateYourNumber();

    }

    private void updateYourNumber() {
        binding.progressBar.setVisibility(View.VISIBLE);

        mReal.getReference("CustomerPortalAccess")
                .child("CustomerAccessKey")
                .child("removedNumber")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int removedNumber = snapshot.getValue(Integer.class);
                        int myNum = Integer.parseInt(binding.userQueueNumberTextView.getText().toString());

                        if((removedNumber!= 0) && (removedNumber < myNum)){
                            myNum--;
                        }
                        binding.userQueueNumberTextView.setText(""+myNum);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void getDatabase() {
        binding.progressBar.setVisibility(View.VISIBLE);
        mReal.getReference("CustomerPortalAccess")
                .child("CustomerAccessKey")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        CustomerAccessKey accessKey = snapshot.getValue(CustomerAccessKey.class);
                        if(accessKey!=null){
                            getQueue(accessKey);
                            updateQueue(accessKey);
                        }
                        binding.progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


    private void getQueue(CustomerAccessKey accessKey) {
        mReal.getReference("QueueTable")
                .child(accessKey.adminIdKey)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Queue queue = snapshot.getValue(Queue.class);
                        queue.lastNumber = queue.lastNumber+1;
                        queue.customerLeft = queue.lastNumber-queue.currentNumber;
                        updateScreen(queue);
                        updateDatabase(queue,accessKey);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void updateScreen(Queue queue) {
        binding.userQueueNumberTextView.setText(""+queue.lastNumber);
        long ETAinNano = LocalTime.parse(queue.averageTime).toNanoOfDay() * queue.customerLeft;
        LocalTime ETA = LocalTime.ofNanoOfDay(ETAinNano);
        int hour = ETA.getHour();
        int min = ETA.getMinute();
        int sec = ETA.getSecond();

        binding.currentNumberOnCallTextView.setText(""+queue.currentNumber);
        binding.userQueueNumberTextView.setText(""+queue.lastNumber);
        binding.ETATextView.setText("" + hour + " h, " + min + " m, " + sec + " s");
        sendNotification(queue);
    }

    private void updateDatabase(Queue updatedQueue, CustomerAccessKey accessKey) {
        mReal.getReference("QueueTable")
                .child(accessKey.adminIdKey)
                .setValue(updatedQueue)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(CustomerScreenPortal.this, "You are added to the Queue", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void updateQueue(CustomerAccessKey accessKey) {
        mReal.getReference("QueueTable")
                .child(accessKey.adminIdKey)
                .addValueEventListener(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Queue queue = snapshot.getValue(Queue.class);
                        binding.currentNumberOnCallTextView.setText(""+queue.currentNumber);

                        long ETAinNano = LocalTime.parse(queue.averageTime).toNanoOfDay() * queue.customerLeft;
                        LocalTime ETA = LocalTime.ofNanoOfDay(ETAinNano);
                        int hour = ETA.getHour();
                        int min = ETA.getMinute();
                        int sec = ETA.getSecond();
                        binding.ETATextView.setText("" + hour + " h, " + min + " m, " + sec + " s");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }




    private void sendNotification(Queue queue) {

    }

    public void exitQueueClicked(View view) {

        mReal.getReference("CustomerPortalAccess")
                .child("CustomerAccessKey")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        CustomerAccessKey accessKey = snapshot.getValue(CustomerAccessKey.class);
                        updateLastNumber(accessKey);
                        updateEveryoneWithNewNumber(accessKey);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        Intent intent = new Intent(this, MainActivity2.class);
        startActivity(intent);

    }

    private void updateEveryoneWithNewNumber(CustomerAccessKey accessKey) {
        mReal.getReference("CustomerPortalAccess")
                .child("CustomerAccessKey")
                .child("removedNumber")
                .setValue(Integer.parseInt(binding.userQueueNumberTextView.getText().toString()));

    }

    private void updateLastNumber(CustomerAccessKey accessKey) {
        mReal.getReference("QueueTable")
                .child(accessKey.adminIdKey)
                .child("lastNumber")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int lastNumber = snapshot.getValue(Integer.class);
                        lastNumber--;
                        mReal.getReference("QueueTable")
                                .child(accessKey.adminIdKey)
                                .child("lastNumber")
                                .setValue(lastNumber)
                                .addOnCompleteListener(task -> {
                                    if(task.isSuccessful())
                                        Toast.makeText(CustomerScreenPortal.this, "You have Exited the Queue!", Toast.LENGTH_SHORT).show();
                                });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}
