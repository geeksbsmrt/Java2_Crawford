package com.adamcrawford.service.data;

import android.content.Context;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Author:  Adam Crawford
 * Project: Service
 * Package: com.adamcrawford.service.data
 * File:    DataStorage
 * Purpose: TODO Minimum 2 sentence description
 */
public class DataStorage {
    private static DataStorage dataInstance = null;

    public static DataStorage getInstance() {
        if (dataInstance == null) {
           dataInstance = new DataStorage();
        }
        return dataInstance;
    }

    private DataStorage() {
    }

    public void writeFile (String fileName, String data, Context context) {

        try {
            FileOutputStream fos = context.openFileOutput(fileName.toLowerCase(), Context.MODE_PRIVATE);
            fos.write(data.getBytes());
            fos.close();
            Log.i("Data Storage", "File Written");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public String readFile (String fileName, Context context){
        Log.i("Reading file: ", fileName);
        try {
            FileInputStream fis = context.openFileInput(fileName.toLowerCase());
            BufferedInputStream bis = new BufferedInputStream(fis);
            byte[] contentBytes = new byte[1024];
            int bytesRead;
            String content;
            StringBuffer contentBuffer = new StringBuffer();

            while ((bytesRead = bis.read(contentBytes)) != -1) {
                content = new String(contentBytes, 0, bytesRead);
                contentBuffer.append(content);
            }
            Log.i("Data Storage", String.valueOf(contentBuffer));
            return String.valueOf(contentBuffer);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
