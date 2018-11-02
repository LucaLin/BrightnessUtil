package com.example.r30_a.BrightnessTestProject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button btn_getBarcode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.r30_a.BrightnessTestProject.R.layout.activity_main);

        btn_getBarcode = (Button)findViewById(com.example.r30_a.BrightnessTestProject.R.id.btn_getBarcode);

        btn_getBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,BarCodeActivity.class));
            }
        });


    }
}
