package com.maneletorres.safebites.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.maneletorres.safebites.R;
import com.maneletorres.safebites.data.Product;
import com.maneletorres.safebites.ui.ProductActivity;
import com.maneletorres.safebites.ui.fragments.CompleteProductFragment;
import com.maneletorres.safebites.ui.fragments.FavoritesFragment;
import com.maneletorres.safebites.ui.fragments.SearchFragment;

import java.util.ArrayList;
import java.util.List;

import static com.maneletorres.safebites.utils.Utils.PRODUCT;

public class ProductAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int ITEM = 0;
    private static final int LOADING = 1;

    // Product adapter components:
    private Context mContext;
    private Fragment mCurrentFragment;
    private ProductViewHolder mProductViewHolder;
    private List<Product> mProducts;
    private boolean mTwoPane;
    private boolean isLoadingAdded = false;

    public ProductAdapter(Context context, Fragment fragment, boolean twoPane) {
        this.mProducts = new ArrayList<>();
        this.mContext = context;
        this.mCurrentFragment = fragment;
        this.mTwoPane = twoPane;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case ITEM:
                viewHolder = getViewHolder(parent, inflater);
                break;
            case LOADING:
                View view = inflater.inflate(R.layout.item_progress, parent, false);
                viewHolder = new LoadingViewHolder(view);
                break;
        }
        return viewHolder;
    }

    @NonNull
    private RecyclerView.ViewHolder getViewHolder(ViewGroup parent, LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        return (position == mProducts.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        Product currentProduct = mProducts.get(position);

        switch (getItemViewType(position)) {
            case ITEM:
                mProductViewHolder = (ProductViewHolder) holder;

                String image_resource = currentProduct.getImage_resource();
                if (image_resource.equals("-")) {
                    mProductViewHolder.mImageResource
                            .setImageResource(R.drawable.no_image_available);
                } else {
                    Glide.with(mContext)
                            .load(currentProduct.getImage_resource())
                            .listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e,
                                                            Object model, Target<Drawable> target,
                                                            boolean isFirstResource) {
                                    mProductViewHolder.mImageResource
                                            .setImageResource(R.drawable.no_image_available);
                                    mProductViewHolder.mProgressBar.setVisibility(View.GONE);
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model,
                                                               Target<Drawable> target,
                                                               DataSource dataSource,
                                                               boolean isFirstResource) {
                                    mProductViewHolder.mProgressBar.setVisibility(View.GONE);
                                    return false;
                                }
                            }).into(mProductViewHolder.mImageResource);
                }

                mProductViewHolder.itemView.setTag(currentProduct);
                mProductViewHolder.mName.setText(currentProduct.getName());
                mProductViewHolder.mUpc.setText(currentProduct.getUpc());
                break;
            case LOADING:
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mProducts == null ? 0 : mProducts.size();
    }

    public void add(Product p) {
        mProducts.add(p);
        notifyItemInserted(mProducts.size() - 1);
    }

    public void addAll(List<Product> moveProducts) {
        if (moveProducts != null) {
            for (Product product : moveProducts) {
                add(product);
            }
        }
    }

    public void removeProduct(String productUpc) {
        boolean condition = false;
        Product product = null;
        for (int i = 0; i < mProducts.size() && !condition; i++) {
            Product currentProduct = mProducts.get(i);
            if (currentProduct.getUpc().equals(productUpc)) {
                product = currentProduct;
                condition = true;
            }
        }

        if (product != null) {
            mProducts.remove(product);
        }
    }

    public Product getItem(int position) {
        return mProducts.get(position);
    }

    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new Product());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = mProducts.size() - 1;
        Product product = getItem(position);

        if (product != null) {
            mProducts.remove(position);
            notifyItemRemoved(position);
        }
    }

    private class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mUpc;
        private TextView mName;
        private ImageView mImageResource;
        private ProgressBar mProgressBar;

        ProductViewHolder(View itemView) {
            super(itemView);

            mUpc = itemView.findViewById(R.id.product_extra_information);
            mName = itemView.findViewById(R.id.product_name);
            mImageResource = itemView.findViewById(R.id.product_image);
            mProgressBar = itemView.findViewById(R.id.image_progress_bar);

            // OnClickListener configuration on the current product to access it:
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == this.itemView.getId()) {
                if (mTwoPane) {
                    Bundle arguments = new Bundle();
                    arguments.putParcelable(PRODUCT, (Parcelable) v.getTag());

                    CompleteProductFragment completeProductFragment = new CompleteProductFragment();
                    completeProductFragment.setArguments(arguments);

                    FragmentTransaction fragmentTransaction = (((FragmentActivity) mContext))
                            .getSupportFragmentManager()
                            .beginTransaction();

                    if (mCurrentFragment instanceof SearchFragment) {
                        fragmentTransaction.replace(R.id.search_frame_layout,
                                completeProductFragment);
                    } else if (mCurrentFragment instanceof FavoritesFragment) {
                        fragmentTransaction.replace(R.id.favorites_frame_layout,
                                completeProductFragment);
                    }

                    fragmentTransaction.commit();
                } else {
                    Intent intent = new Intent(mContext, ProductActivity.class);
                    intent.putExtra(PRODUCT, (Parcelable) v.getTag());
                    mContext.startActivity(intent);
                }
            }
        }
    }

    private class LoadingViewHolder extends RecyclerView.ViewHolder {
        LoadingViewHolder(View itemView) {
            super(itemView);
        }
    }
}