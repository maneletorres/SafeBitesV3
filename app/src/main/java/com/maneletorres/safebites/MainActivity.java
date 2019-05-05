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
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.maneletorres.safebites.fragments.CompareFragment;
import com.maneletorres.safebites.fragments.FavoritesFragment;
import com.maneletorres.safebites.fragments.ScanFragment;
import com.maneletorres.safebites.fragments.SearchFragment;
import com.maneletorres.safebites.fragments.SectionsPageAdapter;
import com.maneletorres.safebites.utils.Utils;

import static com.maneletorres.safebites.utils.Utils.CLASS_NAME;
import static com.maneletorres.safebites.utils.Utils.TOAST_MESSAGE;
import static com.maneletorres.safebites.utils.Utils.sCompareFragment;
import static com.maneletorres.safebites.utils.Utils.sFavoriteFragment;
import static com.maneletorres.safebites.utils.Utils.sUser;
import static com.maneletorres.safebites.utils.Utils.staticListenerLoad;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout mDrawerLayout;
    private EditText mSearchEditText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // Load of static Listener on the FRDB:
        //if (sProduct == null){
        if (sUser.getProducts() == null) {
            staticListenerLoad();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            switch (extras.getInt(TOAST_MESSAGE)) {
                case 0:
                    Toast.makeText(this, "User " + sUser.getDisplayName() + " has been registered in SafeBites!", Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    Toast.makeText(this, "Welcome back to SafeBites " + sUser.getDisplayName() + "!", Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    Toast.makeText(this, "Changes in the user's allergens have been saved.", Toast.LENGTH_SHORT).show();
                    break;
            }
        }

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

        View headerView = navigationView.getHeaderView(1);
        TextView displayNameTextView = headerView.findViewById(R.id.displayNameTextView);
        displayNameTextView.setText(sUser.getDisplayName());

        TextView emailTextView = headerView.findViewById(R.id.emailTextView);
        emailTextView.setText(sUser.getEmail());

        ViewPager viewPager = findViewById(R.id.viewPager);
        setupViewPager(viewPager);

        TabLayout TabLayout = findViewById(R.id.tabs);
        TabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Hide keyboard when navigating between fragments:
        mSearchEditText.setVisibility(View.GONE);
        Utils.hideSoftKeyboard(this);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                startActivity(new Intent(this, MainActivity.class));
                finish();
                break;
            case R.id.nav_log_out:
                AuthUI.getInstance()
                        .signOut(this)
                        .addOnCompleteListener(task -> {
                            // User is now signed out:
                            Intent intent = new Intent(MainActivity.this, AuthActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                    | Intent.FLAG_ACTIVITY_CLEAR_TOP
                                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        });
                break;
            case R.id.nav_settings:
                Intent intent = new Intent(this, PreferenceActivity.class);
                intent.putExtra(CLASS_NAME, "MainActivity");
                startActivity(intent);
                break;
            case R.id.nav_share:
                String message = getResources().getString(R.string.accompanying_message) + "\n" +
                        getResources().getString(R.string.github_url); // App name in Play Store.
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
        adapter.addFragment(new ScanFragment(), "Scan");
        adapter.addFragment(new SearchFragment(), "Search");
        sCompareFragment = new CompareFragment();
        adapter.addFragment(sCompareFragment, "Compare");
        sFavoriteFragment = new FavoritesFragment();
        adapter.addFragment(sFavoriteFragment, "Favorites");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {

    }

    public interface MyInterface {
        void updateProducts();
    }
}