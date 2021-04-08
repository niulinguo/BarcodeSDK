package com.niles.barcode;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.photopicker.activity.BGAPhotoPickerActivity;
import cn.bingoogolapple.qrcode.core.BarcodeType;
import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zbar.BarcodeFormat;
import cn.bingoogolapple.qrcode.zbar.ZBarView;

/**
 * Created by Niles
 * Date 2018/11/22 07:38
 * Email niulinguo@163.com
 */
public class ScanActivity extends AppCompatActivity implements QRCodeView.Delegate {

    private static final String TAG = "ScanActivity";
    private static final int REQUEST_CODE_CHOOSE_QRCODE_FROM_GALLERY = 666;

    private ZBarView mZBarView;

    private boolean flashlightOpened = false;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_scan_layout);
//        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        mZBarView = findViewById(R.id.zbarview);
        mZBarView.setDelegate(this);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mZBarView.startCamera(); // 打开后置摄像头开始预览，但是并未开始识别
//        mZBarView.startCamera(Camera.CameraInfo.CAMERA_FACING_FRONT); // 打开前置摄像头开始预览，但是并未开始识别
        mZBarView.setType(BarcodeType.ALL, null);
        mZBarView.startSpotAndShowRect(); // 显示扫描框，并开始识别
    }

    @Override
    protected void onStop() {
        mZBarView.stopCamera(); // 关闭摄像头预览，并且隐藏扫描框
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mZBarView.onDestroy(); // 销毁二维码扫描控件
        super.onDestroy();
    }

    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        if (vibrator != null) {
            vibrator.vibrate(200);
        }
    }

    @Override
    public void onScanQRCodeSuccess(String result) {
        Log.i(TAG, "result:" + result);
//        setTitle("扫描结果为：" + result);
        vibrate();

        Intent intent = new Intent();
        intent.putExtra("result", result);
        setResult(RESULT_OK, intent);
        finish();

//        mZBarView.startSpot(); // 开始识别
    }

    @Override
    public void onCameraAmbientBrightnessChanged(boolean isDark) {
        // 这里是通过修改提示文案来展示环境是否过暗的状态，接入方也可以根据 isDark 的值来实现其他交互效果
        String tipText = mZBarView.getScanBoxView().getTipText();
        String ambientBrightnessTip = "\n环境过暗，请打开闪光灯";
        if (isDark) {
            if (!tipText.contains(ambientBrightnessTip)) {
                mZBarView.getScanBoxView().setTipText(tipText + ambientBrightnessTip);
            }
        } else {
            if (tipText.contains(ambientBrightnessTip)) {
                tipText = tipText.substring(0, tipText.indexOf(ambientBrightnessTip));
                mZBarView.getScanBoxView().setTipText(tipText);
            }
        }
    }

    @Override
    public void onScanQRCodeOpenCameraError() {
        Log.e(TAG, "打开相机出错");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mZBarView.showScanRect();

        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_CHOOSE_QRCODE_FROM_GALLERY) {
            final String picturePath = BGAPhotoPickerActivity.getSelectedPhotos(data).get(0);
            mZBarView.decodeQRCode(picturePath);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.scan_menu_layout, menu);
        setFlashMenuText(menu.findItem(R.id.scan_flash));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.scan_flash) {
            if (flashlightOpened) {
                flashlightOpened = false;
                mZBarView.closeFlashlight();
            } else {
                flashlightOpened = true;
                mZBarView.openFlashlight();
            }
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

    private void setFlashMenuText(MenuItem item) {
        if (flashlightOpened) {
            item.setTitle("关闭闪光灯");
        } else {
            item.setTitle("开启闪光灯");
        }
    }

    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.start_preview) {
            mZBarView.startCamera(); // 打开后置摄像头开始预览，但是并未开始识别
        } else if (id == R.id.stop_preview) {
            mZBarView.stopCamera(); // 关闭摄像头预览，并且隐藏扫描框
        } else if (id == R.id.start_spot) {
            mZBarView.startSpot(); // 开始识别
        } else if (id == R.id.stop_spot) {
            mZBarView.stopSpot(); // 停止识别
        } else if (id == R.id.start_spot_showrect) {
            mZBarView.startSpotAndShowRect(); // 显示扫描框，并开始识别
        } else if (id == R.id.stop_spot_hiddenrect) {
            mZBarView.stopSpotAndHiddenRect(); // 停止识别，并且隐藏扫描框
        } else if (id == R.id.show_scan_rect) {
            mZBarView.showScanRect(); // 显示扫描框
        } else if (id == R.id.hidden_scan_rect) {
            mZBarView.hiddenScanRect(); // 隐藏扫描框
        } else if (id == R.id.decode_scan_box_area) {
            mZBarView.getScanBoxView().setOnlyDecodeScanBoxArea(true); // 仅识别扫描框中的码
        } else if (id == R.id.decode_full_screen_area) {
            mZBarView.getScanBoxView().setOnlyDecodeScanBoxArea(false); // 识别整个屏幕中的码
        } else if (id == R.id.open_flashlight) {
            mZBarView.openFlashlight(); // 打开闪光灯
        } else if (id == R.id.close_flashlight) {
            mZBarView.closeFlashlight(); // 关闭闪光灯
        } else if (id == R.id.scan_one_dimension) {
            mZBarView.changeToScanBarcodeStyle(); // 切换成扫描条码样式
            mZBarView.setType(BarcodeType.ONE_DIMENSION, null); // 只识别一维条码
            mZBarView.startSpotAndShowRect(); // 显示扫描框，并开始识别
        } else if (id == R.id.scan_two_dimension) {
            mZBarView.changeToScanQRCodeStyle(); // 切换成扫描二维码样式
            mZBarView.setType(BarcodeType.TWO_DIMENSION, null); // 只识别二维条码
            mZBarView.startSpotAndShowRect(); // 显示扫描框，并开始识别
        } else if (id == R.id.scan_qr_code) {
            mZBarView.changeToScanQRCodeStyle(); // 切换成扫描二维码样式
            mZBarView.setType(BarcodeType.ONLY_QR_CODE, null); // 只识别 QR_CODE
            mZBarView.startSpotAndShowRect(); // 显示扫描框，并开始识别
        } else if (id == R.id.scan_code128) {
            mZBarView.changeToScanBarcodeStyle(); // 切换成扫描条码样式
            mZBarView.setType(BarcodeType.ONLY_CODE_128, null); // 只识别 CODE_128
            mZBarView.startSpotAndShowRect(); // 显示扫描框，并开始识别
        } else if (id == R.id.scan_ean13) {
            mZBarView.changeToScanBarcodeStyle(); // 切换成扫描条码样式
            mZBarView.setType(BarcodeType.ONLY_EAN_13, null); // 只识别 EAN_13
            mZBarView.startSpotAndShowRect(); // 显示扫描框，并开始识别
        } else if (id == R.id.scan_high_frequency) {
            mZBarView.changeToScanQRCodeStyle(); // 切换成扫描二维码样式
            mZBarView.setType(BarcodeType.HIGH_FREQUENCY, null); // 只识别高频率格式，包括 QR_CODE、ISBN13、UPC_A、EAN_13、CODE_128
            mZBarView.startSpotAndShowRect(); // 显示扫描框，并开始识别
        } else if (id == R.id.scan_all) {
            mZBarView.changeToScanQRCodeStyle(); // 切换成扫描二维码样式
            mZBarView.setType(BarcodeType.ALL, null); // 识别所有类型的码
            mZBarView.startSpotAndShowRect(); // 显示扫描框，并开始识别
        } else if (id == R.id.scan_custom) {
            mZBarView.changeToScanQRCodeStyle(); // 切换成扫描二维码样式

            List<BarcodeFormat> formatList = new ArrayList<>();
            formatList.add(BarcodeFormat.QRCODE);
            formatList.add(BarcodeFormat.ISBN13);
            formatList.add(BarcodeFormat.UPCA);
            formatList.add(BarcodeFormat.EAN13);
            formatList.add(BarcodeFormat.CODE128);
            mZBarView.setType(BarcodeType.CUSTOM, formatList); // 自定义识别的类型

            mZBarView.startSpotAndShowRect(); // 显示扫描框，并开始识别
        } else if (id == R.id.choose_qrcde_from_gallery) {/*
                从相册选取二维码图片，这里为了方便演示，使用的是
                https://github.com/bingoogolapple/BGAPhotoPicker-Android
                这个库来从图库中选择二维码图片，这个库不是必须的，你也可以通过自己的方式从图库中选择图片
                 */
            Intent photoPickerIntent = new BGAPhotoPickerActivity.IntentBuilder(this)
                    .cameraFileDir(null)
                    .maxChooseCount(1)
                    .selectedPhotos(null)
                    .pauseOnScroll(false)
                    .build();
            startActivityForResult(photoPickerIntent, REQUEST_CODE_CHOOSE_QRCODE_FROM_GALLERY);
        }
    }
}
