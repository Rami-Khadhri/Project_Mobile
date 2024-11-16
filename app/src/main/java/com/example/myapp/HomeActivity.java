package com.example.myapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.myapp.databinding.ActivityHomeBinding;
import com.example.myapp.fragments.CoursesFragment;
import com.example.myapp.fragments.ProfileFragment;
import com.example.myapp.fragments.TasksFragment;

public class HomeActivity extends AppCompatActivity {
    private ActivityHomeBinding binding;
    private int userId;
    private String username, email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Get user data from intent
        Intent intent = getIntent();
        userId = intent.getIntExtra("userId", -1);
        username = intent.getStringExtra("username");
        email = intent.getStringExtra("email");
        password = intent.getStringExtra("password");

        // Set up the toolbar (AppBar)
        setSupportActionBar(binding.toolbar);

        // Load default fragment
       // loadFragment(new ProfileFragment(userId, username, email, password));

        // Set up bottom navigation
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment fragment;
            if (item.getItemId() == R.id.nav_profile) {
                fragment = new ProfileFragment(userId, username, email, password);
            } else if (item.getItemId() == R.id.nav_courses) {
                fragment = new CoursesFragment();
            } else if (item.getItemId() == R.id.nav_tasks) {
                fragment = new TasksFragment();
            } else {
                return false;
            }
            loadFragment(fragment);
            return true;
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu for the toolbar
        getMenuInflater().inflate(R.menu.courses_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle menu item selections using if-else
        int itemId = item.getItemId();
        if (itemId == R.id.action_add) {
            // Add Course action
            // Add your logic here
            return true;
        } else if (itemId == R.id.action_search) {
            // Search action
            // Add your logic here
            return true;
        } else if (itemId == R.id.action_list_all) {
            // List All action
            // Add your logic here
            return true;
        } else if (itemId == R.id.action_clear_all) {
            // Clear All action
            // Add your logic here
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}
