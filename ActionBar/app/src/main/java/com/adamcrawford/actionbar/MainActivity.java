package com.adamcrawford.actionbar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.adamcrawford.actionbar.data.DataStorage;
import com.adamcrawford.actionbar.data.SyncService;
import com.adamcrawford.actionbar.toon.ToonConstructor;
import com.adamcrawford.actionbar.toon.ToonDetail;
import com.adamcrawford.actionbar.toon.ToonDetailFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.ref.WeakReference;

/**
 * Author:  Adam Crawford
 * Project: Fragments
 * Package: com.adamcrawford.actionbar
 * File:    MainActivity
 * Purpose: Controls application including UI, OnClick Functionality, Network Connectivity, and message handling.
 */

public class MainActivity extends Activity implements MainActivityFragment.OnToonSelected, ToonDetailFragment.onToonLoaded {

    private static String TAG = "MainActivity";
    private Context context = this;
    public static Context sContext;
    static File env = Environment.getDataDirectory();
    static String fPath;
    static File storedFile;
    static String absPath;
    public static Boolean isConnected;
    private MainActivityFragment maf;
    public static final int THEME_DARK = R.style.AppTheme;
    public static final int THEME_LIGHT = R.style.UserTheme;
    public static int theme = THEME_DARK;
    private SharedPreferences preferences;
    public static String rName;
    public static String gName;

    public static String getrName() {
        return rName;
    }

    public static String getgName() {
        return gName;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //TODO Pull UserPreference theme, if it exists
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        theme = preferences.getInt("theme", THEME_DARK);
        this.applyTheme(this);

        rName = preferences.getString("realm", "Llane");
        gName = preferences.getString("guild", "Remnants of Sanity");

        setContentView(R.layout.fragment_main_activity);

        maf = (MainActivityFragment) getFragmentManager().findFragmentById(R.id.MainActivityFragment);
        sContext = this;
        isConnected = getStatus(this);
    }

    public void processData() {
        // This string is static set for a guild that exists.  Future functionality of this application will allow the user to select a rName and guild name.  Week 1, "Services" has some of this functionality.  Since it is unnecessary for this assignment, it was removed.
        String fName = rName + "_" + gName;

        env = Environment.getDataDirectory();
        fPath = String.format("%s/data/%s/files/%s", env, this.getPackageName(), fName.toLowerCase());
        storedFile = new File(fPath);
        absPath = String.format("Reading File: %s", storedFile.getAbsolutePath());
        if (getStatus(this)) {
            // This string is static set for a guild that exists.  Future functionality of this application will allow the user to select a rName and guild name.  Week 1, "Services" has some of this functionality.  Since it is unnecessary for this assignment, it was removed.
            getData(gName.replace(" ", "%20").toLowerCase(), rName);
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
    public Boolean getStatus(Context c) {
        Log.i(TAG, "In getStatus");

        //build connectivity manager and network info
        ConnectivityManager conMan = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMan.getActiveNetworkInfo();

        //true/false based on connectivity
        return netInfo != null && netInfo.isConnected();
    }

    private void readFile(String fName) {
        Log.i(TAG, "In readFile");
        try {
            JSONObject jsonFromFile = new JSONObject(DataStorage.getInstance().readFile(fName, context));
            maf.writeList(jsonFromFile, context);
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private void filterFile(String fName, String filter) {
        Log.i(TAG, "In filterFile");
        try {
            JSONObject jsonFromFile = new JSONObject(DataStorage.getInstance().readFile(fName, context));
            JSONArray toonArray = jsonFromFile.getJSONArray("members");
            JSONArray filteredArray = new JSONArray();
            JSONObject filteredJSON = new JSONObject();
            for (int i = 0; i < toonArray.length(); i++) {
                JSONObject toon = toonArray.getJSONObject(i);
                JSONObject character = toon.getJSONObject("character");
                if (character.getString("name").toLowerCase().contains(filter.toLowerCase())){
                    filteredArray.put(toonArray.getJSONObject(i));
                }
            }
            filteredJSON.put("members", filteredArray);
            Log.e(TAG, "ToonArray: " + toonArray.toString());
            Log.e(TAG, "Filtered JSON: " + filteredJSON.toString());

            maf.writeList(filteredJSON, context);
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    //method to display error to user
    private void printToast(String message) {
        //set length for message to be displayed
        int duration = Toast.LENGTH_LONG;
        //create message based on input parameter then display it
        Toast error = Toast.makeText(context, message, duration);
        error.show();
    }

    public void getData(String guild, String realm) {
        Intent getJSON = new Intent(this, SyncService.class);
        getJSON.putExtra("guild", guild);
        getJSON.putExtra("realm", realm);
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

    public static Context getContext() {
        return sContext;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "In Activity Result");
        if (resultCode == RESULT_OK && requestCode == 0) {
            Bundle extras = data.getExtras();
            //Log.i(TAG, extras.getString("rating"));
            String toonRating = extras.getString("rating");
            String tnName = extras.getString("name");
            maf.displayRating(tnName, toonRating);
        }
    }

    private void startActivity(ToonConstructor selected, Boolean connected) {
        Intent intent = new Intent(context, ToonDetail.class);
        intent.putExtra("name", selected.toonName);
        intent.putExtra("level", selected.toonLevel);
        intent.putExtra("icon", selected.toonIcon);
        intent.putExtra("class", selected.tnClass);
        intent.putExtra("color", selected.tnColor);
        intent.putExtra("race", selected.tnRace);
        intent.putExtra("role", selected.toonRole);
        intent.putExtra("spec", selected.toonSpec);
        intent.putExtra("connected", connected.toString());

        Log.i(TAG, "Sending to ToonDetail");
        startActivityForResult(intent, 0);
    }

    public void onToonSelected(ToonConstructor selected, Boolean connected) {

        ToonDetailFragment tdf = (ToonDetailFragment) getFragmentManager().findFragmentById(R.id.toonDetailFragment);

        if (tdf != null && tdf.isInLayout()) {
            //reset image
            tdf.mySmartImage.setImageResource(R.drawable.blankprofilepic);
            Bundle tnDetails = new Bundle();
            tnDetails.putString("name", selected.toonName);
            tnDetails.putString("level", selected.toonLevel);
            tnDetails.putString("icon", selected.toonIcon);
            tnDetails.putString("class", selected.tnClass);
            tnDetails.putString("color", selected.tnColor);
            tnDetails.putString("race", selected.tnRace);
            tnDetails.putString("role", selected.toonRole);
            tnDetails.putString("spec", selected.toonSpec);
            tnDetails.putString("connected", connected.toString());

            tdf.displayToon(tnDetails);
        } else {
            startActivity(selected, connected);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search: {
                //TODO Search Action
                Log.i(TAG, "Search Action Item pressed");
                filterFile(rName + "_" + gName, "A");
                return true;
            }
            case R.id.action_favorite: {
                //TODO Favorite Action
                Log.i(TAG, "Fav Action Item pressed");

                return true;
            }
            case R.id.action_prefs: {
                //TODO Preferences Action
                Log.i(TAG, "Prefs Action Item pressed");
                SharedPreferences.Editor edit = preferences.edit();
                if (theme == THEME_DARK) {
                    Log.i(TAG, "Setting them to User");
                    changeTheme(this, THEME_LIGHT);
                    edit.putInt("theme", THEME_LIGHT);
                } else {
                    Log.i(TAG, "Setting theme to Default");
                    changeTheme(this, THEME_DARK);
                    edit.putInt("theme", THEME_DARK);
                }
                edit.apply();
                return true;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    private void changeTheme(Activity act, int newTheme){
        act.finish();
        theme = newTheme;
        act.startActivity(new Intent(act, act.getClass()));
    }

    private void applyTheme(Activity act) {
        act.setTheme(theme);
    }
}