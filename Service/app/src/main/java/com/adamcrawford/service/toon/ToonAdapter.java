package com.adamcrawford.service.toon;

/**
 * Author:  Adam Crawford
 * Project: Service
 * Package: com.adamcrawford.service.toon
 * File:    ToonAdapter
 * Purpose: Adapter for the ListView on the MainActivity screen.  Takes TonnConstructor objects.
 */
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.adamcrawford.service.R;

import java.util.ArrayList;


public class ToonAdapter extends ArrayAdapter<ToonConstructor>
{
    private Context context;
    private ArrayList<ToonConstructor> objects;

    public ToonAdapter(Context context, int resource, ArrayList<ToonConstructor> objects)
    {
        super(context, resource, objects);
        this.context = context;
        this.objects = objects;
        Log.i("TA Objects: ", objects.toString());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder holder;
        ToonConstructor toon = objects.get(position);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_toon, null);
            holder = new ViewHolder();
            holder.toonClassView = (TextView) convertView.findViewById(R.id.toonClass);
            holder.toonLevelView = (TextView) convertView.findViewById(R.id.toonLevel);
            holder.toonNameView = (TextView) convertView.findViewById(R.id.toonName);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.toonNameView.setText(toon.toonName);
        holder.toonLevelView.setText(toon.toonLevel);
        holder.toonClassView.setText(toon.tnClass);

        return convertView;
    }

    static class ViewHolder {
        TextView toonNameView;
        TextView toonLevelView;
        TextView toonClassView;
    }
}