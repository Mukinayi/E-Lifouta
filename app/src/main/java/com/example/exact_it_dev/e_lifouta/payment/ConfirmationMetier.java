package com.example.exact_it_dev.e_lifouta.payment;

import android.app.ProgressDialog;
import android.content.Context;
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

public class ConfirmationMetier implements IPayment {
    static  Context context;
    NetworkConnection networkConnection;
    static ProgressDialog progressDialog;

    public ConfirmationMetier(Context context) {
        this.context = context;
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(false);
        progressDialog.setMessage("Chargement");
        progressDialog.setTitle("Traiement");
    }

    @Override
    public JSONObject Payer(String senderaccount, String recipientaccount, double amount, String invoicenumber, String motif, String senderpin) {
        return null;
    }

    public static void writeToast(String string){
        Toast.makeText(context, string,Toast.LENGTH_SHORT).show();
    }

    @Override
    public JSONObject ConfirmationTransaction(String otp, String senderaccount, final String recipientaccount, String transtype) {
        final JSONObject retourJSON = new JSONObject();
        networkConnection = new NetworkConnection(context);
        HashMap dt = new HashMap();
        dt.put("optclient",otp);
        dt.put("senderaccount",senderaccount);
        dt.put("receiveraccount",recipientaccount);
        dt.put("transtype",transtype);
        final String URL = networkConnection.getUrl();
        if(otp.isEmpty() || senderaccount.isEmpty() || recipientaccount.isEmpty() || transtype.isEmpty()){
            if(progressDialog.isShowing()){progressDialog.dismiss();}
            writeToast("Veuillez remplir tous les champs");
            return null;
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
                                        writeToast("OTP non existant");
                                    break;
                                case "181":
                                    progressDialog.dismiss();
                                    writeToast("OTP expiré");
                                    break;
                                case "201":
                                    progressDialog.dismiss();
                                    writeToast("Echec mise à jour");
                                    break;
                                default:
                                        try {
                                            JSONArray jsonArray = new JSONArray(s);
                                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                                            retourJSON.put("idtrans",jsonObject.getString("idtrans"));
                                        }catch (JSONException e){
                                            progressDialog.dismiss();
                                            writeToast("Erreur de données");
                                        }
                                    break;
                            }
                        }
                    });
                    tache.execute(URL+"lifoutacourant/APIS/confirmtransaction.php");
                    return retourJSON;
                }catch (Exception e){
                    progressDialog.dismiss();
                    writeToast("Erreur du serveur");
                    return retourJSON;
                }
            }else{
                progressDialog.dismiss();
                writeToast("Erreur connexion internet");
                return retourJSON;
            }
        }


    }
}
