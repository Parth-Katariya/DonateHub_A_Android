package com.example.admindonatehub;

import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class NavigationDrawerHelper {
    public static void setupNavigationDrawer(final AppCompatActivity activity) {
        final DrawerLayout drawerLayout = activity.findViewById(R.id.drawerLayout);
        ImageView menu = activity.findViewById(R.id.menu);
        LinearLayout managerecord = activity.findViewById(R.id.managerecord);
        LinearLayout viewtransaction = activity.findViewById(R.id.viewtransaction);
        LinearLayout logout = activity.findViewById(R.id.logout);

        // Add click listeners for menu items
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDrawer(drawerLayout);
            }
        });

        managerecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, homepage.class);
                activity.startActivity(intent);
            }
        });

        viewtransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, viewtransaction.class);
                activity.startActivity(intent);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Clear user data, such as username or user ID.
                // You can use SharedPreferences or any other storage mechanism.
                // For example, if you stored the user ID in SharedPreferences:
                SharedPreferences preferences = activity.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                preferences.edit().clear().apply();

                // Navigate to the login page
                Intent intent = new Intent(activity, adminlogin.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                activity.startActivity(intent);
            }
        });
    }

    public static void openDrawer(DrawerLayout drawerLayout) {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    public static void closeDrawer(DrawerLayout drawerLayout) {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }
}
