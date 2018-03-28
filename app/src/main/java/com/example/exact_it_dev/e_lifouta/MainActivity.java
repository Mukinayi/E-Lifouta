package com.example.exact_it_dev.e_lifouta;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.exact_it_dev.e_lifouta.network.NetworkConnection;
import com.example.exact_it_dev.e_lifouta.payment.Payment;
import com.example.exact_it_dev.e_lifouta.transfert.CompteCash;
import com.example.exact_it_dev.e_lifouta.virement.Virement;
import com.kosalgeek.genasync12.AsyncResponse;
import com.kosalgeek.genasync12.PostResponseAsyncTask;
import com.pushbots.push.Pushbots;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
        ProgressDialog progressDialog;
        AlertDialog.Builder alb;
        NetworkConnection networkConnection;
        AlertDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Pushbots.sharedInstance().registerForRemoteNotifications();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (id){
            case R.id.nav_pay:
                Intent intentPay = new Intent(getApplicationContext(), Payment.class);
                startActivity(intentPay);
                break;
            case R.id.nav_virement:
                Intent i = new Intent(MainActivity.this, Virement.class);
                startActivity(i);
                break;
            case R.id.nav_transfer:
                Intent tra = new Intent(MainActivity.this, CompteCash.class);
                startActivity(tra);
                break;
            case R.id.nav_solde:
                monsolde();
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void monsolde(){
        networkConnection = new NetworkConnection(MainActivity.this);
        alb = new AlertDialog.Builder(MainActivity.this);
        final StringBuilder str = new StringBuilder();
        final String URL = networkConnection.getUrl();
        final String numcompte = networkConnection.storedDatas("numcompte");
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setTitle("Demande de solde");
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Récupération du solde");
        progressDialog.show();

        HashMap dt = new HashMap();
        dt.put("moncompte",numcompte);
        if(networkConnection.isConnected()){
            try {
                PostResponseAsyncTask p = new PostResponseAsyncTask(MainActivity.this, dt, false, new AsyncResponse() {
                    @Override
                    public void processFinish(String s) {
                        switch (s){
                            case "180":
                                networkConnection.writeToast("Une erreure est survenue");
                                progressDialog.dismiss();
                                break;
                            default:
                                progressDialog.dismiss();
                                alb.setTitle("Information du solde");
                                str.append("Votre solde est de : \n\n\n");
                                str.append(String.format("%,.2f",Double.parseDouble(s)) +" CFA");
                                alb.setMessage(str.toString());
                                alb.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                dialog = alb.create();
                                dialog.show();

                                break;
                        }
                    }
                });
                p.execute(URL+"lifoutacourant/APIS/solde.php");
            }catch (Exception e){
                networkConnection.writeToast("Erreur connexion serveur");
                progressDialog.dismiss();
            }
        }else{
            networkConnection.writeToast("Erreur connexion internet");
            progressDialog.dismiss();
        }
    }
}
