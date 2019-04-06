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
import com.maneletorres.safebites.CaptureActivityPortrait;
import com.maneletorres.safebites.ProductActivity;
import com.maneletorres.safebites.R;
import com.maneletorres.safebites.entities.Product;
import com.maneletorres.safebites.utils.AsyncResponse;
import com.maneletorres.safebites.utils.JsonTask;

import java.util.Objects;

import static android.app.Activity.RESULT_CANCELED;
import static com.google.zxing.integration.android.IntentIntegrator.parseActivityResult;
import static com.maneletorres.safebites.utils.Utils.HEADER_SPECIFIC_PRODUCT_URL;
import static com.maneletorres.safebites.utils.Utils.PRODUCT;
import static com.maneletorres.safebites.utils.Utils.RC_SCAN;
import static com.maneletorres.safebites.utils.Utils.TAIL_SPECIFIC_PRODUCT_URL;

public class ScannerFragment extends Fragment implements View.OnClickListener, AsyncResponse {
    private Button mScanButton;
    private ProgressBar mScanProgressBar;
    private TextView mScanTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scanner, container, false);

        mScanProgressBar = view.findViewById(R.id.scan_progress_bar);
        mScanTextView = view.findViewById(R.id.scan_text);
        mScanButton = view.findViewById(R.id.scan_button);
        mScanButton.setOnClickListener(ScannerFragment.this);

        return view;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.scan_button) {
            IntentIntegrator.forSupportFragment(ScannerFragment.this)
                    .setRequestCode(RC_SCAN)
                    .setPrompt("Scan a product's barcode")
                    .setCameraId(0)
                    .setCaptureActivity(CaptureActivityPortrait.class)
                    .setOrientationLocked(false)
                    .setBeepEnabled(true)
                    .initiateScan();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_SCAN) {
            IntentResult scanResult = parseActivityResult(IntentIntegrator.REQUEST_CODE, resultCode, data);
            if (scanResult != null) {
                if (scanResult.getContents() == null || resultCode == RESULT_CANCELED) {
                    Toast.makeText(getContext(), "Scan canceled by the user", Toast.LENGTH_SHORT).show();
                } else {
                    startScan(scanResult.getContents());
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    @Override
    public void processFinish(Product output) {
        if (output != null) {
            Intent intent = new Intent(getContext(), ProductActivity.class);
            intent.putExtra(PRODUCT, output);
            startActivity(intent);
        } else {
            Toast.makeText(getContext(), "Product not found", Toast.LENGTH_SHORT).show();
        }

        mScanButton.setVisibility(View.VISIBLE);
        mScanProgressBar.setVisibility(View.GONE);
        mScanTextView.setVisibility(View.GONE);
    }

    private void startScan(String scanResult) {
        String queryUrl = HEADER_SPECIFIC_PRODUCT_URL + scanResult + TAIL_SPECIFIC_PRODUCT_URL;

        mScanButton.setVisibility(View.GONE);
        mScanProgressBar.setVisibility(View.VISIBLE);
        mScanTextView.setVisibility(View.VISIBLE);

        JsonTask jsonTask = new JsonTask(Objects.requireNonNull(getContext()));
        jsonTask.delegate = this;
        jsonTask.execute(queryUrl);
    }
}