package com.maneletorres.safebites;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.maneletorres.safebites.fragments.ComparatorFragment;
import com.maneletorres.safebites.fragments.FavoritesFragment;
import com.maneletorres.safebites.fragments.ScannerFragment;
import com.maneletorres.safebites.fragments.SectionsPageAdapter;
import com.maneletorres.safebites.fragments.SeekerFragment;
import com.maneletorres.safebites.utils.Utils;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout mDrawerLayout;
    private EditText mSearchEditText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialization of the components:
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSearchEditText = findViewById(R.id.search_edit_text);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                Utils.hideSoftKeyboard(MainActivity.this);
            }
        };
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ViewPager viewPager = findViewById(R.id.viewPager);
        setupViewPager(viewPager);

        TabLayout TabLayout = findViewById(R.id.tabs);
        TabLayout.setupWithViewPager(viewPager);
    }

    // Study whether to delete or move this code:
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        // Delete the EditText content?
        mSearchEditText.setVisibility(View.GONE);
        Utils.hideSoftKeyboard(this);

        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.nav_log_out:
                AuthUI.getInstance()
                        .signOut(this)
                        .addOnCompleteListener(task -> {
                            // User is now signed out:
                            Intent intent = new Intent(MainActivity.this, AuthActivity.class);
                            startActivity(intent);
                            finish();
                        });
                break;
            case R.id.nav_settings:
                Intent intent = new Intent(this, AllergiesActivity.class);
                intent.putExtra("CLASS_NAME", "MainActivity");
                startActivity(intent);
                break;
            case R.id.nav_share:
                String message = getResources().getString(R.string.email_content) + "\n" +
                        getResources().getString(R.string.email_url); // App name in Play Store.
                intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, message);
                intent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));
                startActivity(Intent.createChooser(intent, getResources().getString(R.string.chooser_message)));
                break;
            case R.id.nav_information:
                startActivity(new Intent(this, InformationActivity.class));
                break;
        }

        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setupViewPager(ViewPager viewPager) {
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new ScannerFragment(), "Scan");
        adapter.addFragment(new ComparatorFragment(), "Compare");
        adapter.addFragment(new SeekerFragment(), "Search");
        adapter.addFragment(new FavoritesFragment(), "Favorites");
        viewPager.setAdapter(adapter);
    }
}