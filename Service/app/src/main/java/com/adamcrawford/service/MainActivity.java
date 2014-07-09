package com.adamcrawford.service;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
