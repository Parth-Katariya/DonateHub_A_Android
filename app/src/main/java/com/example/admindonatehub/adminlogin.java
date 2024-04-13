package com.example.admindonatehub;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class adminlogin extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private DatabaseReference mDatabase;
    private ProgressBar progressBar;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adminlogin);

        // Initialize Firebase Realtime Database
        mDatabase = FirebaseDatabase.getInstance().getReference();

        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        progressBar = findViewById(R.id.progressBar);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);

        // Check if the user is already logged in
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
        if (isLoggedIn) {
            // If the user is already logged in, redirect to the homepage
            startActivity(new Intent(adminlogin.this, homepage.class));
            finish();
        }
    }

    public void login(View view) {
        final String enteredUsername = usernameEditText.getText().toString();
        final String enteredPassword = passwordEditText.getText().toString();

        // Basic validation: Check for empty fields
        if (enteredUsername.isEmpty() || enteredPassword.isEmpty()) {
            // Display a toast message indicating that fields are empty
            Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
            return; // Exit the method early if validation fails
        }

        // Show the ProgressBar
        progressBar.setVisibility(View.VISIBLE);

        DatabaseReference adminRef = mDatabase.child("registration").child("admin");

        adminRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Hide the ProgressBar when the data is retrieved
                progressBar.setVisibility(View.GONE);

                if (dataSnapshot.exists()) {
                    String savedUsername = dataSnapshot.child("username").getValue(String.class);
                    String savedPassword = dataSnapshot.child("password").getValue(String.class);
                    String savedName = dataSnapshot.child("name").getValue(String.class);

                    if (enteredUsername.equals(savedUsername) && enteredPassword.equals(savedPassword)) {
                        // Username and password match, login successful
                        Intent intent = new Intent(adminlogin.this, viewtransaction.class);
                        Toast.makeText(adminlogin.this, "Welcome " + savedName + "!", Toast.LENGTH_SHORT).show();
                        startActivity(intent);

                        // Set the login state to true in SharedPreferences
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("isLoggedIn", true);
                        editor.apply();

                        finish(); // Finish the login activity to prevent going back
                    } else {
                        // Username or password doesn't match
                        // Handle invalid credentials
                        Toast.makeText(adminlogin.this, "Invalid credentials. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Admin credentials not found in the database
                    // Handle invalid credentials
                    Toast.makeText(adminlogin.this, "Admin credentials not found. Please register.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Hide the ProgressBar when a database error occurs
                progressBar.setVisibility(View.GONE);

                // Handle database errors
                Toast.makeText(adminlogin.this, "Database error occurred. Please try again later.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
