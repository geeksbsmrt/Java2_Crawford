package com.adamcrawford.multipleactivities.toon;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.adamcrawford.multipleactivities.R;
import com.adamcrawford.multipleactivities.data.ImageSync;

import java.lang.ref.WeakReference;


public class ToonDetail extends Activity {
    private String TAG = "ToonDetail";
    private ImageView toonImg;

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
                    //TODO get image
                    activity.setImg(returned);
                } else {
                    Log.i(activity.TAG, "File Not Exist");
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
}
