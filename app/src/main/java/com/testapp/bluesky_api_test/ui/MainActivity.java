package com.testapp.bluesky_api_test.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;


import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.testapp.bluesky_api_test.R;
import com.testapp.bluesky_api_test.repository.AuthRepository;
import com.testapp.bluesky_api_test.repository.AuthorRepository;
import com.testapp.bluesky_api_test.repository.PostRepository;
import com.testapp.bluesky_api_test.viewmodel.MainViewModel;
import com.testapp.bluesky_api_test.viewmodel.ViewModelFactory;

public class MainActivity extends AppCompatActivity {

    private MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AuthRepository authRepository = new AuthRepository(getApplication());
        AuthorRepository authorRepository = new AuthorRepository(getApplication());
        PostRepository postRepository = new PostRepository(getApplication());
        ViewModelFactory factory = new ViewModelFactory(authRepository, authorRepository, postRepository);
        viewModel = new ViewModelProvider(this, factory).get(MainViewModel.class);


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

        Button buttonToGroup = findViewById(R.id.button_to_group);
        buttonToGroup.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, GroupActivity.class);
            startActivity(intent);
        });

        // タイムラインの取得を開始
        viewModel.fetchTimeline();
    }
}
