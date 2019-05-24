package com.maneletorres.safebites;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.maneletorres.safebites.entities.Product;
import com.maneletorres.safebites.fragments.IngredientsFragment;
import com.maneletorres.safebites.fragments.NutrientsFragment;
import com.maneletorres.safebites.fragments.SectionsPageAdapter;

import java.util.Objects;

import static com.maneletorres.safebites.utils.Utils.INGREDIENTS;
import static com.maneletorres.safebites.utils.Utils.PRODUCT;
import static com.maneletorres.safebites.utils.Utils.TWO_PANE;

public class ProductActivity extends AppCompatActivity {
    private Bundle mBundle;
    private Product mProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        mBundle = getIntent().getExtras();
        if (mBundle != null) {
            boolean twoPane = mBundle.getBoolean(TWO_PANE);
            mProduct = mBundle.getParcelable(PRODUCT);

            if (!twoPane) {
                ViewPager mViewPager = findViewById(R.id.viewPagerContainer);
                setupViewPager(mViewPager);

                TabLayout tabLayout = findViewById(R.id.tabs);
                tabLayout.setupWithViewPager(mViewPager);
            } else {
                Bundle bundle1 = new Bundle();
                bundle1.putParcelable(PRODUCT, mProduct);

                NutrientsFragment nutrientsFragments = new NutrientsFragment();
                nutrientsFragments.setArguments(mBundle);

                Bundle bundle2 = new Bundle();
                bundle2.putString(INGREDIENTS, mProduct.getIngredients());
                IngredientsFragment ingredientsFragment = new IngredientsFragment();
                ingredientsFragment.setArguments(bundle2);

                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.product_frame_layout_1, nutrientsFragments)
                        .add(R.id.product_frame_layout_2, ingredientsFragment)
                        .commit();
            }
        }
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

        NutrientsFragment nutrientsFragments = new NutrientsFragment();
        nutrientsFragments.setArguments(mBundle);

        Bundle bundle = new Bundle();
        bundle.putString(INGREDIENTS, mProduct.getIngredients());
        IngredientsFragment ingredientsFragment = new IngredientsFragment();
        ingredientsFragment.setArguments(bundle);

        adapter.addFragment(nutrientsFragments, getString(R.string.nutrients_fragment));
        adapter.addFragment(ingredientsFragment, getString(R.string.ingredients_fragment));
        viewPager.setAdapter(adapter);
    }
}