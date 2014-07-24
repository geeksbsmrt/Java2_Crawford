package com.adamcrawford.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.adamcrawford.fragments.toon.ToonAdapter;
import com.adamcrawford.fragments.toon.ToonConstructor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Author:  Adam Crawford
 * Project: Fragments
 * Package: com.adamcrawford.fragments
 * File:    MainActivityFragment
 * Purpose: Controls MainActivity UI, OnClick Functionality.
 */

public class MainActivityFragment extends Fragment {
    String TAG = "MAF: ";
    public ListView charList;
    ArrayList<ToonConstructor> toonNames;

    public interface OnToonSelected {
        void onToonSelected(ToonConstructor toon, Boolean connected);
    }

    private OnToonSelected parentActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.i(TAG, "Inflating activity_main from fragment");
        View myView = inflater.inflate(R.layout.activity_main, container);

        charList = (ListView) myView.findViewById(R.id.charList);
        charList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ToonConstructor toon = (ToonConstructor) charList.getItemAtPosition(i);
                parentActivity.onToonSelected(toon, MainActivity.isConnected);
            }
        });
        return myView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnToonSelected) {
            try {
                parentActivity = (OnToonSelected) activity;
            } catch (ClassCastException e) {
                throw new ClassCastException(activity.toString()
                        + " must implement OnToonSelected");
            }
        }
    }

    public void writeList(JSONObject data, Context context) {

        //create string array
        toonNames = new ArrayList<ToonConstructor>();

        try {
            //get members array out of returned JSON object
            JSONArray dataArray = data.getJSONArray("members");

            //loop through array putting member names into string array
            for (int i = 0, j = dataArray.length(); i < j; i++) {
                JSONObject toon = (JSONObject) dataArray.get(i);
                ToonConstructor tc = new ToonConstructor(toon);
                toonNames.add(tc);
            }

            //build listAdapter
            ToonAdapter guildListAdapter = new ToonAdapter(context, R.id.charList, toonNames);

            //refresh the data
            guildListAdapter.notifyDataSetChanged();
            charList.setAdapter(guildListAdapter);
            //show view on screen
            charList.setVisibility(View.VISIBLE);

            //handle errors
        } catch (JSONException e) {
            Log.e(TAG, "Cannot build list");
        }
    }

    public void displayRating(String name, String rating) {
        AlertDialog.Builder displayRating = new AlertDialog.Builder(MainActivity.getContext());
        displayRating.setTitle("Character: " + name).setMessage("Rating: " + rating).setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        displayRating.create();
        displayRating.show();
    }
}
