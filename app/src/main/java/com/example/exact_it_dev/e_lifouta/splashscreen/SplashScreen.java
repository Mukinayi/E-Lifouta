package com.example.exact_it_dev.e_lifouta.splashscreen;

import android.Manifest;
import android.app.Service;
import android.bluetooth.BluetoothClass;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.example.exact_it_dev.e_lifouta.MainActivity;
import com.example.exact_it_dev.e_lifouta.R;
import com.example.exact_it_dev.e_lifouta.login.Login;
import com.example.exact_it_dev.e_lifouta.network.NetworkConnection;
import com.kosalgeek.genasync12.AsyncResponse;
import com.kosalgeek.genasync12.PostResponseAsyncTask;

import java.util.HashMap;

public class SplashScreen extends AppCompatActivity {
    NetworkConnection networkConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        getSupportActionBar().hide();

        networkConnection = new NetworkConnection(this);
        final String numcompte = networkConnection.storedDatas("numcompte");
        final String idcompte = networkConnection.storedDatas("idcompte");
        TelephonyManager tm = (TelephonyManager) getSystemService(Service.TELEPHONY_SERVICE);
        String imei ="";
        final String URL = networkConnection.getUrl();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            imei = null;
        }else{
            if(tm.getDeviceId()!=null){
                imei = tm.getDeviceId();
            }else{
                Intent noi = new Intent(SplashScreen.this,NoImei.class);
                noi.putExtra("message","Dispositif sans IMEI");
                startActivity(noi);
            }
        }

        HashMap dt = new HashMap();
        dt.put("deviceimei",imei);
        if(numcompte!=null){
            dt.put("idcompte",idcompte);
            if(networkConnection.isConnected()){
                PostResponseAsyncTask tache = new PostResponseAsyncTask(this, dt, false, new AsyncResponse() {
                    @Override
                    public void processFinish(String s) {
                        switch (s){
                            case "180":
                                Toast.makeText(getApplicationContext(),"Appareil non encore connecté",Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(SplashScreen.this, Login.class);
                                startActivity(i);
                                finish();
                                break;
                            case "181":
                                Toast.makeText(getApplicationContext(),"Dispositif désactivé",Toast.LENGTH_SHORT).show();
                                Intent it = new Intent(SplashScreen.this, NoImei.class);
                                it.putExtra("message","Dispositif désactivé");
                                startActivity(it);
                                finish();
                                break;
                            default:
                                Intent main = new Intent(SplashScreen.this,MainActivity.class);
                                startActivity(main);
                                finish();
                                break;

                        }
                    }
                });
                tache.execute(URL+"lifoutacourant/APIS/compare.php");
            }else{
                Toast.makeText(getApplicationContext(),"Erreur connexion intenet",Toast.LENGTH_SHORT).show();
                Intent it = new Intent(SplashScreen.this, NoImei.class);
                it.putExtra("message","Erreur connexion intenet");
                startActivity(it);
                finish();
            }
        }else{
            Intent i = new Intent(SplashScreen.this, Login.class);
            startActivity(i);
            finish();
        }

    }
}
