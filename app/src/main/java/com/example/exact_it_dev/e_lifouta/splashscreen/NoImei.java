package com.example.exact_it_dev.e_lifouta.splashscreen;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.exact_it_dev.e_lifouta.R;

public class NoImei extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_imei);
        getSupportActionBar().hide();

        TextView tv = (TextView)findViewById(R.id.tvMess);
        Intent intent = getIntent();
        tv.setText(intent.getStringExtra("message"));
    }
}
