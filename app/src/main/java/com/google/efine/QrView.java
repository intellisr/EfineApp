package com.google.efine;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.google.zxing.WriterException;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class QrView extends AppCompatActivity {

    private DrivingLicence eUser;
    private QRGEncoder qrgEncoder;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_view);
        imageView=findViewById(R.id.imageView2);
        eUser= (DrivingLicence) getIntent().getSerializableExtra("User");
        String inputValue=eUser.getLicenceNo();
        qrgEncoder = new QRGEncoder(inputValue,null, QRGContents.Type.TEXT,50*50);
        try {
            Bitmap bitmap = qrgEncoder.encodeAsBitmap();
            imageView.setImageBitmap(bitmap);
        } catch (WriterException e) {
            Log.v("tr", e.toString());
        }
    }
}