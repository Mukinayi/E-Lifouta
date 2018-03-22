package com.example.exact_it_dev.e_lifouta.login;

import android.app.ProgressDialog;
import android.content.Intent;
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

import java.util.HashMap;

public class Login extends AppCompatActivity {
    public ProgressDialog progressDialog;
    Button btnlogin;
    EditText etLoginNumcompte;
    EditText etLoginPin;
    NetworkConnection networkConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle("Connexion Lifouta");

        btnlogin = (Button)findViewById(R.id.btnlogin);
        etLoginNumcompte = (EditText)findViewById(R.id.etLoginNumcompte);
        etLoginPin = (EditText)findViewById(R.id.etLoginPin);
        progressDialog = new ProgressDialog(Login.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Traitement...");
        networkConnection = new NetworkConnection(this);
        final String URL = networkConnection.getUrl();
        final String IMEI = networkConnection.getImeiNumber();




        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap datas = new HashMap();
                datas.put("imei",IMEI);
                datas.put("numcompte",etLoginNumcompte.getText().toString());
                datas.put("pin",etLoginPin.getText().toString());
                if(etLoginNumcompte.getText().toString().isEmpty() || etLoginPin.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(),"Veuillez remplir tous les champs",Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }else{
                    progressDialog.show();
                    if(networkConnection.isConnected()){
                        try {
                            PostResponseAsyncTask poste = new PostResponseAsyncTask(Login.this, datas, false, new AsyncResponse() {
                                @Override
                                public void processFinish(String s) {
                                    switch (s){
                                        case "180":
                                                Toast.makeText(getApplicationContext(),"Compte Lifouta non reconnu",Toast.LENGTH_SHORT).show();
                                                progressDialog.dismiss();
                                            break;
                                        case "181":
                                            Toast.makeText(getApplicationContext(),"Pin Lifouta incorrecte",Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();
                                            break;
                                        default:
                                            Intent conf = new Intent(Login.this,ConfirmationConnexion.class);
                                            conf.putExtra("numcompte",etLoginNumcompte.getText().toString());
                                            startActivity(conf);
                                            break;
                                    }
                                }
                            });
                            poste.execute(URL+"lifoutacourant/APIS/clientconnexion.php");
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
