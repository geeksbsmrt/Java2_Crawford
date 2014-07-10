package com.adamcrawford.service;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.adamcrawford.service.toon.ToonAdapter;
import com.adamcrawford.service.toon.ToonConstructor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class MainActivity extends Activity {

    private ListView charList;
    private static String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Boolean isConnected = getStatus(this);

        //get and create onclick for button
        Button submitButton = (Button) findViewById(R.id.submitButton);
        submitButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (isConnected) {
                    //dismiss keyboard
                    InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    manager.hideSoftInputFromWindow(v.getWindowToken(), 0);

                    //pull selected name out of spinner
                    String realmName = "Llane";
                    //			    System.out.println(realmName);

                    //pull entered user text
                    //String guild = guildName.getText().toString().replace(" ", "%20");
                    String guild = "Remnants of Sanity";

                    //ensure entry in guildEdit
                    if (! guild.equals("")) {
                        //call method to populate members list
                        updateList(realmName, guild);
                    } else {
                        //warn if edit is blank
                        printToast(getString(R.string.noEntry));
                    }
                } else {
                    //Throw not connected message
                    Log.i(TAG, "You are not connected");
                    printToast(getString(R.string.notConnected));
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

    //method to get information to put into listview
    private void updateList (String realm, String guild) {

        //call class to connect to network and pull info based on realm selection and input guild
        //JSONObject toons = new CharSync().execute(realm, guild).get();
        //TODO Use this to get information from local file
        JSONObject toons = null;
        try {
            toons = new JSONObject().put("char1","toonName");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //check for data inside JSON object
        if (toons != null) {
            //data exists - put it out to screen
            writeList(toons);
        } else {
            //data does not exist

            //clear screen if successful query previously run
            charList.setVisibility(View.GONE);
            //throw error to screen
            printToast(getString(R.string.notFound));
        }
    }

    //method to output values to list
    private void writeList (JSONObject data) {

        //System.out.println(data.toString());

        //create string array
        ArrayList<ToonConstructor> toonNames = new ArrayList<ToonConstructor>();

        try {

            //get members array out of returned JSON object
            JSONArray dataArray = data.getJSONArray("members");

            //loop through array putting member names into string array
            for (int i=0, j=dataArray.length(); i<j; i++) {
                JSONObject toon = (JSONObject) dataArray.get(i);
                //toonNames.add(toon.getJSONObject("character").getString("name"));
                ToonConstructor tc = new ToonConstructor(toon);
                toonNames.add(tc);
            }

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
}
