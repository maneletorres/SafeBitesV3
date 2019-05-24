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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.maneletorres.safebites.R;
import com.maneletorres.safebites.adapters.NutrientAdapter;
import com.maneletorres.safebites.entities.Product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.maneletorres.safebites.utils.Utils.PRODUCT;

public class NutrientsFragment extends Fragment implements View.OnClickListener {
    // FRDB variables:
    private DatabaseReference mUserDBRef;
    private DatabaseReference mUserFavoritesDBRef;
    private ValueEventListener mUserAllergiesValueEventListener;
    private ValueEventListener mUserProductsValueEventListener;

    // Other variables:
    private TextView apt_product;
    private String mUid;
    private FloatingActionButton mSaveOrDeleteFAB;
    private Product mProduct;
    private boolean mProductExists;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_nutrients, container, false);

        Bundle extras = getArguments();
        if (extras != null) {
            mProduct = extras.getParcelable(PRODUCT);

            // Initialization of the components:
            mUid = FirebaseAuth.getInstance().getUid();
            mUserDBRef = FirebaseDatabase.getInstance()
                    .getReference(getString(R.string.users)).child(mUid);
            mUserFavoritesDBRef = FirebaseDatabase.getInstance()
                    .getReference().child(getString(R.string.productsUser)).child(mUid);

            ImageView image_nutrients = view.findViewById(R.id.image_view_nutrients_header);
            TextView name = view.findViewById(R.id.product_name_text_view);
            TextView upc = view.findViewById(R.id.product_upc_text_view);
            apt_product = view.findViewById(R.id.apt_product_text_view);
            TextView header_per_100g = view.findViewById(R.id.header_per_100g);
            TextView header_per_serving = view.findViewById(R.id.header_per_serving);
            mSaveOrDeleteFAB = view.findViewById(R.id.save_or_delete_FAB);
            mSaveOrDeleteFAB.setOnClickListener(this);

            // Loading of nutrients:
            NutrientAdapter nutrientAdapter = new NutrientAdapter(mProduct.getNutrients());
            RecyclerView recyclerView = view.findViewById(R.id.nutrient_recycler);
            recyclerView.setAdapter(nutrientAdapter);

            // Loading the information in the elements of the layout:
            String image_resource = mProduct.getImage_resource();
            if (image_resource.equals("-")) {
                image_nutrients.setImageResource(R.drawable.no_image_available);
            } else {
                Glide.with(Objects.requireNonNull(getActivity()))
                        .load(image_resource)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                        Target<Drawable> target,
                                                        boolean isFirstResource) {
                                image_nutrients.setImageResource(R.drawable.no_image_available);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model,
                                                           Target<Drawable> target,
                                                           DataSource dataSource,
                                                           boolean isFirstResource) {
                                return false;
                            }
                        }).into(image_nutrients);
            }

            name.setText(mProduct.getName());
            upc.setText(mProduct.getUpc());

            String serving_size = mProduct.getServing_size();
            if (serving_size.contains("ml")) {
                header_per_100g.setText(getString(R.string.standard_size_in_millimeters));
            }

            header_per_serving.setText(portionHeaderFormat(serving_size));
        }

        return view;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.save_or_delete_FAB) {
            if (mProductExists) {
                // Removal of the product from the FRDB:

                // 'favorites':
                DatabaseReference favoritesDatabaseReference = FirebaseDatabase.getInstance()
                        .getReference().child(getString(R.string.productsUser)).child(mUid).child(mProduct.getUpc());
                favoritesDatabaseReference.removeValue();

                // 'products':
                DatabaseReference productsDatabaseReference = FirebaseDatabase.getInstance()
                        .getReference().child(getString(R.string.usersProduct)).child(mProduct.getUpc());
                productsDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getChildrenCount() == 0) {
                            FirebaseDatabase.getInstance().getReference().child(getString(R.string.products))
                                    .child(mProduct.getUpc()).removeValue();
                            productsDatabaseReference.removeEventListener(this);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                // 'tests':
                DatabaseReference testsDatabaseReference = FirebaseDatabase.getInstance()
                        .getReference().child(getString(R.string.usersProduct)).child(mProduct.getUpc()).child(mUid);
                testsDatabaseReference.removeValue();

                mSaveOrDeleteFAB.setImageResource(R.drawable.content_save);
                mSaveOrDeleteFAB.setContentDescription(getString(R.string.save_data_button_description));
                mProductExists = false;
            } else {
                // Upload of the product to the FRDB:

                // 'favorites':
                Map<String, Object> favorites = new HashMap<>();
                favorites.put(mProduct.getUpc(), true);

                DatabaseReference favoritesDatabaseReference = FirebaseDatabase.getInstance()
                        .getReference().child(getString(R.string.productsUser)).child(mUid);
                favoritesDatabaseReference.updateChildren(favorites);

                // 'products':
                Map<String, Object> products = new HashMap<>();
                products.put(mProduct.getUpc(), mProduct);

                DatabaseReference productsDatabaseReference = FirebaseDatabase.getInstance()
                        .getReference().child(getString(R.string.products));
                productsDatabaseReference.updateChildren(products);

                // 'tests':
                Map<String, Object> tests = new HashMap<>();
                tests.put(mUid, true);

                DatabaseReference dr = FirebaseDatabase.getInstance().getReference().child(getString(R.string.usersProduct));
                dr.child(mProduct.getUpc()).updateChildren(tests);

                mSaveOrDeleteFAB.setImageResource(R.drawable.delete);
                mSaveOrDeleteFAB.setContentDescription(getString(R.string.delete_data_button_description));
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

    @Override
    public void onPause() {
        super.onPause();

        detachDatabaseReadListener();
    }

    @Override
    public void onResume() {
        super.onResume();

        attachDatabaseReadListener();
    }

    private void attachDatabaseReadListener() {
        if (mUserAllergiesValueEventListener == null) {
            mUserAllergiesValueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    ArrayList<String> productAllergens = mProduct.getAllergens();
                    if (productAllergens != null) {
                        HashMap<String, Boolean> userAllergens = (HashMap<String, Boolean>)
                                dataSnapshot.getValue();

                        boolean condition = false;
                        for (Map.Entry<String, Boolean> entry : userAllergens.entrySet()) {
                            if (!condition) {
                                String key = entry.getKey();
                                Boolean value = entry.getValue();

                                for (int i = 0; i < productAllergens.size() && !condition; i++) {
                                    String allergy = productAllergens.get(i);
                                    if (allergy.equals(key) && value) {
                                        condition = true;
                                    }
                                }
                            } else {
                                break;
                            }
                        }

                        if (condition) {
                            apt_product.setText(getString(R.string.unfit_product));
                            apt_product.setBackgroundColor(Color.parseColor("#FF0000"));
                        } else {
                            apt_product.setText(getString(R.string.suitable_product));
                            apt_product.setBackgroundColor(Color.parseColor("#ff99cc00"));
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };
            mUserDBRef.child(getString(R.string.allergies)).addListenerForSingleValueEvent(mUserAllergiesValueEventListener);
        }

        if (mUserProductsValueEventListener == null) {
            mUserProductsValueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String upc = mProduct.getUpc();
                    if (snapshot.hasChild(upc)) {
                        mSaveOrDeleteFAB.setImageResource(R.drawable.delete);
                        mSaveOrDeleteFAB.setContentDescription("Floating delete data button");
                        mProductExists = true;
                    } else {
                        mSaveOrDeleteFAB.setImageResource(R.drawable.content_save);
                        mSaveOrDeleteFAB.setContentDescription("Floating save data button");
                        mProductExists = false;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };
            mUserFavoritesDBRef.addListenerForSingleValueEvent(mUserProductsValueEventListener);
        }
    }

    private void detachDatabaseReadListener() {
        if (mUserAllergiesValueEventListener != null) {
            mUserDBRef.removeEventListener(mUserAllergiesValueEventListener);
            mUserAllergiesValueEventListener = null;
        }

        if (mUserProductsValueEventListener != null) {
            mUserFavoritesDBRef.removeEventListener(mUserProductsValueEventListener);
            mUserProductsValueEventListener = null;
        }
    }
}