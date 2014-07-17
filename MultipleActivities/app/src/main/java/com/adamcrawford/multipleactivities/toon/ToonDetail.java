package com.adamcrawford.multipleactivities.toon;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.adamcrawford.multipleactivities.R;
import com.adamcrawford.multipleactivities.data.image.SmartImageView;

import org.jetbrains.annotations.NotNull;


public class ToonDetail extends Activity {
    private String TAG = "ToonDetail";
    private int toonRating;
    private String tnName;
    TextView toonClass;
    TextView toonLevel;
    TextView toonName;
    TextView toonRace;
    TextView toonRole;
    TextView toonSpec;
    SmartImageView mySmartImage;
    RatingBar toonRatingBar;
    static final String STATE_CLASS = "toonClass";
    static final String STATE_LEVEL = "toonLevel";
    static final String STATE_NAME = "toonName";
    static final String STATE_RACE = "toonRace";
    static final String STATE_ROLE = "toonRole";
    static final String STATE_SPEC = "toonSpec";
    static final String STATE_RATE = "toonRating";
    static final String STATE_IMG = "toonImg";
    static final String STATE_COLOR = "toonColor";
    Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toon_detail);

        toonClass = (TextView) findViewById(R.id.detailToonClass);
        toonLevel = (TextView) findViewById(R.id.detailToonLevel);
        toonName = (TextView) findViewById(R.id.detailToonName);
        toonRace = (TextView) findViewById(R.id.detailToonRace);
        toonRole = (TextView) findViewById(R.id.detailToonRole);
        toonSpec = (TextView) findViewById(R.id.detailToonSpec);
        mySmartImage = (SmartImageView) findViewById(R.id.smartToonImg);
        toonRatingBar = (RatingBar) findViewById(R.id.toonRating);
        Button getWebInfo = (Button) findViewById(R.id.getWebInfo);
        Button share = (Button) findViewById(R.id.shareToon);

        getWebInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pageString = "http://us.battle.net/wow/en/character/llane/" + tnName.toLowerCase() +"/simple";
                Log.i(TAG, pageString);
                Uri toonPage = Uri.parse(pageString);
                Intent webIntent = new Intent(Intent.ACTION_VIEW, toonPage);
                Intent chooser = Intent.createChooser(webIntent, tnName + " Web Page");
                if (webIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(chooser);
                }
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pageString = "http://us.battle.net/wow/en/character/llane/" + tnName.toLowerCase() +"/simple";
                Intent email = new Intent(Intent.ACTION_SEND);
                email.putExtra(Intent.EXTRA_EMAIL, new String[]{"WoWGuildMembers@adamcrawford.com"});
                email.putExtra(Intent.EXTRA_SUBJECT, tnName);
                email.putExtra(Intent.EXTRA_TEXT, pageString);
                email.setType("message/rfc822");
                Intent chooser = Intent.createChooser(email, "Send with:");
                if (email.resolveActivity(getPackageManager()) != null) {
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

        extras = getIntent().getExtras();
        if (extras != null) {
            toonClass.setText(extras.getString("class"));
            toonLevel.setText(extras.getString("level"));
            toonName.setText(extras.getString("name"));
            toonRace.setText(extras.getString("race"));
            toonRole.setText(extras.getString("role"));
            toonSpec.setText(extras.getString("spec"));
            toonClass.setTextColor(Color.parseColor(extras.getString("color")));
            if (extras.getString("connected").equals("true")) {
                mySmartImage.setImageUrl("http://us.battle.net/static-render/us/"+extras.getString("icon"));
                mySmartImage.setVisibility(View.VISIBLE);
            }
            tnName = extras.getString("name");
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
        data.putExtra("rating", String.valueOf(toonRating));
        data.putExtra("name", tnName);
        setResult(RESULT_OK, data);
        super.finish();
    }

    @Override
    public void onSaveInstanceState(@NotNull Bundle savedInstanceState){
        Log.i(TAG, "Saving Instance Data");
        savedInstanceState.putString(STATE_CLASS, extras.getString("class"));
        savedInstanceState.putString(STATE_COLOR, extras.getString("color"));
        savedInstanceState.putString(STATE_LEVEL, extras.getString("level"));
        savedInstanceState.putString(STATE_NAME, extras.getString("name"));
        savedInstanceState.putString(STATE_RACE, extras.getString("race"));
        savedInstanceState.putString(STATE_ROLE, extras.getString("role"));
        savedInstanceState.putString(STATE_SPEC, extras.getString("spec"));
        savedInstanceState.putString(STATE_IMG, extras.getString("icon"));
        savedInstanceState.putString(STATE_RATE, String.valueOf(toonRating));

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState (@NotNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        Log.i(TAG, "Restoring Instacne State");
        toonClass.setText(savedInstanceState.getString(STATE_CLASS));
        toonClass.setTextColor(Color.parseColor(savedInstanceState.getString(STATE_COLOR)));
        toonLevel.setText(savedInstanceState.getString(STATE_LEVEL));
        toonName.setText(savedInstanceState.getString(STATE_NAME));
        toonRace.setText(savedInstanceState.getString(STATE_RACE));
        toonRole.setText(savedInstanceState.getString(STATE_ROLE));
        toonSpec.setText(savedInstanceState.getString(STATE_SPEC));
        mySmartImage.setImageUrl("http://us.battle.net/static-render/us/"+savedInstanceState.getString(STATE_IMG));
        mySmartImage.setVisibility(View.VISIBLE);
        toonRatingBar.setRating(Float.valueOf(savedInstanceState.getString(STATE_RATE)));
    }
}
