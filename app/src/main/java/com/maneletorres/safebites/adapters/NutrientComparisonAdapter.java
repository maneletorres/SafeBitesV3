package com.maneletorres.safebites.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.maneletorres.safebites.R;
import com.maneletorres.safebites.data.NutrientComparison;

import java.util.List;

public class NutrientComparisonAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    /**
     * List of simplified nutrients.
     */
    private final List<NutrientComparison> mSimplifiedNutrients;

    public NutrientComparisonAdapter(List<NutrientComparison> products) {
        mSimplifiedNutrients = products;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SimpleNutrientViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_nutrient_comparison, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
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

    class SimpleNutrientViewHolder extends RecyclerView.ViewHolder {
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