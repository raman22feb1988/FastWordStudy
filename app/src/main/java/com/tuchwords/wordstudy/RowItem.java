package com.tuchwords.wordstudy;

public class RowItem {
    String label;
    String colour;

    public RowItem(String tag, String rgb)
    {
        label = tag;
        colour = rgb;
    }

    public String getTag() {
        return label;
    }

    public String getColour() {
        return colour;
    }
}