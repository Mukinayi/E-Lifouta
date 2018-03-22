package com.example.exact_it_dev.e_lifouta.network;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import java.net.URL;

/**
 * Created by EXACT-IT-DEV on 3/21/2018.
 */

public class NetworkConnection {
    private Context context;
    SharedPreferences.Editor storage;
    SharedPreferences sharedPreferences;
    private static final String LIFOUTA_MEMORY = "LIFOUTA_MEMORY";

    public NetworkConnection(Context context) {
        this.context = context;
    }


    public boolean isConnected(){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Service.CONNECTIVITY_SERVICE);
        if(cm!=null){
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            if(networkInfo!=null){
                if(networkInfo.getState()== NetworkInfo.State.CONNECTED || networkInfo.getState()== NetworkInfo.State.CONNECTING){
                    return true;
                }
            }
        }
        return false;
    }


    public boolean saveProfile(String devicestate,String numcompe, String phone,String typecompe, String fname, String lname, String adresse,String currency,String idcompte){
        try {
            storage = context.getSharedPreferences(LIFOUTA_MEMORY,Context.MODE_PRIVATE).edit();
            storage.putString("numcompte",numcompe);
            storage.putString("phone",phone);
            storage.putString("typecompte",typecompe);
            storage.putString("currency",currency);
            storage.putString("fname",fname);
            storage.putString("lname",lname);
            storage.putString("adresse",adresse);
            storage.putString("devicestate",devicestate);
            storage.putString("idcompte",idcompte);
            storage.commit();
            return true;
        }catch (Exception e){
            Toast.makeText(context,"Erreur stockage",Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    public String storedDatas(String key){
        sharedPreferences = context.getSharedPreferences(LIFOUTA_MEMORY,Context.MODE_PRIVATE);
        return sharedPreferences.getString(key,null);
    }

    public boolean storeURL(String url){
        try {
            storage = context.getSharedPreferences(LIFOUTA_MEMORY, Context.MODE_PRIVATE).edit();
            storage.putString("URL", url);
            storage.commit();
            return true;
        }catch (Exception e){
            Toast.makeText(context,"Erreur stockage url",Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    public String getUrl(){
        sharedPreferences = context.getSharedPreferences(LIFOUTA_MEMORY,Context.MODE_PRIVATE);
        String URL = sharedPreferences.getString("URL","https://www.lifouta.com/");
        return URL;
    }

    public String getImeiNumber(){
        String imei = "";
        TelephonyManager tm = (TelephonyManager)context.getSystemService(Service.TELEPHONY_SERVICE);
        if(tm!=null){
            if(ActivityCompat.checkSelfPermission(context,Manifest.permission.READ_PHONE_STATE)!= PackageManager.PERMISSION_GRANTED) {
                imei = null;
            }else{
                imei = tm.getDeviceId();
            }
        }
        return imei;
    }
    public String getDeviceName(){
        String devicename = "";
        TelephonyManager tm = (TelephonyManager)context.getSystemService(Service.TELEPHONY_SERVICE);
        if(tm!=null){
            if(ActivityCompat.checkSelfPermission(context,Manifest.permission.READ_PHONE_STATE)!=PackageManager.PERMISSION_GRANTED){
                devicename = null;
            }else{
                devicename = Build.MODEL;
            }
        }
        return devicename;
    }

}