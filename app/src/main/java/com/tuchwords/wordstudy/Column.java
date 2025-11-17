package com.tuchwords.wordstudy;

public class Column {
    String column;
    String type;

    public Column(String column, String type) {
        this.column = column;
        this.type = type;
    }

    public String getColumn(boolean autoUnderscores) {
        return (autoUnderscores ? column.substring(1, column.length() - 1) : column);
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return column.substring(1, column.length() - 1);
    }
}