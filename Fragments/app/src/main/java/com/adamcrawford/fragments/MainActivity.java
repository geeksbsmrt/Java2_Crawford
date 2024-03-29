package com.adamcrawford.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.widget.Toast;

import com.adamcrawford.fragments.data.DataStorage;
import com.adamcrawford.fragments.data.SyncService;
import com.adamcrawford.fragments.toon.ToonConstructor;
import com.adamcrawford.fragments.toon.ToonDetail;
import com.adamcrawford.fragments.toon.ToonDetailFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.ref.WeakReference;

/**
 * Author:  Adam Crawford
 * Project: Fragments
 * Package: com.adamcrawford.fragments
 * File:    MainActivity
 * Purpose: Controls application including UI, OnClick Functionality, Network Connectivity, and message handling.
 */

public class MainActivity extends Activity implements MainActivityFragment.OnToonSelected, ToonDetailFragment.onToonLoaded {

    private static String TAG = "MainActivity";
    private Context context = this;
    public static Context sContext;
    static File env = Environment.getDataDirectory();
    static String fPath;
    static File storedFile;
    static String absPath;
    public static Boolean isConnected;
    private MainActivityFragment maf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_main_activity);

        maf = (MainActivityFragment) getFragmentManager().findFragmentById(R.id.MainActivityFragment);
        sContext = this;
        isConnected = getStatus(this);
    }

    public void processData() {
        // This string is static set for a guild that exists.  Future functionality of this application will allow the user to select a realm and guild name.  Week 1, "Services" has some of this functionality.  Since it is unnecessary for this assignment, it was removed.
        String fName = "Llane_Remnants of Sanity";

        env = Environment.getDataDirectory();
        fPath = String.format("%s/data/%s/files/%s", env, this.getPackageName(), fName.toLowerCase());
        storedFile = new File(fPath);
        absPath = String.format("Reading File: %s", storedFile.getAbsolutePath());
        if (getStatus(this)) {
            // This string is static set for a guild that exists.  Future functionality of this application will allow the user to select a realm and guild name.  Week 1, "Services" has some of this functionality.  Since it is unnecessary for this assignment, it was removed.
            getData("remnants%20of%20sanity");
        } else {
            //Throw not connected message
            Log.i(TAG, "You are not connected");
            printToast(getString(R.string.notConnected));
            if (storedFile.exists()) {
                printToast(getString(R.string.staticData));
                readFile(fName);
            } else {
                Log.i(TAG, "File Not Exist");
                printToast(getString(R.string.noLocal));
            }
        }
    }

    //method to check for connectivity
    public Boolean getStatus(Context c) {
        Log.i(TAG, "In getStatus");

        //build connectivity manager and network info
        ConnectivityManager conMan = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMan.getActiveNetworkInfo();

        //true/false based on connectivity
        return netInfo != null && netInfo.isConnected();
    }

    private void readFile(String fName) {
        Log.i(TAG, "In readFile");
        try {
            JSONObject jsonFromFile = new JSONObject(DataStorage.getInstance().readFile(fName, context));
            maf.writeList(jsonFromFile, context);
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    //method to display error to user
    private void printToast(String message) {
        //set length for message to be displayed
        int duration = Toast.LENGTH_LONG;
        //create message based on input parameter then display it
        Toast error = Toast.makeText(context, message, duration);
        error.show();
    }

    public void getData(String guild) {
        Intent getJSON = new Intent(this, SyncService.class);
        getJSON.putExtra("guild", guild);
        final DataHandler handler = new DataHandler(this);

        Messenger msgr = new Messenger(handler);
        getJSON.putExtra("msgr", msgr);
        startService(getJSON);
    }

    private static class DataHandler extends Handler {
        private final WeakReference<MainActivity> mainActivityWeakReference;

        public DataHandler(MainActivity activity) {
            mainActivityWeakReference = new WeakReference<MainActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity activity = mainActivityWeakReference.get();
            if (activity != null) {
                Resources res = activity.getResources();
                String returned = (String) msg.obj;
                if (msg.arg1 == RESULT_OK && returned != null) {
                    Log.i(TAG, "Data stored");
                    activity.readFile(returned);
                } else if (storedFile.exists()) {
                    activity.printToast(res.getString(R.string.staticData));
                    activity.readFile(returned);
                } else {
                    Log.i(TAG, "File Not Exist");
                    activity.printToast(res.getString(R.string.notReturned));
                    activity.printToast(res.getString(R.string.noLocal));
                }
            }
        }
    }

    public static Context getContext() {
        return sContext;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "In Activity Result");
        if (resultCode == RESULT_OK && requestCode == 0) {
            Bundle extras = data.getExtras();
            //Log.i(TAG, extras.getString("rating"));
            String toonRating = extras.getString("rating");
            String tnName = extras.getString("name");
            maf.displayRating(tnName, toonRating);
        }
    }

    private void startActivity(ToonConstructor selected, Boolean connected) {
        Intent intent = new Intent(context, ToonDetail.class);
        intent.putExtra("name", selected.toonName);
        intent.putExtra("level", selected.toonLevel);
        intent.putExtra("icon", selected.toonIcon);
        intent.putExtra("class", selected.tnClass);
        intent.putExtra("color", selected.tnColor);
        intent.putExtra("race", selected.tnRace);
        intent.putExtra("role", selected.toonRole);
        intent.putExtra("spec", selected.toonSpec);
        intent.putExtra("connected", connected.toString());

        Log.i(TAG, "Sending to ToonDetail");
        startActivityForResult(intent, 0);
    }

    public void onToonSelected(ToonConstructor selected, Boolean connected) {

        ToonDetailFragment tdf = (ToonDetailFragment) getFragmentManager().findFragmentById(R.id.toonDetailFragment);

        if (tdf != null && tdf.isInLayout()) {
            //reset image
            tdf.mySmartImage.setImageResource(R.drawable.blankprofilepic);
            Bundle tnDetails = new Bundle();
            tnDetails.putString("name", selected.toonName);
            tnDetails.putString("level", selected.toonLevel);
            tnDetails.putString("icon", selected.toonIcon);
            tnDetails.putString("class", selected.tnClass);
            tnDetails.putString("color", selected.tnColor);
            tnDetails.putString("race", selected.tnRace);
            tnDetails.putString("role", selected.toonRole);
            tnDetails.putString("spec", selected.toonSpec);
            tnDetails.putString("connected", connected.toString());

            tdf.displayToon(tnDetails);
        } else {
            startActivity(selected, connected);
        }
    }
}