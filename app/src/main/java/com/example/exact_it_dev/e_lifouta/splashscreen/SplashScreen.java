package com.example.exact_it_dev.e_lifouta.splashscreen;

import android.Manifest;
import android.app.Service;
import android.bluetooth.BluetoothClass;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.example.exact_it_dev.e_lifouta.MainActivity;
import com.example.exact_it_dev.e_lifouta.R;
import com.example.exact_it_dev.e_lifouta.login.Login;
import com.example.exact_it_dev.e_lifouta.network.NetworkConnection;

import java.util.HashMap;

public class SplashScreen extends AppCompatActivity {
    NetworkConnection networkConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        getSupportActionBar().hide();

        networkConnection = new NetworkConnection(this);
        String numcompte = networkConnection.storedDatas("numcompte");
        int state = Integer.parseInt(networkConnection.storedDatas("devicestate"));
        TelephonyManager tm = (TelephonyManager) getSystemService(Service.TELEPHONY_SERVICE);
        String imei ="";

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

            imei = "00000000000000000";
            //imei = "362523432421083";
        }else{

            imei = tm.getDeviceId();
        }

        HashMap dt = new HashMap();
        dt.put("deviceimei",imei);
        if(numcompte!=null){
            if(networkConnection.isConnected()){

            }else{
                Toast.makeText(getApplicationContext(),"Erreur connexion intenet",Toast.LENGTH_SHORT).show();

            }
        }else{
            Intent i = new Intent(SplashScreen.this, Login.class);
            startActivity(i);
            finish();
        }

    }
}
