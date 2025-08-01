package com.tuchwords.wordstudy;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class ColourAdapter extends ArrayAdapter<Pair<String, String>> {
    LayoutInflater inflater;
    boolean mode;
    String white;

    public ColourAdapter(Context context, int resourceId, int textviewId, List<Pair<String, String>> list, Activity parentActivity, boolean name) {
        super(context, resourceId, textviewId, list);
        inflater = parentActivity.getLayoutInflater();
        mode = name;

        int nightModeFlags =
                context.getResources().getConfiguration().uiMode &
                        Configuration.UI_MODE_NIGHT_MASK;
        white = (nightModeFlags == Configuration.UI_MODE_NIGHT_YES ? "#000000" : "#FFFFFF");
    }

    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        Pair<String, String> rowItem = getItem(position);
        View rowview = inflater.inflate(R.layout.colour, null, true);

        String colour = rowItem.second;
        TextView t1 = rowview.findViewById(R.id.textview41);
        t1.setText(mode ? rowItem.first : colour);
        if (colour != null && !colour.equals(white)) {
            t1.setTextColor(Color.parseColor(colour));
        }

        return rowview;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Pair<String, String> rowItem = getItem(position);
        View rowview = inflater.inflate(R.layout.colour, null, true);

        String colour = rowItem.second;
        TextView t1 = rowview.findViewById(R.id.textview41);
        t1.setText(mode ? rowItem.first : colour);
        if (colour != null && !colour.equals(white)) {
            t1.setTextColor(Color.parseColor(colour));
        }

        return rowview;
    }
}