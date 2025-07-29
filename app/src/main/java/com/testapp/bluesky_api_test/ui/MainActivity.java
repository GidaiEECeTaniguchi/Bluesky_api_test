package com.testapp.bluesky_api_test.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.testapp.bluesky_api_test.R;

public class MainActivity extends AppCompatActivity {

    private Button buttonToGroup;
    private Button buttonToPost;
    private Button buttonToTags;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        BottomNavigationView navView = findViewById(R.id.nav_view);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_notifications, R.id.navigation_profile)
                .build();

        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_home);
        NavController navController = navHostFragment.getNavController();

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        buttonToGroup = findViewById(R.id.button_to_group);
        buttonToPost = findViewById(R.id.button_to_post);
        buttonToTags = findViewById(R.id.button_to_tags);

        buttonToGroup.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, GroupActivity.class);
            startActivity(intent);
        });

        buttonToPost.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PostViewActivity.class);
            startActivity(intent);
        });

        buttonToTags.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TagViewActivity.class);
            startActivity(intent);
        });

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.navigation_home) {
                buttonToGroup.setVisibility(View.VISIBLE);
                buttonToPost.setVisibility(View.VISIBLE);
                buttonToTags.setVisibility(View.VISIBLE);
            } else {
                buttonToGroup.setVisibility(View.GONE);
                buttonToPost.setVisibility(View.GONE);
                buttonToTags.setVisibility(View.GONE);
            }
        });
    }
}
