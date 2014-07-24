package com.adamcrawford.fragments.toon;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
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

import com.adamcrawford.fragments.R;
import com.adamcrawford.fragments.data.image.SmartImageView;

/**
 * Author:  Adam Crawford
 * Project: Fragments
 * Package: com.adamcrawford.fragments
 * File:    ToonDetailsFragment
 * Purpose: Controls ToonDetail UI, OnClick Functionality.
 */

public class ToonDetailFragment extends Fragment {

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

    public interface onToonLoaded {

    }

    private onToonLoaded parentActivity;

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

        getWebInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pageString = "http://us.battle.net/wow/en/character/llane/" + tnName.toLowerCase() + "/simple";
                Log.i(TAG, pageString);
                Uri toonPage = Uri.parse(pageString);
                Intent webIntent = new Intent(Intent.ACTION_VIEW, toonPage);
                Intent chooser = Intent.createChooser(webIntent, tnName + " Web Page");
                if (webIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(chooser);
                }
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
            }
        });

        toonRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                toonRating = (int) rating;
                Log.i(TAG, String.valueOf(toonRating));
            }
        });

        return myView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof onToonLoaded) {
            try {
                parentActivity = (onToonLoaded) activity;
            } catch (ClassCastException e) {
                throw new ClassCastException(activity.toString()
                        + " must implement OnToonSelected");
            }
        }
    }

    public void displayToon(Bundle toon) {
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
}
