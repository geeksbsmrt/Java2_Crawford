package com.adamcrawford.actionbar.toon;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.adamcrawford.actionbar.R;
import com.adamcrawford.actionbar.data.image.SmartImageView;

import org.jetbrains.annotations.NotNull;

/**
 * Author:  Adam Crawford
 * Project: Fragments
 * Package: com.adamcrawford.actionbar
 * File:    ToonDetailsFragment
 * Purpose: Controls ToonDetail UI, OnClick Functionality.
 */

public class ToonDetailFragment extends Fragment implements View.OnClickListener {

    private String TAG = "TDF: ";
    public int toonRating;
    public String tnName;
    public TextView toonClass;
    public TextView toonLevel;
    public TextView toonName;
    public TextView toonRace;
    public TextView toonRole;
    public TextView toonSpec;
    public SmartImageView mySmartImage;
    public RatingBar toonRatingBar;
    private Bundle toonBundle;
    static final String STATE_CLASS = "toonClass";
    static final String STATE_LEVEL = "toonLevel";
    static final String STATE_NAME = "toonName";
    static final String STATE_RACE = "toonRace";
    static final String STATE_ROLE = "toonRole";
    static final String STATE_SPEC = "toonSpec";
    static final String STATE_RATE = "toonRating";
    static final String STATE_IMG = "toonImg";
    static final String STATE_COLOR = "toonColor";

    public interface onToonLoaded {
        void onActivityResult(int requestCode, int resultCode, Intent data);
    }

    private onToonLoaded mainParent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.i(TAG, "Inflating TDF");

        View myView = inflater.inflate(R.layout.activity_toon_detail, container);

        toonClass = (TextView) myView.findViewById(R.id.detailToonClass);
        toonLevel = (TextView) myView.findViewById(R.id.detailToonLevel);
        toonName = (TextView) myView.findViewById(R.id.detailToonName);
        toonRace = (TextView) myView.findViewById(R.id.detailToonRace);
        toonRole = (TextView) myView.findViewById(R.id.detailToonRole);
        toonSpec = (TextView) myView.findViewById(R.id.detailToonSpec);
        mySmartImage = (SmartImageView) myView.findViewById(R.id.smartToonImg);
        toonRatingBar = (RatingBar) myView.findViewById(R.id.toonRating);
        Button getWebInfo = (Button) myView.findViewById(R.id.getWebInfo);
        Button share = (Button) myView.findViewById(R.id.shareToon);

        getWebInfo.setOnClickListener(this);
        share.setOnClickListener(this);

        toonRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                toonRating = (int) rating;
                Log.i(TAG, String.valueOf(toonRating));
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    Intent data = new Intent();
                    data.putExtra("rating", String.valueOf(toonRating));
                    data.putExtra("name", tnName);
                    try {
                        mainParent.onActivityResult(0, Activity.RESULT_OK, data);
                    } catch (NullPointerException e) {
                        //This throws an NPE when switching from Portrait to Landscape when the rating has been changed
                        e.printStackTrace();
                    }
                }
            }
        });
        return myView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey("DetailSaved")) {
            Log.i(TAG, "Has saved toon info");
        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            Bundle extras = getActivity().getIntent().getExtras();
            displayToon(extras);
        }
    }

    //called to display the saved toon data.  Breaks code when enabled.
    private void displaySaved (Bundle savedInstanceState) {
        Log.i(TAG, "Restoring Instance State");
        Log.i(TAG, savedInstanceState.getString(STATE_CLASS));
        Log.i(TAG, savedInstanceState.getString(STATE_NAME));
        Log.i(TAG, savedInstanceState.getString(STATE_LEVEL));
        Log.i(TAG, savedInstanceState.getString(STATE_RACE));
        Log.i(TAG, savedInstanceState.getString(STATE_ROLE));
        Log.i(TAG, savedInstanceState.getString(STATE_RATE));
        Log.i(TAG, savedInstanceState.getString(STATE_SPEC));
        Log.i(TAG, savedInstanceState.getString(STATE_IMG));
        //typically breaks here but it seems to hit this twice?
        toonClass.setText(savedInstanceState.getString(STATE_CLASS));
        toonClass.setTextColor(Color.parseColor(savedInstanceState.getString(STATE_COLOR)));
        toonLevel.setText(savedInstanceState.getString(STATE_LEVEL));
        toonName.setText(savedInstanceState.getString(STATE_NAME));
        toonRace.setText(savedInstanceState.getString(STATE_RACE));
        toonRole.setText(savedInstanceState.getString(STATE_ROLE));
        toonSpec.setText(savedInstanceState.getString(STATE_SPEC));
        mySmartImage.setImageUrl("http://us.battle.net/static-render/us/" + savedInstanceState.getString(STATE_IMG));
        mySmartImage.setVisibility(View.VISIBLE);
        toonRatingBar.setRating(Float.valueOf(savedInstanceState.getString(STATE_RATE)));
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof onToonLoaded) {
            try {
                mainParent = (onToonLoaded) activity;
            } catch (ClassCastException e) {
                throw new ClassCastException(activity.toString()
                        + " must implement OnToonLoaded");
            }
        }
    }

    public void displayToon(Bundle toon) {
        Log.i(TAG, "Displaying Toon");
        toonBundle = toon;
        toonClass.setText(toon.getString("class"));
        toonLevel.setText(toon.getString("level"));
        toonName.setText(toon.getString("name"));
        toonRace.setText(toon.getString("race"));
        toonRole.setText(toon.getString("role"));
        toonSpec.setText(toon.getString("spec"));
        toonClass.setTextColor(Color.parseColor(toon.getString("color")));
        if (toon.getString("connected").equals("true")) {
            mySmartImage.setImageUrl("http://us.battle.net/static-render/us/" + toon.getString("icon"));
            mySmartImage.setVisibility(View.VISIBLE);
        }
        tnName = toon.getString("name");
        //reset rating
        toonRatingBar.setRating(0);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.getWebInfo: {
                String pageString = "http://us.battle.net/wow/en/character/llane/" + tnName.toLowerCase() + "/simple";
                Log.i(TAG, pageString);
                Uri toonPage = Uri.parse(pageString);
                Intent webIntent = new Intent(Intent.ACTION_VIEW, toonPage);
                Intent chooser = Intent.createChooser(webIntent, tnName + " Web Page");
                if (webIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(chooser);
                }
                return;
            }
            case R.id.shareToon: {
                String pageString = "http://us.battle.net/wow/en/character/llane/" + tnName.toLowerCase() + "/simple";
                Intent email = new Intent(Intent.ACTION_SEND);
                email.putExtra(Intent.EXTRA_EMAIL, new String[]{"WoWGuildMembers@adamcrawford.com"});
                email.putExtra(Intent.EXTRA_SUBJECT, tnName);
                email.putExtra(Intent.EXTRA_TEXT, pageString);
                email.setType("message/rfc822");
                Intent chooser = Intent.createChooser(email, "Send with:");
                if (email.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(chooser);
                }
                return;
            }
            default: {
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NotNull Bundle savedInstanceState) {
        Log.i(TAG, "Saving Instance Data");
        try {
            savedInstanceState.putString("DetailSaved", "true");
            savedInstanceState.putString(STATE_CLASS, toonBundle.getString("class"));
            savedInstanceState.putString(STATE_COLOR, toonBundle.getString("color"));
            savedInstanceState.putString(STATE_LEVEL, toonBundle.getString("level"));
            savedInstanceState.putString(STATE_NAME, toonBundle.getString("name"));
            savedInstanceState.putString(STATE_RACE, toonBundle.getString("race"));
            savedInstanceState.putString(STATE_ROLE, toonBundle.getString("role"));
            savedInstanceState.putString(STATE_SPEC, toonBundle.getString("spec"));
            savedInstanceState.putString(STATE_IMG, toonBundle.getString("icon"));
            savedInstanceState.putString(STATE_RATE, String.valueOf(toonRating));
        } catch (NullPointerException e) {
            //throws NPE if switching from Landscape to Portrait without a toon selected.
            e.printStackTrace();
        }
        super.onSaveInstanceState(savedInstanceState);
    }
}
