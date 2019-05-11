package com.maneletorres.safebites.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.maneletorres.safebites.ComparatorActivity;
import com.maneletorres.safebites.CustomScannerActivity;
import com.maneletorres.safebites.MainActivity;
import com.maneletorres.safebites.R;
import com.maneletorres.safebites.api.ProductApi;
import com.maneletorres.safebites.api.ProductResponse;
import com.maneletorres.safebites.api.ProductService;
import com.maneletorres.safebites.entities.Product;
import com.maneletorres.safebites.entities.ProductNotFormatted;

import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.google.zxing.integration.android.IntentIntegrator.parseActivityResult;
import static com.maneletorres.safebites.utils.Utils.IMAGE_RESOURCE_A;
import static com.maneletorres.safebites.utils.Utils.IMAGE_RESOURCE_B;
import static com.maneletorres.safebites.utils.Utils.INGREDIENTS_A;
import static com.maneletorres.safebites.utils.Utils.INGREDIENTS_B;
import static com.maneletorres.safebites.utils.Utils.PRODUCT_A;
import static com.maneletorres.safebites.utils.Utils.PRODUCT_B;
import static com.maneletorres.safebites.utils.Utils.RC_SCAN_OPTION_1_FIRST_EXECUTION;
import static com.maneletorres.safebites.utils.Utils.RC_SCAN_OPTION_1_SECOND_EXECUTION;
import static com.maneletorres.safebites.utils.Utils.RC_SCAN_OPTION_2;
import static com.maneletorres.safebites.utils.Utils.TWO_PANE;
import static com.maneletorres.safebites.utils.Utils.formatProduct;
import static com.maneletorres.safebites.utils.Utils.sUser;

public class CompareFragment extends Fragment implements View.OnClickListener, MainActivity.MyInterface {
    /**
     * Whether or not the fragment is in two-pane mode, i.e. running on a tablet device.
     */
    private boolean mTwoPane;

    private LinearLayout mProductAContainer;
    private LinearLayout mProductBContainer;
    private Button mScanButton;
    private ProgressBar mScanProgressBar;
    private TextView mScanTextView;
    private Spinner mProductASpinner;
    private Spinner mProductBSpinner;
    private List<Product> mFavoriteProducts;
    private int mOption;
    private int request_code;
    private Product mProductA;
    private ProductService mProductService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_compare, container, false);

        // Master-detail configuration:
        if (view.findViewById(R.id.compare_frame_layout_1) != null) {
            mTwoPane = true;
        }

        // Initialization of the components:
        mProductAContainer = view.findViewById(R.id.product_A_container);
        mProductASpinner = view.findViewById(R.id.product_A_spinner);
        mProductBContainer = view.findViewById(R.id.product_B_container);
        mProductBSpinner = view.findViewById(R.id.product_B_spinner);
        mScanProgressBar = view.findViewById(R.id.scan_progress_bar);
        mScanTextView = view.findViewById(R.id.scan_text);
        mScanButton = view.findViewById(R.id.compare_button);
        RadioGroup radioGroup = view.findViewById(R.id.options_radio_group);

        // Loading the names of the products:
        prepareProductsNamesLoading();

        // OnCheckedChangeListener configuration on the RadioGroup and selection of the second radio
        // button by default:
        mOption = 2;
        radioGroup.check(R.id.radio_button_2);
        radioGroup.setOnCheckedChangeListener((rg, i) -> {
            switch (rg.getCheckedRadioButtonId()) {
                case R.id.radio_button_1:
                    mOption = 1;
                    mProductAContainer.setVisibility(View.GONE);
                    mProductBContainer.setVisibility(View.GONE);
                    break;
                case R.id.radio_button_2:
                    mOption = 2;
                    mProductAContainer.setVisibility(View.VISIBLE);
                    mProductBContainer.setVisibility(View.VISIBLE);
                    break;
                case R.id.radio_button_3:
                    mOption = 3;
                    mProductAContainer.setVisibility(View.VISIBLE);
                    mProductBContainer.setVisibility(View.GONE);
                    break;
            }
        });

        // OnClickListener configuration on the button to scan products:
        mScanButton.setOnClickListener(this);

        // Initialization of the product service:
        mProductService = ProductApi.getProduct().create(ProductService.class);

        return view;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.compare_button) {
            switch (mOption) {
                case 1:
                    startComparisonOption1(RC_SCAN_OPTION_1_FIRST_EXECUTION);
                    break;
                case 2:
                    startComparisonOption2();
                    break;
                case 3:
                    if (mProductASpinner.getCount() > 0) {
                        startComparisonOption1(RC_SCAN_OPTION_2);
                    } else {
                        Toast.makeText(getContext(), getString(R.string.error_during_comparison_1), Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        request_code = requestCode;

        switch (request_code) {
            case RC_SCAN_OPTION_1_FIRST_EXECUTION:
            case RC_SCAN_OPTION_1_SECOND_EXECUTION:
            case RC_SCAN_OPTION_2:
                IntentResult scanResult = parseActivityResult(IntentIntegrator.REQUEST_CODE, resultCode, data);
                if (scanResult != null) {
                    if (scanResult.getContents() == null) {
                        if (resultCode == RESULT_CANCELED) {
                            Toast.makeText(getContext(), getString(R.string.user_cancellation), Toast.LENGTH_SHORT).show();
                        } else if (resultCode != RESULT_OK) {
                            Toast.makeText(getContext(), getString(R.string.error_during_scanning), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        startScan(scanResult.getContents());
                    }
                } else {
                    Toast.makeText(getContext(), getString(R.string.error_during_scanning), Toast.LENGTH_SHORT).show();
                    super.onActivityResult(requestCode, resultCode, data);
                }
                break;
        }
    }

    // IMPORTANT: this method can be executed before the onCreateView method is executed.
    @Override
    public void updateProducts() {
        if (mFavoriteProducts != null) {
            prepareProductsNamesLoading();
        }
    }

    private void callProductApi(String scanResult) {
        mProductService.getProduct(scanResult).enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(@NonNull Call<ProductResponse> call, @NonNull Response<ProductResponse> response) {
                ProductNotFormatted productNotFormatted = Objects.requireNonNull(response.body()).getProduct();
                Product product = formatProduct(productNotFormatted);

                mScanProgressBar.setVisibility(View.GONE);
                mScanTextView.setVisibility(View.GONE);
                mScanButton.setVisibility(View.VISIBLE);

                if (product != null) {
                    switch (request_code) {
                        case RC_SCAN_OPTION_1_FIRST_EXECUTION:
                            mProductA = product;

                            startComparisonOption1(RC_SCAN_OPTION_1_SECOND_EXECUTION);
                            break;
                        case RC_SCAN_OPTION_1_SECOND_EXECUTION:
                            Product productB = product;

                            if (mTwoPane) {
                                fragmentPreparation(mProductA, productB);
                            } else {
                                startActivity(productB);
                            }
                            break;
                        case RC_SCAN_OPTION_2:
                            mProductA = product;
                            productB = (Product) mProductASpinner.getSelectedItem();

                            if (mTwoPane) {
                                fragmentPreparation(mProductA, productB);
                            } else {
                                startActivity(productB);
                            }
                            break;
                    }
                } else {
                    Toast.makeText(getContext(), getString(R.string.product_not_found), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ProductResponse> call, @NonNull Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void startScan(String scanResult) {
        mScanButton.setVisibility(View.GONE);
        mScanProgressBar.setVisibility(View.VISIBLE);
        mScanTextView.setVisibility(View.VISIBLE);

        callProductApi(scanResult);
    }

    private void startComparisonOption1(int requestCode) {
        IntentIntegrator.forSupportFragment(this)
                .setRequestCode(requestCode)
                .setCameraId(0)
                .setOrientationLocked(false)
                .setCaptureActivity(CustomScannerActivity.class)
                .setOrientationLocked(false)
                .setBeepEnabled(true)
                .initiateScan();
    }

    private void startComparisonOption2() {
        if (mProductASpinner.getCount() > 0 && mProductBSpinner.getCount() > 0) {
            if (mTwoPane) {
                fragmentPreparation((Product) mProductASpinner.getSelectedItem(), (Product) mProductBSpinner.getSelectedItem());
            } else {
                Intent intent = new Intent(getContext(), ComparatorActivity.class);
                intent.putExtra(TWO_PANE, mTwoPane);
                intent.putExtra(PRODUCT_A, (Product) mProductASpinner.getSelectedItem());
                intent.putExtra(PRODUCT_B, (Product) mProductBSpinner.getSelectedItem());
                startActivity(intent);
            }
        } else {
            Toast.makeText(getContext(), getString(R.string.error_during_comparison_2), Toast.LENGTH_SHORT).show();
        }
    }

    private void prepareProductsNamesLoading() {
        //mFavoriteProducts = sProducts;
        mFavoriteProducts = sUser.getProducts();
        ArrayAdapter<Product> adapterFavoriteProducts = new ArrayAdapter<>(Objects.requireNonNull(getContext()), android.R.layout.simple_spinner_dropdown_item, mFavoriteProducts);
        adapterFavoriteProducts.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mProductASpinner.setAdapter(adapterFavoriteProducts);
        mProductBSpinner.setAdapter(adapterFavoriteProducts);
    }

    private void fragmentPreparation(Product productA, Product productB) {
        Bundle bundle1 = new Bundle();
        bundle1.putParcelable(PRODUCT_A, productA);
        bundle1.putParcelable(PRODUCT_B, productB);

        NutrientsComparisonFragment nutrientsComparison = new NutrientsComparisonFragment();
        nutrientsComparison.setArguments(bundle1);

        Bundle bundle2 = new Bundle();
        bundle2.putString(INGREDIENTS_A, productA.getIngredients());
        bundle2.putString(IMAGE_RESOURCE_A, productA.getImage_resource());
        bundle2.putString(INGREDIENTS_B, productB.getIngredients());
        bundle2.putString(IMAGE_RESOURCE_B, productB.getImage_resource());

        IngredientsComparisonFragment ingredientsComparison = new IngredientsComparisonFragment();
        ingredientsComparison.setArguments(bundle2);

        Objects.requireNonNull(this.getFragmentManager())
                .beginTransaction()
                .replace(R.id.compare_frame_layout_1, nutrientsComparison)
                .replace(R.id.compare_frame_layout_2, ingredientsComparison)
                .commit();
    }

    private void startActivity(Product productB) {
        Intent intent = new Intent(getContext(), ComparatorActivity.class);
        intent.putExtra(PRODUCT_A, mProductA);
        intent.putExtra(PRODUCT_B, productB);
        startActivity(intent);
    }
}