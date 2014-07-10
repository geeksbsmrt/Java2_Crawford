package com.adamcrawford.service.data;

/**
 * Author:  Adam Crawford
 * Project: Service
 * Package: com.adamcrawford.service.data
 * File:    CharSync
 * Purpose: TODO Minimum 2 sentence description
 */
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class CharSync extends AsyncTask<String, Void, JSONObject> {

    private static final String TAG = "CharSync";

    public CharSync() {
    }

    @Override
    protected JSONObject doInBackground(String... strings) {

        URL url;
        StringBuffer contentBuffer = null;
        InputStream bin = null;

        try {
            //build URL from user inputs
            String loc = "https://us.battle.net/api/wow/guild/llane/" + strings[0] + "?fields=members";
            url = new URL(loc);

            //create connection
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            System.out.println(conn.getResponseMessage());
            //if valid response handle data
            if (conn.getResponseMessage().equals("OK")) {

                //create buffer stream
                bin = new BufferedInputStream(conn.getInputStream());
                //set byte size for buffer stream
                byte[] connBytes = new byte[1024];
                //bytes read by input stream
                int byteRead;
                //string to put data into
                String content;
                //stringbuffer
                contentBuffer = new StringBuffer();

                //while data is incoming from the HTTPRequest append data to the string buffer
                //BIS returns -1 when end of content reached
                while ((byteRead = bin.read(connBytes)) != - 1) {
                    content = new String(connBytes, 0, byteRead);
                    contentBuffer.append(content);
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "Guild not found");
            e.printStackTrace();
        } finally {
            if (bin != null) {
                try {
                    //close inputstream
                    bin.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        System.out.println(contentBuffer);
        JSONObject myJson = null;

        try {
            //create JSON from StringBuffer
            if (contentBuffer != null) {
                myJson = new JSONObject(String.valueOf(contentBuffer));
            }
        } catch (JSONException e) {
            Log.e(TAG, "JSON Error");
            e.printStackTrace();
        }

        return myJson;
    }

}