package com.example.exact_it_dev.e_lifouta.payment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.exact_it_dev.e_lifouta.R;
import com.example.exact_it_dev.e_lifouta.network.NetworkConnection;
import com.kosalgeek.genasync12.AsyncResponse;
import com.kosalgeek.genasync12.PostResponseAsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Random;

public class Payment extends AppCompatActivity {
    EditText etReciNumcompte,etMontant,etMotif,etPin;
    Button btnVal;
    NetworkConnection networkConnection;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        setTitle("Paiement");

        networkConnection = new NetworkConnection(Payment.this);
        progressDialog = new ProgressDialog(Payment.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Traitement");
        progressDialog.setMessage("En cours de traitement...");
        final String URL = networkConnection.getUrl();
        final String numcompte = networkConnection.storedDatas("numcompte");

        etMontant =  (EditText)findViewById(R.id.etMontant);
        etMotif = (EditText)findViewById(R.id.etMotif);
        etReciNumcompte = (EditText)findViewById(R.id.etReciNumcompte);
        etPin = (EditText)findViewById(R.id.etPin);
        btnVal = (Button)findViewById(R.id.btnVal);
        final Random random = new Random(100);

        btnVal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap dt = new HashMap();
                dt.put("senderaccount",numcompte);
                dt.put("receiveraccount",etReciNumcompte.getText().toString());
                dt.put("description",etMotif.getText().toString());
                dt.put("amount",etMontant.getText().toString());
                dt.put("invoicenumber",String.valueOf(random.nextInt()));
                dt.put("senderpin",etPin.getText().toString());
                if(etMontant.getText().toString().isEmpty() || etMotif.getText().toString().isEmpty() || etPin.getText().toString().isEmpty() || etReciNumcompte.getText().toString().isEmpty()){
                    if(progressDialog.isShowing()){progressDialog.dismiss();}
                    networkConnection.writeToast("Veuillez remplir tous les champs");
                }else{
                    progressDialog.show();
                    if(networkConnection.isConnected()){
                        try {
                            PostResponseAsyncTask tache = new PostResponseAsyncTask(Payment.this, dt, false, new AsyncResponse() {
                                @Override
                                public void processFinish(String s) {
                                    switch (s){
                                        case "180":
                                            networkConnection.writeToast("Votre compte n\'est pas reconnu");
                                            progressDialog.dismiss();
                                            break;
                                        case "181":
                                            networkConnection.writeToast("Votre compte est désactivé");
                                            progressDialog.dismiss();
                                            break;
                                        case "182":
                                            networkConnection.writeToast("Votre PIN est incorrecte");
                                            progressDialog.dismiss();
                                            break;
                                        case "183":
                                            networkConnection.writeToast("Compte bénéficiaire non reconnu");
                                            progressDialog.dismiss();
                                            break;
                                        case "184":
                                            networkConnection.writeToast("Compte bénéficiaire désactivé");
                                            progressDialog.dismiss();
                                            break;
                                        case "185":
                                            networkConnection.writeToast("Votre solde est insuffisant");
                                            progressDialog.dismiss();
                                            break;
                                        default:
                                            progressDialog.dismiss();
                                            try {
                                                JSONArray jsonArray = new JSONArray(s);
                                                JSONObject jsonObject = jsonArray.getJSONObject(0);
                                                Intent intent = new Intent(Payment.this,Confirmation.class);
                                                intent.putExtra("recipientaccount",jsonObject.getString("recipientaccount"));
                                                intent.putExtra("senderaccount",jsonObject.getString("senderaccount"));
                                                intent.putExtra("optclient",jsonObject.getString("optclient"));
                                                intent.putExtra("transtype",jsonObject.getString("transtype"));
                                                startActivity(intent);
                                                finish();
                                            }catch (JSONException je){
                                                networkConnection.writeToast("Erreur de données");
                                                progressDialog.dismiss();
                                            }

                                            break;
                                    }
                                    Log.i("seth",s);
                                }
                            });
                            tache.execute(URL+"lifoutacourant/APIS/payer.php");
                        }catch (Exception e){
                            networkConnection.writeToast("Erreur du serveur");
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
