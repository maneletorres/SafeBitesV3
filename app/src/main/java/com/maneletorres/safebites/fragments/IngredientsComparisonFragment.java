package com.maneletorres.safebites.fragments;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.maneletorres.safebites.R;

import java.util.Objects;

import static com.maneletorres.safebites.utils.Utils.IMAGE_RESOURCE_A;
import static com.maneletorres.safebites.utils.Utils.IMAGE_RESOURCE_B;
import static com.maneletorres.safebites.utils.Utils.INGREDIENTS_A;
import static com.maneletorres.safebites.utils.Utils.INGREDIENTS_B;

public class IngredientsComparisonFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ingredients_comparison, container,
                false);

        Bundle extras = getArguments();
        if (extras != null) {
            formatProductImage(view.findViewById(R.id.product_A_image),
                    Objects.requireNonNull(extras.getString(IMAGE_RESOURCE_A)));
            formatProductIngredients(Objects.requireNonNull(extras.getString(INGREDIENTS_A)),
                    view.findViewById(R.id.ingredients_A_textView));

            formatProductImage(view.findViewById(R.id.product_B_image),
                    Objects.requireNonNull(extras.getString(IMAGE_RESOURCE_B)));
            formatProductIngredients(Objects.requireNonNull(extras.getString(INGREDIENTS_B)),
                    view.findViewById(R.id.ingredients_B_textView));
        }

        return view;
    }

    private void formatProductImage(ImageView productImageView, String productImageResource) {
        if (productImageResource.equals("-")) {
            productImageView.setImageResource(R.drawable.no_image_available);
        } else {
            Glide.with(Objects.requireNonNull(getActivity()))
                    .load(productImageResource)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                    Target<Drawable> target,
                                                    boolean isFirstResource) {
                            productImageView.setImageResource(R.drawable.no_image_available);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model,
                                                       Target<Drawable> target,
                                                       DataSource dataSource,
                                                       boolean isFirstResource) {
                            return false;
                        }
                    })
                    .into(productImageView);
        }
    }

    private void formatProductIngredients(String ingredientsText, TextView ingredientsTextView) {
        if (ingredientsText.equals("-")) {
            ingredientsTextView.setText(getString(R.string.no_registered_ingredients));
        } else {
            ingredientsTextView.setText(ingredientsText);
        }
    }
}