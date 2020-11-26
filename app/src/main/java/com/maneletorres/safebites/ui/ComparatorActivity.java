package com.maneletorres.safebites.ui;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.maneletorres.safebites.R;
import com.maneletorres.safebites.data.Product;
import com.maneletorres.safebites.ui.fragments.IngredientsComparisonFragment;
import com.maneletorres.safebites.ui.fragments.NutrientsComparisonFragment;
import com.maneletorres.safebites.ui.fragments.SectionsPageAdapter;

import java.util.Objects;

import static com.maneletorres.safebites.utils.Utils.IMAGE_RESOURCE_A;
import static com.maneletorres.safebites.utils.Utils.IMAGE_RESOURCE_B;
import static com.maneletorres.safebites.utils.Utils.INGREDIENTS_A;
import static com.maneletorres.safebites.utils.Utils.INGREDIENTS_B;
import static com.maneletorres.safebites.utils.Utils.PRODUCT_A;
import static com.maneletorres.safebites.utils.Utils.PRODUCT_B;

public class ComparatorActivity extends AppCompatActivity {
    private Bundle mBundle;
    private String mProductAIngredients;
    private String mProductBIngredients;
    private String mProductAImageResource;
    private String mProductBImageResource;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        mBundle = getIntent().getExtras();
        if (mBundle != null) {
            Product productA = mBundle.getParcelable(PRODUCT_A);
            mProductAIngredients = Objects.requireNonNull(productA).getIngredients();
            mProductAImageResource = productA.getImage_resource();

            Product productB = mBundle.getParcelable(PRODUCT_B);
            mProductBIngredients = Objects.requireNonNull(productB).getIngredients();
            mProductBImageResource = productB.getImage_resource();
        }

        ViewPager mViewPager = findViewById(R.id.viewPagerContainer);
        setupViewPager(mViewPager);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupViewPager(ViewPager viewPager) {
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());

        NutrientsComparisonFragment nutrientsComparison = new NutrientsComparisonFragment();
        nutrientsComparison.setArguments(mBundle);

        Bundle bundle = new Bundle();
        bundle.putString(INGREDIENTS_A, mProductAIngredients);
        bundle.putString(IMAGE_RESOURCE_A, mProductAImageResource);
        bundle.putString(INGREDIENTS_B, mProductBIngredients);
        bundle.putString(IMAGE_RESOURCE_B, mProductBImageResource);

        IngredientsComparisonFragment ingredientsComparison = new IngredientsComparisonFragment();
        ingredientsComparison.setArguments(bundle);

        adapter.addFragment(nutrientsComparison, getString(R.string.nutrients_comparison_fragment));
        adapter.addFragment(ingredientsComparison, getString(R.string.ingredients_comparison_fragment));
        viewPager.setAdapter(adapter);
    }
}