package com.example.admindonatehub;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class usertransactions extends AppCompatActivity {

    private RecyclerView userDetailsRecyclerView;
    private UserDetailsAdapter adapter;
    private List<UserDetail> userDetailList;

    TextView userNameTextView, userEmailTextView, totalcollection;

    String trtName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usertransactions);

        userDetailList = new ArrayList<>();
        userDetailsRecyclerView = findViewById(R.id.userDetailsRecyclerView);
        userDetailsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        userNameTextView = findViewById(R.id.username);
        userEmailTextView = findViewById(R.id.useremail);
        totalcollection = findViewById(R.id.totalcollection);

        // Pass the context when creating the adapter
        adapter = new UserDetailsAdapter(this, userDetailList);
        userDetailsRecyclerView.setAdapter(adapter);

        trtName = getIntent().getStringExtra("trustName");

        displayUserInfo();

        NavigationDrawerHelper.setupNavigationDrawer(this);

        // Initialize Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference userRef = database.getReference("registration/users");
        DatabaseReference traRef = database.getReference("payments/transaction/SuccessTransaction");

        traRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot transactionSnapshot) {
                double totalAmount = 0.0;

                for (DataSnapshot transaction : transactionSnapshot.getChildren()) {
                    String amountString = transaction.child("amount").getValue(String.class);
                    String trustName = transaction.child("trustname").getValue(String.class);

                    if (trustName != null && trustName.equals(trtName)) {
                        double amount = Double.parseDouble(amountString.replace("₹", ""));
                        totalAmount += amount;
                    }
                }

                DecimalFormat decimalFormat = new DecimalFormat("₹#,##0.00");
                totalcollection.setText(decimalFormat.format(totalAmount));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("FirebaseError", "Firebase Database Error: " + databaseError.getMessage());
            }
        });

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot userSnapshot) {
                userDetailList.clear(); // Clear the list to avoid duplicates
                for (DataSnapshot user : userSnapshot.getChildren()) {
                    String name = user.child("name").getValue(String.class);
                    String email = user.child("email").getValue(String.class);
                    String image = user.child("profileImageUrl").getValue(String.class);
                    String userUID = user.getKey();

                    // Check if the user has any transactions for the selected trust
                    checkDonationStatus(traRef, name, userUID, email, image);
                }
                adapter.notifyDataSetChanged(); // Notify the adapter of the data change
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("FirebaseError", "Firebase Database Error: " + databaseError.getMessage());
            }
        });
    }

    private void checkDonationStatus(DatabaseReference traRef, String userName, String userUID, String email, String image) {
        traRef.orderByChild("username").equalTo(userName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot transactionSnapshot) {
                if (transactionSnapshot.exists()) {
                    for (DataSnapshot transaction : transactionSnapshot.getChildren()) {
                        String amountString = transaction.child("amount").getValue(String.class);
                        String trustName = transaction.child("trustname").getValue(String.class);

                        if (trustName != null && trustName.equals(trtName)) {
                            double amount = Double.parseDouble(amountString.replace("₹", ""));
                            UserDetail userDetail = new UserDetail(userName, email, amount, image);
                            userDetailList.add(userDetail);
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("FirebaseError", "Firebase Database Error: " + databaseError.getMessage());
            }
        });
    }

    private void displayUserInfo() {
        String userId = "admin";

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("registration").child(userId);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String userName = dataSnapshot.child("name").getValue(String.class);
                    String userEmail = dataSnapshot.child("email").getValue(String.class);

                    userNameTextView.setText(userName);
                    userEmailTextView.setText(userEmail);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors here
            }
        });
    }
}
