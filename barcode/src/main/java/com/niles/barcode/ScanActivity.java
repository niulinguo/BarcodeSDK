package com.niles.barcode;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;

/**
 * Created by Niles
 * Date 2018/11/22 07:38
 * Email niulinguo@163.com
 */
public class ScanActivity extends AppCompatActivity implements ZBarScannerView.ResultHandler {

    private static final String TAG = "ScanActivity";

    private ZBarScannerView mScannerView;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        mScannerView = new ZBarScannerView(this);   // Programmatically initialize the scanner view
        setContentView(mScannerView);                // Set the scanner view as the content view
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
        mScannerView.resumeCameraPreview(this);
    }

}
