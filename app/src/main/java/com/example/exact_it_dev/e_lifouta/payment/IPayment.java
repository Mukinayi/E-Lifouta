package com.example.exact_it_dev.e_lifouta.payment;

import org.json.JSONObject;

/**
 * Created by EXACT-IT-DEV on 3/24/2018.
 */

public interface IPayment {
    public JSONObject Payer(String senderaccount, String recipientaccount, double amount, String invoicenumber, String motif, String senderpin);
    public JSONObject ConfirmationTransaction(String otp,String senderaccount,String recipientaccount,String transtype);
}
