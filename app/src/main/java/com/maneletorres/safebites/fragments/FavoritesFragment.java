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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.maneletorres.safebites.R;
import com.maneletorres.safebites.adapters.ProductAdapter;
import com.maneletorres.safebites.entities.Product;

import java.util.Objects;

import static com.maneletorres.safebites.utils.Utils.PRODUCT;

public class FavoritesFragment extends Fragment {
    // FRDB variables:
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mFavoritesDatabaseReference;
    private ChildEventListener mChildEventListener;

    // Other variables:
    private boolean mTwoPane;
    private RecyclerView mFavoriteProductsRecyclerView;
    private ProductAdapter mProductAdapter;
    private TextView mEmptyTextView;
    private View mView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);

        // Master-detail configuration:
        mView = view.findViewById(R.id.favorites_frame_layout);
        if (mView != null) {
            mTwoPane = true;
        }

        // Initialization of the FRDB components:
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mFavoritesDatabaseReference = FirebaseDatabase.getInstance().getReference("favorites")
                .child(mFirebaseUser.getUid());

        // Initialization of the components:
        mEmptyTextView = view.findViewById(R.id.empty_textView);
        mFavoriteProductsRecyclerView = view.findViewById(R.id.favorite_products_recycler_view);

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

    private void prepareProductsLoading() {
        checkProductsNumber();

        if (mTwoPane) {
            if (mProductAdapter.getItemCount() > 0) {
                Bundle arguments = new Bundle();
                arguments.putParcelable(PRODUCT, mProductAdapter.getItem(0));

                CompleteProductFragment completeProductFragment = new CompleteProductFragment();
                completeProductFragment.setArguments(arguments);

                Objects.requireNonNull(this.getChildFragmentManager())
                        .beginTransaction()
                        .replace(R.id.favorites_frame_layout, completeProductFragment)
                        .commitAllowingStateLoss();
            } else {
                mView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        mProductAdapter = new ProductAdapter(getContext(), this, mTwoPane);
        mFavoriteProductsRecyclerView.setAdapter(mProductAdapter);

        checkProductsNumber();
        attachDatabaseReadListener();
    }

    @Override
    public void onPause() {
        super.onPause();

        detachDatabaseReadListener();
    }

    private void attachDatabaseReadListener() {
        if (mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    String product_upc = dataSnapshot.getKey();
                    if (product_upc != null) {
                        DatabaseReference productDatabaseReference = FirebaseDatabase.getInstance()
                                .getReference("products").child(product_upc);
                        productDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                mProductAdapter.add(dataSnapshot.getValue(Product.class));
                                mProductAdapter.notifyDataSetChanged();

                                prepareProductsLoading();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                    String product_upc = dataSnapshot.getKey();
                    if (product_upc != null) {
                        DatabaseReference dr = FirebaseDatabase.getInstance().getReference("users")
                                .child(mFirebaseUser.getUid()).child("products").child(product_upc);
                        dr.removeValue();

                        // Crear una nueva tabla productos / usuarios ya que es muchos a mucho y actuar en consecuencia
                        mProductAdapter.removeProduct(product_upc);
                        mProductAdapter.notifyDataSetChanged();

                        prepareProductsLoading();
                    }
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };
            mFavoritesDatabaseReference.addChildEventListener(mChildEventListener);
        }
    }

    private void detachDatabaseReadListener() {
        if (mChildEventListener != null) {
            mFavoritesDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }
    }
}