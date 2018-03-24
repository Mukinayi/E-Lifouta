package com.example.exact_it_dev.e_lifouta.payment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import com.example.exact_it_dev.e_lifouta.network.NetworkConnection;
import com.kosalgeek.genasync12.AsyncResponse;
import com.kosalgeek.genasync12.PostResponseAsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;

/**
 * Created by EXACT-IT-DEV on 3/24/2018.
 */

public class PaymentMetier implements IPayment {
    static Context context;
    NetworkConnection networkConnection;
    static ProgressDialog progressDialog;
    public PaymentMetier() {
    }

    public PaymentMetier(Context context) {
        this.context = context;
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(false);
        progressDialog.setMessage("Chargement");
        progressDialog.setTitle("Traiement");
    }

    public static void writeToast(String string){
        Toast.makeText(context, string,Toast.LENGTH_SHORT).show();
    }

    public static JSONObject retourJSON(String jsonArray){
        try {
            JSONArray retour = new JSONArray(jsonArray);
            JSONObject jsonObject = retour.getJSONObject(0);
            return jsonObject;
        }catch (JSONException e){
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            return null;
        }
    }


    @Override
    public JSONObject Payer(String senderaccount, String recipientaccount, double amount, String invoicenumber, String motif, String senderpin) {
        networkConnection = new NetworkConnection(context);
        final JSONObject data = new JSONObject();
        final String URL = networkConnection.getUrl();
        HashMap dt = new HashMap();
        dt.put("senderaccount",senderaccount);
        dt.put("receiveraccount",recipientaccount);
        dt.put("amount",String.valueOf(amount));
        dt.put("description",motif);
        dt.put("invoicenumber",invoicenumber);
        dt.put("senderpin",senderpin);
        if(senderaccount.isEmpty() || recipientaccount.isEmpty() || String.valueOf(amount).isEmpty() || invoicenumber.isEmpty() || motif.isEmpty() || senderpin.isEmpty()){
            if(progressDialog.isShowing()){progressDialog.dismiss();}
            Toast.makeText(context,"Veuillez remplir tous les champs",Toast.LENGTH_SHORT).show();
        }else{
            progressDialog.show();
            if(networkConnection.isConnected()){
                try {
                    PostResponseAsyncTask tache = new PostResponseAsyncTask(context, dt, false, new AsyncResponse() {
                        @Override
                        public void processFinish(String s) {
                            switch (s){
                                case "180":
                                        progressDialog.dismiss();
                                        writeToast("Votre compte est inexistant");

                                    break;
                                case "181":
                                        progressDialog.dismiss();
                                        writeToast("Votre compte est désactivé");
                                    break;
                                case "182":
                                        progressDialog.dismiss();
                                        writeToast("Votre PIN n\'est pas correcte");
                                    break;
                                case "183":
                                        progressDialog.dismiss();
                                        writeToast("Compte bénéficiaire non reconnu");
                                    break;
                                case "184":
                                        progressDialog.dismiss();
                                        writeToast("Compte bénéficiaire désactivé");
                                    break;
                                case "185":
                                        progressDialog.dismiss();
                                        writeToast("Votre solde est insuffisant");
                                    break;

                                default:
                                    progressDialog.dismiss();
                                    try {
                                        JSONObject obj = retourJSON(s);
                                        data.put("senderaccount",obj.getString("senderaccount"));
                                        data.put("recipientaccount",obj.getString("recipientaccount"));
                                        data.put("transtype",obj.getString("transtype"));
                                        data.put("otpclient",obj.getString("optclient"));

                                    }catch (JSONException j){
                                        progressDialog.dismiss();
                                        writeToast("Erreur des données");

                                    }
                                    break;
                            }
                        }
                    });
                    tache.execute(URL+"lifoutacourant/APIS/payer.php");
                    return data;
                }catch (Exception e){
                    Toast.makeText(context,"Erreur serveur",Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    return null;
                }
            }else{
                Toast.makeText(context,"Erreur connexion internet",Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                return null;
            }
        }
        return null;
    }

    @Override
    public Boolean ConfirmationTransaction(String otp, String senderaccount, String recipientaccount, String transtype) {
        return null;
    }
}
