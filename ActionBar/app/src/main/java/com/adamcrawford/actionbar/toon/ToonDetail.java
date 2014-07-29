package com.adamcrawford.actionbar.toon;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.adamcrawford.actionbar.MainActivity;
import com.adamcrawford.actionbar.R;


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
