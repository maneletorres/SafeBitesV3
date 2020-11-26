package com.maneletorres.safebites.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.tabs.TabLayout;
import com.maneletorres.safebites.R;
import com.maneletorres.safebites.data.Product;

import java.util.Objects;

import static com.maneletorres.safebites.utils.Utils.INGREDIENTS;
import static com.maneletorres.safebites.utils.Utils.PRODUCT;

public class CompleteProductFragment extends Fragment {
    private Fragment mFragment;
    private FragmentManager mFragmentManager;
    private Product mProduct;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_complete_product, container,
                false);

        Bundle extras = getArguments();
        if (extras != null) {
            // Initialization of the components:
            mProduct = extras.getParcelable(PRODUCT);
            mFragmentManager = this.getChildFragmentManager();

            // Configuration of the fragment that will load initially:
            NutrientsFragment nutrientsFragment = new NutrientsFragment();
            nutrientsFragment.setArguments(extras);
            mFragment = nutrientsFragment;

            fragmentLoad();

            TabLayout tabLayout = view.findViewById(R.id.tabLayout);
            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    Bundle bundle = new Bundle();

                    switch (tab.getPosition()) {
                        case 0:
                            bundle.putParcelable(PRODUCT, mProduct);
                            mFragment = new NutrientsFragment();
                            break;
                        case 1:
                            bundle.putString(INGREDIENTS, mProduct.getIngredients());
                            mFragment = new IngredientsFragment();
                            break;
                    }
                    mFragment.setArguments(bundle);

                    fragmentLoad();
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });
        }

        return view;
    }

    private void fragmentLoad() {
        Objects.requireNonNull(mFragmentManager)
                .beginTransaction()
                .replace(R.id.complete_product_frame_layout, mFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }
}