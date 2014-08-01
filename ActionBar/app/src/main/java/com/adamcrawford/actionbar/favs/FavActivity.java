package com.adamcrawford.actionbar.favs;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.adamcrawford.actionbar.MainActivity;
import com.adamcrawford.actionbar.R;


/**
 * Author:  Adam Crawford
 * Project: ActionBar
 * Package: com.adamcrawford.actionbar
 * File:    FavActivity
 * Purpose: TODO Minimum 2 sentence description
 */
public class FavActivity extends Activity {
    private String TAG = "FavActivity";
    FavActivityFragment faf;
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

        setContentView(R.layout.fragment_favs);

        faf = (FavActivityFragment) getFragmentManager().findFragmentById(R.id.favFrag);
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

}
