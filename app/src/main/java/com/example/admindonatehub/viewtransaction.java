package com.example.admindonatehub;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;

public class viewtransaction extends AppCompatActivity {
    DrawerLayout drawerLayout;
    ImageView menu;
    LinearLayout managerecord, viewtransaction, logout;
    RecyclerView recyclerView;
    ArrayList<Model> arrayList = new ArrayList<>();
    ImageButton btn;
    DatabaseReference databaseReference;
    EditText searchEditText;
    TextView userNameTextView, userEmailTextView, totalcollection, totalhelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewtransaction);

        // Initialize views
        drawerLayout = findViewById(R.id.drawerLayout);
        menu = findViewById(R.id.menu);
        managerecord = findViewById(R.id.managerecord);
        logout = findViewById(R.id.logout);
        viewtransaction = findViewById(R.id.viewtransaction);
        btn = findViewById(R.id.btn1);
        userNameTextView = findViewById(R.id.username);
        userEmailTextView = findViewById(R.id.useremail);
        totalcollection = findViewById(R.id.totalcollection);
        totalhelper = findViewById(R.id.totalhelper);
        recyclerView = findViewById(R.id.rcylview);

        // Fetch and display user information
        displayUserInfo();
        displayAmount();
        countUsersWithSuccessTransactions();


        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerView,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Model selectedModel = arrayList.get(position);

                        Log.d("TAG", String.valueOf(selectedModel));
                        // Create an intent to start the TrustDetailsActivity
                        Intent intent = new Intent(viewtransaction.this, usertransactions.class);

                        // Pass the data to display in TrustDetailsActivity
                        intent.putExtra("recordKey",selectedModel.getRecordKey());
                        intent.putExtra("trustName", selectedModel.getName());
                        intent.putExtra("trustDetails", selectedModel.getDntdetail());
                        intent.putExtra("img",selectedModel.getImagePath());

                        startActivity(intent);
                    }
                })
        );


        // Initialize Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("trusts/alltrust");

        fetchDataFromFirebase();

        // Initialize search EditText
        searchEditText = findViewById(R.id.searchEditText);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not used
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Filter the list based on the user's input
                filterList(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Not used
            }
        });

        NavigationDrawerHelper.setupNavigationDrawer(this);

    }

    public static void openDrawer(DrawerLayout drawerLayout) {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    private void closeDrawer(DrawerLayout drawerLayout) {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    private void fetchDataFromFirebase() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrayList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String recordKey = snapshot.child("recordKey").getValue(String.class);
                    String name = snapshot.child("name").getValue(String.class);
                    String detail = snapshot.child("dntdetail").getValue(String.class);
                    String imagePath = snapshot.child("imagePath").getValue(String.class);

                    Model dataModel = new Model(imagePath, name, detail,recordKey);
                    arrayList.add(dataModel);
                }

                // Create an adapter with the retrieved data
                ModelRecyclerViewAdapter adapter = new ModelRecyclerViewAdapter(com.example.admindonatehub.viewtransaction.this, arrayList);
                recyclerView.setAdapter(adapter);

                // Set an item click listener for the RecyclerView

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors here
            }
        });
    }

    private void filterList(String searchText) {
        ArrayList<Model> filteredList = new ArrayList<>();

        for (Model model : arrayList) {
            if (model.getName().toLowerCase().startsWith(searchText.toLowerCase())) {
                filteredList.add(model);
            }
        }

        ModelRecyclerViewAdapter adapter = new ModelRecyclerViewAdapter(com.example.admindonatehub.viewtransaction.this, filteredList);
        recyclerView.setAdapter(adapter);
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

    private void displayAmount() {
        DatabaseReference transactionsRef = FirebaseDatabase.getInstance().getReference("payments/transaction/SuccessTransaction");

        transactionsRef.addChildEventListener(new ChildEventListener() {
            double totalAmount = 0.0;

            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String amountString = dataSnapshot.child("amount").getValue(String.class);

                if (amountString != null) {
                    String numericAmountString = amountString.replaceAll("[^\\d.]", "");

                    try {
                        double amount = Double.parseDouble(numericAmountString);
                        totalAmount += amount;
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }

                String totalAmountString = String.format("₹%.2f", totalAmount);
                totalcollection.setText(totalAmountString);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                totalAmount = 0;
                totalcollection.setText("");
                String amountString = dataSnapshot.child("amount").getValue(String.class);

                if (amountString != null) {
                    String numericAmountString = amountString.replaceAll("[^\\d.]", "");

                    try {
                        double oldAmount = Double.parseDouble(numericAmountString);
                        totalAmount -= oldAmount;

                        double newAmount = Double.parseDouble(amountString);
                        totalAmount += newAmount;
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }

                String totalAmountString = String.format("₹%.2f", totalAmount);
                totalcollection.setText(totalAmountString);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                String amountString = dataSnapshot.child("amount").getValue(String.class);

                if (amountString != null) {
                    String numericAmountString = amountString.replaceAll("[^\\d.]", "");

                    try {
                        double amount = Double.parseDouble(numericAmountString);
                        totalAmount -= amount;
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }

                String totalAmountString = String.format("₹%.2f", totalAmount);
                totalcollection.setText(totalAmountString);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                // Handle if needed
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors here
                Log.e("FirebaseError", "Firebase Database Error: " + databaseError.getMessage());
                Toast.makeText(com.example.admindonatehub.viewtransaction.this, "Error fetching amount from database: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void countUsersWithSuccessTransactions() {
        DatabaseReference transactionsRef = FirebaseDatabase.getInstance().getReference("payments/transaction/SuccessTransaction");

        transactionsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashSet<String> uniqueEmails = new HashSet<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String email = snapshot.child("userEmail").getValue(String.class);

                    if (email != null) {
                        uniqueEmails.add(email);
                    }
                }

                int userCount = uniqueEmails.size();
                totalhelper.setText(String.valueOf(userCount));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FirebaseError", "Firebase Database Error: " + databaseError.getMessage());
                Toast.makeText(com.example.admindonatehub.viewtransaction.this, "Error counting users from the database: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        closeDrawer(drawerLayout);
    }
}