package com.maneletorres.safebites.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.maneletorres.safebites.R;

import java.util.Objects;

import static com.maneletorres.safebites.utils.Utils.IMAGE_RESOURCE_A;
import static com.maneletorres.safebites.utils.Utils.INGREDIENTS_A;
import static com.maneletorres.safebites.utils.Utils.INGREDIENTS_B;

public class IngredientsComparisonFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ingredients_comparison, container, false);

        Bundle extras = getArguments();
        if (extras != null) {
            String productAIngredients = extras.getString(INGREDIENTS_A);
            String productBIngredients = extras.getString(INGREDIENTS_B);
            String productAImageResource = extras.getString(IMAGE_RESOURCE_A);
            String productBImageResource = extras.getString(IMAGE_RESOURCE_A);

            ImageView productAImageView = view.findViewById(R.id.product_A_image);
            Glide.with(Objects.requireNonNull(getActivity()))
                    .load(productAImageResource)
                    .into(productAImageView);

            ImageView productBImageView = view.findViewById(R.id.product_B_image);
            Glide.with(Objects.requireNonNull(getActivity()))
                    .load(productBImageResource)
                    .into(productBImageView);

            TextView ingredientsATextView = view.findViewById(R.id.ingredients_A_textView);
            ingredientsATextView.setText(productAIngredients);

            TextView ingredientsBTextView = view.findViewById(R.id.ingredients_B_textView);
            ingredientsBTextView.setText(productBIngredients);
        }

        return view;
    }
}
