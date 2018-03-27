package com.example.exact_it_dev.e_lifouta.transfert;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.exact_it_dev.e_lifouta.MainActivity;
import com.example.exact_it_dev.e_lifouta.R;
import com.example.exact_it_dev.e_lifouta.network.NetworkConnection;
import com.kosalgeek.genasync12.AsyncResponse;
import com.kosalgeek.genasync12.PostResponseAsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class CompteCash extends AppCompatActivity {
    Button btntransfert;
    EditText etrecifname,etrecilname,etreciamount,etsendpin,etreciphone;

    NetworkConnection networkConnection;
    ProgressDialog progressDialog;
    AlertDialog.Builder alb;
    AlertDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compte_cash);
        setTitle("Transfert Compte à Cash");

        networkConnection = new NetworkConnection(CompteCash.this);
        progressDialog = new ProgressDialog(CompteCash.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("En traitement");
        progressDialog.setMessage("En cours de traitement");
        final String URL = networkConnection.getUrl();
        final String senderaccount = networkConnection.storedDatas("numcompte");

        alb = new AlertDialog.Builder(CompteCash.this);
        alb.setCancelable(false);

        btntransfert = (Button)findViewById(R.id.btntransfert);
        etrecifname = (EditText)findViewById(R.id.etrecifname);
        etrecilname = (EditText)findViewById(R.id.etrecilname);
        etreciamount = (EditText)findViewById(R.id.etreciamount);
        etsendpin = (EditText)findViewById(R.id.etsendpin);
        etreciphone = (EditText)findViewById(R.id.etreciphone);
        final StringBuilder str = new StringBuilder();

        btntransfert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap dt = new HashMap();
                dt.put("senderaccount",senderaccount);
                dt.put("amount",etreciamount.getText().toString());
                dt.put("nombene",etrecilname.getText().toString());
                dt.put("prenombene",etrecifname.getText().toString());
                dt.put("phoneexp",etreciphone.getText().toString());
                dt.put("pinexp",etsendpin.getText().toString());

                if(etreciamount.getText().toString().isEmpty() || etrecifname.getText().toString().isEmpty() || etrecilname.getText().toString().isEmpty() || etreciphone.getText().toString().isEmpty() || etsendpin.getText().toString().isEmpty()){
                    networkConnection.writeToast("Veuillez remplir tous les champs");
                    if(progressDialog.isShowing()){progressDialog.dismiss();}
                }else{
                    progressDialog.show();
                    if(networkConnection.isConnected()){
                        try {
                            PostResponseAsyncTask tache = new PostResponseAsyncTask(CompteCash.this, dt, false, new AsyncResponse() {
                                @Override
                                public void processFinish(String s) {
                                    switch (s){
                                        case "180":
                                            networkConnection.writeToast("Votre compte n\'existe pas");
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
                                            networkConnection.writeToast("Votre solde est insuffisant");
                                            progressDialog.dismiss();
                                            break;
                                        case "201":
                                            networkConnection.writeToast("Echec transfert");
                                            progressDialog.dismiss();
                                            break;
                                        default:
                                            progressDialog.dismiss();
                                            networkConnection.writeToast("Transfert réussi");
                                            alb.setTitle("Résumé du transfert");
                                            try {
                                                JSONArray jsonArray = new JSONArray(s);
                                                JSONObject jsonObject = jsonArray.getJSONObject(0);
                                                str.append("Expéditeur : " +jsonObject.getString("senderaccount")+"\n");
                                                str.append("Nom bénéficiaire : " +jsonObject.getString("recipienttransfertlname")+"\n");
                                                str.append("Prénom bénéficiaire : " +jsonObject.getString("recipienttransfertfname")+"\n");
                                                str.append("Portable bénéficiaire : " +jsonObject.getString("recipienttransfertphone")+"\n");
                                                str.append("Montant : " +jsonObject.getString("amount")+" CFA \n");
                                                str.append("Frais transfert : " +String.valueOf(Double.parseDouble(jsonObject.getString("systemfees")) + Double.parseDouble(jsonObject.getString("agwithtransfees")))+" CFA\n");
                                                str.append("Code du transfert : " +jsonObject.getString("tranfertcode")+"\n");
                                                str.append("Etat transaction : réussie \n");
                                                alb.setMessage(str.toString());
                                                alb.setPositiveButton("Terminer", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        Intent intent = new Intent(CompteCash.this, MainActivity.class);
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                });
                                                dialog = alb.create();
                                                dialog.show();


                                            }catch (JSONException e){
                                                networkConnection.writeToast("Erreur des données");
                                                progressDialog.dismiss();
                                            }

                                            break;
                                    }
                                }
                            });
                            tache.execute(URL+"lifoutacourant/APIS/transfertcomptetocash.php");
                        }catch (Exception e){
                            networkConnection.writeToast("Erreur du serveur");
                            progressDialog.dismiss();
                        }
                    }else{
                        networkConnection.writeToast("Erreurn connexion internet");
                        progressDialog.dismiss();
                    }
                }
            }
        });





    }
}
