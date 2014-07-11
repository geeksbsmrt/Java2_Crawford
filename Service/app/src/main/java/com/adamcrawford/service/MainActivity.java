package com.adamcrawford.service;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.adamcrawford.service.data.DataStorage;
import com.adamcrawford.service.data.SyncService;
import com.adamcrawford.service.toon.ToonAdapter;
import com.adamcrawford.service.toon.ToonConstructor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Author:  Adam Crawford
 * Project: Service
 * Package: com.adamcrawford.service
 * File:    MainActivity
 * Purpose: Controls application including UI, OnClick Functionality, Network Connectivity, and message handling.
 */

public class MainActivity extends Activity {

    private ListView charList;
    private static String TAG = "MainActivity";
    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //final Boolean isConnected = getStatus(this);

        charList = (ListView) findViewById(R.id.charList);
        final EditText guildName = (EditText) findViewById(R.id.guildText);

        //get and create onclick for button
        Button submitButton = (Button) findViewById(R.id.submitButton);
        submitButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                //pull entered user text
                String guild = guildName.getText().toString().replace(" ", "%20");
                //This is static set here but in a fully functional app, the user would be able to pick from a list of realms and it would update the name accordingly
                String realm = "Llane";
                String fName = String.format("%s_%s", realm, guildName.getText().toString());

                final Boolean isConnected = getStatus(context);

                if (isConnected) {
                    //dismiss keyboard
                    InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    manager.hideSoftInputFromWindow(v.getWindowToken(), 0);

                    //ensure entry in guildEdit
                    if (! guild.equals("")) {
                        //Call service to get data
                        getData(guild);
                    } else {
                        //warn if edit is blank
                        printToast(getString(R.string.noEntry));
                    }
                } else {
                    //Throw not connected message
                    Log.i(TAG, "You are not connected");
                    Log.i(TAG, fName);
                    printToast(getString(R.string.notConnected));
                    File env = Environment.getDataDirectory();
                    String fPath = String.format("%s/data/%s/files/%s", env, getPackageName(), fName.toLowerCase());
                    File storedFile = new File(fPath);
                    Log.i(TAG, storedFile.getAbsolutePath());
                    if (storedFile.exists()) {
                        try {
                            JSONObject jsonFromFile = new JSONObject(DataStorage.getInstance().readFile(fName, context));
                            writeList(jsonFromFile);
                            printToast(getString(R.string.staticData));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        charList.setVisibility(View.GONE);
                        printToast(getString(R.string.noLocal));
                    }
                }

            }
        });

    }

    //method to check for connectivity
    private Boolean getStatus(Context c){
        //Log.i(TAG, "In getStatus");
        //build connectivity manager and network info
        ConnectivityManager conMan = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMan.getActiveNetworkInfo();

        //true/false based on connectivity
        return netInfo != null && netInfo.isConnected();
    }

    //method to output values to list
    private void writeList (JSONObject data) {

       Log.i("Write List Data: ",data.toString());

        //create string array
        ArrayList<ToonConstructor> toonNames = new ArrayList<ToonConstructor>();

        try {

            //get members array out of returned JSON object
            JSONArray dataArray = data.getJSONArray("members");

            Log.i("DataArray Members: ", dataArray.toString());
            //loop through array putting member names into string array
            for (int i=0, j=dataArray.length(); i<j; i++) {
                JSONObject toon = (JSONObject) dataArray.get(i);
                //Log.i("Looping Toons: ", toon.toString());
                //toonNames.add(toon.getJSONObject("character").getString("name"));
                ToonConstructor tc = new ToonConstructor(toon);
                toonNames.add(tc);
            }

            Log.i("ToonNames: ", toonNames.toString());

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
            e.printStackTrace();
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
                JSONObject returned = (JSONObject) msg.obj;
                if (msg.arg1 == RESULT_OK && returned != null) {
                    Log.i(TAG, "Data returned");
                    //activity.writeList(returned);
                    try {
                        String gName = returned.getString("name");
                        String rName = returned.getString("realm");
                        String fName = String.format("%s_%s", rName, gName);
                        DataStorage.getInstance().writeFile(fName, returned.toString(), activity.context);
                        JSONObject json = new JSONObject(DataStorage.getInstance().readFile(fName, activity.context));
                        activity.writeList(json);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    Log.i(TAG, "No data");
                    activity.charList.setVisibility(View.GONE);
                    //throw error to screen

                    activity.printToast(activity.getString(R.string.notFound));
                }
            }
        }

    }
}

