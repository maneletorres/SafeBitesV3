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

import com.maneletorres.safebites.MainActivity;
import com.maneletorres.safebites.R;
import com.maneletorres.safebites.adapters.ProductAdapter;

import static com.maneletorres.safebites.utils.Utils.sUser;

public class FavoritesFragment extends Fragment implements MainActivity.MyInterface {
    /**
     * Whether or not the fragment is in two-pane mode, i.e. running on a tablet device.
     */
    private boolean mTwoPane;

    // Other variables:
    private RecyclerView mFavoriteProductsRecyclerView;
    private ProductAdapter mProductAdapter;
    private TextView mEmptyTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);

        // Master-detail configuration:
        if (view.findViewById(R.id.favorites_frame_layout) != null) {
            mTwoPane = true;
        }

        // Initialization of the components:
        mEmptyTextView = view.findViewById(R.id.empty_textView);
        mFavoriteProductsRecyclerView = view.findViewById(R.id.favorite_products_recycler_view);

        // Loading of the products:
        prepareProductsLoading();

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

    // IMPORTANT: this method can be executed before the onCreateView method is executed.
    @Override
    public void updateProducts() {
        if (mProductAdapter != null) {
            prepareProductsLoading();
        }
    }

    private void prepareProductsLoading() {
        mProductAdapter = new ProductAdapter(getContext(), this, mTwoPane);
        //mProductAdapter.addAll(sProducts);
        mProductAdapter.addAll(sUser.getProducts());
        mFavoriteProductsRecyclerView.setAdapter(mProductAdapter);
        checkProductsNumber();
    }
}