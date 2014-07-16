package com.adamcrawford.multipleactivities.data;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.adamcrawford.multipleactivities.toon.ToonDetail;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * Author:  Adam Crawford
 * Project: Multiple Activities
 * Package: com.adamcrawford.multipleactivities.data
 * File:    ImageSync
 * Purpose: TODO Minimum 2 sentence description
 */
public class ImageSync extends IntentService {
    public ImageSync() {
        super("ImageSync");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        String imgUrl = extras.getString("img");
        Bitmap image = getImg(imgUrl);

        Messenger msgr = (Messenger) extras.get("msgr");
        Message msg = Message.obtain();

        if (image != null) {
            msg.arg1 = ToonDetail.RESULT_OK;
            msg.obj = image;
        }

        try {
            msgr.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private Bitmap getImg (String img) {
        Bitmap imgFile = null;
        String loc = "http://us.battle.net/static-render/us/" + img;
        BufferedInputStream bis = null;

        try {
            URL url = new URL(loc);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            if (conn.getResponseMessage().equals("Not Found")) {
                Log.e("ImgSync", "Image not found on server");
            }
            //if valid response handle data
            if (conn.getResponseMessage().equals("OK")) {
                bis = new BufferedInputStream(conn.getInputStream());
                imgFile = BitmapFactory.decodeStream(bis);
                Log.i("IS: ", "Connected storing img");
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bis != null) {
                try {
                    //close inputstream
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return imgFile;
    }
}
