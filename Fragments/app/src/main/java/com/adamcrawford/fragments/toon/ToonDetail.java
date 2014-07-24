package com.adamcrawford.fragments.toon;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.adamcrawford.fragments.R;

import org.jetbrains.annotations.NotNull;


/**
 * Author:  Adam Crawford
 * Project: Fragments
 * Package: com.adamcrawford.fragments
 * File:    ToonDetail
 * Purpose: Controls data on the ToonDetail page.  Is passed toon information based on which character is selected from MainActivity.  Passes back the toon rating selected by the user.
 */

public class ToonDetail extends Activity implements ToonDetailFragment.onToonLoaded {
    private String TAG = "ToonDetail";
    static final String STATE_CLASS = "toonClass";
    static final String STATE_LEVEL = "toonLevel";
    static final String STATE_NAME = "toonName";
    static final String STATE_RACE = "toonRace";
    static final String STATE_ROLE = "toonRole";
    static final String STATE_SPEC = "toonSpec";
    static final String STATE_RATE = "toonRating";
    static final String STATE_IMG = "toonImg";
    static final String STATE_COLOR = "toonColor";
    ToonDetailFragment tdf;
    Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            finish();
        }

        setContentView(R.layout.fragment_toon_detail);

        tdf = (ToonDetailFragment) getFragmentManager().findFragmentById(R.id.toonDetailFragment);

        extras = getIntent().getExtras();
        if (extras != null) {
            Log.i(TAG, extras.getString("name"));
            tdf.displayToon(extras);
        } else {
            Log.wtf(TAG, "Got here without data.");
        }
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
        Log.i(TAG, "FINISHING");
        Intent data = new Intent();
        data.putExtra("rating", String.valueOf(tdf.toonRating));
        data.putExtra("name", tdf.tnName);
        setResult(RESULT_OK, data);
        super.finish();
    }

    @Override
    public void onSaveInstanceState(@NotNull Bundle savedInstanceState) {
        Log.i(TAG, "Saving Instance Data");
        savedInstanceState.putString(STATE_CLASS, extras.getString("class"));
        savedInstanceState.putString(STATE_COLOR, extras.getString("color"));
        savedInstanceState.putString(STATE_LEVEL, extras.getString("level"));
        savedInstanceState.putString(STATE_NAME, extras.getString("name"));
        savedInstanceState.putString(STATE_RACE, extras.getString("race"));
        savedInstanceState.putString(STATE_ROLE, extras.getString("role"));
        savedInstanceState.putString(STATE_SPEC, extras.getString("spec"));
        savedInstanceState.putString(STATE_IMG, extras.getString("icon"));
        savedInstanceState.putString(STATE_RATE, String.valueOf(tdf.toonRating));

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(@NotNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        Log.i(TAG, "Restoring Instacne State");
        tdf.toonClass.setText(savedInstanceState.getString(STATE_CLASS));
        tdf.toonClass.setTextColor(Color.parseColor(savedInstanceState.getString(STATE_COLOR)));
        tdf.toonLevel.setText(savedInstanceState.getString(STATE_LEVEL));
        tdf.toonName.setText(savedInstanceState.getString(STATE_NAME));
        tdf.toonRace.setText(savedInstanceState.getString(STATE_RACE));
        tdf.toonRole.setText(savedInstanceState.getString(STATE_ROLE));
        tdf.toonSpec.setText(savedInstanceState.getString(STATE_SPEC));
        tdf.mySmartImage.setImageUrl("http://us.battle.net/static-render/us/" + savedInstanceState.getString(STATE_IMG));
        tdf.mySmartImage.setVisibility(View.VISIBLE);
        tdf.toonRatingBar.setRating(Float.valueOf(savedInstanceState.getString(STATE_RATE)));
    }
}
