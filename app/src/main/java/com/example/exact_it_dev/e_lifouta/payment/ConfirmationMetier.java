package com.example.exact_it_dev.e_lifouta.payment;

import android.app.ProgressDialog;
import android.content.Context;

import com.example.exact_it_dev.e_lifouta.network.NetworkConnection;

import org.json.JSONObject;

/**
 * Created by EXACT-IT-DEV on 3/24/2018.
 */

public class ConfirmationMetier implements IPayment {
    Context context;
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

    @Override
    public String ConfirmationTransaction(String otp, String senderaccount, String recipientaccount, String transtype) {

        return null;
    }
}
