package com.example.bottomtest;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity {
    String mainselected = "1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);

        Intent intent = getIntent();
        mainselected = intent.getStringExtra("mainselected");



        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        if (mainselected != null) {
            switch (mainselected) {
                case "1":
                    navView.setSelectedItemId(R.id.navigation_home);
                case "2":
                    navView.setSelectedItemId(R.id.navigation_dashboard);
                case "3":
                    navView.setSelectedItemId(R.id.navigation_notifications);
            }
        }
        NavigationUI.setupWithNavController(navView, navController);


    }

}
