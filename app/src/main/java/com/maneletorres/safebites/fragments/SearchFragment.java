package com.maneletorres.safebites.fragments;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.maneletorres.safebites.R;
import com.maneletorres.safebites.adapters.ProductAdapter;
import com.maneletorres.safebites.api.ProductApi;
import com.maneletorres.safebites.api.ProductService;
import com.maneletorres.safebites.api.ProductsResponse;
import com.maneletorres.safebites.entities.Product;
import com.maneletorres.safebites.entities.ProductNotFormatted;
import com.maneletorres.safebites.utils.PaginationScrollListener;
import com.maneletorres.safebites.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.maneletorres.safebites.utils.Utils.formatProduct;


public class SearchFragment extends Fragment {
    /**
     * Number of the initial page.
     */
    private static final int PAGE_START = 1;

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet device.
     */
    private boolean mTwoPane;

    // Components:
    private EditText mSearchEditText;
    private TextView mEmptyTextView;
    private ProgressBar mProgressBar;
    //private TextView mProgressBarTextView;
    private MenuItem mSearchMenuItem;
    private MenuItem mCancelMenuItem;
    private RecyclerView mProductsRecyclerView;

    // Product adapter:
    private ProductAdapter mProductAdapter;

    // REST API:
    private ProductService mProductService;

    /**
     * Number of the current page.
     */
    private int mCurrentPage = PAGE_START;

    /**
     * Total number of pages.
     */
    private int mTotalPages;

    /**
     * Last page condition.
     */
    private boolean mIsLastPage = false;

    /**
     * Load condition.
     */
    private boolean mIsLoading = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        // Master-detail configuration:
        if (view.findViewById(R.id.search_frame_layout) != null) {
            mTwoPane = true;
        }

        // Placement of the back arrow on the Toolbar:
        Toolbar toolbar = Objects.requireNonNull(getActivity()).findViewById(R.id.toolbar);
        setHasOptionsMenu(true);

        // Initialization of the components:
        mProgressBar = view.findViewById(R.id.main_progress_bar);
        //mProgressBarTextView = view.findViewById(R.id.text_progress_bar);
        mEmptyTextView = view.findViewById(R.id.empty_textView);

        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getContext());
        mProductsRecyclerView = view.findViewById(R.id.product_recycler_view);
        mProductsRecyclerView.setLayoutManager(mLinearLayoutManager);
        mProductsRecyclerView.addOnScrollListener(new PaginationScrollListener(mLinearLayoutManager) {
            @Override
            protected void loadMoreItems() {
                mIsLoading = true;
                mCurrentPage += 1;
                loadNextPage();
            }

            @Override
            public boolean isLastPage() {
                return mIsLastPage;
            }

            @Override
            public boolean isLoading() {
                return mIsLoading;
            }
        });

        mSearchEditText = toolbar.findViewById(R.id.search_edit_text);
        mSearchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                Utils.hideSoftKeyboard(Objects.requireNonNull(getActivity()));

                reloadData();
                loadFirstPage();
                return true;
            }
            return false;
        });

        // Initialization of the empty product adapter:
        //mProductAdapter = new ProductAdapter(getContext(), this, mTwoPane);

        // Linking the product adapter to the RecyclerView:
        //mProductsRecyclerView.setAdapter(mProductAdapter);

        // Initialization of the product service:
        mProductService = ProductApi.getClient().create(ProductService.class);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater); // Can it be deleted?
        inflater.inflate(R.menu.main, menu);

        mSearchMenuItem = menu.findItem(R.id.action_search);
        mCancelMenuItem = menu.findItem(R.id.action_cancel);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_search) {
            mSearchMenuItem.setVisible(false);
            mCancelMenuItem.setVisible(true);
            mSearchEditText.setVisibility(View.VISIBLE);
            mSearchEditText.requestFocus();

            InputMethodManager imm = (InputMethodManager) Objects.requireNonNull(getActivity()).getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(mSearchEditText, InputMethodManager.SHOW_IMPLICIT);
        } else if (id == R.id.action_cancel) {
            mCancelMenuItem.setVisible(false);
            mSearchMenuItem.setVisible(true);
            mSearchEditText.setVisibility(View.GONE);

            Utils.hideSoftKeyboard(Objects.requireNonNull(getActivity()));
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean isConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) Objects.requireNonNull(getContext()).getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            mEmptyTextView.setVisibility(View.GONE);
        } else {
            mEmptyTextView.setText("No Internet connection");
            mEmptyTextView.setVisibility(View.VISIBLE);
        }

        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    private void loadFirstPage() {
        // Reload adapter:
        mProductAdapter = new ProductAdapter(getContext(), this, mTwoPane);
        mProductsRecyclerView.setAdapter(mProductAdapter);

        /*mProductAdapter.remove();
        mProductAdapter.notifyDataSetChanged();*/

        if (isConnected()) {
            if (mSearchEditText != null && mSearchEditText.getText().length() > 0) {
                //The ProgressBar becomes visible:
                mProgressBar.setVisibility(View.VISIBLE);
                //mProgressBarTextView.setVisibility(View.VISIBLE);

                // This line of code is used so that the 'mSearchEditText' loses focus and the
                // keyboard can be hidden properly.
                //mProductsRecyclerView.requestFocus();

                callProductsApi().enqueue(new Callback<ProductsResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<ProductsResponse> call, @NonNull Response<ProductsResponse> response) {
                        List<ProductNotFormatted> productsNotFormatted = fetchResults(response);
                        if (productsNotFormatted.isEmpty()) {
                            mProgressBar.setVisibility(View.GONE);
                            //mProgressBarTextView.setVisibility(View.GONE);

                            mEmptyTextView.setText("No products found");
                            mEmptyTextView.setVisibility(View.VISIBLE);
                        } else {
                            //createJSONNutrients(products);
                            List<Product> products = new ArrayList<>();
                            for (int i = 0; i < productsNotFormatted.size(); i++) {
                                Product product = formatProduct(productsNotFormatted.get(i));
                                products.add(product);
                            }
                            mProductAdapter.addAll(products);

                            mProgressBar.setVisibility(View.GONE);
                            // The RecyclerView 'mProductsRecyclerView' starts as GONE due to the
                            // shock it causes with the ProgressBar if it is visible.
                            //mProductsRecyclerView.setVisibility(View.VISIBLE);
                            //mProductsRecyclerView.requestFocus();

                            //mProgressBarTextView.setVisibility(View.GONE);
                            mEmptyTextView.setVisibility(View.GONE);

                            if (mCurrentPage < mTotalPages) mProductAdapter.addLoadingFooter();
                            else mIsLastPage = true;
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ProductsResponse> call, @NonNull Throwable t) {
                        t.printStackTrace();
                    }
                });
            } else {
                Toast.makeText(getActivity(), "You must enter a search.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loadNextPage() {
        if (isConnected()) {
            callProductsApi().enqueue(new Callback<ProductsResponse>() {
                @Override
                public void onResponse(@NonNull Call<ProductsResponse> call, @NonNull Response<ProductsResponse> response) {
                    mProductAdapter.removeLoadingFooter();
                    mIsLoading = false;

                    List<ProductNotFormatted> productsNotFormatted = fetchResults(response);
                    List<Product> products = new ArrayList<>();
                    for (int i = 0; i < productsNotFormatted.size(); i++) {
                        Product product = formatProduct(productsNotFormatted.get(i));
                        products.add(product);
                    }

                    mProductAdapter.addAll(products);

                    if (mCurrentPage != mTotalPages) mProductAdapter.addLoadingFooter();
                    else mIsLastPage = true;
                }

                @Override
                public void onFailure(@NonNull Call<ProductsResponse> call, @NonNull Throwable t) {
                    t.printStackTrace();
                }
            });
        }
    }

    private void reloadData() {
        mCurrentPage = PAGE_START;
        mIsLoading = false;
        mIsLastPage = false;
    }

    private List<ProductNotFormatted> fetchResults(Response<ProductsResponse> response) {
        List<ProductNotFormatted> products = Objects.requireNonNull(response.body()).getProducts();
        mTotalPages = response.body().getCount() / response.body().getPageSize() + 1;
        return products;
    }

    private Call<ProductsResponse> callProductsApi() {
        return mProductService.getProducts(
                mSearchEditText.getText().toString(),
                1,
                "process",
                1,
                10,
                mCurrentPage
        );
    }

    // CODE FOR DEVICE'S ROTATION:
    /*@Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        Toast.makeText(getContext(), "onViewStateRestored", Toast.LENGTH_SHORT).show();
        super.onViewStateRestored(savedInstanceState);

        if (mProductAdapter != null && mProductAdapter.getItemCount() > 0) {
            mProgressBar.setVisibility(View.VISIBLE);
            mProgressBarTextView.setVisibility(View.VISIBLE);
            reloadData();
            loadFirstPage();
        }
    }*/
}