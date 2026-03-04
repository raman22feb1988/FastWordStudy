package com.tuchwords.wordstudy;

public class Filter {
    int length;
    String query;
    String sort;
    String name;
    long serial;

    public Filter(int length, String query, String sort, String name, long serial) {
        this.length = length;
        this.query = query;
        this.sort = sort;
        this.name = name;
        this.serial = serial;
    }

    public String getName() {
        return name;
    }

    public int getLength() {
        return length;
    }

    public String getQuery() {
        return query;
    }

    public String getSort() {
        return sort;
    }

    public long getSerial() {
        return serial;
    }
}