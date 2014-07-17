package com.adamcrawford.multipleactivities.toon;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.adamcrawford.multipleactivities.R;
import com.adamcrawford.multipleactivities.data.ImageSync;

import java.lang.ref.WeakReference;


public class ToonDetail extends Activity {
    private String TAG = "ToonDetail";
    private ImageView toonImg;
    private int toonRating;
    private String tnName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toon_detail);

        TextView toonClass = (TextView) findViewById(R.id.detailToonClass);
        TextView toonLevel = (TextView) findViewById(R.id.detailToonLevel);
        TextView toonName = (TextView) findViewById(R.id.detailToonName);
        TextView toonRace = (TextView) findViewById(R.id.detailToonRace);
        TextView toonRole = (TextView) findViewById(R.id.detailToonRole);
        TextView toonSpec = (TextView) findViewById(R.id.detailToonSpec);
        toonImg = (ImageView) findViewById(R.id.detailToonImg);
        RatingBar toonRatingBar = (RatingBar) findViewById(R.id.toonRating);
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



        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            toonClass.setText(extras.getString("class"));
            toonLevel.setText(extras.getString("level"));
            toonName.setText(extras.getString("name"));
            toonRace.setText(extras.getString("race"));
            toonRole.setText(extras.getString("role"));
            toonSpec.setText(extras.getString("spec"));
            toonClass.setTextColor(Color.parseColor(extras.getString("color")));
            if (extras.getString("connected").equals("true")) {
                getData(extras.getString("icon"));
            }
            tnName = extras.getString("name");
        } else {
            Log.wtf(TAG, "Got here without data.");
        }
    }

    private void getData(String img) {
        Intent getImg = new Intent(this, ImageSync.class);
        getImg.putExtra("img", img);

        final DataHandler handler = new DataHandler(this);

        Messenger msgr = new Messenger(handler);
        getImg.putExtra("msgr", msgr);
        startService(getImg);
    }

    private void setImg (Bitmap img) {
        toonImg.setImageBitmap(img);
        toonImg.setVisibility(View.VISIBLE);
    }

    private static class DataHandler extends Handler {
        private final WeakReference<ToonDetail> toonDetailWeakReference;
        public DataHandler(ToonDetail activity) {
            toonDetailWeakReference = new WeakReference<ToonDetail>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            ToonDetail activity = toonDetailWeakReference.get();
            if (activity != null) {
                Resources res = activity.getResources();
                Bitmap returned = (Bitmap) msg.obj;
                if (msg.arg1 == RESULT_OK && returned != null) {
                    Log.i(activity.TAG, "Data stored");
                    activity.setImg(returned);
                } else {
                    //Log.i(activity.TAG, "File Not Exist");
                    activity.printToast(res.getString(R.string.noImage));
                }
            }
        }
    }

    private void printToast(String message) {
        //get active context
        Context c = getApplicationContext();
        //set length for message to be displayed
        int duration = Toast.LENGTH_LONG;
        //create message based on input parameter then display it
        Toast error = Toast.makeText(c, message, duration);
        error.show();
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
}
