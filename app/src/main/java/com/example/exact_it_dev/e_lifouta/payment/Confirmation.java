package com.example.exact_it_dev.e_lifouta.payment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.exact_it_dev.e_lifouta.MainActivity;
import com.example.exact_it_dev.e_lifouta.R;
import com.example.exact_it_dev.e_lifouta.network.NetworkConnection;
import com.kosalgeek.genasync12.AsyncResponse;
import com.kosalgeek.genasync12.PostResponseAsyncTask;

import java.util.HashMap;

public class Confirmation extends AppCompatActivity {
    Button btnOTPconf;
    EditText etOTPconf;
    ImageButton imgbtnresendotp;
    NetworkConnection networkConnection;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);
        setTitle("Confirmation du Paiement");

        //Récupération des données de l'activité précédente
        Intent me = getIntent();
        final String recipientaccount = me.getStringExtra("recipientaccount");
        final String senderaccount = me.getStringExtra("senderaccount");
        final String optclient = me.getStringExtra("optclient");
        final String transtype = me.getStringExtra("transtype");

        networkConnection = new NetworkConnection(Confirmation.this);
        progressDialog = new ProgressDialog(Confirmation.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Traitement");
        progressDialog.setMessage("EN cours de chargement");
        final String URL = networkConnection.getUrl();

        btnOTPconf = (Button)findViewById(R.id.btnOTPconf);
        etOTPconf = (EditText)findViewById(R.id.etOTPconf);
        imgbtnresendotp = (ImageButton)findViewById(R.id.imgbtnresendotp);

        btnOTPconf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap dt = new HashMap();
                dt.put("senderaccount",senderaccount);
                dt.put("receiveraccount",recipientaccount);
                dt.put("optclient",etOTPconf.getText().toString());
                dt.put("transtype",transtype);

                if(etOTPconf.getText().toString().isEmpty()){
                    networkConnection.writeToast("Veuillez renseigner le OTP");
                    progressDialog.dismiss();
                }else{
                    progressDialog.show();
                    if(networkConnection.isConnected()){
                        try {
                            PostResponseAsyncTask tache = new PostResponseAsyncTask(Confirmation.this, dt, false, new AsyncResponse() {
                                @Override
                                public void processFinish(String s) {
                                    switch (s){
                                        case "180":
                                            networkConnection.writeToast("OTP non reconnu");
                                            progressDialog.dismiss();
                                            break;
                                        case "181":
                                            networkConnection.writeToast("OTP expiré");
                                            progressDialog.dismiss();
                                            break;
                                        case "201":
                                            networkConnection.writeToast("Echec transaction");
                                            progressDialog.dismiss();
                                            break;
                                        default:
                                            Intent main = new Intent(Confirmation.this, MainActivity.class);
                                            startActivity(main);
                                            finish();
                                            break;
                                    }
                                }
                            });
                            tache.execute(URL+"lifoutacourant/APIS/confirmtransaction.php");
                        }catch (Exception e){
                            networkConnection.writeToast("Erreur serveur");
                            progressDialog.dismiss();
                        }
                    }else{
                        networkConnection.writeToast("Erreur connexion internet");
                        progressDialog.dismiss();
                    }
                }
            }
        });




    }
}
