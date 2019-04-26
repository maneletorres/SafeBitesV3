package com.maneletorres.safebites.fragments;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.maneletorres.safebites.R;
import com.maneletorres.safebites.adapters.NutrientAdapter;
import com.maneletorres.safebites.entities.Product;
import com.maneletorres.safebites.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.maneletorres.safebites.utils.Utils.PRODUCT;

public class NutrientsFragment extends Fragment implements View.OnClickListener {
    // Firebase Realtime Database components:
    private DatabaseReference mProductsDatabaseReference;
    private DatabaseReference mUserDatabaseReference;

    // Components:
    private FloatingActionButton mSaveOrDeleteFAB;
    private Product mProduct;
    private boolean mProductExists;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_nutrients, container, false);

        Bundle extras = getArguments();
        if (extras != null) {
            mProduct = extras.getParcelable(PRODUCT);
            if (mProduct != null) {
                // Initialization of the components:
                ImageView image_nutrients = view.findViewById(R.id.image_view_nutrients_header);
                TextView name = view.findViewById(R.id.product_name_text_view);
                TextView upc = view.findViewById(R.id.product_upc_text_view);
                TextView header_per_serving = view.findViewById(R.id.header_per_serving);
                TextView apt_product = view.findViewById(R.id.apt_product_text_view);
                mSaveOrDeleteFAB = view.findViewById(R.id.save_or_delete_FAB);

                NutrientAdapter nutrientAdapter = new NutrientAdapter(mProduct.getNutrients());
                RecyclerView recyclerView = view.findViewById(R.id.nutrient_recycler);
                recyclerView.setAdapter(nutrientAdapter);

                // FAB OnClickListener:
                mSaveOrDeleteFAB.setOnClickListener(this);

                // Initialization of the Firebase Realtime Database components:
                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

                mUserDatabaseReference = firebaseDatabase.getReference("users").child(Utils.sUser.getUser_id()).child("allergies");
                mUserDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ArrayList<String> productAllergens = mProduct.getAllergens();
                        if(productAllergens != null){
                            HashMap<String, Boolean> userAllergens = (HashMap<String, Boolean>) dataSnapshot.getValue();

                            boolean condition = false;
                            for(Map.Entry<String, Boolean> entry : userAllergens.entrySet()) {
                                String key = entry.getKey();
                                Boolean value = entry.getValue();
                                Log.v("USER ALLERGY", key + " " + value);

                                for(int i = 0; i < productAllergens.size() && !condition; i++){
                                    String allergy = productAllergens.get(i);
                                    Log.v("PRODUCT ALLERGY", allergy);
                                    if(allergy.equals(key) && value){
                                        Log.v("unfit","unfit");
                                        condition = true;
                                        apt_product.setText("Unfit");
                                        apt_product.setBackgroundColor(Color.parseColor("#FF0000"));
                                    } else {
                                        Log.v("suitable","suitable");
                                        apt_product.setText("Suitable");
                                        apt_product.setBackgroundColor(Color.parseColor("#ff99cc00"));
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                mProductsDatabaseReference = firebaseDatabase.getReference("users").child(Utils.sUser.getUser_id()).child("products");
                // Listener that checks if the product exists in the Firebase Realtime Database to
                // modify accordingly 'mSaveOrDeleteFAB':
                mProductsDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        String upc = mProduct.getUpc();
                        if (snapshot.hasChild(upc)) {
                            mSaveOrDeleteFAB.setImageResource(R.drawable.delete);
                            mSaveOrDeleteFAB.setContentDescription("Delete ImageButton");
                            mProductExists = true;
                        } else {
                            mSaveOrDeleteFAB.setImageResource(R.drawable.content_save);
                            mSaveOrDeleteFAB.setContentDescription("Save ImageButton");
                            mProductExists = false;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            // Loading the information in the elements of the layout:
            String image_resource = mProduct.getImage_resource();
            if (image_resource.equals("-")) {
                image_nutrients.setImageResource(R.drawable.no_image_available);
            } else {
                Glide.with(Objects.requireNonNull(getActivity()))
                        .load(image_resource)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                image_nutrients.setImageResource(R.drawable.no_image_available);
                                //mProgressBar.setVisibility(View.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                //mProgressBar.setVisibility(View.GONE);
                                return false;
                            }
                        }).into(image_nutrients);
            }

            name.setText(mProduct.getName());
            upc.setText(mProduct.getUpc());

            String serving_size = mProduct.getServing_size();
            if (serving_size.contains("ml")) {
                header_per_100g.setText("100 ml");
            }

            header_per_serving.setText(portionHeaderFormat(serving_size));
        }

        return view;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.save_or_delete_FAB) {
            if (mProductExists) {
                // Removal of the product from the Firebase Realtime Database:
                mProductsDatabaseReference.child(mProduct.getUpc()).removeValue();

                mSaveOrDeleteFAB.setImageResource(R.drawable.content_save);
                mSaveOrDeleteFAB.setContentDescription("Save ImageButton");
                mProductExists = false;
            } else {
                // Upload of the product to the Firebase Realtime Database:
                mProductsDatabaseReference.child(mProduct.getUpc()).setValue(mProduct);

                mSaveOrDeleteFAB.setImageResource(R.drawable.delete);
                mSaveOrDeleteFAB.setContentDescription("Delete ImageButton");
                mProductExists = true;
            }
        }
    }

    private String portionHeaderFormat(String serving_size) {
        if (serving_size.equals("-")) {
            return "No portion";
        } else {
            String aux = serving_size.trim();
            int delimiterPosition;

            if (serving_size.contains(" ")) {
                return aux;
            } else {
                if (aux.contains("g")) {
                    delimiterPosition = aux.indexOf("g");
                } else if (aux.contains("ml")) {
                    delimiterPosition = aux.indexOf("m");
                } else {
                    return aux;
                }

                return aux.substring(0, delimiterPosition) + " " + aux.substring(delimiterPosition);
            }
        }
    }
}