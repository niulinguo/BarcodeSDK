package com.niles.barcode;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;

/**
 * Created by Niles
 * Date 2018/11/22 07:38
 * Email niulinguo@163.com
 */
public class ScanActivity extends AppCompatActivity implements ZBarScannerView.ResultHandler {

    private static final String TAG = "ScanActivity";

    private MyScannerView mScannerView;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mScannerView = new MyScannerView(this);   // Programmatically initialize the scanner view
        setContentView(mScannerView);                // Set the scanner view as the content view

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void handleResult(Result rawResult) {
        // Do something with the result here
        if (BuildConfig.DEBUG) {
            Log.v(TAG, rawResult.getContents()); // Prints scan results
            Log.v(TAG, rawResult.getBarcodeFormat().toString()); // Prints the scan format (qrcode, pdf417 etc.)
        }

        // If you would like to resume scanning, call this method below:
//        mScannerView.resumeCameraPreview(this);

        Intent intent = new Intent();
        intent.putExtra("result", rawResult.getContents());
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.scan_menu_layout, menu);
        setFlashMenuText(menu.findItem(R.id.scan_flash));
        return true;
    }

    private void setFlashMenuText(MenuItem item) {
        if (item == null) {
            return;
        }
        try {
            if (mScannerView.getFlash()) {
                item.setTitle("关闭闪光灯");
            } else {
                item.setTitle("开启闪光灯");
            }
        } catch (Exception e) {
            item.setTitle("开启闪光灯");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.scan_flash) {
            mScannerView.setFlash(!mScannerView.getFlash());
            setFlashMenuText(item);
            return true;
        } else if (itemId == android.R.id.home) {
            setResult(RESULT_CANCELED);
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}
