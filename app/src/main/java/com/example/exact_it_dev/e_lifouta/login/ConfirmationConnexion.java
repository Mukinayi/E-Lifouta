package com.example.exact_it_dev.e_lifouta.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.exact_it_dev.e_lifouta.R;
import com.example.exact_it_dev.e_lifouta.network.NetworkConnection;
import com.kosalgeek.genasync12.AsyncResponse;
import com.kosalgeek.genasync12.PostResponseAsyncTask;

import java.util.HashMap;

public class ConfirmationConnexion extends AppCompatActivity {
    ProgressDialog progressDialog;
    ImageButton imgbtnresend;
    Button btnconfirmcode;
    EditText etLoginCode;
    NetworkConnection networkConnection;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation_connexion);
        setTitle("Confirmation de la connexion");

        Intent me = getIntent();

        progressDialog = new ProgressDialog(ConfirmationConnexion.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Chargement...");

        imgbtnresend = (ImageButton)findViewById(R.id.imgbtnresend);
        btnconfirmcode = (Button)findViewById(R.id.btnconfirmcode);
        etLoginCode = (EditText)findViewById(R.id.etLoginCode);
        networkConnection = new NetworkConnection(this);
        final String numcompte = me.getStringExtra("numcompte");

        final String URL = networkConnection.getUrl();

        btnconfirmcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap dt = new HashMap();
                dt.put("logincode",etLoginCode.getText().toString());
                dt.put("numcompte",numcompte);
                dt.put("deviceimei",networkConnection.getImeiNumber());
                dt.put("devicename",networkConnection.getDeviceName());
                dt.put("devicemodel",networkConnection.getDeviceName());

                if(etLoginCode.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(),"Veuillez renseigner le code OTP",Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }else{
                    progressDialog.show();
                    if(networkConnection.isConnected()){
                        try {
                            PostResponseAsyncTask poste = new PostResponseAsyncTask(ConfirmationConnexion.this, dt, false, new AsyncResponse() {
                                @Override
                                public void processFinish(String s) {
                                    progressDialog.dismiss();
                                    Log.i("retour",s);
                                }
                            });
                            poste.execute(URL+"lifoutacourant/APIS/mobileconfirmation.php");
                        }catch (Exception e){
                            Toast.makeText(getApplicationContext(),"Erreur du serveur",Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }else{
                        Toast.makeText(getApplicationContext(),"Erreur connexion internet",Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }
            }
        });



    }
}
