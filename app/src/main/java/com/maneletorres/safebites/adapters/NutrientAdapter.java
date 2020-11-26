package com.maneletorres.safebites.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.maneletorres.safebites.R;
import com.maneletorres.safebites.data.Nutrient;

import java.util.List;

public class NutrientAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final List<Nutrient> mNutrients;

    public NutrientAdapter(List<Nutrient> nutrients) {
        mNutrients = nutrients;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NutrientViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_nutrient, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
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

    class NutrientViewHolder extends RecyclerView.ViewHolder {
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