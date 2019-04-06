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

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.maneletorres.safebites.CaptureActivityPortrait;
import com.maneletorres.safebites.ComparatorActivity;
import com.maneletorres.safebites.R;
import com.maneletorres.safebites.entities.Product;
import com.maneletorres.safebites.utils.AsyncResponse;
import com.maneletorres.safebites.utils.JsonTask;

import java.util.ArrayList;
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
import static com.maneletorres.safebites.utils.Utils.sUser;

public class ComparatorFragment extends Fragment implements View.OnClickListener, AsyncResponse {
    private Fragment mFragment;
    private LinearLayout mProductAContainer;
    private LinearLayout mProductBContainer;
    private Button mScanButton;
    private ProgressBar mScanProgressBar;
    private TextView mScanTextView;
    private Spinner mProductASpinner;
    private Spinner mProductBSpinner;
    private List<Product> favoriteProducts;
    private ArrayAdapter<Product> adapterFavoriteProducts;
    private int mOption;
    private int request_code;
    private Product mProductA;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comparator, container, false);

        // Firebase Realtime Database components initialization:
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users").child(sUser.getUser_id()).child("products");
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Product product = dataSnapshot.getValue(Product.class);
                favoriteProducts.add(product);
                adapterFavoriteProducts.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Product product = dataSnapshot.getValue(Product.class);
                if (product != null) {
                    favoriteProducts.remove(product);
                    adapterFavoriteProducts.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        // Linking the ChildEventListener to the DatabaseReference:
        databaseReference.addChildEventListener(childEventListener);

        // Initialization of the components:
        mFragment = this;
        mProductAContainer = view.findViewById(R.id.product_A_container);
        mProductASpinner = view.findViewById(R.id.product_A_spinner);
        mProductBContainer = view.findViewById(R.id.product_B_container);
        mProductBSpinner = view.findViewById(R.id.product_B_spinner);
        mScanProgressBar = view.findViewById(R.id.scan_progress_bar);
        mScanTextView = view.findViewById(R.id.scan_text);
        mScanButton = view.findViewById(R.id.scan_button);
        mScanButton.setOnClickListener(this);

        favoriteProducts = new ArrayList<>();
        adapterFavoriteProducts = new ArrayAdapter<>(Objects.requireNonNull(getContext()), android.R.layout.simple_spinner_dropdown_item, favoriteProducts);
        adapterFavoriteProducts.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mProductASpinner.setAdapter(adapterFavoriteProducts);
        mProductBSpinner.setAdapter(adapterFavoriteProducts);

        RadioGroup radioGroup = view.findViewById(R.id.options_radio_group);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (radioGroup.getCheckedRadioButtonId()) {
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
            }
        });

        return view;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.scan_button) {
            switch (mOption) {
                case 1:
                    startComparisonOption1(RC_SCAN_OPTION_1_FIRST_EXECUTION);
                    break;
                case 2:
                    startComparisonOption2();
                    break;
                case 3:
                    startComparisonOption1(RC_SCAN_OPTION_2);
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
                    if (scanResult.getContents() == null || resultCode == RESULT_CANCELED) {
                        Toast.makeText(getContext(), "Scan canceled by the user", Toast.LENGTH_SHORT).show();
                    } else {
                        startScan(scanResult.getContents());
                    }
                } else {
                    super.onActivityResult(requestCode, resultCode, data);
                }
                break;
        }
    }

    @Override
    public void processFinish(Product output) {
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

                    if (mProductASpinner.getCount() > 0) {
                        productB = (Product) mProductASpinner.getSelectedItem();

                        intent = new Intent(getContext(), ComparatorActivity.class);
                        intent.putExtra(PRODUCT_A, mProductA);
                        intent.putExtra(PRODUCT_B, productB);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getContext(), "There are not enough products saved as a favorite to make the comparison", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        } else {
            Toast.makeText(getContext(), "Product not found", Toast.LENGTH_SHORT).show();
        }

        mScanProgressBar.setVisibility(View.GONE);
        mScanTextView.setVisibility(View.GONE);
        mScanButton.setVisibility(View.VISIBLE);
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

    private void startComparisonOption1(int requestCode) {
        IntentIntegrator.forSupportFragment(mFragment)
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
}
