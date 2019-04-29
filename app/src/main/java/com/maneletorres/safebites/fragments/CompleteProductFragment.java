package com.maneletorres.safebites.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.maneletorres.safebites.R;
import com.maneletorres.safebites.entities.Product;

import java.util.Objects;

import static com.maneletorres.safebites.utils.Utils.INGREDIENTS;
import static com.maneletorres.safebites.utils.Utils.PRODUCT;

public class CompleteProductFragment extends Fragment {
    private Fragment mFragment;
    private FragmentManager mFragmentManager;
    private Product mProduct;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_complete_product, container, false);

        Bundle extras = getArguments();
        if (extras != null) {
            // Initialization of the components:
            mProduct = extras.getParcelable(PRODUCT);
            mFragmentManager = this.getFragmentManager();

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

    public void fragmentLoad(){
        Objects.requireNonNull(mFragmentManager)
                .beginTransaction()
                .replace(R.id.frameLayout, mFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }
}