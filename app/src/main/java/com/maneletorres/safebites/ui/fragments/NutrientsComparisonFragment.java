package com.maneletorres.safebites.ui.fragments;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.maneletorres.safebites.R;
import com.maneletorres.safebites.adapters.NutrientComparisonAdapter;
import com.maneletorres.safebites.data.Nutrient;
import com.maneletorres.safebites.data.NutrientComparison;
import com.maneletorres.safebites.data.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.maneletorres.safebites.utils.Utils.PRODUCT_A;
import static com.maneletorres.safebites.utils.Utils.PRODUCT_B;

public class NutrientsComparisonFragment extends Fragment {
    private List<NutrientComparison> createSimplifiedNutrientObjects(
            List<Nutrient> productANutrients, List<Nutrient> productBNutrients) {
        List<NutrientComparison> simplifiedNutrients = new ArrayList<>();

        List<Nutrient> unitedNutrients;
        if (productANutrients != null) {
            unitedNutrients = new ArrayList<>(productANutrients);
        } else {
            unitedNutrients = new ArrayList<>();
        }

        if (productBNutrients != null) {
            unitedNutrients.addAll(productBNutrients);
        }

        for (int i = 0; i < unitedNutrients.size(); i++) {
            Nutrient firstNutrient = unitedNutrients.get(i);

            if (!exists(simplifiedNutrients, firstNutrient.getName())) {
                NutrientComparison simplifiedNutrient = null;

                for (int j = i + 1; j < unitedNutrients.size(); j++) {
                    Nutrient secondNutrient = unitedNutrients.get(j);
                    if (firstNutrient.getName().equals(secondNutrient.getName())) {
                        simplifiedNutrient = new NutrientComparison(firstNutrient.getName(),
                                firstNutrient.getPer_100g(), secondNutrient.getPer_100g(),
                                firstNutrient.getUnit());
                        simplifiedNutrients.add(simplifiedNutrient);
                    }
                }

                if (simplifiedNutrient == null) {
                    if (productANutrients != null && i < productANutrients.size()) {
                        simplifiedNutrient = new NutrientComparison(firstNutrient.getName(),
                                firstNutrient.getPer_100g(), "-", firstNutrient
                                .getUnit());
                    } else {
                        simplifiedNutrient = new NutrientComparison(firstNutrient.getName(),
                                "-", firstNutrient.getPer_100g(), firstNutrient
                                .getUnit());
                    }
                    simplifiedNutrients.add(simplifiedNutrient);
                }
            }
        }

        return simplifiedNutrients;
    }

    private boolean exists(List<NutrientComparison> simplifiedNutrients, String nutrientName) {
        boolean condition = false;
        for (int i = 0; i < simplifiedNutrients.size() && !condition; i++) {
            NutrientComparison currentSimplifiedNutrient = simplifiedNutrients.get(i);
            if (currentSimplifiedNutrient.getName().equals(nutrientName)) {
                condition = true;
            }
        }
        return condition;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_nutrients_comparison, container,
                false);

        Bundle extras = getArguments();
        if (extras != null) {
            Product mProductA = extras.getParcelable(PRODUCT_A);
            Product mProductB = extras.getParcelable(PRODUCT_B);

            // Initialization of the components:
            formatProductImage(view.findViewById(R.id.product_A_image),
                    Objects.requireNonNull(mProductA).getImage_resource());
            formatProductName(view.findViewById(R.id.product_A_name), mProductA.getName());

            formatProductImage(view.findViewById(R.id.product_B_image),
                    Objects.requireNonNull(mProductB).getImage_resource());
            formatProductName(view.findViewById(R.id.product_B_name), mProductB.getName());

            List<NutrientComparison> simplifiedNutrients = createSimplifiedNutrientObjects(mProductA
                    .getNutrients(), mProductB.getNutrients());
            NutrientComparisonAdapter mSimplifiedNutrientAdapter =
                    new NutrientComparisonAdapter(simplifiedNutrients);
            RecyclerView recyclerView = view.findViewById(R.id.simplified_nutrients_recycler);
            recyclerView.setAdapter(mSimplifiedNutrientAdapter);
        }

        return view;
    }

    private void formatProductImage(ImageView productImageView, String productImageResource) {
        if (productImageResource.equals("-")) {
            productImageView.setImageResource(R.drawable.no_image_available);
        } else {
            Glide.with(getActivity())
                    .load(productImageResource)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                    Target<Drawable> target,
                                                    boolean isFirstResource) {
                            productImageView.setImageResource(R.drawable.no_image_available);
                            //mProductViewHolder.mProgressBar.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model,
                                                       Target<Drawable> target,
                                                       DataSource dataSource,
                                                       boolean isFirstResource) {
                            //mProductViewHolder.mProgressBar.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(productImageView);
        }
    }

    private void formatProductName(TextView productTextView, String productName) {
        if (productName.length() > 15) {
            productTextView.setText(productName.substring(0, 15));
        } else {
            productTextView.setText(productName);
        }
    }
}