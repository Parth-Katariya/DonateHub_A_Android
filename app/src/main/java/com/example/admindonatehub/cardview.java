package com.example.admindonatehub;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class cardview extends AppCompatActivity {
    ImageView img1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cardview);
        img1 = findViewById(R.id.img1);

        String imagePath = "https://tse4.mm.bing.net/th?id=OIP.lsMLah0x-8f5--qR6QyKigHaHa&pid=Api&P=0&h=180";

        Picasso.get().load(imagePath).into(img1);
    }
}