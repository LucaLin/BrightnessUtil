package com.example.r30_a.BrightnessTestProject;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.r30_a.BrightnessTestProject.util.BrightnessUtil;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.EnumMap;
import java.util.Map;

public class BarCodeActivity extends AppCompatActivity {

    private Button btnLaunchQR;
    private ImageView imgQRCode;
    int img_QrCode_width = 300;//條碼顯示寬度
    int img_QrCode_height = 300;//修碼顯示高度

    BrightnessUtil brightnessUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.r30_a.BrightnessTestProject.R.layout.activity_bar_code);

        brightnessUtil = new BrightnessUtil(this);
        btnLaunchQR = (Button)findViewById(com.example.r30_a.BrightnessTestProject.R.id.btn_ScanQR);
        imgQRCode = (ImageView)findViewById(com.example.r30_a.BrightnessTestProject.R.id.img_qr_code);

        makeBarcode();

        //開啟QR_CODE掃描鏡頭
        btnLaunchQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchQRScanner();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        brightnessUtil.toMaxBrightness();
    }

    @Override
    protected void onPause() {
        super.onPause();
        brightnessUtil.toOriginalBrightness();
    }

    private void launchQRScanner() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},1);
        }else {
            new IntentIntegrator(BarCodeActivity.this)
                    .setCameraId(0)
                    .setPrompt("please scan QR Code in this scope")
                    .setOrientationLocked(false)
                    .initiateScan();
        }
    }

    private void makeBarcode() {
        //製作QR Code內容編碼
        Map<EncodeHintType, Object> hints = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
        hints.put(EncodeHintType.CHARACTER_SET,"UTF-8");
        MultiFormatWriter writer = new MultiFormatWriter();
        //繪製QR_Code需要使用try/catch來處理
        try {
            //設定容錯率，但可將它想成是解析度的設定，其中分為四級
            //L(7%)、M(15%)、Q(25%)、H(30%)
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            //建立QR Code的資料矩陣，後面再使用點陣圖繪製
            //參數第一位為此QRCODE的真實內容，可以是網址或一個後續行為
            BitMatrix result = writer.encode("this is my QRCODE test",
                    BarcodeFormat.QR_CODE, img_QrCode_width,img_QrCode_height,hints);
            //建立點陣圖
            Bitmap bitmap = Bitmap.createBitmap(img_QrCode_width,img_QrCode_height, Bitmap.Config.ARGB_8888);
            //將QR Code的資料矩陣繪製到點陣圖上
            for(int y = 0; y<img_QrCode_height; y++){
                for(int x =0; x< img_QrCode_width; x++){
                    bitmap.setPixel(x,y,result.get(x,y) ? Color.BLACK : Color.WHITE);
                }
            }
            //將繪製好的bitmap設定給我們的imgview上
            imgQRCode.setImageBitmap(bitmap);
        }catch (WriterException e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if(result != null){
            if(result.getContents() == null){
                Toast.makeText(this,"成功掃描，條碼值為：" + result.getContents(),Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this,"取消掃描",Toast.LENGTH_SHORT).show();
            }
        }else {
            super.onActivityResult(requestCode,resultCode,data);
            Toast.makeText(this,"發生錯誤",Toast.LENGTH_SHORT).show();
        }
    }
}
