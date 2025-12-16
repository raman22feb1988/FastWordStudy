package com.tuchwords.wordstudy;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.os.Environment;
import android.text.Html;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.google.android.material.slider.Slider;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class sqliteDB extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "CSW2024.db";
    private static int DATABASE_VERSION;
    public Context lastActivity;
    ArrayList<String> last;
    boolean recreate;

    ArrayList<String> queryTables;
    ArrayList<String> comparators;
    ArrayList<String> comparator;

    public sqliteDB(Context context, int version, ArrayList<String> ultimate, boolean create) {
        super(context, DATABASE_NAME, null, version);
        // TODO Auto-generated constructor stub
        lastActivity = context;
        DATABASE_VERSION = version;
        last = ultimate;
        recreate = create;

        queryTables = new ArrayList<>();
        queryTables.add("words");

        comparators = new ArrayList<>();
        comparators.add("=");
        comparators.add(">");
        comparators.add("<");
        comparators.add(">=");
        comparators.add("<=");
        comparators.add("!=");

        comparator = new ArrayList<>();
        comparator.add("IN");
        comparator.add("NOT IN");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
                "create table if not exists words(_word_ text collate nocase primary key, _length_ integer, _alphagram_ text collate nocase, _definition_ text collate nocase, _probability_ real, _back_ text collate nocase, _front_ text collate nocase, _tag_ text collate nocase, _page_ integer, _answers_ integer, _csw24_ integer, _csw21_ integer, _csw19_ integer, _csw15_ integer, _csw12_ integer, _csw07_ integer, _nwl23_ integer, _nwl20_ integer, _nwl18_ integer, _twl06_ integer, _nswl23_ integer, _nswl20_ integer, _nswl18_ integer, _wims_ integer, _cel21_ integer, _serial_ integer, _position_ integer, _timestamp_ text collate nocase, _reverse_ text collate nocase, _zetagram_ text collate nocase, _no_a_ integer, _no_b_ integer, _no_c_ integer, _no_d_ integer, _no_e_ integer, _no_f_ integer, _no_g_ integer, _no_h_ integer, _no_i_ integer, _no_j_ integer, _no_k_ integer, _no_l_ integer, _no_m_ integer, _no_n_ integer, _no_o_ integer, _no_p_ integer, _no_q_ integer, _no_r_ integer, _no_s_ integer, _no_t_ integer, _no_u_ integer, _no_v_ integer, _no_w_ integer, _no_x_ integer, _no_y_ integer, _no_z_ integer, _vowels_ integer, _consonants_ integer, _points_ integer, _power_ integer)"
        );
        db.execSQL(
                "create table if not exists filters(_length_ integer, _query_ text collate nocase, _counter_ integer, _sort_ text collate nocase, _name_ text collate nocase, _serial_ integer primary key)"
        );
        db.execSQL(
                "create table if not exists colours(_tag_ text collate nocase, _colour_ text collate nocase)"
        );
        db.execSQL(
                "create table if not exists zoom(_activity_ text collate nocase, _rows_ integer, _columns_ integer, _size_ integer, _spinner_ integer)"
        );
        db.execSQL(
                "create table if not exists prefixes(_prefix_ text collate nocase, _before_ text collate nocase)"
        );
        db.execSQL(
                "create table if not exists suffixes(_suffix_ text collate nocase, _after_ text collate nocase)"
        );
        db.execSQL(
                "create table if not exists letters(_letter_ text collate nocase, _frequency_ integer, _points_ integer, _is_vowel_ integer, _is_consonant_ integer, _is_power_ integer)"
        );
    }

    public void initialize() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(
                "DROP TABLE if exists filters"
        );
        db.execSQL(
                "create table if not exists filters(_length_ integer, _query_ text collate nocase, _counter_ integer, _sort_ text collate nocase, _name_ text collate nocase, _serial_ integer primary key)"
        );
        db.execSQL(
                "UPDATE zoom SET _activity_ = \"Grid\" WHERE _activity_ = \"Study\""
        );
        ContentValues contentValues = new ContentValues();
        contentValues.put("_activity_", "List");
        contentValues.put("_rows_", 100);
        contentValues.put("_columns_", 0);
        contentValues.put("_size_", 11);
        contentValues.put("_spinner_", 20);
        db.insert("zoom", null, contentValues);

        db.execSQL(
                "create table if not exists letters(_letter_ text collate nocase, _frequency_ integer, _points_ integer, _is_vowel_ integer, _is_consonant_ integer, _is_power_ integer)"
        );

        String[] alphabetList = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
        int[] frequency = {9, 2, 2, 4, 12, 2, 3, 2, 9, 1, 1, 4, 2, 6, 8, 2, 1, 6, 4, 6, 4, 2, 2, 1, 2, 1};
        int[] point = {1, 3, 3, 2, 1, 4, 2, 4, 1, 8, 5, 1, 3, 1, 1, 3, 10, 1, 1, 1, 1, 4, 4, 8, 4, 10};
        boolean[] vowel = {true, false, false, false, true, false, false, false, true, false, false, false, false, false, true, false, false, false, false, false, true, false, false, false, false, false};
        boolean[] consonant = {false, true, true, true, false, true, true, true, false, true, true, true, true, true, false, true, true, true, true, true, false, true, true, true, true, true};
        boolean[] power = {false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, true, false, false, false, false, false, false, true, false, true};

        for (int alphabetPosition = 0; alphabetPosition < alphabetList.length; alphabetPosition++) {
            ContentValues letterValues = new ContentValues();
            letterValues.put("_letter_", alphabetList[alphabetPosition]);
            letterValues.put("_frequency_", frequency[alphabetPosition]);
            letterValues.put("_points_", point[alphabetPosition]);
            letterValues.put("_is_vowel_", vowel[alphabetPosition]);
            letterValues.put("_is_consonant_", consonant[alphabetPosition]);
            letterValues.put("_is_power_", power[alphabetPosition]);
            db.insert("letters", null, letterValues);
        }

        db.execSQL(
                "DELETE FROM prefixes"
        );

        db.execSQL(
                "DELETE FROM suffixes"
        );

        ArrayList<Pair<String, String>> myPrefixes = new ArrayList<>();
        myPrefixes.add(new Pair<>("", ""));

        for (Pair<String, String> columnItem : myPrefixes) {
            ContentValues prefixValues = new ContentValues();
            prefixValues.put("_prefix_", columnItem.first);
            prefixValues.put("_before_", columnItem.second);
            db.insert("prefixes", null, prefixValues);
        }

        ArrayList<Pair<String, String>> mySuffixes = new ArrayList<>();
        mySuffixes.add(new Pair<>("", ""));
        mySuffixes.add(new Pair<>("-", "E"));
        mySuffixes.add(new Pair<>("-I", "Y"));

        for (Pair<String, String> columnItem : mySuffixes) {
            ContentValues suffixValues = new ContentValues();
            suffixValues.put("_suffix_", columnItem.first);
            suffixValues.put("_after_", columnItem.second);
            db.insert("suffixes", null, suffixValues);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        try {
            if (last != null) {
                for (String myQueries : last) {
                    db.execSQL(myQueries);
                }

                if (recreate) {
                    onCreate(db);
                }
            }
        }
        catch (SQLiteException e) {
            alertBox("Error", e.toString(), lastActivity);
        }
    }

    public void dropTable(Context activity)
    {
        DATABASE_VERSION++;

        ArrayList<String> dropStatements = new ArrayList<>();
        dropStatements.add("DROP TABLE if exists words");
        dropStatements.add("DROP TABLE if exists scores");
        dropStatements.add("DROP TABLE if exists colours");
        dropStatements.add("DROP TABLE if exists zoom");
        dropStatements.add("DROP TABLE if exists prefixes");
        dropStatements.add("DROP TABLE if exists suffixes");
        dropStatements.add("DROP TABLE if exists letters");

        MainActivity myActivity = (MainActivity) activity;
        myActivity.reload(dropStatements, DATABASE_VERSION, true);
    }

    public void myQuery(String sqlQuery, Context activity) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            String[] tokens = sqlQuery.split("\\s+");
            ArrayList<String> theQueries = new ArrayList<>();
            theQueries.add(sqlQuery);
            if (tokens[0].equalsIgnoreCase("ALTER") || tokens[0].equalsIgnoreCase("DROP") || tokens[0].equalsIgnoreCase("CREATE") || tokens[0].equalsIgnoreCase("TRUNCATE"))
            {
                DATABASE_VERSION++;

                MainActivity myActivity = (MainActivity) activity;
                myActivity.reload(theQueries, DATABASE_VERSION, false);
            }
            else
            {
                db.execSQL(sqlQuery);
            }

            refresh(activity);
        }
        catch (SQLiteException e) {
            alertBox("Error", e.toString(), activity);
        }
    }

    public void exportDB(Context situation)
    {
        LayoutInflater myInflater = LayoutInflater.from(situation);
        final View myCustomView = myInflater.inflate(R.layout.progressbar, null);

        ProgressBar p4 = myCustomView.findViewById(R.id.progressbar1);
        TextView t40 = myCustomView.findViewById(R.id.textview57);
        TextView t41 = myCustomView.findViewById(R.id.textview58);

        AlertDialog myDialog = new AlertDialog.Builder(situation)
                .setTitle("Exporting CSV")
                .setView(myCustomView)
                .create();
        myDialog.show();

        SQLiteDatabase db = this.getReadableDatabase();

        Thread thread4 = new Thread(() -> {
            File[] inputDir = ContextCompat.getExternalFilesDirs(situation, Environment.DIRECTORY_DOCUMENTS);
            File exportDir = inputDir[0];

            ArrayList<String> tables = getTableNames();
            StringBuilder outputDir = new StringBuilder();
            try {
                for (String table : tables) {
                    File file = new File(exportDir, table + ".csv");
                    file.createNewFile();
                    CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
                    Cursor curCSV = db.rawQuery("SELECT * FROM " + table, null);
                    String[] columnsList = curCSV.getColumnNames();
                    for (int number = 0; number < columnsList.length; number++) {
                        columnsList[number] = columnsList[number].substring(1, columnsList[number].length() - 1);
                    }
                    csvWrite.writeNext(columnsList);
                    double myLine = 0.0;
                    double myStep = curCSV.getCount() / 100.0;
                    while (curCSV.moveToNext()) {
                        String[] arrStr = new String[columnsList.length];
                        for (int index = 0; index < columnsList.length; index++) {
                            arrStr[index] = curCSV.getString(index);
                        }
                        csvWrite.writeNext(arrStr);
                        myLine++;
                        if (myLine % myStep < 1 || myLine == 1.0)
                        {
                            updateProgressBar(situation, p4, t40, t41, myDialog, (int) (myLine / myStep), ((int) myLine) + "/" + curCSV.getCount());
                        }
                    }
                    csvWrite.close();
                    curCSV.close();
                    outputDir.append("\nSaved ").append(table).append(" table to ").append(file.getAbsolutePath()).append(".");
                    uiThreadBox("Export CSV", "Export CSV complete." + new String(outputDir), situation);
                }
            } catch (Exception sqlEx) {
                myDialog.dismiss();
                uiThreadBox("Error", sqlEx.toString(), situation);
            } finally {
                myDialog.dismiss();
            }
        });

        thread4.start();
    }

    public void importDB(Context situation)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        File[] inputDir = ContextCompat.getExternalFilesDirs(situation, Environment.DIRECTORY_DOCUMENTS);
        File exportDir = inputDir[0];
        File storageDir = Environment.getExternalStorageDirectory();
        String dataDir = exportDir.getAbsolutePath() + "/words.csv";
        String path = dataDir.substring((storageDir.getAbsolutePath()).length() + 1);
        String database = "words";

        LayoutInflater inflater = LayoutInflater.from(situation);
        final View yourCustomView = inflater.inflate(R.layout.path, null);

        TextView t4 = yourCustomView.findViewById(R.id.textview5);
        EditText e2 = yourCustomView.findViewById(R.id.edittext3);
        EditText e3 = yourCustomView.findViewById(R.id.edittext4);

        t4.setText(storageDir.getAbsolutePath() + "/");
        e2.setText(path);
        e3.setText(database);

        AlertDialog dialog = new AlertDialog.Builder(situation)
                .setTitle("File name")
                .setView(yourCustomView)
                .setPositiveButton("OK", (dialog1, whichButton) -> {
                    LayoutInflater myInflater = LayoutInflater.from(situation);
                    final View myCustomView = myInflater.inflate(R.layout.progressbar, null);

                    ProgressBar p2 = myCustomView.findViewById(R.id.progressbar1);
                    TextView t36 = myCustomView.findViewById(R.id.textview57);
                    TextView t37 = myCustomView.findViewById(R.id.textview58);

                    AlertDialog myDialog = new AlertDialog.Builder(situation)
                            .setTitle("Importing CSV")
                            .setView(myCustomView)
                            .create();
                    myDialog.show();

                    Thread thread2 = new Thread(() -> {
                        String databaseName = (e3.getText()).toString();
                        ArrayList<String> databases = getTableNames();
                        if (databases.contains(databaseName)) {
                            File file = new File(storageDir, (e2.getText()).toString());
                            try {
                                CSVReader csvRead = new CSVReader(new FileReader(file));
                                db.beginTransaction();
                                try {
                                    String[] columns = csvRead.readNext();
                                    String[] nextLine = csvRead.readNext();

                                    int lines = -1;
                                    BufferedReader reader = new BufferedReader(new FileReader(file));
                                    while (reader.readLine() != null) {
                                        lines++;
                                    }
                                    reader.close();

                                    double myLine = 0.0;
                                    double myStep = lines / 100.0;
                                    do {
                                        ContentValues contentValues = new ContentValues();
                                        for (int column = 0; column < columns.length; column++) {
                                            contentValues.put("_" + columns[column] + "_", nextLine[column]);
                                        }
                                        db.insert(databaseName, null, contentValues);
                                        nextLine = csvRead.readNext();
                                        myLine++;
                                        if (myLine % myStep < 1 || myLine == 1.0) {
                                            updateProgressBar(situation, p2, t36, t37, myDialog, (int) (myLine / myStep), ((int) myLine) + "/" + lines);
                                        }
                                    } while (nextLine != null);
                                    csvRead.close();
                                    db.setTransactionSuccessful();
                                    uiThreadRefresh(situation, true);
                                    uiThreadBox("Import CSV", "Import CSV complete.", situation);
                                } finally {
                                    db.endTransaction();
                                }
                            } catch (Exception e) {
                                uiThreadBox("Error", e.toString(), situation);
                            } finally {
                                myDialog.dismiss();
                            }
                        } else {
                            myDialog.dismiss();
                            uiThreadBox("Error", "Table '" + databaseName + "' not found. Create a new table with the name '" + databaseName + "' at first.", situation);
                        }
                    });

                    thread2.start();
                }).create();
        dialog.show();
    }

    public void exportLabels(Context situation)
    {
        LayoutInflater myInflater = LayoutInflater.from(situation);
        final View myCustomView = myInflater.inflate(R.layout.progressbar, null);

        ProgressBar p3 = myCustomView.findViewById(R.id.progressbar1);
        TextView t38 = myCustomView.findViewById(R.id.textview57);
        TextView t39 = myCustomView.findViewById(R.id.textview58);

        AlertDialog myDialog = new AlertDialog.Builder(situation)
                .setTitle("Exporting tags")
                .setView(myCustomView)
                .create();
        myDialog.show();

        SQLiteDatabase db = this.getReadableDatabase();

        Thread thread3 = new Thread(() -> {
            File[] inputDir = ContextCompat.getExternalFilesDirs(situation, Environment.DIRECTORY_DOCUMENTS);
            File exportDir = inputDir[0];

            File file = new File(exportDir, "tags.csv");
            try {
                file.createNewFile();
                CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
                Cursor curCSV = db.rawQuery("SELECT _word_, _tag_, _timestamp_ FROM words WHERE _timestamp_ != \"\" OR _tag_ != \"\"", null);
                String[] columnsList = curCSV.getColumnNames();
                for (int number = 0; number < columnsList.length; number++) {
                    columnsList[number] = columnsList[number].substring(1, columnsList[number].length() - 1);
                }
                csvWrite.writeNext(columnsList);
                double myLine = 0.0;
                double myStep = curCSV.getCount() / 100.0;
                while (curCSV.moveToNext()) {
                    String[] arrStr = new String[columnsList.length];
                    for (int index = 0; index < columnsList.length; index++) {
                        arrStr[index] = curCSV.getString(index);
                    }
                    csvWrite.writeNext(arrStr);
                    myLine++;
                    if (myLine % myStep < 1 || myLine == 1.0)
                    {
                        updateProgressBar(situation, p3, t38, t39, myDialog, (int) (myLine / myStep), ((int) myLine) + "/" + curCSV.getCount());
                    }
                }
                csvWrite.close();
                curCSV.close();
                uiThreadBox("Export tags", "Export tags complete.\nSaved tags to " + file.getAbsolutePath() + ".", situation);
            } catch (Exception sqlEx) {
                myDialog.dismiss();
                uiThreadBox("Error", sqlEx.toString(), situation);
            } finally {
                myDialog.dismiss();
            }
        });

        thread3.start();
    }

    public void importLabels(Context situation)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        File[] inputDir = ContextCompat.getExternalFilesDirs(situation, Environment.DIRECTORY_DOCUMENTS);
        File exportDir = inputDir[0];
        File storageDir = Environment.getExternalStorageDirectory();
        String dataDir = exportDir.getAbsolutePath() + "/tags.csv";
        String path = dataDir.substring((storageDir.getAbsolutePath()).length() + 1);

        LayoutInflater inflater = LayoutInflater.from(situation);
        final View yourCustomView = inflater.inflate(R.layout.message, null);

        TextView t3 = yourCustomView.findViewById(R.id.textview6);
        EditText e1 = yourCustomView.findViewById(R.id.edittext5);

        t3.setText(storageDir.getAbsolutePath() + "/");
        e1.setText(path);

        AlertDialog dialog = new AlertDialog.Builder(situation)
                .setTitle("File name")
                .setView(yourCustomView)
                .setPositiveButton("OK", (dialog1, whichButton) -> {
                    LayoutInflater myInflater = LayoutInflater.from(situation);
                    final View myCustomView = myInflater.inflate(R.layout.progressbar, null);

                    ProgressBar p1 = myCustomView.findViewById(R.id.progressbar1);
                    TextView t34 = myCustomView.findViewById(R.id.textview57);
                    TextView t35 = myCustomView.findViewById(R.id.textview58);

                    AlertDialog myDialog = new AlertDialog.Builder(situation)
                            .setTitle("Importing tags")
                            .setView(myCustomView)
                            .create();
                    myDialog.show();

                    Thread thread1 = new Thread(() -> {
                        File file = new File(storageDir, (e1.getText()).toString());
                        try {
                            CSVReader csvRead = new CSVReader(new FileReader(file));
                            db.beginTransaction();
                            try {
                                String[] columns = csvRead.readNext();
                                String[] nextLine = csvRead.readNext();

                                int lines = -1;
                                BufferedReader reader = new BufferedReader(new FileReader(file));
                                while (reader.readLine() != null) {
                                    lines++;
                                }
                                reader.close();

                                double myLine = 0.0;
                                double myStep = lines / 100.0;
                                do {
                                    ContentValues contentValues = new ContentValues();
                                    int wordIndex = 0;
                                    for (int column = 0; column < columns.length; column++) {
                                        if (columns[column].equals("word")) {
                                            wordIndex = column;
                                        } else {
                                            contentValues.put("_" + columns[column] + "_", nextLine[column]);
                                        }
                                    }
                                    db.update("words", contentValues, "_word_ = ?",
                                            new String[] {nextLine[wordIndex]});
                                    nextLine = csvRead.readNext();
                                    myLine++;
                                    if (myLine % myStep < 1 || myLine == 1.0) {
                                        updateProgressBar(situation, p1, t34, t35, myDialog, (int) (myLine / myStep), ((int) myLine) + "/" + lines);
                                    }
                                } while (nextLine != null);
                                csvRead.close();
                                db.setTransactionSuccessful();
                                uiThreadRefresh(situation, false);
                                uiThreadBox("Import tags", "Import tags complete.", situation);
                            } finally {
                                db.endTransaction();
                            }
                        } catch (Exception e) {
                            uiThreadBox("Error", e.toString(), situation);
                        } finally {
                            myDialog.dismiss();
                        }
                    });

                    thread1.start();
                }).create();
        dialog.show();
    }

    public List<Pair<String, String>> getAllLabels()
    {
        HashSet<String> labelsList = new HashSet<>();
        List<Pair<String, String>> columnItemList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT _tag_, _colour_ FROM colours", null);

        if (cursor.moveToLast()) {
            do {
                String label = cursor.getString(0);
                String colour = cursor.getString(1);

                if (!labelsList.contains(label)) {
                    labelsList.add(label);
                    columnItemList.add(new Pair<>(label, colour));
                }
            } while (cursor.moveToPrevious());
        }
        cursor.close();
        Collections.sort(columnItemList, (o1, o2) -> (o1.first).compareTo(o2.first));
        return columnItemList;
    }

    public List<Pair<String, String>> getAllColours()
    {
        HashSet<String> tagList = new HashSet<>();
        List<Pair<String, String>> rowItemList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT _tag_, _colour_ FROM colours", null);

        if (cursor.moveToLast()) {
            do {
                String label = cursor.getString(0);
                String colour = cursor.getString(1);

                if (!tagList.contains(colour)) {
                    tagList.add(colour);
                    rowItemList.add(new Pair<>(label, colour));
                }
            } while (cursor.moveToPrevious());
        }
        cursor.close();
        Collections.sort(rowItemList, (o1, o2) -> (o1.second).compareTo(o2.second));
        return rowItemList;
    }

    public HashMap<String, String> getColours()
    {
        HashMap<String, String> colourList = new HashMap<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT _tag_, _colour_ FROM colours", null);

        if (cursor.moveToFirst()) {
            do {
                String label = cursor.getString(0);
                String colour = cursor.getString(1);

                colourList.put(label, colour);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return colourList;
    }

    public void updateLabel(String line, String category) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("_tag_", category);

        long timestamp = System.currentTimeMillis();
        SimpleDateFormat iso8601Format = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        String simpleDateFormat = iso8601Format.format(timestamp);
        values.put("_timestamp_", simpleDateFormat);

        db.update("words", values, "_word_ = ?",
                new String[] {line});
    }

    public String getAllAnswers(String order)
    {
        StringBuilder unsolvedAnswers = new StringBuilder();
        int total = 1;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT _word_, _definition_, _back_, _front_, _length_, _csw24_, _csw19_, _csw15_, _csw12_, _csw07_, _nwl23_, _nwl18_, _twl06_, _nswl23_, _wims_, _cel21_ FROM words WHERE _alphagram_ = \"" + order + "\"", null);

        if (cursor.moveToFirst()) {
            do {
                String data = cursor.getString(0);
                String definition = cursor.getString(1);
                String back = cursor.getString(2);
                String front = cursor.getString(3);
                int length = cursor.getInt(4);
                int csw24 = cursor.getInt(5);
                int csw19 = cursor.getInt(6);
                int csw15 = cursor.getInt(7);
                int csw12 = cursor.getInt(8);
                int csw07 = cursor.getInt(9);
                int nwl23 = cursor.getInt(10);
                int nwl18 = cursor.getInt(11);
                int twl06 = cursor.getInt(12);
                int nswl23 = cursor.getInt(13);
                int cel21 = cursor.getInt(14);
                int wims = cursor.getInt(15);

                ArrayList<String> dictionaryList = dictionaries(length, csw24, csw19, csw15, csw12, csw07, nwl23, nwl18, twl06, nswl23, wims, cel21);

                if (total == 1) {
                    unsolvedAnswers.append(total).append(". <b><small>").append(front).append("</small> ").append(data).append(" <small>").append(back).append("</small></b> ").append(definition).append(" <b>").append(dictionaryList.get(0)).append(" ").append(dictionaryList.get(1)).append(" ").append(dictionaryList.get(2)).append(" ").append(dictionaryList.get(3)).append("</b>");
                }
                else {
                    unsolvedAnswers.append("<br>").append(total).append(". <b><small>").append(front).append("</small> ").append(data).append(" <small>").append(back).append("</small></b> ").append(definition).append(" <b>").append(dictionaryList.get(0)).append(" ").append(dictionaryList.get(1)).append(" ").append(dictionaryList.get(2)).append(" ").append(dictionaryList.get(3)).append("</b>");
                }

                total++;
            } while (cursor.moveToNext());
        }
        cursor.close();
        return new String(unsolvedAnswers);
    }

    public String getLabelColours(Context parent)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT _tag_, _colour_ FROM colours ORDER BY _tag_", null);

        StringBuilder labelColours = new StringBuilder("<b>");
        int line = 1;

        int nightModeFlags =
                parent.getResources().getConfiguration().uiMode &
                        Configuration.UI_MODE_NIGHT_MASK;
        String white = (nightModeFlags == Configuration.UI_MODE_NIGHT_YES ? "#000000" : "#FFFFFF");

        if (cursor.moveToFirst()) {
            do {
                String label = cursor.getString(0);
                String colour = cursor.getString(1);

                if (colour.equals(white)) {
                    if (line == 1) {
                        labelColours.append(line).append(". ").append(label.isEmpty() ? "(Default)" : label).append(": ").append(colour);
                    } else {
                        labelColours.append("<br>").append(line).append(". ").append(label.isEmpty() ? "(Default)" : label).append(": ").append(colour);
                    }
                }
                else {
                    if (line == 1) {
                        labelColours.append("<font color=\"").append(colour).append("\">").append(line).append(". ").append(label.isEmpty() ? "(Default)" : label).append(": ").append(colour).append("</font>");
                    } else {
                        labelColours.append("<br><font color=\"").append(colour).append("\">").append(line).append(". ").append(label.isEmpty() ? "(Default)" : label).append(": ").append(colour).append("</font>");
                    }
                }
                line++;
            } while (cursor.moveToNext());
        }
        cursor.close();
        labelColours.append("</b>");
        return new String(labelColours);
    }

    public ArrayList<String> getTableNames()
    {
        ArrayList<String> tableList = new ArrayList<>();
        int idx = 0;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type = 'table'", null);

        if (cursor.moveToFirst()) {
            do {
                String data = cursor.getString(0);

                if (idx > 0) {
                    tableList.add(data);
                }
                idx++;
            } while (cursor.moveToNext());
        }
        cursor.close();
        return tableList;
    }

    public String getSchema()
    {
        StringBuilder schema = new StringBuilder();
        ArrayList<String> tablesList = getTableNames();

        for (String tableName : tablesList)
        {
            String[] columnList = getAllColumns(tableName);
            ArrayList<String> columnArray = new ArrayList<>();
            for (String columnName : columnList)
            {
                columnArray.add(columnName.substring(1, columnName.length() - 1));
            }
            schema.append(schema.length() == 0 ? tableName + "\n" + columnArray : "\n" + tableName + "\n" + columnArray);
        }
        return new String(schema);
    }

    public void insertScores(int letters, int counter, String sqlQuery, String orderBy)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("_length_", letters);
        contentValues.put("_counter_", counter);
        contentValues.put("_query_", sqlQuery);
        contentValues.put("_sort_", orderBy);

        db.insert("scores", null, contentValues);
    }

    public int getMaximumWordLength()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT MAX(_length_) FROM words", null);
        int maximumWordLength = 0;

        if (cursor.moveToFirst()) {
            do {
                maximumWordLength = cursor.getInt(0);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return maximumWordLength;
    }

    public double probability(String st)
    {
        int[] frequency = new int[] {9, 2, 2, 4, 12, 2, 3, 2, 9, 1, 1, 4, 2, 6, 8, 2, 1, 6, 4, 6, 4, 2, 2, 1, 2, 1};
        int count = 100;
        double chance = 1;
        for (int j = 0; j < st.length(); j++)
        {
            char ch = st.charAt(j);
            int ord = ((int) ch) - 65;
            chance *= frequency[ord];
            chance /= count;
            if (frequency[ord] > 0) {
                frequency[ord]--;
            }
            count--;
        }
        return chance;
    }

    public void insertWord(Context myContext, HashMap<String, String> dictionary, HashMap<String, Integer> anagramsList, HashMap<String, String> lexicon)
    {
        String[] alphabetList = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
        int[] frequency = {9, 2, 2, 4, 12, 2, 3, 2, 9, 1, 1, 4, 2, 6, 8, 2, 1, 6, 4, 6, 4, 2, 2, 1, 2, 1};
        int[] point = {1, 3, 3, 2, 1, 4, 2, 4, 1, 8, 5, 1, 3, 1, 1, 3, 10, 1, 1, 1, 1, 4, 4, 8, 4, 10};
        boolean[] vowel = {true, false, false, false, true, false, false, false, true, false, false, false, false, false, true, false, false, false, false, false, true, false, false, false, false, false};
        boolean[] consonant = {false, true, true, true, false, true, true, true, false, true, true, true, true, true, false, true, true, true, true, true, false, true, true, true, true, true};
        boolean[] power = {false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, true, false, false, false, false, false, false, true, false, true};

        LayoutInflater myInflater = LayoutInflater.from(myContext);
        final View myCustomView = myInflater.inflate(R.layout.progressbar, null);

        ProgressBar p5 = myCustomView.findViewById(R.id.progressbar1);
        TextView t42 = myCustomView.findViewById(R.id.textview57);
        TextView t43 = myCustomView.findViewById(R.id.textview58);

        AlertDialog myDialog = new AlertDialog.Builder(myContext)
                .setTitle("Inserting words")
                .setView(myCustomView)
                .create();
        myDialog.show();

        SQLiteDatabase db = this.getWritableDatabase();

        Thread thread5 = new Thread(() -> {
            db.beginTransaction();

            try {
                Iterator<Map.Entry<String, String>> itr = dictionary.entrySet().iterator();
                double myLine = 0.0;
                double myStep1 = dictionary.size() / 40.0;
                while (itr.hasNext()) {
                    Map.Entry<String, String> entry = itr.next();
                    String word = entry.getKey();
                    char[] c = word.toCharArray();
                    Arrays.sort(c);
                    String anagram = new String(c);
                    int solutions = anagramsList.get(anagram);
                    String definition = entry.getValue();
                    StringBuilder back = new StringBuilder();
                    StringBuilder front = new StringBuilder();
                    for (char letter = 'A'; letter <= 'Z'; letter++) {
                        if (dictionary.containsKey(word + letter)) {
                            back.append(letter);
                        }
                        if (dictionary.containsKey(letter + word)) {
                            front.append(letter);
                        }
                    }

                    String lexiconList = lexicon.get(word);
                    String[] lexiconsList = lexiconList.split(",");

                    int csw24 = Integer.parseInt(lexiconsList[1]);
                    int csw21 = Integer.parseInt(lexiconsList[2]);
                    int csw19 = Integer.parseInt(lexiconsList[3]);
                    int csw15 = Integer.parseInt(lexiconsList[4]);
                    int csw12 = Integer.parseInt(lexiconsList[5]);
                    int csw07 = Integer.parseInt(lexiconsList[6]);
                    int nwl23 = Integer.parseInt(lexiconsList[7]);
                    int nwl20 = Integer.parseInt(lexiconsList[8]);
                    int nwl18 = Integer.parseInt(lexiconsList[9]);
                    int twl06 = Integer.parseInt(lexiconsList[10]);
                    int nswl23 = Integer.parseInt(lexiconsList[11]);
                    int nswl20 = Integer.parseInt(lexiconsList[12]);
                    int nswl18 = Integer.parseInt(lexiconsList[13]);
                    int cel21 = Integer.parseInt(lexiconsList[14]);
                    int wims = Integer.parseInt(lexiconsList[15]);

                    ContentValues contentValues = new ContentValues();

                    contentValues.put("_word_", word);
                    contentValues.put("_length_", word.length());
                    contentValues.put("_alphagram_", anagram);
                    contentValues.put("_definition_", definition);
                    contentValues.put("_probability_", probability(word));
                    contentValues.put("_back_", new String(back));
                    contentValues.put("_front_", new String(front));
                    contentValues.put("_tag_", (word.length() <= 15 && csw21 == 0) ? "New" : "");
                    contentValues.put("_page_", 0);
                    contentValues.put("_answers_", solutions);
                    contentValues.put("_csw24_", csw24);
                    contentValues.put("_csw21_", csw21);
                    contentValues.put("_csw19_", csw19);
                    contentValues.put("_csw15_", csw15);
                    contentValues.put("_csw12_", csw12);
                    contentValues.put("_csw07_", csw07);
                    contentValues.put("_nwl23_", nwl23);
                    contentValues.put("_nwl20_", nwl20);
                    contentValues.put("_nwl18_", nwl18);
                    contentValues.put("_twl06_", twl06);
                    contentValues.put("_nswl23_", nswl23);
                    contentValues.put("_nswl20_", nswl20);
                    contentValues.put("_nswl18_", nswl18);
                    contentValues.put("_cel21_", cel21);
                    contentValues.put("_wims_", wims);
                    contentValues.put("_serial_", 0);
                    contentValues.put("_position_", 0);
                    contentValues.put("_timestamp_", "");
                    contentValues.put("_reverse_", ((new StringBuilder(word)).reverse()).toString());
                    contentValues.put("_zetagram_", ((new StringBuilder(anagram)).reverse()).toString());

                    int vowels = 0;
                    int consonants = 0;
                    int points = 0;
                    int powers = 0;

                    int[] occurrence = new int[26];
                    for (int myRadix = 0; myRadix < word.length(); myRadix++) {
                        char theCharacter = word.charAt(myRadix);
                        int positionInAlphabet = theCharacter - 65;
                        occurrence[positionInAlphabet]++;
                        points += point[positionInAlphabet];

                        if (vowel[positionInAlphabet]) {
                            vowels++;
                        }

                        if (consonant[positionInAlphabet]) {
                            consonants++;
                        }

                        if (power[positionInAlphabet]) {
                            powers++;
                        }
                    }

                    for (int theRadix = 0; theRadix < 26; theRadix++) {
                        char occurrences = (char) (theRadix + 97);
                        contentValues.put("_no_" + occurrences + "_", occurrence[theRadix]);
                    }

                    contentValues.put("_vowels_", vowels);
                    contentValues.put("_consonants_", consonants);
                    contentValues.put("_points_", points);
                    contentValues.put("_power_", powers);

                    db.insert("words", null, contentValues);

                    myLine++;
                    if (myLine % myStep1 < 1 || myLine == 1.0)
                    {
                        updateProgressBar(myContext, p5, t42, t43, myDialog, (int) (myLine / myStep1), ((int) myLine) + "/" + dictionary.size());
                    }
                }

                Cursor[] anagramList = new Cursor[getMaximumWordLength() - 1];
                int[] wordLength = new int[anagramList.length];
                int[] pages = new int[anagramList.length];
                int maximumPages = 0;

                for (int lengths = 0; lengths < anagramList.length; lengths++) {
                    anagramList[lengths] = getAllRegularAnagrams(lengths + 2);
                    wordLength[lengths] = anagramList[lengths].getCount();
                    pages[lengths] = (((wordLength[lengths] - 1) / 100) + 1);

                    if (pages[lengths] > maximumPages) {
                        maximumPages = pages[lengths];
                    }
                }

                uiThreadTitle("Setting page numbers", myDialog, myContext);
                double myStep2 = maximumPages / 50.0;
                for (int positionNumber = 1; positionNumber <= maximumPages; positionNumber++) {
                    ArrayList<String> pageHash = new ArrayList<>();

                    for (int lengths = 0; lengths < anagramList.length; lengths++) {
                        if (positionNumber <= pages[lengths]) {
                            int open = (positionNumber - 1) * 100;
                            int close = Math.min(positionNumber * 100, wordLength[lengths]);

                            if (anagramList[lengths].moveToPosition(open)) {
                                do {
                                    pageHash.add(anagramList[lengths].getString(0));
                                } while (anagramList[lengths].moveToNext() && anagramList[lengths].getPosition() < close);
                            }
                        }
                    }

                    String pageString = ((((pageHash).toString()).replace("[", "(\"")).replace("]", "\")")).replace(", ", "\", \"");
                    ContentValues values = new ContentValues();
                    values.put("_page_", positionNumber);

                    db.update("words", values, ("_word_ IN") + pageString,
                            new String[] {});

                    if (positionNumber % myStep2 < 1 || positionNumber == 1)
                    {
                        updateProgressBar(myContext, p5, t42, t43, myDialog, 40 + ((int) (positionNumber / myStep2)), positionNumber + "/" + maximumPages);
                    }
                }

                uiThreadTitle("Setting grid numbers", myDialog, myContext);
                double myStep3 = 10.0;
                for (int cellNumber = 1; cellNumber <= 100.0; cellNumber++) {
                    ArrayList<String> cellHash = new ArrayList<>();

                    for (int lengths = 0; lengths < anagramList.length; lengths++) {
                        if (cellNumber <= wordLength[lengths]) {
                            if (anagramList[lengths].moveToPosition(cellNumber - 1)) {
                                do {
                                    cellHash.add(anagramList[lengths].getString(0));
                                } while (anagramList[lengths].move(100));
                            }
                        }
                    }

                    String cellString = ((((cellHash).toString()).replace("[", "(\"")).replace("]", "\")")).replace(", ", "\", \"");
                    ContentValues values = new ContentValues();
                    values.put("_position_", cellNumber);

                    db.update("words", values, ("_word_ IN ") + cellString,
                            new String[] {});

                    if (cellNumber % myStep3 < 1 || cellNumber == 1)
                    {
                        updateProgressBar(myContext, p5, t42, t43, myDialog, 90 + ((int) (cellNumber / myStep3)), cellNumber + "/100");
                    }
                }

                for (Cursor cursors : anagramList) {
                    cursors.close();
                }

                int nightModeFlags =
                        myContext.getResources().getConfiguration().uiMode &
                                Configuration.UI_MODE_NIGHT_MASK;
                String white = (nightModeFlags == Configuration.UI_MODE_NIGHT_YES ? "#000000" : "#FFFFFF");
                String black = (nightModeFlags == Configuration.UI_MODE_NIGHT_YES ? "#FFFFFF" : "#000000");

                HashMap<String, String> coloursList = new HashMap<>();

                coloursList.put("Known", "#00C000");
                coloursList.put("Unknown", "#FF0000");
                coloursList.put("Compound", "#FF00C0");
                coloursList.put("Prefix", "#C000FF");
                coloursList.put("Suffix", "#8080FF");
                coloursList.put("Plural", "#808080");
                coloursList.put("Guessable", "#FF8000");
                coloursList.put("Past", "#00C0FF");
                coloursList.put("Learnt", "#B97A57");
                coloursList.put("New", "#FFFF00");
                coloursList.put("Removed", black);
                coloursList.put("", white);

                for (Map.Entry<String, String> enter : coloursList.entrySet()) {
                    String tag = enter.getKey();
                    String tags = coloursList.get(tag);

                    ContentValues contentValues = new ContentValues();

                    contentValues.put("_tag_", tag);
                    contentValues.put("_colour_", tags);

                    db.insert("colours", null, contentValues);
                }

                ContentValues contentValues = new ContentValues();
                contentValues.put("_activity_", "Grid");
                contentValues.put("_rows_", 25);
                contentValues.put("_columns_", 4);
                contentValues.put("_size_", 11);
                contentValues.put("_spinner_", 20);
                db.insert("zoom", null, contentValues);

                contentValues = new ContentValues();
                contentValues.put("_activity_", "List");
                contentValues.put("_rows_", 100);
                contentValues.put("_columns_", 0);
                contentValues.put("_size_", 11);
                contentValues.put("_spinner_", 20);
                db.insert("zoom", null, contentValues);

                ArrayList<Pair<String, String>> myPrefixes = new ArrayList<>();
                myPrefixes.add(new Pair<>("", ""));

                for (Pair<String, String> columnItem : myPrefixes) {
                    ContentValues prefixValues = new ContentValues();
                    prefixValues.put("_prefix_", columnItem.first);
                    prefixValues.put("_before_", columnItem.second);
                    db.insert("prefixes", null, prefixValues);
                }

                ArrayList<Pair<String, String>> mySuffixes = new ArrayList<>();
                mySuffixes.add(new Pair<>("", ""));
                mySuffixes.add(new Pair<>("-", "E"));
                mySuffixes.add(new Pair<>("-I", "Y"));

                for (Pair<String, String> columnItem : mySuffixes) {
                    ContentValues suffixValues = new ContentValues();
                    suffixValues.put("_suffix_", columnItem.first);
                    suffixValues.put("_after_", columnItem.second);
                    db.insert("suffixes", null, suffixValues);
                }

                db.execSQL("UPDATE words SET _serial_ = ((_page_ - 1) * 100) + _position_");

                for (int alphabetPosition = 0; alphabetPosition < alphabetList.length; alphabetPosition++) {
                    ContentValues letterValues = new ContentValues();
                    letterValues.put("_letter_", alphabetList[alphabetPosition]);
                    letterValues.put("_frequency_", frequency[alphabetPosition]);
                    letterValues.put("_points_", point[alphabetPosition]);
                    letterValues.put("_is_vowel_", vowel[alphabetPosition]);
                    letterValues.put("_is_consonant_", consonant[alphabetPosition]);
                    letterValues.put("_is_power_", power[alphabetPosition]);
                    db.insert("letters", null, letterValues);
                }

                uiThreadRefresh(myContext, true);
                uiThreadBox("Prepare database", "Database preparation complete.", myContext);
                db.setTransactionSuccessful();
            } catch (Exception e) {
                myDialog.dismiss();
            } finally {
                myDialog.dismiss();
                db.endTransaction();
            }
        });

        thread5.start();
    }

    public ArrayList<Integer> getZoom(String parentActivity)
    {
        ArrayList<Integer> zoomList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT _rows_, _columns_, _size_, _spinner_ FROM zoom WHERE _activity_ = \"" + parentActivity + "\"", null);

        if (cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {
                    int rows = cursor.getInt(0);
                    int dimensions = cursor.getInt(1);
                    int font = cursor.getInt(2);
                    int combo = cursor.getInt(3);

                    zoomList.add(rows);
                    zoomList.add(dimensions);
                    zoomList.add(font);
                    zoomList.add(combo);
                } while (cursor.moveToNext());
            }
        }
        else {
            zoomList.add(25);
            zoomList.add(4);
            zoomList.add(11);
            zoomList.add(20);
        }

        cursor.close();
        return zoomList;
    }

    public void setZoom(String parentActivity, int rows, int dimensions, int font, int combo)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT EXISTS(SELECT 1 FROM zoom WHERE _activity_ = \"" + parentActivity + "\")", null);

        int exists = 0;

        if (cursor.moveToFirst()) {
            do {
                exists = cursor.getInt(0);
            } while (cursor.moveToNext());
        }
        cursor.close();

        ContentValues values = new ContentValues();
        values.put("_rows_", rows);
        values.put("_columns_", dimensions);
        values.put("_size_", font);
        values.put("_spinner_", combo);

        if (exists != 0) {
            db.update("zoom", values, "_activity_ = ?",
                    new String[] {parentActivity});
        }
        else {
            values.put("_activity_", parentActivity);
            db.insert("zoom", null, values);
        }
    }

    public int getCounter(int letters, String sqlQuery, String orderBy)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT _counter_ FROM scores WHERE _length_ = " + letters + " AND _query_ = \"" + sqlQuery + "\" AND _sort_ = \"" + orderBy + "\"", null);

        String data = null;

        if (cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {
                    data = cursor.getString(0);
                } while (cursor.moveToNext());
            }

            cursor.close();
            return Integer.parseInt(data);
        }
        else {
            cursor.close();
            return 0;
        }
    }

    public boolean getExist(int letters, String sqlQuery, String orderBy)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT EXISTS(SELECT 1 FROM scores WHERE _length_ = " + letters + " AND _query_ = \"" + sqlQuery + "\" AND _sort_ = \"" + orderBy + "\")", null);

        int exist = 0;

        if (cursor.moveToFirst()) {
            do {
                exist = cursor.getInt(0);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return (exist != 0);
    }

    public Cursor getAllAnagrams(int letters, String sqlQuery, String orderBy)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT _word_ FROM words" + ((letters > 1 || !sqlQuery.equals("*")) ? " WHERE " : "") + (letters > 1 ? "_length_ = " + letters : "") + ((letters > 1 && !sqlQuery.equals("*")) ? " AND " : "") + (!sqlQuery.equals("*") ? "_tag_ = \"" + sqlQuery + "\"" : "") + (orderBy.charAt(0) == ' ' ? orderBy : ""), null);
    }

    public Cursor getAllRegularAnagrams(int letters)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT _word_ FROM words WHERE _length_ = " + letters + " ORDER BY _probability_ DESC", null);
    }

    public ArrayList<String> getDefinition(String guess)
    {
        ArrayList<String> h = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT _word_, _definition_, _front_, _back_, _answers_, _tag_, _length_, _csw24_, _csw19_, _csw15_, _csw12_, _csw07_, _nwl23_, _nwl18_, _twl06_, _nswl23_, _wims_, _cel21_ FROM words WHERE _word_ = \"" + guess + "\"", null);

        if (cursor.moveToFirst()) {
            do {
                String data = cursor.getString(0);
                String definition = cursor.getString(1);
                String front = cursor.getString(2);
                String back = cursor.getString(3);
                String answers = cursor.getString(4);
                String label = cursor.getString(5);
                int length = cursor.getInt(6);
                int csw24 = cursor.getInt(7);
                int csw19 = cursor.getInt(8);
                int csw15 = cursor.getInt(9);
                int csw12 = cursor.getInt(10);
                int csw07 = cursor.getInt(11);
                int nwl23 = cursor.getInt(12);
                int nwl18 = cursor.getInt(13);
                int twl06 = cursor.getInt(14);
                int nswl23 = cursor.getInt(15);
                int cel21 = cursor.getInt(16);
                int wims = cursor.getInt(17);

                ArrayList<String> dictionaryList = dictionaries(length, csw24, csw19, csw15, csw12, csw07, nwl23, nwl18, twl06, nswl23, wims, cel21);
                String lexicons = dictionaryList.get(0) + " " + dictionaryList.get(1) + " " + dictionaryList.get(2) + " " + dictionaryList.get(3);

                h.add("<b><small>" + front + "</small> " + data + " <small>" + back + "</small></b> " + definition);
                h.add(answers);
                h.add(label);
                h.add(lexicons);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return h;
    }

    public HashMap<String, ArrayList<String>> getAllWords(List<String> wordsList)
    {
        HashMap<String, ArrayList<String>> jumbles = new HashMap<>();
        String anagramsList = (((wordsList.toString()).replace("[", "(\"")).replace("]", "\")")).replace(", ", "\", \"");

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT _word_, _definition_, _front_, _back_, _answers_, _tag_, _length_, _csw24_, _csw19_, _csw15_, _csw12_, _csw07_, _nwl23_, _nwl18_, _twl06_, _nswl23_, _wims_, _cel21_ FROM words WHERE _word_ IN " + anagramsList, null);

        if (cursor.moveToFirst()) {
            do {
                String data = cursor.getString(0);
                String definition = cursor.getString(1);
                String front = cursor.getString(2);
                String back = cursor.getString(3);
                String answers = cursor.getString(4);
                String label = cursor.getString(5);
                int length = cursor.getInt(6);
                int csw24 = cursor.getInt(7);
                int csw19 = cursor.getInt(8);
                int csw15 = cursor.getInt(9);
                int csw12 = cursor.getInt(10);
                int csw07 = cursor.getInt(11);
                int nwl23 = cursor.getInt(12);
                int nwl18 = cursor.getInt(13);
                int twl06 = cursor.getInt(14);
                int nswl23 = cursor.getInt(15);
                int cel21 = cursor.getInt(16);
                int wims = cursor.getInt(17);

                ArrayList<String> dictionaryList = dictionaries(length, csw24, csw19, csw15, csw12, csw07, nwl23, nwl18, twl06, nswl23, wims, cel21);
                String lexicons = dictionaryList.get(0) + " " + dictionaryList.get(1) + " " + dictionaryList.get(2) + " " + dictionaryList.get(3);

                ArrayList<String> h = new ArrayList<>();
                h.add("<b><small>" + front + "</small> " + data + " <small>" + back + "</small></b> " + definition);
                h.add(answers);
                h.add(label);
                h.add(lexicons);

                jumbles.put(data, h);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return jumbles;
    }

    public Cursor getSqlQuery(String sqlQuery, Context activity, String orderBy)
    {
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            return db.rawQuery("SELECT _word_ FROM words WHERE " + sqlQuery + (orderBy.charAt(0) == ' ' ? orderBy : ""), null);
        }
        catch (SQLiteException e)
        {
            alertBox("Error", e.toString(), activity);
            return null;
        }
    }

    public void updateCounter(int letters, int counter, String sqlQuery, String orderBy) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("_counter_", counter);

        db.update("scores", values, "_length_ = ? AND _query_ = ? AND _sort_ = ?",
                new String[] {Integer.toString(letters), sqlQuery, orderBy});
    }

    public void alertBox(String title, String message, Context location)
    {
        LayoutInflater inflater = LayoutInflater.from(location);
        final View yourCustomView = inflater.inflate(R.layout.display, null);

        TextView t1 = yourCustomView.findViewById(R.id.textview4);
        t1.setText(message);

        AlertDialog dialog = new AlertDialog.Builder(location)
                .setTitle(title)
                .setView(yourCustomView)
                .setPositiveButton("OK", (dialog1, whichButton) -> {
                }).create();
        dialog.show();
    }

    public void uiThreadBox(String title, String message, Context location)
    {
        MainActivity homeActivity = (MainActivity) location;

        homeActivity.runOnUiThread(() -> {
            LayoutInflater inflater = LayoutInflater.from(location);
            final View yourCustomView = inflater.inflate(R.layout.display, null);

            TextView t1 = yourCustomView.findViewById(R.id.textview4);
            t1.setText(message);

            AlertDialog dialog = new AlertDialog.Builder(location)
                    .setTitle(title)
                    .setView(yourCustomView)
                    .setPositiveButton("OK", (dialog1, whichButton) -> {
                    }).create();
            dialog.show();
        });
    }

    public void messageBox(String title, String message, Context location)
    {
        LayoutInflater inflater = LayoutInflater.from(location);
        final View yourCustomView = inflater.inflate(R.layout.display, null);

        TextView t2 = yourCustomView.findViewById(R.id.textview4);
        t2.setText(Html.fromHtml(message));

        AlertDialog dialog = new AlertDialog.Builder(location)
                .setTitle(title)
                .setView(yourCustomView)
                .setPositiveButton("OK", (dialog1, whichButton) -> {
                }).create();
        dialog.show();
    }

    public void uiThreadTitle(String title, AlertDialog theDialog, Context location)
    {
        MainActivity homeActivity = (MainActivity) location;

        homeActivity.runOnUiThread(() -> theDialog.setTitle(title));
    }

    public String addUnderscores(String argumentQuery)
    {
        String firstQuery = " " + argumentQuery + " ";
        String[] secondQuery = firstQuery.split("'");

        ArrayList<String> tablesList = getTableNames();
        ArrayList<String> regexList = getTableNames();
        HashSet<String> done = new HashSet<>();
        for (String tableName : tablesList)
        {
            String[] columnList = getAllColumns(tableName);
            for (String attribute : columnList)
            {
                if (!done.contains(attribute)) {
                    argumentQuery = argumentQuery.replaceAll(attribute.substring(1, attribute.length() - 1), attribute);
                    for (int regex = 0; regex < regexList.size(); regex++) {
                        regexList.set(regex, (regexList.get(regex)).replaceAll(attribute.substring(1, attribute.length() - 1), attribute));
                    }
                    done.add(attribute);
                }
            }
        }
        for (int regexLists = 0; regexLists < regexList.size(); regexLists++)
        {
            argumentQuery = argumentQuery.replaceAll(regexList.get(regexLists), tablesList.get(regexLists));
        }

        argumentQuery = (argumentQuery.replace("_time_stamp", "_timestamp_"))
                .replace("ze_tag_ram", "_zetagram_")
                .replace("is__power_", "_is_power_");

        for (String tableName : tablesList)
        {
            String[] columnList = getAllColumns(tableName);
            for (String attribute : columnList)
            {
                argumentQuery = argumentQuery.replaceAll("_+" + attribute.substring(1, attribute.length() - 1) + "_+", attribute);
            }
        }

        String thirdQuery = " " + argumentQuery + " ";
        String[] lastQuery = thirdQuery.split("'");
        ArrayList<String> finalQuery = new ArrayList<>();
        for (int columnsArray = 0; columnsArray < lastQuery.length; columnsArray++)
        {
            finalQuery.add(columnsArray % 2 == 0 ? lastQuery[columnsArray] : secondQuery[columnsArray]);
        }
        StringBuilder ultimateQuery = new StringBuilder();
        ultimateQuery.append(finalQuery.get(0));
        for (int rowName = 1; rowName < finalQuery.size(); rowName++)
        {
            ultimateQuery.append("'").append(finalQuery.get(rowName));
        }

        return (new String(ultimateQuery)).trim();
    }

    public void refresh(Context theContext)
    {
        MainActivity mainActivity = (MainActivity) theContext;
        mainActivity.refresh();
    }

    public void uiThreadRefresh(Context theContext, boolean prepared)
    {
        MainActivity mainActivity = (MainActivity) theContext;

        mainActivity.runOnUiThread(() -> {
            mainActivity.refresh();
            if (prepared) {
                mainActivity.setPrepared();
            }
        });
    }

    public boolean addLabel(String label, String hexCode)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("_tag_", label);
        contentValues.put("_colour_", hexCode);

        db.insert("colours", null, contentValues);
        return true;
    }

    public int renameLabel(String oldCode, String label, String colour, boolean name) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("_tag_", label);
        values.put("_colour_", colour);

        return db.update("colours", values, name ? "_tag_ = ?" : "_colour_ = ?",
                new String[] {oldCode});
    }

    public void deleteLabel(String oldCode, boolean name) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("colours", name ? "_tag_ = ?" : "_colour_ = ?",
                new String[] {oldCode});
    }

    public void addByLabel(Context theContext)
    {
        LayoutInflater inflater = LayoutInflater.from(theContext);
        final View yourCustomView = inflater.inflate(R.layout.add, null);

        EditText e4 = yourCustomView.findViewById(R.id.edittext11);
        TextView t5 = yourCustomView.findViewById(R.id.textview18);
        TextView t6 = yourCustomView.findViewById(R.id.textview20);
        TextView t7 = yourCustomView.findViewById(R.id.textview22);
        TextView t8 = yourCustomView.findViewById(R.id.textview24);
        TextView t9 = yourCustomView.findViewById(R.id.textview26);
        Slider z1 = yourCustomView.findViewById(R.id.slider1);
        Slider z2 = yourCustomView.findViewById(R.id.slider2);
        Slider z3 = yourCustomView.findViewById(R.id.slider3);

        final int[] rgb = {(int) z1.getValue(), (int) z2.getValue(), (int) z3.getValue()};
        t6.setText(Integer.toString(rgb[0]));
        t7.setText(Integer.toString(rgb[1]));
        t8.setText(Integer.toString(rgb[2]));
        final String[] hexValue = {String.format("#%02X%02X%02X", rgb[0], rgb[1], rgb[2])};
        t5.setText(hexValue[0]);
        t5.setTextColor(Color.rgb(rgb[0], rgb[1], rgb[2]));
        t9.setBackgroundColor(Color.rgb(rgb[0], rgb[1], rgb[2]));

        int nightModeFlags =
                theContext.getResources().getConfiguration().uiMode &
                        Configuration.UI_MODE_NIGHT_MASK;
        String white = (nightModeFlags == Configuration.UI_MODE_NIGHT_YES ? "#000000" : "#FFFFFF");
        int grey = t6.getCurrentTextColor();

        z1.addOnChangeListener((slider, value, fromUser) -> {
            rgb[0] = (int) value;
            t6.setText(Integer.toString(rgb[0]));
            hexValue[0] = String.format("#%02X%02X%02X", rgb[0], rgb[1], rgb[2]);
            t5.setText(hexValue[0]);
            t5.setTextColor(hexValue[0].equals(white) ? grey : Color.rgb(rgb[0], rgb[1], rgb[2]));
            t9.setBackgroundColor(Color.rgb(rgb[0], rgb[1], rgb[2]));
        });

        z2.addOnChangeListener((slider, value, fromUser) -> {
            rgb[1] = (int) value;
            t7.setText(Integer.toString(rgb[1]));
            hexValue[0] = String.format("#%02X%02X%02X", rgb[0], rgb[1], rgb[2]);
            t5.setText(hexValue[0]);
            t5.setTextColor(hexValue[0].equals(white) ? grey : Color.rgb(rgb[0], rgb[1], rgb[2]));
            t9.setBackgroundColor(Color.rgb(rgb[0], rgb[1], rgb[2]));
        });

        z3.addOnChangeListener((slider, value, fromUser) -> {
            rgb[2] = (int) value;
            t8.setText(Integer.toString(rgb[2]));
            hexValue[0] = String.format("#%02X%02X%02X", rgb[0], rgb[1], rgb[2]);
            t5.setText(hexValue[0]);
            t5.setTextColor(hexValue[0].equals(white) ? grey : Color.rgb(rgb[0], rgb[1], rgb[2]));
            t9.setBackgroundColor(Color.rgb(rgb[0], rgb[1], rgb[2]));
        });

        AlertDialog dialog = new AlertDialog.Builder(theContext)
                .setTitle("Add new tag")
                .setView(yourCustomView)
                .setPositiveButton("OK", (dialog1, whichButton) -> {
                    addLabel((e4.getText()).toString(), hexValue[0]);
                    refresh(theContext);
                }).create();
        dialog.show();
    }

    public void renameByLabel(Context theContext, boolean name, int fontSize)
    {
        LayoutInflater inflater = LayoutInflater.from(theContext);
        final View yourCustomView = inflater.inflate(R.layout.rename, null);

        EditText e5 = yourCustomView.findViewById(R.id.edittext12);
        Spinner s1 = yourCustomView.findViewById(R.id.spinner4);
        TextView t10 = yourCustomView.findViewById(R.id.textview27);
        TextView t11 = yourCustomView.findViewById(R.id.textview28);
        TextView t12 = yourCustomView.findViewById(R.id.textview29);
        TextView t13 = yourCustomView.findViewById(R.id.textview32);
        TextView t14 = yourCustomView.findViewById(R.id.textview34);
        TextView t15 = yourCustomView.findViewById(R.id.textview36);
        TextView t16 = yourCustomView.findViewById(R.id.textview38);
        TextView t17 = yourCustomView.findViewById(R.id.textview40);
        Slider z4 = yourCustomView.findViewById(R.id.slider4);
        Slider z5 = yourCustomView.findViewById(R.id.slider5);
        Slider z6 = yourCustomView.findViewById(R.id.slider6);

        t10.setText(name ? "Old Name:" : "Old Colour:");
        t11.setText(name ? "Old Colour:" : "Old Name:");
        final String[] hexValue = new String[1];
        final String[] old = new String[1];
        final int[] rgb = new int[3];

        t13.setText(String.format("#%02X%02X%02X", rgb[0], rgb[1], rgb[2]));
        t14.setText(Integer.toString(rgb[0]));
        t15.setText(Integer.toString(rgb[1]));
        t16.setText(Integer.toString(rgb[2]));
        t17.setBackgroundColor(Color.rgb(rgb[0], rgb[1], rgb[2]));

        int nightModeFlags =
                theContext.getResources().getConfiguration().uiMode &
                        Configuration.UI_MODE_NIGHT_MASK;
        String white = (nightModeFlags == Configuration.UI_MODE_NIGHT_YES ? "#000000" : "#FFFFFF");
        int grey = t10.getCurrentTextColor();

        List<Pair<String, String>> spinnerList = (name ? getAllLabels() : getAllColours());
        ColourAdapter colourAdapter = new ColourAdapter(theContext, R.layout.colour, R.id.textview41, spinnerList, (MainActivity) theContext, name, fontSize);
        s1.setAdapter(colourAdapter);

        s1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String oldValue = (spinnerList.get(i)).first;
                e5.setText(oldValue);
                hexValue[0] = (spinnerList.get(i)).second;
                int hexColour = Integer.parseInt(hexValue[0].substring(1), 16);
                rgb[0] = (hexColour >> 16) & 255;
                rgb[1] = (hexColour >> 8) & 255;
                rgb[2] = hexColour & 255;
                t12.setText(name ? hexValue[0] : oldValue);
                t12.setTextColor(hexValue[0].equals(white) ? grey : Color.rgb(rgb[0], rgb[1], rgb[2]));
                t13.setTextColor(hexValue[0].equals(white) ? grey : Color.rgb(rgb[0], rgb[1], rgb[2]));
                z4.setValue(rgb[0]);
                z5.setValue(rgb[1]);
                z6.setValue(rgb[2]);
                old[0] = (name ? oldValue : hexValue[0]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        z4.addOnChangeListener((slider, value, fromUser) -> {
            rgb[0] = (int) value;
            t14.setText(Integer.toString(rgb[0]));
            hexValue[0] = String.format("#%02X%02X%02X", rgb[0], rgb[1], rgb[2]);
            t13.setText(hexValue[0]);
            t13.setTextColor(hexValue[0].equals(white) ? grey : Color.rgb(rgb[0], rgb[1], rgb[2]));
            t17.setBackgroundColor(Color.rgb(rgb[0], rgb[1], rgb[2]));
        });

        z5.addOnChangeListener((slider, value, fromUser) -> {
            rgb[1] = (int) value;
            t15.setText(Integer.toString(rgb[1]));
            hexValue[0] = String.format("#%02X%02X%02X", rgb[0], rgb[1], rgb[2]);
            t13.setText(hexValue[0]);
            t13.setTextColor(hexValue[0].equals(white) ? grey : Color.rgb(rgb[0], rgb[1], rgb[2]));
            t17.setBackgroundColor(Color.rgb(rgb[0], rgb[1], rgb[2]));
        });

        z6.addOnChangeListener((slider, value, fromUser) -> {
            rgb[2] = (int) value;
            t16.setText(Integer.toString(rgb[2]));
            hexValue[0] = String.format("#%02X%02X%02X", rgb[0], rgb[1], rgb[2]);
            t13.setText(hexValue[0]);
            t13.setTextColor(hexValue[0].equals(white) ? grey : Color.rgb(rgb[0], rgb[1], rgb[2]));
            t17.setBackgroundColor(Color.rgb(rgb[0], rgb[1], rgb[2]));
        });

        AlertDialog dialog = new AlertDialog.Builder(theContext)
                .setTitle(name ? "Change tag colour by name" : "Rename tag by colour")
                .setView(yourCustomView)
                .setPositiveButton("OK", (dialog1, whichButton) -> {
                    int result = renameLabel(old[0], (e5.getText()).toString(), hexValue[0], name);
                    refresh(theContext);
                }).create();
        dialog.show();
    }

    public void deleteByLabel(Context theContext, boolean name, int fontSize)
    {
        LayoutInflater inflater = LayoutInflater.from(theContext);
        final View yourCustomView = inflater.inflate(R.layout.delete, null);

        Spinner s2 = yourCustomView.findViewById(R.id.spinner5);
        TextView t18 = yourCustomView.findViewById(R.id.textview42);
        TextView t19 = yourCustomView.findViewById(R.id.textview43);
        TextView t20 = yourCustomView.findViewById(R.id.textview44);

        t18.setText(name ? "Tag:" : "Colour:");
        t19.setText(name ? "Colour:" : "Tag:");
        final String[] old = new String[1];

        int nightModeFlags =
                theContext.getResources().getConfiguration().uiMode &
                        Configuration.UI_MODE_NIGHT_MASK;
        String white = (nightModeFlags == Configuration.UI_MODE_NIGHT_YES ? "#000000" : "#FFFFFF");
        int grey = t18.getCurrentTextColor();

        List<Pair<String, String>> spinnerList = (name ? getAllLabels() : getAllColours());
        ColourAdapter colourAdapter = new ColourAdapter(theContext, R.layout.colour, R.id.textview41, spinnerList, (MainActivity) theContext, name, fontSize);
        s2.setAdapter(colourAdapter);

        s2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String oldValue = (spinnerList.get(i)).first;
                String hexNumber = (spinnerList.get(i)).second;
                t20.setText(name ? hexNumber : oldValue);
                t20.setTextColor(hexNumber.equals(white) ? grey : Color.parseColor(hexNumber));
                old[0] = (name ? oldValue : hexNumber);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        AlertDialog dialog = new AlertDialog.Builder(theContext)
                .setTitle(name ? "Delete tag by name" : "Delete tag by colour")
                .setView(yourCustomView)
                .setPositiveButton("OK", (dialog1, whichButton) -> {
                    deleteLabel(old[0], name);
                    refresh(theContext);
                }).create();
        dialog.show();
    }

    public ArrayList<String> dictionaries(int wordSize, int csw24, int csw19, int csw15, int csw12, int csw07, int nwl23, int nwl18, int twl06, int nswl23, int wims, int cel21)
    {
        ArrayList<String> dictionariesList = new ArrayList<>();

        if (wordSize <= 15) {
            dictionariesList.add(csw24 == 0 ? "$" : (csw07 == 1 ? "CSW07" : (csw12 == 1 ? "CSW12" : (csw15 == 1 ? "CSW15" : (csw19 == 1 ? "CSW19" : "CSW24")))));
            dictionariesList.add(nwl23 == 0 ? "#" : (nswl23 == 0 ? "!" : (twl06 == 1 ? "TWL06" : (nwl18 == 1 ? "NWL18" : "NWL23"))));
        }
        else {
            switch (csw24) {
                case 0: dictionariesList.add("$");
                    break;
                case 1: dictionariesList.add("Fj00 CLSW");
                    break;
                case 2: dictionariesList.add("Grubbcc CLSW");
                    break;
                case 3: dictionariesList.add("Both CLSW");
                    break;
                default: dictionariesList.add("");
            }

            switch (nwl23) {
                case 0: dictionariesList.add("#");
                    break;
                case 1: dictionariesList.add("Fj00 NLWL");
                    break;
                case 2: dictionariesList.add("Grubbcc NLWL");
                    break;
                case 3: dictionariesList.add("Both NLWL");
                    break;
                default: dictionariesList.add("");
            }
        }

        dictionariesList.add(cel21 == 1 ? "CEL" : "");
        dictionariesList.add(wims == 1 ? "WIMS" : "");

        return dictionariesList;
    }

    public List<Pair<String, String>> getAllPrefixes()
    {
        ArrayList<Pair<String, String>> prefixesList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT _prefix_, _before_ FROM prefixes ORDER BY _prefix_", null);

        if (cursor.moveToFirst()) {
            do {
                prefixesList.add(new Pair<>(cursor.getString(0), cursor.getString(1)));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return prefixesList;
    }

    public List<Pair<String, String>> getAllSuffixes()
    {
        ArrayList<Pair<String, String>> suffixesList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT _suffix_, _after_ FROM suffixes ORDER BY _suffix_", null);

        if (cursor.moveToFirst()) {
            do {
                suffixesList.add(new Pair<>(cursor.getString(0), cursor.getString(1)));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return suffixesList;
    }

    public void getSuffix(Context theContext)
    {
        ArrayList<String> prefixList = new ArrayList<>();
        ArrayList<String> suffixList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor1 = db.rawQuery("SELECT _prefix_ FROM prefixes ORDER BY _prefix_", null);

        if (cursor1.moveToFirst()) {
            do {
                String thePrefix = cursor1.getString(0);
                prefixList.add(thePrefix.isEmpty() ? "(None)" : cursor1.getString(0));
            } while (cursor1.moveToNext());
        }

        cursor1.close();

        Cursor cursor2 = db.rawQuery("SELECT _suffix_ FROM suffixes ORDER BY _suffix_", null);

        if (cursor2.moveToFirst()) {
            do {
                String theSuffix = cursor2.getString(0);
                suffixList.add(theSuffix.isEmpty() ? "(None)" : cursor2.getString(0));
            } while (cursor2.moveToNext());
        }

        cursor2.close();

        String prefixes = prefixList.toString();
        String suffixes = suffixList.toString();

        messageBox("View all prefixes and suffixes", "<b>Prefixes:</b> " + prefixes.substring(1, prefixes.length() - 1) + "<br><b>Suffixes:</b> " + suffixes.substring(1, suffixes.length() - 1), theContext);
    }

    public void addSuffix(Context theContext, boolean suffix, String mode)
    {
        LayoutInflater inflater = LayoutInflater.from(theContext);
        final View yourCustomView = inflater.inflate(R.layout.modify, null);

        TextView t21 = yourCustomView.findViewById(R.id.textview45);
        TextView t22 = yourCustomView.findViewById(R.id.textview46);
        Spinner s3 = yourCustomView.findViewById(R.id.spinner6);
        Spinner s4 = yourCustomView.findViewById(R.id.spinner7);
        EditText e6 = yourCustomView.findViewById(R.id.edittext13);
        EditText e7 = yourCustomView.findViewById(R.id.edittext14);

        ArrayList<String> beforeList = new ArrayList<>();
        ArrayList<String> afterList = new ArrayList<>();
        final int[] variable = {0, 0};

        if (suffix)
        {
            t21.setText("Suffix:");
            t22.setText("After last letters:");
            beforeList.add("No changes to word");
            beforeList.add("Drop last letter");
            beforeList.add("Double last letter");
            afterList.add("After all last letters");
            afterList.add("After specific last letters");
            e7.setHint("(After all last letters)");
        }
        else
        {
            t21.setText("Prefix:");
            t22.setText("Before first letters:");
            beforeList.add("No changes to word");
            beforeList.add("Drop first letter");
            beforeList.add("Double first letter");
            afterList.add("Before all first letters");
            afterList.add("Before specific first letters");
            e7.setHint("(Before all first letters)");
        }

        ArrayAdapter<String> beforeAdapter = new ArrayAdapter<>(theContext, android.R.layout.simple_spinner_item, beforeList);
        beforeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s3.setAdapter(beforeAdapter);

        s3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                variable[0] = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        ArrayAdapter<String> afterAdapter = new ArrayAdapter<>(theContext, android.R.layout.simple_spinner_item, afterList);
        afterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s4.setAdapter(afterAdapter);

        s4.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                variable[1] = i;
                if (i == 0)
                {
                    t22.setVisibility(View.INVISIBLE);
                    e7.setVisibility(View.INVISIBLE);
                }
                else
                {
                    t22.setVisibility(View.VISIBLE);
                    e7.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        AlertDialog dialog = new AlertDialog.Builder(theContext)
                .setTitle(suffix ? "Add new suffix" : "Add new prefix")
                .setView(yourCustomView)
                .setPositiveButton("OK", (dialog1, whichButton) -> {
                    if (suffix)
                    {
                        addPrefix((variable[0] == 0 ? "" : (variable[0] == 1 ? "-" : "+")) + ((e6.getText()).toString()).toUpperCase(), variable[1] == 0 ? "" : ((e7.getText()).toString()).toUpperCase(), suffix);
                    }
                    else
                    {
                        addPrefix(((e6.getText()).toString()).toUpperCase() + (variable[0] == 0 ? "" : (variable[0] == 1 ? "-" : "+")), variable[1] == 0 ? "" : ((e7.getText()).toString()).toUpperCase(), suffix);
                    }

                    if (mode != null) {
                        MainActivity home = (MainActivity) theContext;
                        home.refreshDefinition();
                    }
                }).create();
        dialog.show();
    }

    public void changeSuffix(Context theContext, boolean suffix, String mode)
    {
        LayoutInflater inflater = LayoutInflater.from(theContext);
        final View yourCustomView = inflater.inflate(R.layout.change, null);

        TextView t23 = yourCustomView.findViewById(R.id.textview47);
        TextView t24 = yourCustomView.findViewById(R.id.textview48);
        TextView t25 = yourCustomView.findViewById(R.id.textview49);
        TextView t26 = yourCustomView.findViewById(R.id.textview50);
        TextView t27 = yourCustomView.findViewById(R.id.textview51);
        TextView t28 = yourCustomView.findViewById(R.id.textview52);
        Spinner s5 = yourCustomView.findViewById(R.id.spinner8);
        Spinner s6 = yourCustomView.findViewById(R.id.spinner9);
        Spinner s7 = yourCustomView.findViewById(R.id.spinner10);
        EditText e8 = yourCustomView.findViewById(R.id.edittext15);
        EditText e9 = yourCustomView.findViewById(R.id.edittext16);

        List<Pair<String, String>> insertList;
        ArrayList<String> queryList = new ArrayList<>();
        ArrayList<String> beforeList = new ArrayList<>();
        ArrayList<String> afterList = new ArrayList<>();
        final int[] variable = {0, 0, 0};

        if (suffix)
        {
            insertList = getAllSuffixes();
            for (Pair<String, String> object : insertList)
            {
                queryList.add(object.first);
            }
            t23.setText("Old suffix:");
            t25.setText("After last letters:");
            t27.setText("New suffix:");
            t28.setText("After last letters:");
            beforeList.add("No changes to word");
            beforeList.add("Drop last letter");
            beforeList.add("Double last letter");
            afterList.add("After all last letters");
            afterList.add("After specific last letters");
            e9.setHint("(After all last letters)");
        }
        else
        {
            insertList = getAllPrefixes();
            for (Pair<String, String> object : insertList)
            {
                queryList.add(object.first);
            }
            t23.setText("Old prefix:");
            t25.setText("Before first letters:");
            t27.setText("New prefix:");
            t28.setText("Before first letters:");
            beforeList.add("No changes to word");
            beforeList.add("Drop first letter");
            beforeList.add("Double first letter");
            afterList.add("Before all first letters");
            afterList.add("Before specific first letters");
            e9.setHint("(Before all first letters)");
        }

        ArrayAdapter<String> queryAdapter = new ArrayAdapter<>(theContext, android.R.layout.simple_spinner_item, queryList);
        queryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s5.setAdapter(queryAdapter);

        s5.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                variable[0] = i;
                String prefix = queryList.get(i);
                String myColour = (insertList.get(i)).second;

                s7.setSelection(myColour.isEmpty() ? 0 : 1);
                e9.setText(myColour);

                if (suffix)
                {
                    t24.setText((!prefix.isEmpty() && prefix.charAt(0) == '+') ? (prefix.length() > 1 ? "Double last letter, " : "Double last letter") + prefix.substring(1) : ((!prefix.isEmpty() && prefix.charAt(0) == '-') ? (prefix.length() > 1 ? "Drop last letter, " : "Drop last letter") + prefix.substring(1) : (!prefix.isEmpty() ? "No changes to word, " : "No changes to word") + prefix));
                    t26.setText(myColour.isEmpty() ? "(After all last letters)" : myColour);

                    s6.setSelection((!prefix.isEmpty() && prefix.charAt(0) == '-') ? 1 : ((!prefix.isEmpty() && prefix.charAt(0) == '+') ? 2 : 0));
                    e8.setText((!prefix.isEmpty() && (prefix.charAt(0) == '+' || prefix.charAt(0) == '-')) ? prefix.substring(1) : prefix);
                }
                else
                {
                    int variables = prefix.length() - 1;
                    t24.setText((!prefix.isEmpty() && prefix.charAt(variables) == '+') ? prefix.substring(0, variables) + (prefix.length() > 1 ? ", double first letter" : "Double first letter") : ((!prefix.isEmpty() && prefix.charAt(variables) == '-') ? prefix.substring(0, variables) + (prefix.length() > 1 ? ", drop first letter" : "Drop first letter") : prefix + (!prefix.isEmpty() ? ", no changes to word" : "No changes to word")));
                    t26.setText(myColour.isEmpty() ? "(Before all first letters)" : myColour);

                    s6.setSelection((!prefix.isEmpty() && prefix.charAt(variables) == '-') ? 1 : ((!prefix.isEmpty() && prefix.charAt(variables) == '+') ? 2 : 0));
                    e8.setText((!prefix.isEmpty() && (prefix.charAt(variables) == '+' || prefix.charAt(variables) == '-')) ? prefix.substring(0, variables) : prefix);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        ArrayAdapter<String> beforeAdapter = new ArrayAdapter<>(theContext, android.R.layout.simple_spinner_item, beforeList);
        beforeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s6.setAdapter(beforeAdapter);

        s6.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                variable[1] = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        ArrayAdapter<String> afterAdapter = new ArrayAdapter<>(theContext, android.R.layout.simple_spinner_item, afterList);
        afterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s7.setAdapter(afterAdapter);

        s7.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                variable[2] = i;
                if (i == 0)
                {
                    t28.setVisibility(View.INVISIBLE);
                    e9.setVisibility(View.INVISIBLE);
                }
                else
                {
                    t28.setVisibility(View.VISIBLE);
                    e9.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        AlertDialog dialog = new AlertDialog.Builder(theContext)
                .setTitle(suffix ? "Change suffix" : "Change prefix")
                .setView(yourCustomView)
                .setPositiveButton("OK", (dialog1, whichButton) -> {
                    if (suffix)
                    {
                        changePrefix((insertList.get(variable[0])).first, (insertList.get(variable[0])).second, (variable[1] == 0 ? "" : (variable[1] == 1 ? "-" : "+")) + ((e8.getText()).toString()).toUpperCase(), variable[2] == 0 ? "" : ((e9.getText()).toString()).toUpperCase(), suffix);
                    }
                    else
                    {
                        changePrefix((insertList.get(variable[0])).first, (insertList.get(variable[0])).second, ((e8.getText()).toString()).toUpperCase() + (variable[1] == 0 ? "" : (variable[1] == 1 ? "-" : "+")), variable[2] == 0 ? "" : ((e9.getText()).toString()).toUpperCase(), suffix);
                    }

                    if (mode != null) {
                        MainActivity home = (MainActivity) theContext;
                        home.refreshDefinition();
                    }
                }).create();
        dialog.show();
    }

    public void deleteSuffix(Context theContext, boolean suffix, String mode)
    {
        LayoutInflater inflater = LayoutInflater.from(theContext);
        final View yourCustomView = inflater.inflate(R.layout.remove, null);

        TextView t29 = yourCustomView.findViewById(R.id.textview53);
        TextView t30 = yourCustomView.findViewById(R.id.textview54);
        TextView t31 = yourCustomView.findViewById(R.id.textview55);
        TextView t32 = yourCustomView.findViewById(R.id.textview56);
        Spinner s8 = yourCustomView.findViewById(R.id.spinner11);

        List<Pair<String, String>> insertList;
        ArrayList<String> queryList = new ArrayList<>();
        final int variable[] = {0};

        if (suffix)
        {
            insertList = getAllSuffixes();
            for (Pair<String, String> object : insertList)
            {
                queryList.add(object.first);
            }
            t29.setText("Suffix:");
            t31.setText("After last letters:");
        }
        else
        {
            insertList = getAllPrefixes();
            for (Pair<String, String> object : insertList)
            {
                queryList.add(object.first);
            }
            t29.setText("Prefix:");
            t31.setText("Before first letters:");
        }

        ArrayAdapter<String> queryAdapter = new ArrayAdapter<>(theContext, android.R.layout.simple_spinner_item, queryList);
        queryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s8.setAdapter(queryAdapter);

        s8.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                variable[0] = i;
                String prefix = queryList.get(i);
                String myColour = (insertList.get(i)).second;

                if (suffix)
                {
                    t30.setText((!prefix.isEmpty() && prefix.charAt(0) == '+') ? (prefix.length() > 1 ? "Double last letter, " : "Double last letter") + prefix.substring(1) : ((!prefix.isEmpty() && prefix.charAt(0) == '-') ? (prefix.length() > 1 ? "Drop last letter, " : "Drop last letter") + prefix.substring(1) : (!prefix.isEmpty() ? "No changes to word, " : "No changes to word") + prefix));
                    t32.setText(myColour.isEmpty() ? "(After all last letters)" : myColour);
                }
                else
                {
                    int variables = prefix.length() - 1;
                    t30.setText((!prefix.isEmpty() && prefix.charAt(variables) == '+') ? prefix.substring(0, variables) + (prefix.length() > 1 ? ", double first letter" : "Double first letter") : ((!prefix.isEmpty() && prefix.charAt(variables) == '-') ? prefix.substring(0, variables) + (prefix.length() > 1 ? ", drop first letter" : "Drop first letter") : prefix + (!prefix.isEmpty() ? ", no changes to word" : "No changes to word")));
                    t32.setText(myColour.isEmpty() ? "(Before all first letters)" : myColour);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        AlertDialog dialog = new AlertDialog.Builder(theContext)
                .setTitle(suffix ? "Delete suffix" : "Delete prefix")
                .setView(yourCustomView)
                .setPositiveButton("OK", (dialog1, whichButton) -> {
                    if (suffix)
                    {
                        deletePrefix((insertList.get(variable[0])).first, (insertList.get(variable[0])).second, suffix);
                    }
                    else
                    {
                        deletePrefix((insertList.get(variable[0])).first, (insertList.get(variable[0])).second, suffix);
                    }

                    if (mode != null) {
                        MainActivity home = (MainActivity) theContext;
                        home.refreshDefinition();
                    }
                }).create();
        dialog.show();
    }

    public void addPrefix(String prefix, String before, boolean suffix)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(suffix ? "_suffix_" : "_prefix_", prefix);
        contentValues.put(suffix ? "_after_" : "_before_", before);

        db.insert(suffix ? "suffixes" : "prefixes", null, contentValues);
    }

    public void changePrefix(String myPrefix, String mySuffix, String prefix, String before, boolean suffix) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(suffix ? "_suffix_" : "_prefix_", prefix);
        values.put(suffix ? "_after_" : "_before_", before);

        db.update(suffix ? "suffixes" : "prefixes", values, suffix ? "_suffix_ = ? AND _after_ = ?" : "_prefix_ = ? AND _before_ = ?",
                new String[] {myPrefix, mySuffix});
    }

    public void deletePrefix(String myPrefix, String mySuffix, boolean suffix) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(suffix ? "suffixes" : "prefixes", suffix ? "_suffix_ = ? AND _after_ = ?" : "_prefix_ = ? AND _before_ = ?",
                new String[] {myPrefix, mySuffix});
    }

    public void deleteAllRecords(Context theContext, String theTable, String mode)
    {
        LayoutInflater inflater = LayoutInflater.from(theContext);
        final View yourCustomView = inflater.inflate(R.layout.display, null);

        TextView t33 = yourCustomView.findViewById(R.id.textview4);
        t33.setText("Deleting all rows from table '" + theTable + "'");

        AlertDialog dialog = new AlertDialog.Builder(theContext)
                .setTitle("Are you sure?")
                .setView(yourCustomView)
                .setPositiveButton("Yes", (dialog1, whichButton) -> {
                    deleteTable(theTable);
                    if (theTable.equals("colours")) {
                        refresh(theContext);
                    } else {
                        if (mode != null) {
                            MainActivity home = (MainActivity) theContext;
                            home.refreshDefinition();
                        }
                    }
                })
                .setNegativeButton("No", (dialog2, whichButton) -> {
                }).create();
        dialog.show();
    }

    public void deleteTable(String myTable)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(myTable, null, null);
    }

    public String getFullDetails(String myGuess)
    {
        StringBuilder allDetails = new StringBuilder();

        char[] charArray = myGuess.toCharArray();
        Arrays.sort(charArray);
        String myAnagram = new String(charArray);

        List<Pair<String, String>> thePrefixes = getAllPrefixes();
        ArrayList<String> thePrefix = new ArrayList<>();
        for (Pair<String, String> rowItem : thePrefixes)
        {
            String alpha = rowItem.first;
            String beta = rowItem.second;
            boolean match = (beta.isEmpty() || beta.contains(Character.toString(myGuess.charAt(0))));

            if (!alpha.isEmpty() && alpha.charAt(alpha.length() - 1) == '+')
            {
                if (match)
                {
                    thePrefix.add(alpha.substring(0, alpha.length() - 1) + myGuess.charAt(0) + myGuess);
                }
            }
            else if (!alpha.isEmpty() && alpha.charAt(alpha.length() - 1) == '-')
            {
                if (match)
                {
                    thePrefix.add(alpha.substring(0, alpha.length() - 1) + myGuess.substring(1));
                }
            }
            else
            {
                if (match)
                {
                    thePrefix.add(alpha + myGuess);
                }
            }
        }

        StringBuilder result1 = getFullPrefixes(thePrefix, false);
        if (result1.length() > 0) {
            allDetails.append("<br>").append("<b>Prefixes:</b> ").append(result1);
        }

        List<Pair<String, String>> theSuffixes = getAllSuffixes();
        ArrayList<String> theSuffix = new ArrayList<>();
        for (Pair<String, String> rowItem : theSuffixes)
        {
            String alpha = rowItem.first;
            String beta = rowItem.second;
            boolean mismatch = (beta.isEmpty() || beta.contains(Character.toString(myGuess.charAt(myGuess.length() - 1))));

            if (!alpha.isEmpty() && alpha.charAt(0) == '+')
            {
                if (mismatch)
                {
                    theSuffix.add(myGuess + myGuess.charAt(myGuess.length() - 1) + alpha.substring(1));
                }
            }
            else if (!alpha.isEmpty() && alpha.charAt(0) == '-')
            {
                if (mismatch)
                {
                    theSuffix.add(myGuess.substring(0, myGuess.length() - 1) + alpha.substring(1));
                }
            }
            else
            {
                if (mismatch)
                {
                    theSuffix.add(myGuess + alpha);
                }
            }
        }

        StringBuilder result2 = getFullPrefixes(theSuffix, false);
        if (result2.length() > 0) {
            allDetails.append("<br>").append("<b>Suffixes:</b> ").append(result2);
        }

        ArrayList<String> theAnagram = new ArrayList<>();
        theAnagram.add(myAnagram);
        StringBuilder result3 = getFullSuffixes(theAnagram, "_word_ != \"" + myGuess + "\" AND ", true);
        if (result3.length() > 0) {
            allDetails.append("<br>").append("<b>Anagrams:</b> ").append(result3);
        }

        ArrayList<String> singleLetterChange = new ArrayList<>();
        for (int myIndex = 0; myIndex < myGuess.length(); myIndex++)
        {
            singleLetterChange.add(myGuess.substring(0, myIndex) + "_" + myGuess.substring(myIndex + 1));
        }

        StringBuilder result4 = getFullSuffixes(singleLetterChange, "_word_ != \"" + myGuess + "\" AND ", false);
        if (result4.length() > 0) {
            allDetails.append("<br>").append("<b>One letter change by position:</b> ").append(result4);
        }

        ArrayList<String> singleLetterAdd = new ArrayList<>();
        char myCharacter = 0;
        for (int myIndex = 0; myIndex < myAnagram.length(); myIndex++)
        {
            char current = myAnagram.charAt(myIndex);
            if (myIndex > 0 && current == myCharacter)
            {
                continue;
            }
            singleLetterAdd.add(myAnagram.substring(0, myIndex) + myAnagram.substring(myIndex + 1));
            myCharacter = current;
        }

        StringBuilder result5 = getFullPrefixes(singleLetterAdd, true);
        if (result5.length() > 0) {
            allDetails.append("<br>").append("<b>One letter drop:</b> ").append(result5);
        }

        ArrayList<String> oneLetterDrop = new ArrayList<>();
        char previous = 0;
        for (int myIndex = 0; myIndex < myAnagram.length(); myIndex++)
        {
            char present = myAnagram.charAt(myIndex);
            if (myIndex > 0 && present == previous)
            {
                continue;
            }
            StringBuilder singleLetterDrop = new StringBuilder();
            for (int theIndex = 0; theIndex < myAnagram.length(); theIndex++)
            {
                if (theIndex == myIndex)
                {
                    continue;
                }
                singleLetterDrop.append("%").append(myAnagram.charAt(theIndex));
            }
            singleLetterDrop.append("%");
            oneLetterDrop.add(new String(singleLetterDrop));
            previous = present;
        }

        StringBuilder result6 = getFullSuffixes(oneLetterDrop, "_word_ != \"" + myGuess + "\" AND _length_ = " + myGuess.length() + " AND ", true);
        if (result6.length() > 0) {
            allDetails.append("<br>").append("<b>One letter change:</b> ").append(result6);
        }

        StringBuilder oneLetterChange = new StringBuilder();
        for (int myIndex = 0; myIndex < myAnagram.length(); myIndex++)
        {
            oneLetterChange.append("%").append(myAnagram.charAt(myIndex));
        }
        oneLetterChange.append("%");

        ArrayList<String> oneLetterAdd = new ArrayList<>();
        oneLetterAdd.add(new String(oneLetterChange));

        StringBuilder result7 = getFullSuffixes(oneLetterAdd, "_length_ = " + (myGuess.length() + 1) + " AND ", true);
        if (result7.length() > 0) {
            allDetails.append("<br>").append("<b>One letter addition:</b> ").append(result7);
        }

        return new String(allDetails);
    }

    public StringBuilder getFullPrefixes(ArrayList<String> argument, boolean myAlphagram)
    {
        StringBuilder fullDetails = new StringBuilder();
        SQLiteDatabase db = this.getReadableDatabase();
        String listItems = (((argument.toString()).replace("[", "(\"")).replace("]", "\")")).replace(", ", "\", \"");
        Cursor cursor = db.rawQuery("SELECT _front_, _word_, _back_ FROM words WHERE " + (myAlphagram ? "_alphagram_" : "_word_") + " IN " + listItems + " ORDER BY _word_", null);

        int radix = 0;

        if (cursor.moveToFirst()) {
            do {
                String firstItem = cursor.getString(0);
                String secondItem = cursor.getString(1);
                String thirdItem = cursor.getString(2);

                if (radix > 0) {
                    fullDetails.append(", ");
                }

                fullDetails.append("<small>").append(firstItem).append("</small> ").append(secondItem).append(" <small>").append(thirdItem).append("</small>");
                radix++;
            } while (cursor.moveToNext());
        }

        cursor.close();
        return fullDetails;
    }

    public StringBuilder getFullSuffixes(ArrayList<String> argument, String condition, boolean myAlphagram)
    {
        StringBuilder fullDetails = new StringBuilder();
        SQLiteDatabase db = this.getReadableDatabase();

        StringBuilder listItems = new StringBuilder(condition);
        if (argument.size() > 1)
        {
            listItems.append("(");
        }
        int rank = 0;
        for (String argumentItem : argument)
        {
            if (rank == 0)
            {
                listItems.append(myAlphagram ? "_alphagram_ LIKE \"" : "_word_ LIKE \"").append(argumentItem).append("\"");
            }
            else
            {
                listItems.append(myAlphagram ? " OR _alphagram_ LIKE \"" : " OR _word_ LIKE \"").append(argumentItem).append("\"");
            }
            rank++;
        }
        if (argument.size() > 1)
        {
            listItems.append(")");
        }

        Cursor cursor = db.rawQuery("SELECT _front_, _word_, _back_ FROM words WHERE " + listItems + " ORDER BY _word_", null);

        int radix = 0;

        if (cursor.moveToFirst()) {
            do {
                String firstItem = cursor.getString(0);
                String secondItem = cursor.getString(1);
                String thirdItem = cursor.getString(2);

                if (radix > 0) {
                    fullDetails.append(", ");
                }

                fullDetails.append("<small>").append(firstItem).append("</small> ").append(secondItem).append(" <small>").append(thirdItem).append("</small>");
                radix++;
            } while (cursor.moveToNext());
        }

        cursor.close();
        return fullDetails;
    }

    public void updateProgressBar(Context yourContext, ProgressBar progressBar, TextView leftText, TextView rightText, AlertDialog theDialog, int percentage, String fraction)
    {
        MainActivity homeActivity = (MainActivity) yourContext;

        homeActivity.runOnUiThread(() -> {
            if (!theDialog.isShowing()) {
                theDialog.show();
            }

            progressBar.setProgress(percentage);
            leftText.setText(percentage + "%");
            rightText.setText(fraction);
        });
    }

    public String[] getAllColumns(String tableName)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor myCursor = db.query(tableName, null, null, null, null, null, null);
        String[] allColumns = myCursor.getColumnNames();
        myCursor.close();
        return allColumns;
    }

    public ArrayList<Column> getTableInformation(boolean allTables) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Column> tableInfo = new ArrayList<>();
        HashSet<String> covered = new HashSet<>();

        for (String myTables : (allTables ? getTableNames() : queryTables)) {
            Cursor theCursor = db.rawQuery("PRAGMA table_info(\"" + myTables + "\")", null);
            if (theCursor.moveToFirst()) {
                do {
                    String theColumn = theCursor.getString(1);
                    if (!covered.contains(theColumn)) {
                        covered.add(theColumn);
                        String theType = theCursor.getString(2);

                        Column myColumn = new Column(theColumn, theType);
                        tableInfo.add(myColumn);
                    }
                } while (theCursor.moveToNext());
            }
            theCursor.close();
        }

        return tableInfo;
    }

    public void addFunctionalities(Context con, EditText editText, CheckBox autoUnderscores, boolean allTables, View subCustomView) {
        ArrayList<Column> tableInformation = getTableInformation(allTables);
        ArrayAdapter<Column> columnAdapter = new ArrayAdapter<>(con, android.R.layout.simple_spinner_item, tableInformation);
        columnAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Button b1 = subCustomView.findViewById(R.id.button48);
        Button b2 = subCustomView.findViewById(R.id.button37);
        Button b3 = subCustomView.findViewById(R.id.button36);
        Button b4 = subCustomView.findViewById(R.id.button38);
        Button b5 = subCustomView.findViewById(R.id.button32);
        Button b6 = subCustomView.findViewById(R.id.button43);
        Button b7 = subCustomView.findViewById(R.id.button44);
        Button b8 = subCustomView.findViewById(R.id.button45);
        Button b9 = subCustomView.findViewById(R.id.button46);
        Button b10 = subCustomView.findViewById(R.id.button47);
        Button b11 = subCustomView.findViewById(R.id.button39);
        Button b12 = subCustomView.findViewById(R.id.button40);
        Button b13 = subCustomView.findViewById(R.id.button41);
        Button b14 = subCustomView.findViewById(R.id.button42);

        EditText e10 = subCustomView.findViewById(R.id.edittext23);
        EditText e11 = subCustomView.findViewById(R.id.edittext24);
        EditText e12 = subCustomView.findViewById(R.id.edittext25);
        EditText e13 = subCustomView.findViewById(R.id.edittext26);
        EditText e14 = subCustomView.findViewById(R.id.edittext27);

        Spinner s9 = subCustomView.findViewById(R.id.spinner21);
        Spinner s10 = subCustomView.findViewById(R.id.spinner22);
        Spinner s11 = subCustomView.findViewById(R.id.spinner23);
        Spinner s12 = subCustomView.findViewById(R.id.spinner24);
        Spinner s13 = subCustomView.findViewById(R.id.spinner25);
        Spinner s14 = subCustomView.findViewById(R.id.spinner26);

        b1.setOnClickListener(v -> {
            Help help = new Help();
            if (allTables) {
                messageBox("Example SQL queries", help.getSqlHelp(), con);
            }
            else {
                messageBox("Example custom queries", help.getCustomHelp(), con);
            }
        });

        b2.setOnClickListener(v -> {
            String originalString = (editText.getText()).toString();
            boolean space = (originalString.isEmpty() || originalString.charAt(originalString.length() - 1) == ' ');
            editText.append(space ? "AND" : " AND");
        });

        b3.setOnClickListener(v -> {
            String originalString = (editText.getText()).toString();
            boolean space = (originalString.isEmpty() || originalString.charAt(originalString.length() - 1) == ' ');
            editText.append(space ? "OR" : " OR");
        });

        b4.setOnClickListener(v -> {
            String originalString = (editText.getText()).toString();
            boolean space = (originalString.isEmpty() || originalString.charAt(originalString.length() - 1) == ' ');
            editText.append(space ? "NOT" : " NOT");
        });

        b5.setOnClickListener(v -> {
            editText.setText("");
        });

        b6.setOnClickListener(v -> {
            e10.setText("");
        });

        b7.setOnClickListener(v -> {
            e11.setText("");
        });

        b8.setOnClickListener(v -> {
            e12.setText("");
        });

        b9.setOnClickListener(v -> {
            e13.setText("");
        });

        b10.setOnClickListener(v -> {
            e14.setText("");
        });

        b11.setOnClickListener(v -> {
            String originalString = (editText.getText()).toString();
            boolean space = (originalString.isEmpty() || originalString.charAt(originalString.length() - 1) == ' ');
            Column columnName = tableInformation.get(s9.getSelectedItemPosition());
            String conditions = ((e10.getText()).toString()).toUpperCase();
            editText.append((space ? "" : " ") + columnName.getColumn(autoUnderscores.isChecked()) + " " + (s10.getSelectedItem()).toString() + " " + ((columnName.getType()).equalsIgnoreCase("text") ? "'" + conditions + "'" : conditions));
        });

        b12.setOnClickListener(v -> {
            String originalString = (editText.getText()).toString();
            boolean space = (originalString.isEmpty() || originalString.charAt(originalString.length() - 1) == ' ');
            Column columnName = tableInformation.get(s11.getSelectedItemPosition());
            String conditions = ((e11.getText()).toString()).toUpperCase();
            String[] conditionList = conditions.split(",\\s*");
            String conditionsList = ((columnName.getType()).equalsIgnoreCase("text") ? "('" + String.join("', '", conditionList) + "')" : "(" + String.join(", ", conditionList) + ")");
            editText.append((space ? "" : " ") + columnName.getColumn(autoUnderscores.isChecked()) + " " + (s12.getSelectedItem()).toString() + " " + conditionsList);
        });

        b13.setOnClickListener(v -> {
            String originalString = (editText.getText()).toString();
            boolean space = (originalString.isEmpty() || originalString.charAt(originalString.length() - 1) == ' ');
            Column columnName = tableInformation.get(s13.getSelectedItemPosition());
            String conditions = ((e12.getText()).toString()).toUpperCase();
            editText.append((space ? "" : " ") + columnName.getColumn(autoUnderscores.isChecked()) + " LIKE " + ((columnName.getType()).equalsIgnoreCase("text") ? "'" + conditions + "'" : conditions));
        });

        b14.setOnClickListener(v -> {
            String originalString = (editText.getText()).toString();
            boolean space = (originalString.isEmpty() || originalString.charAt(originalString.length() - 1) == ' ');
            Column columnName = tableInformation.get(s14.getSelectedItemPosition());
            String firstCondition = ((e13.getText()).toString()).toUpperCase();
            String secondCondition = ((e14.getText()).toString()).toUpperCase();
            editText.append((space ? "" : " ") + columnName.getColumn(autoUnderscores.isChecked()) + " BETWEEN " + ((columnName.getType()).equalsIgnoreCase("text") ? "'" + firstCondition + "'" : firstCondition) + " AND " + ((columnName.getType()).equalsIgnoreCase("text") ? "'" + secondCondition + "'" : secondCondition));
        });

        s9.setAdapter(columnAdapter);
        s11.setAdapter(columnAdapter);
        s13.setAdapter(columnAdapter);
        s14.setAdapter(columnAdapter);

        ArrayAdapter<String> comparatorsAdapter = new ArrayAdapter<>(con, android.R.layout.simple_spinner_item, comparators);
        comparatorsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s10.setAdapter(comparatorsAdapter);

        ArrayAdapter<String> comparatorAdapter = new ArrayAdapter<>(con, android.R.layout.simple_spinner_item, comparator);
        comparatorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s12.setAdapter(comparatorAdapter);
    }

    public static void main(String[] args) {
        // TODO Auto-generated method stub
    }
}