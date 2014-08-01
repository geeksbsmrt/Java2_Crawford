package com.adamcrawford.actionbar.toon;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.adamcrawford.actionbar.MainActivity;
import com.adamcrawford.actionbar.R;
import com.adamcrawford.actionbar.data.DataStorage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;


/**
 * Author:  Adam Crawford
 * Project: Fragments
 * Package: com.adamcrawford.actionbar
 * File:    ToonDetail
 * Purpose: Controls data on the ToonDetail page.  Is passed toon information based on which character is selected from MainActivity.  Passes back the toon rating selected by the user.
 */

public class ToonDetail extends Activity {
    private String TAG = "ToonDetail";
    ToonDetailFragment tdf;
    private Boolean isLandscape;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTheme(MainActivity.theme);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.i(TAG, "is Landscape");
            isLandscape = true;
            finish();
        } else {
            isLandscape = false;
        }

        setContentView(R.layout.fragment_toon_detail);

        tdf = (ToonDetailFragment) getFragmentManager().findFragmentById(R.id.toonDetailFragment);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish() {
        Log.i(TAG, "In finsh");
        File prefsFile = new File(MainActivity.fPath + "_favs");
        if (tdf.toonRating != 0) {
            JSONObject toonJSON = null;
            JSONObject fileJSON;
            JSONObject favJSON = new JSONObject();
            JSONArray members;
            try {
                fileJSON = new JSONObject(DataStorage.getInstance().readFile(MainActivity.fName, this));
                JSONArray fileMembers =  fileJSON.getJSONArray("members");
                int JSONlength = fileMembers.length();
                for (int i=0; i < JSONlength ; i++) {
                    String memberName = fileMembers.getJSONObject(i).getJSONObject("character").getString("name");
                    if (memberName.equals(tdf.tnName)){
                        JSONObject test = fileMembers.getJSONObject(i).getJSONObject("character");
                        test.put("rating", String.valueOf(tdf.toonRating));
                        toonJSON = fileMembers.getJSONObject(i);
                        Log.i(TAG, toonJSON.toString());
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (prefsFile.exists()){
                Log.i(TAG, "it exists");
                try {
                    JSONObject jsonFromFile = new JSONObject(DataStorage.getInstance().readFile(MainActivity.fName + "_favs", this));
                    members = jsonFromFile.getJSONArray("favs");
                    if (toonJSON != null) {
                        members.put(toonJSON);
                        favJSON.put("favs", members);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                members = new JSONArray();
                try {
                    if (toonJSON != null) {
                        members.put(toonJSON);
                        favJSON.put("favs", members);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            DataStorage.getInstance().writeFile(MainActivity.fName + "_favs", favJSON.toString(), this);
            Log.e(TAG, DataStorage.getInstance().readFile(MainActivity.fName + "_favs", this));
        }
        if (!isLandscape) {
            Log.i(TAG, "FINISHING");
            Intent data = new Intent();
            data.putExtra("rating", String.valueOf(tdf.toonRating));
            data.putExtra("name", tdf.tnName);
            setResult(RESULT_OK, data);
        }

        super.finish();
    }
}
