package com.adamcrawford.actionbar.favs;

import android.app.Fragment;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.adamcrawford.actionbar.R;
import com.adamcrawford.actionbar.toon.ToonConstructor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Author:  Adam Crawford
 * Project: ActionBar
 * Package: com.adamcrawford.actionbar.favs
 * File:    FavActivityFragment
 * Purpose: TODO Minimum 2 sentence description
 */
public class FavActivityFragment extends Fragment {
    public ListView favList;
    ArrayList<ToonConstructor> toonNames;
    private String TAG = "FAF";

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View myView = inflater.inflate(R.layout.activity_favs, container);

        favList = (ListView) myView.findViewById(R.id.favList);


        return myView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey("DetailSaved")) {
            Log.i(TAG, "Has saved toon info");
        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            Bundle extras = getActivity().getIntent().getExtras();
            String json = (String) extras.get("favs");
            JSONObject favJSON = null;
            try {
                favJSON = new JSONObject(json);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (favJSON != null) {
                writeList(favJSON, getActivity().getApplicationContext());
            }
        }
    }

    public void writeList(JSONObject data, Context context) {

        Log.e(TAG, data.toString());

        //create string array
        toonNames = new ArrayList<ToonConstructor>();

        try {
            //get members array out of returned JSON object
            JSONArray dataArray = data.getJSONArray("favs");

            //loop through array putting member names into string array
            for (int i = 0, j = dataArray.length(); i < j; i++) {
                JSONObject toon = (JSONObject) dataArray.get(i);
                ToonConstructor tc = new ToonConstructor(toon);
                toonNames.add(tc);
            }

            //build listAdapter
            FavAdapter guildListAdapter = new FavAdapter(context, R.id.favList, toonNames);

            //refresh the data
            guildListAdapter.notifyDataSetChanged();
            favList.setAdapter(guildListAdapter);
            //show view on screen
            favList.setVisibility(View.VISIBLE);

            //handle errors
        } catch (JSONException e) {
            Log.e(TAG, "Cannot build list");
        }
    }
}
