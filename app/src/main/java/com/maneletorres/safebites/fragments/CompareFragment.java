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
import com.maneletorres.safebites.CaptureActivityPortrait;
import com.maneletorres.safebites.ComparatorActivity;
import com.maneletorres.safebites.MainActivity;
import com.maneletorres.safebites.R;
import com.maneletorres.safebites.entities.Product;
import com.maneletorres.safebites.utils.AsyncResponse;
import com.maneletorres.safebites.utils.JsonTask;

import java.util.List;
import java.util.Objects;

import static android.app.Activity.RESULT_CANCELED;
import static com.google.zxing.integration.android.IntentIntegrator.parseActivityResult;
import static com.maneletorres.safebites.utils.Utils.HEADER_SPECIFIC_PRODUCT_URL;
import static com.maneletorres.safebites.utils.Utils.PRODUCT_A;
import static com.maneletorres.safebites.utils.Utils.PRODUCT_B;
import static com.maneletorres.safebites.utils.Utils.RC_SCAN_OPTION_1_FIRST_EXECUTION;
import static com.maneletorres.safebites.utils.Utils.RC_SCAN_OPTION_1_SECOND_EXECUTION;
import static com.maneletorres.safebites.utils.Utils.RC_SCAN_OPTION_2;
import static com.maneletorres.safebites.utils.Utils.TAIL_SPECIFIC_PRODUCT_URL;
import static com.maneletorres.safebites.utils.Utils.sProducts;

public class CompareFragment extends Fragment implements View.OnClickListener, AsyncResponse, MainActivity.MyInterface {
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_compare, container, false);

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
                        Toast.makeText(getContext(), "There is no product saved as favorite to make the comparison", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(getContext(), "Scan canceled by the user", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Error during scanning.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        startScan(scanResult.getContents());
                    }
                } else {
                    Toast.makeText(getContext(), "Error during scanning.", Toast.LENGTH_SHORT).show();
                    super.onActivityResult(requestCode, resultCode, data);
                }
        }
    }

    @Override
    public void processFinish(Product output) {
        mScanProgressBar.setVisibility(View.GONE);
        mScanTextView.setVisibility(View.GONE);
        mScanButton.setVisibility(View.VISIBLE);

        if (output != null) {
            switch (request_code) {
                case RC_SCAN_OPTION_1_FIRST_EXECUTION:
                    mProductA = output;

                    startComparisonOption1(RC_SCAN_OPTION_1_SECOND_EXECUTION);
                    break;
                case RC_SCAN_OPTION_1_SECOND_EXECUTION:
                    Product productB = output;

                    Intent intent = new Intent(getContext(), ComparatorActivity.class);
                    intent.putExtra(PRODUCT_A, mProductA);
                    intent.putExtra(PRODUCT_B, productB);
                    startActivity(intent);
                    break;
                case RC_SCAN_OPTION_2:
                    mProductA = output;
                    productB = (Product) mProductASpinner.getSelectedItem();

                    intent = new Intent(getContext(), ComparatorActivity.class);
                    intent.putExtra(PRODUCT_A, mProductA);
                    intent.putExtra(PRODUCT_B, productB);
                    startActivity(intent);
                    break;
            }
        } else {
            Toast.makeText(getContext(), "Product not found", Toast.LENGTH_SHORT).show();
        }
    }

    private void startScan(String scanResult) {
        String queryUrl = HEADER_SPECIFIC_PRODUCT_URL + scanResult + TAIL_SPECIFIC_PRODUCT_URL;

        mScanButton.setVisibility(View.GONE);
        mScanProgressBar.setVisibility(View.VISIBLE);
        mScanTextView.setVisibility(View.VISIBLE);

        JsonTask jsonTask = new JsonTask();
        jsonTask.delegate = this;
        jsonTask.execute(queryUrl);
    }

    private void startComparisonOption1(int requestCode) {
        IntentIntegrator.forSupportFragment(this)
                .setRequestCode(requestCode)
                .setPrompt("Scan a product's barcode")
                .setCameraId(0)
                .setCaptureActivity(CaptureActivityPortrait.class)
                .setOrientationLocked(false)
                .setBeepEnabled(true)
                .initiateScan();
    }

    private void startComparisonOption2() {
        if (mProductASpinner.getCount() > 0 && mProductBSpinner.getCount() > 0) {
            Intent intent = new Intent(getContext(), ComparatorActivity.class);
            intent.putExtra(PRODUCT_A, (Product) mProductASpinner.getSelectedItem());
            intent.putExtra(PRODUCT_B, (Product) mProductBSpinner.getSelectedItem());
            startActivity(intent);
        } else {
            Toast.makeText(getContext(), "There are not enough products saved as a favorite to make the comparison", Toast.LENGTH_SHORT).show();
        }
    }

    // IMPORTANT: this method can be executed before the onCreateView method is executed.
    @Override
    public void updateProducts() {
        if (mFavoriteProducts != null) {
            prepareProductsNamesLoading();
        }
    }

    private void prepareProductsNamesLoading() {
        mFavoriteProducts = sProducts;
        ArrayAdapter<Product> adapterFavoriteProducts = new ArrayAdapter<>(Objects.requireNonNull(getContext()), android.R.layout.simple_spinner_dropdown_item, mFavoriteProducts);
        adapterFavoriteProducts.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mProductASpinner.setAdapter(adapterFavoriteProducts);
        mProductBSpinner.setAdapter(adapterFavoriteProducts);
    }
}