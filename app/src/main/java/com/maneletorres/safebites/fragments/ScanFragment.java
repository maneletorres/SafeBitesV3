package com.maneletorres.safebites.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.maneletorres.safebites.CustomScannerActivity;
import com.maneletorres.safebites.ProductActivity;
import com.maneletorres.safebites.R;
import com.maneletorres.safebites.api.ProductApi;
import com.maneletorres.safebites.api.ProductApi.ProductService;
import com.maneletorres.safebites.api.ProductResponse;
import com.maneletorres.safebites.entities.Product;
import com.maneletorres.safebites.entities.ProductNotFormatted;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.google.zxing.integration.android.IntentIntegrator.parseActivityResult;
import static com.maneletorres.safebites.utils.Utils.PRODUCT;
import static com.maneletorres.safebites.utils.Utils.RC_SCAN;
import static com.maneletorres.safebites.utils.Utils.TWO_PANE;
import static com.maneletorres.safebites.utils.Utils.formatProduct;

public class ScanFragment extends Fragment implements View.OnClickListener {
    private boolean mTwoPane;
    private Button mScanButton;
    private ProgressBar mScanProgressBar;
    private TextView mScanTextView;
    private ProductService mProductService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scan, container, false);

        // Master-detail configuration:
        if (getResources().getBoolean(R.bool.has_two_panes)) {
            mTwoPane = true;
        }

        // Initialization of the components:
        mScanProgressBar = view.findViewById(R.id.scan_progress_bar);
        mScanTextView = view.findViewById(R.id.scan_text);

        // OnClickListener configuration on the button to scan:
        mScanButton = view.findViewById(R.id.scan_button);
        mScanButton.setOnClickListener(ScanFragment.this);

        // Initialization of the product service:
        mProductService = ProductApi.getProduct().create(ProductService.class);

        return view;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.scan_button) {
            IntentIntegrator.forSupportFragment(this)
                    .setRequestCode(RC_SCAN)
                    .setCameraId(0)
                    .setOrientationLocked(false)
                    .setCaptureActivity(CustomScannerActivity.class)
                    .setOrientationLocked(false)
                    .setBeepEnabled(true)
                    .initiateScan();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult scanResult = parseActivityResult(IntentIntegrator.REQUEST_CODE,
                resultCode, data);
        if (requestCode == RC_SCAN) {
            if (scanResult != null) {
                if (scanResult.getContents() == null) {
                    if (resultCode == RESULT_CANCELED) {
                        Toast.makeText(getContext(), getString(R.string.scan_cancellation),
                                Toast.LENGTH_SHORT).show();
                    } else if (resultCode != RESULT_OK) {
                        Toast.makeText(getContext(), getString(R.string.error_during_scanning),
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    startScan(scanResult.getContents());
                }
            } else {
                Toast.makeText(getContext(), getString(R.string.error_during_scanning), Toast.LENGTH_SHORT).show();
                super.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    private void startScan(String scanResult) {
        mScanButton.setVisibility(View.GONE);
        mScanProgressBar.setVisibility(View.VISIBLE);
        mScanTextView.setVisibility(View.VISIBLE);

        callProductApi(scanResult);
    }

    private void callProductApi(String scanResult) {
        mProductService.getProduct(scanResult).enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(@NonNull Call<ProductResponse> call,
                                   @NonNull Response<ProductResponse> response) {
                ProductNotFormatted product = Objects.requireNonNull(response.body()).getProduct();
                Product p = formatProduct(getContext(), product);

                if (p != null) {
                    Intent intent = new Intent(getContext(), ProductActivity.class);
                    intent.putExtra(TWO_PANE, mTwoPane);
                    intent.putExtra(PRODUCT, p);
                    startActivity(intent);
                } else {
                    Toast.makeText(getContext(), getString(R.string.product_not_found), Toast.LENGTH_SHORT).show();
                }

                mScanProgressBar.setVisibility(View.GONE);
                mScanTextView.setVisibility(View.GONE);
                mScanButton.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(@NonNull Call<ProductResponse> call, @NonNull Throwable t) {
                t.printStackTrace();
            }
        });
    }
}