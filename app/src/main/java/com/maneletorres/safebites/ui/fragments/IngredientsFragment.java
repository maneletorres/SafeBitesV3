package com.maneletorres.safebites.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.maneletorres.safebites.R;

import static com.maneletorres.safebites.utils.Utils.INGREDIENTS;

public class IngredientsFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ingredients, container, false);

        TextView ingredientsTextView = view.findViewById(R.id.ingredients_textView);

        Bundle extras = getArguments();
        if (extras != null) {
            String ingredients = extras.getString(INGREDIENTS);
            if (ingredients != null && !ingredients.equals("-")) {
                ingredientsTextView.setText(ingredients);
            }
        }

        return view;
    }
}