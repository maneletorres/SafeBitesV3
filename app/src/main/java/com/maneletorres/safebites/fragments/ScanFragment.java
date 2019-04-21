package com.maneletorres.safebites.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.maneletorres.safebites.CaptureActivityPortrait;
import com.maneletorres.safebites.ProductActivity;
import com.maneletorres.safebites.R;
import com.maneletorres.safebites.api.ProductApi;
import com.maneletorres.safebites.api.ProductResponse;
import com.maneletorres.safebites.api.ProductService;
import com.maneletorres.safebites.entities.Product;
import com.maneletorres.safebites.entities.ProductNotFormatted;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_CANCELED;
import static com.google.zxing.integration.android.IntentIntegrator.parseActivityResult;
import static com.maneletorres.safebites.utils.Utils.PRODUCT;
import static com.maneletorres.safebites.utils.Utils.RC_SCAN;
import static com.maneletorres.safebites.utils.Utils.formatProduct;

public class ScanFragment extends Fragment implements View.OnClickListener {
    private LinearLayout mButtonsLinearLayout;
    private ProgressBar mScanProgressBar;
    private TextView mScanTextView;
    private ProductService mProductService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scan, container, false);

        // Initialization of the components:
        mScanProgressBar = view.findViewById(R.id.scan_progress_bar);
        mScanTextView = view.findViewById(R.id.scan_text);
        mButtonsLinearLayout = view.findViewById(R.id.buttons_linear_layout);

        // OnClickListener configuration on the button to scan:
        view.findViewById(R.id.scan_button).setOnClickListener(ScanFragment.this);

        // OnClickListener configuration on the button to barcode manual introduction:
        view.findViewById(R.id.barcode_manual_introduction_button).setOnClickListener(this);

        // Initialization of the product service:
        mProductService = ProductApi.getClient().create(ProductService.class);

        return view;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.scan_button) {
            IntentIntegrator.forSupportFragment(ScanFragment.this)
                    .setRequestCode(RC_SCAN)
                    .setPrompt("Scan a product's barcode")
                    .setCameraId(0)
                    .setCaptureActivity(CaptureActivityPortrait.class) // Path to remove.
                    .setOrientationLocked(false)
                    .setBeepEnabled(true)
                    .initiateScan();
        } else if (id == R.id.barcode_manual_introduction_button) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(50, 0, 50, 0);

            EditText editText = new EditText(getContext());
            editText.setRawInputType(InputType.TYPE_CLASS_NUMBER);
            editText.setLayoutParams(params);

            LinearLayout layout = new LinearLayout(getContext());
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.addView(editText);

            new AlertDialog.Builder(getContext())
                    .setView(layout)
                    .setPositiveButton("SCAN", (dialog, which) -> {
                        String manualBarcode = editText.getText().toString();
                        if (manualBarcode.length() > 0) {
                            startScan(manualBarcode);
                        } else {
                            Toast.makeText(getContext(), "You must enter a valid bar code.", Toast.LENGTH_SHORT).show();
                        }
                    }).setNegativeButton("Cancel", (dialog, which) -> Toast.makeText(getContext(), "Scan canceled by the user", Toast.LENGTH_SHORT).show())
                    .setTitle("Enter a barcode:")
                    .create()
                    .show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_SCAN) {
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

    private void startScan(String scanResult) {
        mButtonsLinearLayout.setVisibility(View.GONE);
        mScanProgressBar.setVisibility(View.VISIBLE);
        mScanTextView.setVisibility(View.VISIBLE);

        callProductApi(scanResult);
    }

    private void callProductApi(String scanResult) {
        mProductService.getProduct(scanResult).enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(@NonNull Call<ProductResponse> call, @NonNull Response<ProductResponse> response) {
                assert response.body() != null;
                ProductNotFormatted product = response.body().getProduct();
                Product p = formatProduct(product);

                if (p != null) {
                    Intent intent = new Intent(getContext(), ProductActivity.class);
                    intent.putExtra(PRODUCT, p);
                    startActivity(intent);
                } else {
                    Toast.makeText(getContext(), "Product not found", Toast.LENGTH_SHORT).show();
                }

                mScanProgressBar.setVisibility(View.GONE);
                mScanTextView.setVisibility(View.GONE);
                mButtonsLinearLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(@NonNull Call<ProductResponse> call, @NonNull Throwable t) {
                t.printStackTrace();
            }
        });
    }
}