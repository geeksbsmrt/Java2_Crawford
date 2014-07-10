package com.adamcrawford.service.data;

import android.app.IntentService;
import android.content.Intent;

import com.adamcrawford.service.MainActivity;

import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

/**
 * Author:  Adam Crawford
 * Project: Service
 * Package: com.adamcrawford.service.data
 * File:    SyncService
 * Purpose: TODO Minimum 2 sentence description
 */
public class SyncService extends IntentService {

    public SyncService() {
        super("SyncService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        JSONObject members = null;

        String[] strings = new String[1];
        //TODO Replace with user input from extras
        strings[0] = "remnants%20of%20sanity";
        try {
            members = new CharSync().execute(strings).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        if (members != null) {

        } else {
            new MainActivity().printToast("test");
        }

    }
}
