package com.maneletorres.safebites.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.maneletorres.safebites.R;
import com.maneletorres.safebites.entities.Nutrient;

import java.util.List;

public class NutrientAdapter extends Adapter<ViewHolder> {
    private final List<Nutrient> mNutrients;

    public NutrientAdapter(List<Nutrient> nutrients) {
        mNutrients = nutrients;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NutrientViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_nutrient, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NutrientViewHolder nutrientViewHolder = (NutrientViewHolder) holder;
        Nutrient currentNutrient = mNutrients.get(position);

        nutrientViewHolder.itemView.setTag(currentNutrient);
        nutrientViewHolder.mName.setText(currentNutrient.getName());

        String unit = currentNutrient.getUnit();
        nutrientViewHolder.mPer100g.setText(currentNutrient.getPer_100g().concat(" " + unit));

        String per_serving = currentNutrient.getPer_serving();
        if (per_serving.equals("-")) {
            nutrientViewHolder.mPerServing.setText(per_serving);
        } else {
            nutrientViewHolder.mPerServing.setText(per_serving.concat(" " + unit));
        }
    }

    @Override
    public int getItemCount() {
        return mNutrients == null ? 0 : mNutrients.size();
    }

    class NutrientViewHolder extends ViewHolder {
        private final TextView mName;
        private final TextView mPer100g;
        private final TextView mPerServing;

        NutrientViewHolder(View itemView) {
            super(itemView);

            mName = itemView.findViewById(R.id.text_name);
            mPer100g = itemView.findViewById(R.id.text_per_100g);
            mPerServing = itemView.findViewById(R.id.text_per_portion);
        }
    }
}