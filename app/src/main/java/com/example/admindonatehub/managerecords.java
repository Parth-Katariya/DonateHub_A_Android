package com.example.admindonatehub;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class managerecords extends AppCompatActivity {
    private DatabaseReference databaseReference;

    DrawerLayout drawerLayout;

    private StorageReference storageReference;
    private String recordKey; // Unique key for the record
    private Uri selectedImageUri; // Store the selected image URI
    private static final int GALLERY_REQ_CODE = 1; // Request code for opening the gallery

    LinearLayout uploadImage;
    ImageView trustImg;

    TextView userNameTextView,userEmailTextView;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_managerecords);




        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("trusts/alltrust");

        // Initialize Firebase Storage reference
        storageReference = FirebaseStorage.getInstance().getReference("trusts/images");

        // Get references to the EditText fields, buttons, and your drawer button (if available)
        EditText titleEditText = findViewById(R.id.title);
        EditText detailsEditText = findViewById(R.id.details);
        Button updateButton = findViewById(R.id.updatebtn);
        Button deleteButton = findViewById(R.id.deletebtn);
        ImageView openDrawerButton = findViewById(R.id.menu); // Adjust as per your layout
        trustImg = findViewById(R.id.img);
        uploadImage = findViewById(R.id.uploadimage);
        drawerLayout = findViewById(R.id.drawerLayout);
        userNameTextView = findViewById(R.id.username);
        userEmailTextView=findViewById(R.id.useremail);




        NavigationDrawerHelper.setupNavigationDrawer(this);

        displayUserInfo();


        // Retrieve the record key from the previous activity
        Intent intent = getIntent();
        if (intent != null) {
            recordKey = intent.getStringExtra("recordKey");
            String trustName = intent.getStringExtra("trustName");
            String trustDetails = intent.getStringExtra("trustDetails");
            String trustImage = intent.getStringExtra("img");

            // Load the existing image from the URL using Glide
            Glide.with(this).load(trustImage).into(trustImg);

            // Set the data to the EditText fields
            titleEditText.setText(trustName);
            detailsEditText.setText(trustDetails);
        }

        // Add a click listener for the "Update" button
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the values from the EditText fields
                String updatedName = titleEditText.getText().toString();
                String updatedDetails = detailsEditText.getText().toString();

                // Update the specific record in the database using the stored recordKey
                DatabaseReference recordToUpdate = databaseReference.child(recordKey);
                recordToUpdate.child("name").setValue(updatedName);
                recordToUpdate.child("dntdetail").setValue(updatedDetails);

                // Check if an image update is needed
                if (selectedImageUri != null) {
                    updateImageInFirebaseStorage();
                } else {
                    // Provide feedback to the user
                    Toast.makeText(getApplicationContext(), "Record updated successfully", Toast.LENGTH_SHORT).show();

                    // After a successful update, navigate back to the homepage
                    Intent intent = new Intent(managerecords.this, homepage.class);
                    startActivity(intent);
                }
            }
        });

//        NavigationDrawerHelper.setupNavigationDrawer(this);


// Add a click listener for the "Delete" button
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Identify the specific record to delete and delete it
                DatabaseReference recordToDelete = databaseReference.child(recordKey); // Use the record key

                // Perform the delete operation
                recordToDelete.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Provide feedback to the user
                        Toast.makeText(getApplicationContext(), "Record deleted successfully", Toast.LENGTH_SHORT).show();

                        // After a successful delete, navigate back to the homepage
                        Intent intent = new Intent(managerecords.this, viewtransaction.class);
                        startActivity(intent);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle any errors
                        Toast.makeText(getApplicationContext(), "Error deleting record: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


        // Add a click listener for the "Upload Image" button
        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the gallery to select a new image for update
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, GALLERY_REQ_CODE);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == GALLERY_REQ_CODE && data != null) {
                selectedImageUri = data.getData();
                trustImg.setImageURI(selectedImageUri);
            }
        }
    }

    private void updateImageInFirebaseStorage() {
        StorageReference imageRef = storageReference.child("trust_images/trust_images" + recordKey + ".jpg");
        UploadTask uploadTask = imageRef.putFile(selectedImageUri);

        uploadTask.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageUrl = uri.toString();

                    // Update the image URL in the Realtime Database
                    databaseReference.child(recordKey).child("imagePath").setValue(imageUrl);

                    // Provide feedback to the user
                    Toast.makeText(managerecords.this, "Record updated successfully", Toast.LENGTH_SHORT).show();
                });
            } else {
                Toast.makeText(managerecords.this, "Image update failed", Toast.LENGTH_SHORT).show();
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