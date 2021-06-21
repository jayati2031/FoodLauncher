package com.launcher.foodlauncher;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.launcher.foodlauncher.ui.favourites.FavouritesFragment;
import com.launcher.foodlauncher.ui.find_food.FindFoodFragment;
import com.launcher.foodlauncher.ui.home.HomeFragment;
import com.launcher.foodlauncher.ui.user.UserFragment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Toolbar toolbar;
    private HomeFragment homeFragment;
    private FindFoodFragment findFoodFragment;
    private FavouritesFragment favouritesFragment;
    private UserFragment userFragment;
    private String tagHome = "HomeFragmentTag", tagFindFood = "FindFoodFragmentTag",
            tagFavourite = "FavouritesFragmentTag", tagUser = "UserFragmentTag";

    private Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);

        mAuth = FirebaseAuth.getInstance();

        if (savedInstanceState != null) { // saved instance state, fragment may exist
            // look up the instance that already exists by tag
            homeFragment = (HomeFragment)
                    getSupportFragmentManager().findFragmentByTag(tagHome);
            Log.i("TAG", "onCreate: homeFragmentInstance: " + homeFragment.getTag());
            findFoodFragment = (FindFoodFragment)
                    getSupportFragmentManager().findFragmentByTag(tagFindFood);
            favouritesFragment = (FavouritesFragment)
                    getSupportFragmentManager().findFragmentByTag(tagFavourite);
            userFragment = (UserFragment)
                    getSupportFragmentManager().findFragmentByTag(tagUser);
        } else if (homeFragment == null || findFoodFragment == null || favouritesFragment == null || userFragment == null) {
            // only create fragment if they haven't been instantiated already
            if (homeFragment == null) {
                homeFragment = new HomeFragment();
            }
            if (findFoodFragment == null) {
                findFoodFragment = new FindFoodFragment();
            }
            if (favouritesFragment == null) {
                favouritesFragment = new FavouritesFragment();
            }
            if (userFragment == null) {
                userFragment = new UserFragment();
            }
        }
        init();

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
//        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
//                R.id.navigation_home, R.id.navigation_find_food, R.id.navigation_favourites, R.id.navigation_user)
//                .build();
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
//        NavigationUI.setupWithNavController(navView, navController);

        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                switch(itemId) {
                    case R.id.navigation_home: {
                        loadFragment(homeFragment, "MainActivity", tagHome);
                        return true;
                    }
                    case R.id.navigation_find_food: {
                        loadFragment(findFoodFragment, "main", tagFindFood);
                        return true;
                    }
                    case R.id.navigation_favourites: {
                        loadFragment(favouritesFragment, "MainActivity", tagFavourite);
                        return true;
                    }
                    case R.id.navigation_user: {
                        loadFragment(userFragment, "MainActivity", tagUser);
                        return true;
                    }
                }
                return false;
            }
        });
    }

    private void init() {
        loadFragment(homeFragment, "Main", tagHome);
    }

    private void loadFragment(Fragment fragment, String comeFrom, String tag) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        Log.i("MainActivity", "loadFragment: Fragment created: " + tag);
        currentFragment = fragment;
        bundle.putString("comefrom", comeFrom);
        fragment.setArguments(bundle);
        transaction.replace(R.id.nav_host_fragment, fragment, tag);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //Save the fragment's instance
        getSupportFragmentManager().putFragment(outState, "CurrentFragment", currentFragment);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null){
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }
    }

}
