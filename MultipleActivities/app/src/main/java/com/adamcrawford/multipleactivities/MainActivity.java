package com.adamcrawford.multipleactivities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.adamcrawford.multipleactivities.data.DataStorage;
import com.adamcrawford.multipleactivities.data.SyncService;
import com.adamcrawford.multipleactivities.toon.ToonAdapter;
import com.adamcrawford.multipleactivities.toon.ToonConstructor;
import com.adamcrawford.multipleactivities.toon.ToonDetail;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Author:  Adam Crawford
 * Project: Service
 * Package: com.adamcrawford.multipleActivities
 * File:    MainActivity
 * Purpose: Controls application including UI, OnClick Functionality, Network Connectivity, and message handling.
 */

public class MainActivity extends Activity {

    private ListView charList;
    private static String TAG = "MainActivity";
    private Context context = this;
    public static Context sContext;
    static File env = Environment.getDataDirectory();
    static String fPath;
    static File storedFile;
    static String absPath;
    ArrayList<ToonConstructor> toonNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null){
            if (savedInstanceState.containsKey("toonNames")) {
                Log.e(TAG, savedInstanceState.getSerializable("toonNames").toString());
            }
        }

            sContext = this;

            final Boolean isConnected = getStatus(this);
            charList = (ListView) findViewById(R.id.charList);
            charList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    ToonConstructor toon = (ToonConstructor) charList.getItemAtPosition(i);
                    //Log.i(TAG, toon.toonName);
                    Intent intent = new Intent(context, ToonDetail.class);
                    intent.putExtra("name", toon.toonName);
                    intent.putExtra("level", toon.toonLevel);
                    intent.putExtra("icon", toon.toonIcon);
                    intent.putExtra("class", toon.tnClass);
                    intent.putExtra("color", toon.tnColor);
                    intent.putExtra("race", toon.tnRace);
                    intent.putExtra("role", toon.toonRole);
                    intent.putExtra("spec", toon.toonSpec);
                    intent.putExtra("connected", isConnected.toString());

                    startActivityForResult(intent, 0);
                }
            });

            // This string is static set for a guild that exists.  Future functionality of this application will allow the user to select a realm and guild name.  Week 1, "Services" has some of this functionality.  Since it is unnecessary for this assignment, it was removed.
            String fName = String.format("%s_%s", "Llane", "Remnants of Sanity");

            env = Environment.getDataDirectory();
            fPath = String.format("%s/data/%s/files/%s", env, this.getPackageName(), fName.toLowerCase());
            storedFile = new File(fPath);
            absPath = String.format("Reading File: %s", storedFile.getAbsolutePath());
            if (isConnected) {
                if ((savedInstanceState == null) || (!savedInstanceState.containsKey("toonNames"))) {
                    // This string is static set for a guild that exists.  Future functionality of this application will allow the user to select a realm and guild name.  Week 1, "Services" has some of this functionality.  Since it is unnecessary for this assignment, it was removed.
                    getData("remnants%20of%20sanity");
                }
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
    public Boolean getStatus(Context c){
        Log.i(TAG, "In getStatus");

        //build connectivity manager and network info
        ConnectivityManager conMan = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMan.getActiveNetworkInfo();

        //true/false based on connectivity
        return netInfo != null && netInfo.isConnected();
    }

    private void readFile (String fName) {
        Log.i(TAG, "In readFile");
        try {
            JSONObject jsonFromFile = new JSONObject(DataStorage.getInstance().readFile(fName, context));
            writeList(jsonFromFile);
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    //method to output values to list
    private void writeList (JSONObject data) {

        //Log.i("Write List Data: ",data.toString());

        //create string array
        toonNames = new ArrayList<ToonConstructor>();

        try {

            //get members array out of returned JSON object
            JSONArray dataArray = data.getJSONArray("members");

            //Log.i("DataArray Members: ", dataArray.toString());
            //loop through array putting member names into string array
            for (int i=0, j=dataArray.length(); i<j; i++) {
                JSONObject toon = (JSONObject) dataArray.get(i);
                ToonConstructor tc = new ToonConstructor(toon);
                toonNames.add(tc);
            }

            //Log.i("ToonNames: ", toonNames.toString());

            //build listAdapter
            ToonAdapter guildListAdapter = new ToonAdapter(this, R.id.charList, toonNames);

            //refresh the data
            guildListAdapter.notifyDataSetChanged();
            charList.setAdapter(guildListAdapter);
            //show view on screen
            charList.setVisibility(View.VISIBLE);
            //System.out.println(toonNames);

            //handle errors
        } catch (JSONException e) {
            Log.e(TAG, "Cannot build list");
            //e.printStackTrace();
        }
    }

    //method to display error to user
    private void printToast(String message) {
        //get active context
        Context c = getApplicationContext();
        //set length for message to be displayed
        int duration = Toast.LENGTH_LONG;
        //create message based on input parameter then display it
        Toast error = Toast.makeText(c, message, duration);
        error.show();
    }

    private void getData(String guild) {
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

    public static Context getContext () {
        return sContext;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "In Activity Result");
        if (resultCode == RESULT_OK && requestCode == 0) {
            Bundle extras = data.getExtras();
            //Log.i(TAG, extras.getString("rating"));
            String toonRating = extras.getString("rating");
            String tnName = extras.getString("name");
            //TODO Display alert
            displayRating(tnName, toonRating);
        }
    }

    public void displayRating (String name, String rating) {
        AlertDialog.Builder displayRating = new AlertDialog.Builder(this);
        displayRating.setTitle("Character: " + name).setMessage("Rating: " + rating).setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        displayRating.create();
        displayRating.show();
    }

    @Override
    public void onSaveInstanceState(@NotNull Bundle savedInstanceState){
        Log.i(TAG, "Saving Instance Data");
        if (toonNames != null && !toonNames.isEmpty()) {
            savedInstanceState.putSerializable("toonNames", toonNames);
        }
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState (@NotNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        Log.i(TAG, "Restoring Instacne State");
         if (savedInstanceState.containsKey("toonNames")) {
             if (savedInstanceState.getSerializable("toonNames") instanceof ArrayList<?>){
                 //Unchecked Cast Warning.  Could not find a good way around this.  This key will always contain the correct cast
                 toonNames = (ArrayList<ToonConstructor>) savedInstanceState.getSerializable("toonNames");
             }
             if (toonNames != null) {
                 Log.i(TAG, toonNames.toString());
                 ToonAdapter adapter = new ToonAdapter(this, R.id.charList, toonNames);
                 charList.setAdapter(adapter);
                 charList.setVisibility(View.VISIBLE);
                 //adapter.notifyDataSetChanged();
             }
        }
    }
}