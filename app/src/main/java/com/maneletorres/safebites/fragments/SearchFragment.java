package com.maneletorres.safebites.fragments;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
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

import com.google.gson.Gson;
import com.maneletorres.safebites.R;
import com.maneletorres.safebites.adapters.ProductAdapter;
import com.maneletorres.safebites.api.ProductApi;
import com.maneletorres.safebites.api.ProductService;
import com.maneletorres.safebites.api.ResponseBody;
import com.maneletorres.safebites.entities.Nutrient;
import com.maneletorres.safebites.entities.Product;
import com.maneletorres.safebites.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFragment extends Fragment {
    // Components:
    private EditText mSearchEditText;
    private RecyclerView mProductsRecyclerView;
    private TextView mEmptyTextView;
    private ProgressBar mProgressBar;
    private TextView mProgressBarTextView;
    private MenuItem mSearchMenuItem;
    private MenuItem mCancelMenuItem;

    // Product adapter:
    private ProductAdapter mProductAdapter;

    // REST API:
    private ProductService mProductService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        // Placement of the back arrow on the Toolbar:
        Toolbar toolbar = Objects.requireNonNull(getActivity()).findViewById(R.id.toolbar);
        setHasOptionsMenu(true);

        // Initialization of the components:
        mProgressBar = view.findViewById(R.id.main_progress_bar);
        mProgressBarTextView = view.findViewById(R.id.text_progress_bar);
        mEmptyTextView = view.findViewById(R.id.empty_textView);
        mProductsRecyclerView = view.findViewById(R.id.product_recycler_view);
        mSearchEditText = toolbar.findViewById(R.id.search_edit_text);
        mSearchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                Utils.hideSoftKeyboard(Objects.requireNonNull(getActivity()));

                loadData();
                return true;
            }
            return false;
        });

        // Initialization of the empty product adapter:
        mProductAdapter = new ProductAdapter(getContext(), this);

        // Linking the product adapter to the RecyclerView:
        //mFavoriteProductsRecyclerView.setAdapter(mProductAdapter);

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

    private void loadData() {
        // The EditText contains text:
        if (mSearchEditText.getText().length() > 0) {
            // The device has an Internet connection:
            if (isConnected()) {
                mProgressBar.setVisibility(View.VISIBLE);
                mProgressBarTextView.setVisibility(View.VISIBLE);

                // The product adapter is overwritten to reload the information:
                mProductAdapter = new ProductAdapter(getContext(), this);

                // Linking the product adapter to the RecyclerView:
                mProductsRecyclerView.setAdapter(mProductAdapter);

                // REST API is called:
                Call<ResponseBody> callProductsApi = mProductService.getProducts(
                        mSearchEditText.getText().toString(),
                        1,
                        "process",
                        1,
                        10,
                        1);

                callProductsApi.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                        assert response.body() != null;
                        List<Product> products = response.body().getProducts();
                        if (products.isEmpty()) {
                            mEmptyTextView.setText("Product not found");
                            mEmptyTextView.setVisibility(View.VISIBLE);
                        } else {
                            mEmptyTextView.setVisibility(View.GONE);
                            createJSONNutrients(products);

                            mProductAdapter.addAll(products);
                        }

                        mProgressBar.setVisibility(View.GONE);
                        mProgressBarTextView.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                        t.printStackTrace();
                    }
                });
            }
        } else {
            Toast.makeText(getActivity(), "You must enter a search.", Toast.LENGTH_SHORT).show();
        }
    }

    private void createJSONNutrients(List<Product> products) {
        for (int i = 0; i < products.size(); i++) {
            Object nutrientsObject = products.get(i).getNutrientsObject();

            Gson gson = new Gson();
            String nutrientsString = gson.toJson(nutrientsObject);

            ArrayList<Nutrient> product_nutrients = new ArrayList<>();
            try {
                JSONObject JSONNutrients = new JSONObject(nutrientsString);

                Utils.createNutrients(product_nutrients, JSONNutrients);
            } catch (JSONException ex) {
                ex.printStackTrace();
            }

            products.get(i).setNutrients(product_nutrients);
        }
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
}