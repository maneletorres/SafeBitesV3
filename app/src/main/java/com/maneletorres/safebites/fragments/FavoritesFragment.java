package com.maneletorres.safebites.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.maneletorres.safebites.R;
import com.maneletorres.safebites.adapters.ProductAdapter;
import com.maneletorres.safebites.entities.Product;

import static com.maneletorres.safebites.utils.Utils.sUser;

public class FavoritesFragment extends Fragment {
    private ProductAdapter mProductAdapter;
    private TextView mEmptyTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);

        // Firebase Realtime Database components initialization:
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users").child(sUser.getUser_id()).child("products");
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Product product = dataSnapshot.getValue(Product.class);
                mProductAdapter.add(product);

                checkProductsNumber();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Product product = dataSnapshot.getValue(Product.class);
                if (product != null) {
                    mProductAdapter.removeItem(product.getUpc());
                }

                checkProductsNumber();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        // Linking the ChildEventListener to the DatabaseReference:
        databaseReference.addChildEventListener(childEventListener);

        // Initialization of the components:
        mEmptyTextView = view.findViewById(R.id.empty_textView);
        RecyclerView favoriteProductsRecyclerView = view.findViewById(R.id.favorite_products_recycler_view);
        mProductAdapter = new ProductAdapter(getContext(), this);
        favoriteProductsRecyclerView.setAdapter(mProductAdapter);

        checkProductsNumber();

        return view;
    }

    // Checking the number of products to know whether to display the 'mEmptyTextView':
    public void checkProductsNumber() {
        if (mProductAdapter.getItemCount() > 0) {
            mEmptyTextView.setVisibility(View.GONE);
        } else {
            mEmptyTextView.setVisibility(View.VISIBLE);
        }
    }
}
