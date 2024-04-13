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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class adddata extends AppCompatActivity {

    private final int GALLERY_REQ_CODE = 1000;
    DrawerLayout drawerLayout;
    ImageView menu;
    ProgressBar progressBar;
    LinearLayout managerecord, viewtransaction, logout, uploadimage;
    TextView userNameTextView, userEmailTextView;
    EditText titleText, detailText;
    Button addBtn;
    ImageView uploadedImage;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adddata);

//        drawerLayout = findViewById(R.id.drawerLayout);
//        menu = findViewById(R.id.menu);
//        managerecord = findViewById(R.id.managerecord);
//        logout = findViewById(R.id.logout);
//        viewtransaction = findViewById(R.id.viewtransaction);
        uploadimage = findViewById(R.id.uploadimage);
        titleText = findViewById(R.id.title);
        detailText = findViewById(R.id.details);
        addBtn = findViewById(R.id.addbtn);
        userNameTextView = findViewById(R.id.username);
        userEmailTextView = findViewById(R.id.useremail);
        uploadedImage = findViewById(R.id.img);
        progressBar = findViewById(R.id.progressBar);
        progressBar.bringToFront();

        displayUserInfo();

        // Initialize Firebase references
        databaseReference = FirebaseDatabase.getInstance().getReference("trusts/alltrust");
        storageReference = FirebaseStorage.getInstance().getReference("trust_images");

        uploadimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, GALLERY_REQ_CODE);
            }
        });

        NavigationDrawerHelper.setupNavigationDrawer(this);


        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);

                String newRecordKey = databaseReference.push().getKey();
                String title = titleText.getText().toString().trim();
                String detail = detailText.getText().toString().trim();

                // Validate the fields
                if (title.isEmpty()) {
                    titleText.setError("Title is required");
                    titleText.requestFocus();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                if (detail.isEmpty()) {
                    detailText.setError("Detail is required");
                    detailText.requestFocus();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                if (selectedImageUri != null) {
                    // Continue with image upload and data addition
                    StorageReference imageRef = storageReference.child("trust_images/" + System.currentTimeMillis() + ".jpg");
                    UploadTask uploadTask = imageRef.putFile(selectedImageUri);

                    uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String imageUrl = uri.toString();

                                        Model data = new Model(imageUrl, title, detail,newRecordKey);

                                        databaseReference.child(newRecordKey).setValue(data);

                                        titleText.setText("");
                                        detailText.setText("");
                                        uploadedImage.setImageResource(R.drawable.ic_launcher_foreground);
                                        progressBar.setVisibility(View.GONE);


                                        Toast.makeText(adddata.this, "Data added successfully", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(adddata.this,homepage.class);
                                        startActivity(intent);
                                    }
                                });
                            } else {
                                Toast.makeText(adddata.this, "Image upload failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(adddata.this, "Please select an image", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == GALLERY_REQ_CODE) {
                selectedImageUri = data.getData();
                uploadedImage.setImageURI(selectedImageUri);
            }
        }
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
