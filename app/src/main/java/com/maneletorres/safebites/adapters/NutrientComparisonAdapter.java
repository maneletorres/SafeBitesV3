package com.maneletorres.safebites.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.maneletorres.safebites.R;
import com.maneletorres.safebites.entities.NutrientComparison;

import java.util.List;

public class NutrientComparisonAdapter extends Adapter<ViewHolder> {
    /**
     * List of simplified nutrients.
     */
    private final List<NutrientComparison> mSimplifiedNutrients;

    public NutrientComparisonAdapter(List<NutrientComparison> products) {
        mSimplifiedNutrients = products;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SimpleNutrientViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_nutrient_comparison, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SimpleNutrientViewHolder simpleNutrientViewHolder = (SimpleNutrientViewHolder) holder;
        NutrientComparison currentSimplifiedNutrient = mSimplifiedNutrients.get(position);

        simpleNutrientViewHolder.itemView.setTag(currentSimplifiedNutrient);
        simpleNutrientViewHolder.mName.setText(currentSimplifiedNutrient.getName());

        String productAQuantity = currentSimplifiedNutrient.getQuantityA();
        if (productAQuantity.equals("-")) {
            simpleNutrientViewHolder.mProductANutrient.setText("-");

        } else {
            simpleNutrientViewHolder.mProductANutrient
                    .setText(productAQuantity.concat(" " + currentSimplifiedNutrient.getUnit()));
        }

        String productBQuantity = currentSimplifiedNutrient.getQuantityB();
        if (productBQuantity.equals("-")) {
            simpleNutrientViewHolder.mProductBNutrient.setText("-");
        } else {
            simpleNutrientViewHolder.mProductBNutrient.setText(productBQuantity
                    .concat(" " + currentSimplifiedNutrient.getUnit()));
        }
    }

    @Override
    public int getItemCount() {
        return mSimplifiedNutrients == null ? 0 : mSimplifiedNutrients.size();
    }

    class SimpleNutrientViewHolder extends ViewHolder {
        final private TextView mName;
        final private TextView mProductANutrient;
        final private TextView mProductBNutrient;

        SimpleNutrientViewHolder(View itemView) {
            super(itemView);

            mName = itemView.findViewById(R.id.text_name);
            mProductANutrient = itemView.findViewById(R.id.text_product_A);
            mProductBNutrient = itemView.findViewById(R.id.text_product_B);
        }
    }
}