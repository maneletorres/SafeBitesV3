package com.maneletorres.safebites;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Toast;

import com.google.zxing.client.android.Intents;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.maneletorres.safebites.utils.Utils;

public class CustomScannerActivity extends AppCompatActivity {
    private CaptureManager capture;
    private DecoratedBarcodeView barcodeScannerView;
    private EditText mEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_scanner);

        mEditText = findViewById(R.id.scan_edit_text);
        mEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (mEditText != null && mEditText.getText().length() > 0) {
                    Utils.hideSoftKeyboard(this);

                    setResult(RESULT_OK, new Intent().putExtra(Intents.Scan.RESULT, mEditText.getText().toString()));
                    finish();

                    return true;
                } else {
                    Toast.makeText(this, "You must enter a search.", Toast.LENGTH_SHORT).show();
                }
            }
            return false;
        });

        barcodeScannerView = findViewById(R.id.zxing_barcode_scanner);

        capture = new CaptureManager(this, barcodeScannerView);
        capture.initializeFromIntent(getIntent(), savedInstanceState);
        capture.decode();
    }

    @Override
    protected void onResume() {
        super.onResume();
        capture.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        capture.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        capture.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        capture.onSaveInstanceState(outState);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return barcodeScannerView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        Log.v("STATUS", "onBackPressed - Custom");
        // Internamente no actúa igual que el botón de retroceso superior. Falta hacer onDestroy cuándo se tira hacia atrás dentro de este método (onBackPressed).
        startActivity(new Intent(this, MainActivity.class));
    }
}
