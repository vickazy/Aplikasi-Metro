package com.jojo.metroapp.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.jojo.metroapp.R;
import com.jojo.metroapp.fragment.HomeFragment;
import com.jojo.metroapp.fragment.LogoutFragment;
import com.jojo.metroapp.fragment.ProfileFragment;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private HomeFragment homeFragment;
    private ProfileFragment profileFragment;
    private LogoutFragment logoutFragment;
    private int fragmentPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        setFragment();
    }

    private void setFragment() {
        homeFragment = new HomeFragment();
        profileFragment = new ProfileFragment();
        logoutFragment = new LogoutFragment();
        addFragmentToRoot(homeFragment);
        addFragmentToRoot(profileFragment);
        addFragmentToRoot(logoutFragment);
        hideFragment(profileFragment);
        hideFragment(logoutFragment);
        setTitle("Home");
    }

    private void addFragmentToRoot(android.support.v4.app.Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.layout_dasar_untuk_fragment, fragment)
                .commit();
    }

    private void showFragment(android.support.v4.app.Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .show(fragment)
                .commit();
    }

    private void hideFragment(android.support.v4.app.Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .hide(fragment)
                .commit();
    }

    private void setActivityTitle(String title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_home && fragmentPosition != 0) {
            setActivityTitle("Home");
            showFragment(homeFragment);
            hideFragment(profileFragment);
            hideFragment(logoutFragment);
            fragmentPosition = 0;
        } else if (id == R.id.nav_profile && fragmentPosition != 1) {
            setActivityTitle("Profile");
            showFragment(profileFragment);
            hideFragment(homeFragment);
            hideFragment(logoutFragment);
            fragmentPosition = 1;
        } else if (id == R.id.nav_logout && fragmentPosition != 2) {
            setActivityTitle("Log Out");
            showFragment(logoutFragment);
            hideFragment(homeFragment);
            hideFragment(profileFragment);
            fragmentPosition = 2;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
