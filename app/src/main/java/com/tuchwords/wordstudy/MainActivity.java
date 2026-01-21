package com.tuchwords.wordstudy;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    sqliteDB db;
    int letters = 0;
    String sqlQuery = "*";
    int mode = 0;
    String label = "(No Action)";
    String ultimate;
    String white;
    boolean hidden;
    boolean detail;
    String orderBy;
    ArrayList<String> wordsList;
    HashMap<String, ArrayList<String>> jumbles;
    HashMap<String, String> colourList;
    List<Pair<String, String>> labelsList;
    HashMap<String, String> dictionary;
    HashMap<String, Integer> anagramsList;
    HashMap<String, String> lexicon;
    ArrayList<String> sort;
    ArrayList<String> allColumns;
    CustomAdapter cusadapter;
    SharedPreferences pref;

    TextView t1;
    TextView t2;
    Button b1;
    Button b2;
    Button b3;
    Button b4;
    Button b5;
    Button b6;

    RecyclerView g1;
    Spinner s1;

    Cursor anagrams;
    int words;
    int counter;

    int rows;
    int columns;
    int font;
    int combo;
    int loader;
    int maximumWordLength;
    int filterSerial;

    // Declare the DrawerLayout, NavigationView and Toolbar
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the DrawerLayout, NavigationView and Toolbar
        drawerLayout = findViewById(R.id.drawer_layout_main);
        NavigationView navigationView = findViewById(R.id.nav_view_main);
        Toolbar toolbar = findViewById(R.id.toolbar_main);

        pref = getApplicationContext().getSharedPreferences("AppData", 0);
        boolean prepared = pref.getBoolean("prepared", false);
        hidden = pref.getBoolean("hidden", false);
        detail = pref.getBoolean("detail", false);
        int version = pref.getInt("version", 1);
        Menu menu = navigationView.getMenu();

        if (hidden)
        {
            MenuItem menuItem = menu.findItem(R.id.button14);
            menuItem.setTitle("Show number of anagrams");
        }

        if (detail)
        {
            MenuItem menuItem = menu.findItem(R.id.button21);
            menuItem.setTitle("Hide similar words (Faster)");
        }

        // Create an ActionBarDrawerToggle to handle
        // the drawer's open / close state
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.nav_open, R.string.nav_close);

        // Add the toggle as a listener to the DrawerLayout
        drawerLayout.addDrawerListener(toggle);

        // Synchronize the toggle's state with the linked DrawerLayout
        toggle.syncState();

        // Set a listener for when an item in the NavigationView is selected
        // Called when an item in the NavigationView is selected.
        navigationView.setNavigationItemSelectedListener(item -> {
            // Handle the selected item based on its ID
            switch(item.getItemId()) {
                case R.id.button4:
                    // Show a Toast message for the Custom query item
                    getQuery();
                    break;
                case R.id.button6:
                    // Show a Toast message for the SQL query item
                    LayoutInflater inflater1 = LayoutInflater.from(MainActivity.this);
                    final View yourCustomView2 = inflater1.inflate(R.layout.sqlquery, null);

                    EditText e5 = yourCustomView2.findViewById(R.id.edittext21);
                    CheckBox c3 = yourCustomView2.findViewById(R.id.checkbox3);
                    FrameLayout f2 = yourCustomView2.findViewById(R.id.framelayout2);

                    LayoutInflater subinflater2 = LayoutInflater.from(MainActivity.this);
                    final View subCustomView2 = subinflater2.inflate(R.layout.sieve, null);
                    f2.addView(subCustomView2);

                    db.addFunctionalities(MainActivity.this, e5, c3, true, subCustomView2);

                    AlertDialog dialog1 = new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Enter your SQL query")
                            .setView(yourCustomView2)
                            .setPositiveButton("OK", (dialog, whichButton) -> {
                                String subQuery = ((e5.getText()).toString()).replace("\"", "'");

                                if (!subQuery.isEmpty()) {
                                    db.myQuery(c3.isChecked() ? db.addUnderscores(subQuery) : subQuery, MainActivity.this);
                                }
                            }).create();
                    dialog1.show();
                    break;
                case R.id.button7:
                    // Show a Toast message for the View all tag colours item
                    String labelColours = db.getLabelColours(MainActivity.this);
                    db.messageBox("Tag colours", labelColours, MainActivity.this);
                    break;
                case R.id.button8:
                    // Show a Toast message for the Export tags item
                    db.exportLabels(MainActivity.this);
                    break;
                case R.id.button9:
                    // Show a Toast message for the Import tags item
                    db.importLabels(MainActivity.this);
                    break;
                case R.id.button10:
                    // Show a Toast message for the Export CSV item
                    db.exportDB(MainActivity.this);
                    break;
                case R.id.button11:
                    // Show a Toast message for the Import CSV item
                    db.importDB(MainActivity.this);
                    break;
                case R.id.button13:
                    // Show a Toast message for the Change rows, columns, font size item
                    zoom();
                    break;
                case R.id.button14:
                    // Show a Toast message for the Hide and show number of anagrams item
                    if (hidden) {
                        hidden = false;
                        item.setTitle("Hide number of anagrams");
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putBoolean("hidden", false);
                        editor.apply();
                        if (cusadapter != null) {
                            cusadapter.setHidden(false);
                            cusadapter.notifyDataSetChanged();
                        }
                    } else {
                        hidden = true;
                        item.setTitle("Show number of anagrams");
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putBoolean("hidden", true);
                        editor.apply();
                        if (cusadapter != null) {
                            cusadapter.setHidden(true);
                            cusadapter.notifyDataSetChanged();
                        }
                    }
                    break;
                case R.id.button15:
                    // Show a Toast message for the Filter by tag item
                    filterByLabel();
                    break;
                case R.id.button16:
                    // Show a Toast message for the Add new tag item
                    db.addByLabel(MainActivity.this);
                    break;
                case R.id.button17:
                    // Show a Toast message for the Rename tag by colour item
                    db.renameByLabel(MainActivity.this, false, combo);
                    break;
                case R.id.button18:
                    // Show a Toast message for the Change tag colour by name item
                    db.renameByLabel(MainActivity.this, true, combo);
                    break;
                case R.id.button19:
                    // Show a Toast message for the Delete single tag by name item
                    db.deleteByLabel(MainActivity.this, true, combo);
                    break;
                case R.id.button20:
                    // Show a Toast message for the Delete single tag by colour item
                    db.deleteByLabel(MainActivity.this, false, combo);
                    break;
                case R.id.button21:
                    // Show a Toast message for the Hide and show similar words item
                    if (detail) {
                        detail = false;
                        item.setTitle("Show similar words (Slower)");
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putBoolean("detail", false);
                        editor.apply();
                    } else {
                        detail = true;
                        item.setTitle("Hide similar words (Faster)");
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putBoolean("detail", true);
                        editor.apply();
                    }

                    if (ultimate != null) {
                        refreshDefinition();
                    }
                    break;
                case R.id.button22:
                    // Show a Toast message for the View all prefixes and suffixes item
                    db.getSuffix(MainActivity.this);
                    break;
                case R.id.button23:
                    // Show a Toast message for the Add new prefix item
                    db.addSuffix(MainActivity.this, false, ultimate);
                    break;
                case R.id.button24:
                    // Show a Toast message for the Change prefix item
                    db.changeSuffix(MainActivity.this, false, ultimate);
                    break;
                case R.id.button25:
                    // Show a Toast message for the Delete single prefix item
                    db.deleteSuffix(MainActivity.this, false, ultimate);
                    break;
                case R.id.button26:
                    // Show a Toast message for the Add new suffix item
                    db.addSuffix(MainActivity.this, true, ultimate);
                    break;
                case R.id.button27:
                    // Show a Toast message for the Change suffix item
                    db.changeSuffix(MainActivity.this, true, ultimate);
                    break;
                case R.id.button28:
                    // Show a Toast message for the Delete single suffix item
                    db.deleteSuffix(MainActivity.this, true, ultimate);
                    break;
                case R.id.button29:
                    // Show a Toast message for the Delete all tags item
                    db.deleteAllRecords(MainActivity.this, "colours", ultimate);
                    break;
                case R.id.button30:
                    // Show a Toast message for the Delete all prefixes item
                    db.deleteAllRecords(MainActivity.this, "prefixes", ultimate);
                    break;
                case R.id.button31:
                    // Show a Toast message for the Delete all suffixes item
                    db.deleteAllRecords(MainActivity.this, "suffixes", ultimate);
                    break;
                case R.id.button33:
                    // Show a Toast message for the Prepare database item
                    promptDictionary(true);
                    break;
                case R.id.button34:
                    // Show a Toast message for the Search for anagrams item
                    getAllSubanagrams(false);
                    break;
                case R.id.button35:
                    // Show a Toast message for the Search for subanagrams item
                    getAllSubanagrams(true);
                    break;
                case R.id.button50:
                    // Show a Toast message for the View all tables and columns item
                    db.messageBox("View all tables and columns", db.getSchema(), MainActivity.this);
                    break;
                case R.id.button51:
                    // Show a Toast message for the Letter distribution item
                    db.letterDistribution(MainActivity.this);
                    break;
                case R.id.button52:
                    // Show a Toast message for the Load saved word list item
                    LayoutInflater inflater3 = LayoutInflater.from(MainActivity.this);
                    final View yourCustomView5 = inflater3.inflate(R.layout.list, null);
                    EditText e17 = yourCustomView5.findViewById(R.id.edittext30);

                    RecyclerView g2 = yourCustomView5.findViewById(R.id.gridview3);
                    RecyclerView.LayoutManager listManager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false);
                    g2.setLayoutManager(listManager);

                    final FilterAdapter[] filterAdapter = {new FilterAdapter(MainActivity.this, R.layout.list, db.loadFilter(""), loader, db)};
                    g2.setAdapter(filterAdapter[0]);

                    e17.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void afterTextChanged(Editable s) {
                        }

                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            filterAdapter[0] = new FilterAdapter(MainActivity.this, R.layout.list, db.loadFilter(s.toString()), loader, db);
                            g2.setAdapter(filterAdapter[0]);
                        }
                    });

                    AlertDialog dialog5 = new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Load saved word list")
                            .setView(yourCustomView5)
                            .setPositiveButton("OK", (dialog6, whichButton3) -> {
                                Filter filterObject = filterAdapter[0].getSelection();
                                if (filterObject == null) {
                                    db.alertBox("Load saved word list", "No item had been selected.", MainActivity.this);
                                }
                                else {
                                    int numberOfLetters = filterObject.getLength();
                                    if (numberOfLetters == 0) {
                                        execute(false, filterObject.getQuery(), filterObject.getSort());
                                    } else {
                                        mode = 1;
                                        letters = numberOfLetters;
                                        sqlQuery = filterObject.getQuery();
                                        orderBy = filterObject.getSort();
                                        start();
                                    }
                                }
                            }).create();
                    dialog5.show();
                    break;
            }

            // Close the drawer after selection
            drawerLayout.closeDrawers();
            // Indicate that the item selection has been handled
            return true;
        });

        // Add a callback to handle the back button press
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            // Called when the back button is pressed.
            @Override
            public void handleOnBackPressed() {
                // Check if the drawer is open
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    // Close the drawer if it's open
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    // Finish the activity if the drawer is closed
                    closeCursor();
                    finish();
                }
            }
        });

        t1 = findViewById(R.id.textview1);
        t2 = findViewById(R.id.textview2);
        b1 = findViewById(R.id.button1);
        b2 = findViewById(R.id.button2);
        b3 = findViewById(R.id.button3);
        b4 = findViewById(R.id.button12);
        b5 = findViewById(R.id.button5);
        b6 = findViewById(R.id.button49);

        db = new sqliteDB(MainActivity.this, version, null, false);

        g1 = findViewById(R.id.gridview1);
        s1 = findViewById(R.id.spinner1);

        if (!prepared) {
            promptDictionary(false);
        }

        ArrayList<Integer> dimensions = db.getZoom("Grid");
        rows = dimensions.get(0);
        columns = dimensions.get(1);
        font = dimensions.get(2);
        combo = dimensions.get(3);
        loader = dimensions.get(4);
        maximumWordLength = db.getMaximumWordLength();

        refreshSpinner();

        sort = new ArrayList<>();
        sort.add("ascending");
        sort.add("descending");

        allColumns = new ArrayList<>();
        allColumns.add("(default)");
        allColumns.add("(random)");

        for (String oneColumn : db.getAllColumns("words")) {
            allColumns.add(oneColumn.substring(1, oneColumn.length() - 1));
        }

        b3.setOnClickListener(view -> getWordLength());

        b4.setOnClickListener(view -> {
            closeCursor();
            finish();
        });

        b6.setOnClickListener(view2 -> {
            LayoutInflater inflater2 = LayoutInflater.from(MainActivity.this);
            final View yourCustomView4 = inflater2.inflate(R.layout.edit, null);

            EditText e15 = yourCustomView4.findViewById(R.id.edittext28);
            e15.setText(db.getFilterName(filterSerial));

            AlertDialog dialog3 = new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Save word list")
                    .setView(yourCustomView4)
                    .setPositiveButton("OK", (dialog4, whichButton2) -> {
                        db.saveFilter(filterSerial, ((e15.getText()).toString()).replace("\"", "'"));
                    }).create();
            dialog3.show();
        });
    }

    public void promptDictionary(boolean deleteTable)
    {
        LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
        final View yourCustomView = inflater.inflate(R.layout.prompt, null);

        TextView t3 = yourCustomView.findViewById(R.id.textview3);
        t3.setText("CSW24 or NWL23?");

        CheckBox c1 = yourCustomView.findViewById(R.id.checkbox1);
        c1.setChecked(deleteTable);

        AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                .setTitle("Choose your lexicon")
                .setView(yourCustomView)
                .setPositiveButton("CSW24", (dialog1, whichButton) -> {
                    if (c1.isChecked()) {
                        db.dropTable(MainActivity.this);
                    }
                    prepareDictionary(true);
                })
                .setNegativeButton("NWL23", (dialog2, whichButton) -> {
                    if (c1.isChecked()) {
                        db.dropTable(MainActivity.this);
                    }
                    prepareDictionary(false);
                }).create();
        dialog.show();
    }

    public void prepareDictionary(boolean international)
    {
        dictionary = new HashMap<>();
        anagramsList = new HashMap<>();
        lexicon = new HashMap<>();

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(getAssets().open(international ? "CSW24.txt" : "NWL23.txt"), "UTF-8"));
            while (true)
            {
                String s = reader.readLine();
                if (s == null)
                {
                    break;
                }
                else
                {
                    String[] t = s.split("=");

                    if (t.length == 1)
                    {
                        dictionary.put(t[0], "");
                    }
                    else {
                        dictionary.put(t[0], t[1]);
                    }

                    char[] jumbled = t[0].toCharArray();
                    Arrays.sort(jumbled);
                    String solution = new String(jumbled);

                    if (anagramsList.containsKey(solution))
                    {
                        anagramsList.put(solution, anagramsList.get(solution) + 1);
                    }
                    else
                    {
                        anagramsList.put(solution, 1);
                    }
                }
            }
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(getAssets().open(international ? "CSW2024.txt" : "NWL2023.txt"), "UTF-8"));

            while (true)
            {
                String s = bufferedReader.readLine();
                if (s == null)
                {
                    break;
                }
                else
                {
                    int comma = s.indexOf(',');
                    String w = s.substring(0, comma);
                    lexicon.put(w, s);
                }
            }
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        prepareDatabase();
    }

    public void prepareDatabase()
    {
        db.insertWord(this, dictionary, anagramsList, lexicon);
    }

    public void updateGridView()
    {
        wordsList = new ArrayList<>();
        int open = counter * rows * columns;
        int close = Math.min((counter + 1) * rows * columns, words);

        if (orderBy.equals("DESC"))
        {
            if (anagrams.moveToPosition(words - 1 - open)) {
                do {
                    String jumble = anagrams.getString(0);

                    wordsList.add(jumble);
                } while (anagrams.moveToPrevious() && anagrams.getPosition() >= (words - close));
            }
        }
        else {
            if (anagrams.moveToPosition(open)) {
                do {
                    String jumble = anagrams.getString(0);

                    wordsList.add(jumble);
                } while (anagrams.moveToNext() && anagrams.getPosition() < close);
            }
        }

        jumbles = db.getAllWords(wordsList);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, Math.max(((wordsList.size() - 1) / columns) + 1, 1), GridLayoutManager.HORIZONTAL, false);
        g1.setLayoutManager(layoutManager);

        cusadapter = new CustomAdapter(MainActivity.this, R.layout.cell, wordsList, jumbles, colourList, columns, font);
        if (hidden)
        {
            cusadapter.setHidden(true);
        }
        g1.setAdapter(cusadapter);
    }

    public void getWordLength()
    {
        LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
        final View yourCustomView = inflater.inflate(R.layout.solve, null);

        EditText e1 = yourCustomView.findViewById(R.id.edittext20);
        TextView t4 = yourCustomView.findViewById(R.id.textview63);
        e1.setHint("Enter a value between 2 and " + maximumWordLength);

        final int[] sortIndex = new int[2];
        Spinner s5 = yourCustomView.findViewById(R.id.spinner13);
        ArrayAdapter<String> orderAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, allColumns);
        orderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s5.setAdapter(orderAdapter);

        Spinner s6 = yourCustomView.findViewById(R.id.spinner14);
        ArrayAdapter<String> sortAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, sort);
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s6.setAdapter(sortAdapter);

        s5.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                sortIndex[0] = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        s6.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                sortIndex[1] = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        s5.setSelection(6);
        s6.setSelection(1);

        final int[] lengthIndex = new int[1];
        Spinner s4 = yourCustomView.findViewById(R.id.spinner12);
        ArrayList<String> lengthList = new ArrayList<>();
        lengthList.add(0, "Specific word length");
        lengthList.add(1, "All word lengths");

        ArrayAdapter<String> lengthAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, lengthList);
        lengthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s4.setAdapter(lengthAdapter);

        s4.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    e1.setVisibility(View.VISIBLE);
                    t4.setVisibility(View.VISIBLE);
                    lengthIndex[0] = 0;
                }
                else {
                    e1.setVisibility(View.INVISIBLE);
                    t4.setVisibility(View.INVISIBLE);
                    lengthIndex[0] = 1;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                .setTitle("Change word length")
                .setView(yourCustomView)
                .setPositiveButton("OK", (dialog1, whichButton) -> {
                    String alphabet = (lengthIndex[0] == 0 ? (e1.getText()).toString() : "1");
                    int precursor = (alphabet.isEmpty() ? 0 : Integer.parseInt(alphabet));

                    if (lengthIndex[0] == 0 && precursor < 2)
                    {
                        Toast.makeText(MainActivity.this, "Enter a value between 2 and " + maximumWordLength + " for word length", Toast.LENGTH_LONG).show();
                        getWordLength();
                    }
                    else
                    {
                        mode = 1;
                        letters = precursor;
                        sqlQuery = "*";
                        orderBy = sortBy(sortIndex);
                        start();
                    }
                }).create();
        dialog.show();
    }

    public void getQuery()
    {
        LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
        final View yourCustomView1 = inflater.inflate(R.layout.query, null);

        EditText e2 = yourCustomView1.findViewById(R.id.edittext2);
        CheckBox c2 = yourCustomView1.findViewById(R.id.checkbox2);
        FrameLayout f1 = yourCustomView1.findViewById(R.id.framelayout1);

        LayoutInflater subinflater1 = LayoutInflater.from(MainActivity.this);
        final View subCustomView1 = subinflater1.inflate(R.layout.sieve, null);
        f1.addView(subCustomView1);

        db.addFunctionalities(MainActivity.this, e2, c2, false, subCustomView1);

        final int[] sortIndex = new int[2];
        Spinner s9 = yourCustomView1.findViewById(R.id.spinner17);
        ArrayAdapter<String> orderAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, allColumns);
        orderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s9.setAdapter(orderAdapter);

        Spinner s10 = yourCustomView1.findViewById(R.id.spinner18);
        ArrayAdapter<String> sortAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, sort);
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s10.setAdapter(sortAdapter);

        s9.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                sortIndex[0] = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        s10.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                sortIndex[1] = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        s9.setSelection(6);
        s10.setSelection(1);

        AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                .setTitle("SELECT front, word, back, definition FROM words WHERE")
                .setView(yourCustomView1)
                .setPositiveButton("OK", (dialog1, whichButton) -> {
                    String temporaryQuery = ((e2.getText()).toString()).replace("\"", "'");
                    execute(c2.isChecked(), temporaryQuery.isEmpty() ? "1" : temporaryQuery, sortBy(sortIndex));
                }).create();
        dialog.show();
    }

    public void start()
    {
        closeCursor();

        int exist = db.getExist(letters, sqlQuery, orderBy);
        filterSerial = (exist == 0 ? db.insertScores(letters, 0, sqlQuery, orderBy) : exist);

        anagrams = db.getAllAnagrams(letters, sqlQuery, orderBy);
        words = anagrams.getCount();
        counter = db.getCounter(letters, sqlQuery, orderBy);

        int high = (words - 1) / (rows * columns);
        if (counter > high && words > 0) {
            counter = high;
            db.updateCounter(letters, counter, sqlQuery, orderBy);
        }

        ultimate = null;
        db.emptyTable(MainActivity.this);
        nextWord();
    }

    public void execute(boolean autoUnderscores, String permanentQuery, String orderIndex)
    {
        String processingQuery = (autoUnderscores ? db.addUnderscores(permanentQuery) : permanentQuery);
        Cursor resultSet = db.getSqlQuery(processingQuery, MainActivity.this, orderIndex);

        if (resultSet != null) {
            sqlQuery = processingQuery;
            orderBy = orderIndex;
            mode = 2;

            closeCursor();
            anagrams = resultSet;
            words = anagrams.getCount();
            letters = 0;

            int exist = db.getExist(letters, sqlQuery, orderBy);
            filterSerial = (exist == 0 ? db.insertScores(letters, counter, sqlQuery, orderBy) : exist);
            counter = (exist == 0 ? 0 : db.getCounter(letters, sqlQuery, orderBy));

            int highest = (words - 1) / (rows * columns);
            if (counter > highest && words > 0) {
                counter = highest;
                db.updateCounter(letters, counter, sqlQuery, orderBy);
            }

            ultimate = null;
            db.emptyTable(MainActivity.this);
            executeSqlQuery();
        }
    }

    public void nextWord()
    {
        b1.setEnabled(true);
        b2.setEnabled(true);
        b5.setEnabled(true);
        b6.setEnabled(true);

        t1.setText("Page " + (counter + 1) + " out of " + (((words - 1) / (rows * columns)) + 1) + " (" + words + (words == 1 ? " word)" : " words)"));
        if (ultimate == null) {
            t2.setText("");
        }

        updateGridView();

        b1.setOnClickListener(view -> {
            counter--;
            if (counter < 0)
            {
                counter = (words - 1) / (rows * columns);
            }
            db.updateCounter(letters, counter, sqlQuery, orderBy);
            ultimate = null;
            nextWord();
        });

        b2.setOnClickListener(view -> {
            counter++;
            if (counter == ((words - 1) / (rows * columns)) + 1)
            {
                counter = 0;
            }
            db.updateCounter(letters, counter, sqlQuery, orderBy);
            ultimate = null;
            nextWord();
        });

        b5.setOnClickListener(view -> {
            LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
            final View yourCustomView = inflater.inflate(R.layout.input, null);

            EditText e3 = yourCustomView.findViewById(R.id.edittext1);
            int maximum = ((words - 1) / (rows * columns)) + 1;
            e3.setHint("Enter a value between 1 and " + maximum);

            AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Go to page")
                    .setView(yourCustomView)
                    .setPositiveButton("OK", (dialog1, whichButton) -> {
                        String pages = (e3.getText()).toString();
                        int page = (pages.isEmpty() ? 0 : Integer.parseInt(pages));
                        if (page < 1 || page > maximum)
                        {
                            Toast.makeText(MainActivity.this, "Enter a value between 1 and " + maximum, Toast.LENGTH_LONG).show();
                        }
                        else
                        {
                            ultimate = null;
                            counter = page - 1;
                            db.updateCounter(letters, counter, sqlQuery, orderBy);
                            nextWord();
                        }
                    }).create();
            dialog.show();
        });
    }

    public void executeSqlQuery()
    {
        b1.setEnabled(true);
        b2.setEnabled(true);
        b5.setEnabled(true);
        b6.setEnabled(true);

        t1.setText("Page " + (counter + 1) + " out of " + (((words - 1) / (rows * columns)) + 1) + " (" + words + (words == 1 ? " word)" : " words)"));
        if (ultimate == null) {
            t2.setText("");
        }

        updateGridView();

        b1.setOnClickListener(view -> {
            counter--;
            if (counter < 0)
            {
                counter = (words - 1) / (rows * columns);
            }
            db.updateCounter(letters, counter, sqlQuery, orderBy);
            ultimate = null;
            executeSqlQuery();
        });

        b2.setOnClickListener(view -> {
            counter++;
            if (counter == ((words - 1) / (rows * columns)) + 1)
            {
                counter = 0;
            }
            db.updateCounter(letters, counter, sqlQuery, orderBy);
            ultimate = null;
            executeSqlQuery();
        });

        b5.setOnClickListener(view -> {
            LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
            final View yourCustomView = inflater.inflate(R.layout.input, null);

            EditText e4 = yourCustomView.findViewById(R.id.edittext1);
            int maximum = ((words - 1) / (rows * columns)) + 1;
            e4.setHint("Enter a value between 1 and " + maximum);

            AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Go to page")
                    .setView(yourCustomView)
                    .setPositiveButton("OK", (dialog1, whichButton) -> {
                        String pages = (e4.getText()).toString();
                        int page = (pages.isEmpty() ? 0 : Integer.parseInt(pages));
                        if (page < 1 || page > maximum)
                        {
                            Toast.makeText(MainActivity.this, "Enter a value between 1 and " + maximum, Toast.LENGTH_LONG).show();
                        }
                        else
                        {
                            counter = page - 1;
                            db.updateCounter(letters, counter, sqlQuery, orderBy);
                            ultimate = null;
                            nextWord();
                        }
                    }).create();
            dialog.show();
        });
    }

    public void onItemClick(int i, LinearLayout l1) {
        String selectedWord = wordsList.get(i);
        ArrayList<String> chosenWord = jumbles.get(selectedWord);

        String meaning = chosenWord.get(0);
        String category = chosenWord.get(2);
        String lexicons = chosenWord.get(3);

        int nightModeFlags =
                this.getResources().getConfiguration().uiMode &
                        Configuration.UI_MODE_NIGHT_MASK;
        white = (nightModeFlags == Configuration.UI_MODE_NIGHT_YES ? "#000000" : "#FFFFFF");
        ultimate = selectedWord;

        if (label.equals("(No Action)"))
        {
            displayDefinition(meaning, category, lexicons);
        }
        else {
            db.updateLabel(selectedWord, label);
            (jumbles.get(selectedWord)).set(2, label);
            String newColour = colourList.get(label);

            l1.setBackgroundColor(Color.parseColor(newColour));

            if (newColour.equals(white)) {
                t2.setText(Html.fromHtml(meaning + " <b>" + (label.isEmpty() ? "(No Tag)" : label) + " " + lexicons + "</b>" + (detail ? db.getFullDetails(selectedWord) : "")));
            } else {
                t2.setText(Html.fromHtml("<font color=\"" + newColour + "\">" + meaning + " <b>" + (label.isEmpty() ? "(No Tag)" : label) + " " + lexicons + "</b>" + (detail ? db.getFullDetails(selectedWord) : "") + "</font>"));
            }
        }
    }

    public void onItemLongClick(int i) {
        String selectedWord = wordsList.get(i);

        char[] character = selectedWord.toCharArray();
        Arrays.sort(character);
        String order = new String(character);

        String allAnagrams = db.getAllAnswers(order);

        db.messageBox("All anagrams", allAnagrams, MainActivity.this);
    }

    public void refresh()
    {
        ArrayList<Integer> dimensions = db.getZoom("Grid");
        rows = dimensions.get(0);
        columns = dimensions.get(1);
        font = dimensions.get(2);
        combo = dimensions.get(3);
        loader = dimensions.get(4);
        maximumWordLength = db.getMaximumWordLength();

        refreshSpinner();

        if (mode == 1) {
            closeCursor();
            anagrams = db.getAllAnagrams(letters, sqlQuery, orderBy);
            words = anagrams.getCount();
            counter = db.getCounter(letters, sqlQuery, orderBy);

            int peak = (words - 1) / (rows * columns);
            if (counter > peak && words > 0) {
                counter = peak;
                db.updateCounter(letters, counter, sqlQuery, orderBy);
            }

            nextWord();
            refreshDefinition();
        } else if (mode == 2) {
            closeCursor();
            anagrams = db.getSqlQuery(sqlQuery, MainActivity.this, orderBy);
            words = anagrams.getCount();
            counter = db.getCounter(letters, sqlQuery, orderBy);

            int apex = (words - 1) / (rows * columns);
            if (counter > apex && words > 0) {
                counter = apex;
                db.updateCounter(letters, counter, sqlQuery, orderBy);
            }

            executeSqlQuery();
            refreshDefinition();
        }
    }

    public void refreshDefinition()
    {
        if (ultimate != null)
        {
            ArrayList<String> tag = db.getDefinition(ultimate);
            String meaning = tag.get(0);
            String category = tag.get(2);
            String lexicons = tag.get(3);

            displayDefinition(meaning, category, lexicons);
        }
    }

    public void displayDefinition(String meaning, String category, String lexicons)
    {
        if (colourList.containsKey(category)) {
            if ((colourList.get(category)).equals(white)) {
                t2.setText(Html.fromHtml(meaning + " <b>" + (category.isEmpty() ? "(No Tag)" : category) + " " + lexicons + "</b>" + (detail ? db.getFullDetails(ultimate) : "")));
            } else {
                t2.setText(Html.fromHtml("<font color=\"" + colourList.get(category) + "\">" + meaning + " <b>" + (category.isEmpty() ? "(No Tag)" : category) + " " + lexicons + "</b>" + (detail ? db.getFullDetails(ultimate) : "") + "</font>"));
            }
        } else if (colourList.containsKey("")) {
            if ((colourList.get("")).equals(white)) {
                t2.setText(Html.fromHtml(meaning + " <b>" + (category.isEmpty() ? "(No Tag)" : category) + " " + lexicons + "</b>" + (detail ? db.getFullDetails(ultimate) : "")));
            } else {
                t2.setText(Html.fromHtml("<font color=\"" + colourList.get("") + "\">" + meaning + " <b>" + (category.isEmpty() ? "(No Tag)" : category) + " " + lexicons + "</b>" + (detail ? db.getFullDetails(ultimate) : "") + "</font>"));
            }
        } else {
            t2.setText(Html.fromHtml(meaning + " <b>" + (category.isEmpty() ? "(No Tag)" : category) + " " + lexicons + "</b>" + (detail ? db.getFullDetails(ultimate) : "")));
        }
    }

    public void refreshSpinner()
    {
        labelsList = db.getAllLabels();
        labelsList.add(0, new Pair<>("(No Action)", null));
        colourList = db.getColours();

        ColourAdapter comboBoxAdapter = new ColourAdapter(MainActivity.this, R.layout.colour, R.id.textview41, labelsList, MainActivity.this, true, combo);
        s1.setAdapter(comboBoxAdapter);

        s1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                label = (labelsList.get(i)).first;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    public void zoom()
    {
        LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
        final View yourCustomView = inflater.inflate(R.layout.zoom, null);

        EditText e6 = yourCustomView.findViewById(R.id.edittext6);
        EditText e7 = yourCustomView.findViewById(R.id.edittext7);
        EditText e8 = yourCustomView.findViewById(R.id.edittext8);
        EditText e14 = yourCustomView.findViewById(R.id.edittext22);
        EditText e16 = yourCustomView.findViewById(R.id.edittext29);

        e6.setHint("Enter a value greater than 0");
        e7.setHint("Enter a value greater than 0");
        e8.setHint("Enter a value greater than 11");
        e14.setHint("Enter a value greater than 11");
        e16.setHint("Enter a value greater than 11");

        e6.setText(Integer.toString(rows));
        e7.setText(Integer.toString(columns));
        e8.setText(Integer.toString(font));
        e14.setText(Integer.toString(combo));
        e16.setText(Integer.toString(loader));

        AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                .setTitle("Change rows, columns and font sizes")
                .setView(yourCustomView)
                .setPositiveButton("OK", (dialog1, whichButton) -> {
                    String old_rows = (e6.getText()).toString();
                    String old_columns = (e7.getText()).toString();
                    String old_font = (e8.getText()).toString();
                    String old_combo = (e14.getText()).toString();
                    String old_loader = (e16.getText()).toString();

                    int new_rows = (old_rows.isEmpty() ? 0 : Integer.parseInt(old_rows));
                    int new_columns = (old_columns.isEmpty() ? 0 : Integer.parseInt(old_columns));
                    int new_font = (old_font.isEmpty() ? 0 : Integer.parseInt(old_font));
                    int new_combo = (old_combo.isEmpty() ? 0 : Integer.parseInt(old_combo));
                    int new_loader = (old_loader.isEmpty() ? 0 : Integer.parseInt(old_loader));

                    StringBuilder sb = new StringBuilder();
                    if (new_rows < 1 && new_columns < 1) {
                        sb.append("Rows, columns should be ≥ 1");
                    }
                    else if (new_rows < 1) {
                        sb.append("Rows should be ≥ 1");
                    }
                    else if (new_columns < 1) {
                        sb.append("Columns should be ≥ 1");
                    }
                    if (new_font < 11 || new_combo < 11 || new_loader < 11) {
                        if (sb.length() > 0) {
                            sb.append("\n");
                        }
                        sb.append("Font sizes should be ≥ 11");
                    }

                    if (sb.length() > 0)
                    {
                        Toast.makeText(MainActivity.this, new String(sb), Toast.LENGTH_LONG).show();
                        zoom();
                    }
                    else
                    {
                        db.setZoom("Grid", new_rows, new_columns, new_font, new_combo, new_loader);
                        refresh();
                    }
                }).create();
        dialog.show();
    }

    public void setPrepared()
    {
        boolean prepared = pref.getBoolean("prepared", false);

        if (!prepared) {
            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean("prepared", true);
            editor.apply();
        }
    }

    public void reload(ArrayList<String> lastQuery, int DATABASE_VERSION, boolean recreate)
    {
        db = new sqliteDB(this, DATABASE_VERSION, lastQuery, recreate);

        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("version", DATABASE_VERSION);
        editor.apply();
    }

    public void closeCursor()
    {
        if (anagrams != null && !anagrams.isClosed())
        {
            anagrams.close();
        }
    }

    public void filterByLabel()
    {
        LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
        final View yourCustomView = inflater.inflate(R.layout.filter, null);

        EditText e9 = yourCustomView.findViewById(R.id.edittext9);
        EditText e10 = yourCustomView.findViewById(R.id.edittext10);
        TextView t5 = yourCustomView.findViewById(R.id.textview15);
        e10.setHint("Enter a value between 2 and " + maximumWordLength);

        final int[] sortIndex = new int[2];
        Spinner s7 = yourCustomView.findViewById(R.id.spinner15);
        ArrayAdapter<String> orderAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, allColumns);
        orderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s7.setAdapter(orderAdapter);

        Spinner s8 = yourCustomView.findViewById(R.id.spinner16);
        ArrayAdapter<String> sortAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, sort);
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s8.setAdapter(sortAdapter);

        s7.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                sortIndex[0] = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        s8.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                sortIndex[1] = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        s7.setSelection(6);
        s8.setSelection(1);

        Spinner s2 = yourCustomView.findViewById(R.id.spinner2);
        List<Pair<String, String>> tagsList = new ArrayList<>(labelsList.subList(1, labelsList.size()));;
        tagsList.add(0, new Pair<>("(All Tags)", null));

        ColourAdapter spinnerAdapter = new ColourAdapter(MainActivity.this, R.layout.colour, R.id.textview41, tagsList, MainActivity.this, true, combo);
        s2.setAdapter(spinnerAdapter);

        s2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    e9.setText("*");
                }
                else {
                    e9.setText((tagsList.get(i)).first);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        final int[] lengthIndex = new int[1];
        Spinner s3 = yourCustomView.findViewById(R.id.spinner3);
        ArrayList<String> lengthList = new ArrayList<>();
        lengthList.add(0, "Specific word length");
        lengthList.add(1, "All word lengths");

        ArrayAdapter<String> lengthAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, lengthList);
        lengthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s3.setAdapter(lengthAdapter);

        s3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    e10.setVisibility(View.VISIBLE);
                    t5.setVisibility(View.VISIBLE);
                    lengthIndex[0] = 0;
                }
                else {
                    e10.setVisibility(View.INVISIBLE);
                    t5.setVisibility(View.INVISIBLE);
                    lengthIndex[0] = 1;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                .setTitle("Filter by tag")
                .setView(yourCustomView)
                .setPositiveButton("OK", (dialog1, whichButton) -> {
                    String intermediate = (e9.getText()).toString();
                    String alphabets = (lengthIndex[0] == 0 ? (e10.getText()).toString() : "1");
                    int temporary = (alphabets.isEmpty() ? 0 : Integer.parseInt(alphabets));

                    if (lengthIndex[0] == 0 && temporary < 2)
                    {
                        Toast.makeText(MainActivity.this, "Enter a value between 2 and " + maximumWordLength + " for word length", Toast.LENGTH_LONG).show();
                        filterByLabel();
                    }
                    else
                    {
                        letters = temporary;
                        sqlQuery = intermediate;
                        orderBy = sortBy(sortIndex);
                        start();
                    }
                }).create();
        dialog.show();
    }

    public void getAllSubanagrams(boolean subanagram)
    {
        LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
        final View yourCustomView3 = inflater.inflate(R.layout.subanagram, null);

        EditText e11 = yourCustomView3.findViewById(R.id.edittext17);
        EditText e12 = yourCustomView3.findViewById(R.id.edittext18);
        EditText e13 = yourCustomView3.findViewById(R.id.edittext19);
        CheckBox c4 = yourCustomView3.findViewById(R.id.checkbox4);
        FrameLayout f3 = yourCustomView3.findViewById(R.id.framelayout3);

        LayoutInflater subinflater3 = LayoutInflater.from(MainActivity.this);
        final View subCustomView3 = subinflater3.inflate(R.layout.sieve, null);
        f3.addView(subCustomView3);

        db.addFunctionalities(MainActivity.this, e11, c4, false, subCustomView3);

        final int[] sortIndex = new int[2];
        Spinner s11 = yourCustomView3.findViewById(R.id.spinner19);
        ArrayAdapter<String> orderAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, allColumns);
        orderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s11.setAdapter(orderAdapter);

        Spinner s12 = yourCustomView3.findViewById(R.id.spinner20);
        ArrayAdapter<String> sortAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, sort);
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s12.setAdapter(sortAdapter);

        s11.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                sortIndex[0] = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        s12.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                sortIndex[1] = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        s11.setSelection(subanagram ? 3 : 6);
        s12.setSelection(1);

        AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                .setTitle(subanagram ? "Search for subanagrams" : "Search for anagrams")
                .setView(yourCustomView3)
                .setPositiveButton("OK", (dialog1, whichButton) -> subanagrams((((e11.getText()).toString()).trim()).toUpperCase(), (e12.getText()).toString(), subanagram, ((e13.getText()).toString()).replace("\"", "'"), c4.isChecked(), sortIndex, true)).create();
        dialog.show();
    }

    public void subanagrams(String letterSequence, String digit, boolean subanagram, String extra, boolean autoUnderscore, int[] sortIndex, boolean extraSql) {
        boolean flag = false;
        int blanks = 0;
        for (int digits = 0; digits < letterSequence.length(); digits++) {
            int flags = letterSequence.charAt(digits);
            if (flags == 46) {
                blanks++;
            }
            else if (flags < 65 || flags > 90) {
                flag = true;
                break;
            }
        }

        blanks += (digit.isEmpty() ? 0 : Integer.parseInt(digit));

        if (flag && extraSql) {
            Toast.makeText(MainActivity.this, "Letters field can contain only letters and dots for blanks", Toast.LENGTH_LONG).show();
            getAllSubanagrams(subanagram);
        }
        else {
            String letter;
            StringBuilder theQuery = new StringBuilder();

            if (flag) {
                letter = "";
                blanks = 0;
            }
            else {
                letter = letterSequence.replace(".", "");
            }

            if (subanagram) {
                int[] occurrence = new int[26];
                for (int myRadix = 0; myRadix < letter.length(); myRadix++) {
                    char theCharacter = letter.charAt(myRadix);
                    occurrence[theCharacter - 65]++;
                }

                for (int theRadix = 0; theRadix < 26; theRadix++) {
                    char occurrences = (char) (theRadix + 97);
                    theQuery.append("_no_").append(occurrences).append("_ <= ").append(occurrence[theRadix] + blanks).append(" AND ");
                }

                for (int myIndex = 0; myIndex < 26; myIndex++) {
                    char occurrences = (char) (myIndex + 97);
                    theQuery.append(myIndex == 0 ? "" : " + ").append("ABS(_no_").append(occurrences).append("_ - ").append(occurrence[myIndex]).append(")");
                }
                theQuery.append(" <= ").append((2 * blanks) + letter.length()).append(" - _length_");
            } else {
                char[] myCharacter = letter.toCharArray();
                Arrays.sort(myCharacter);
                StringBuilder empties = new StringBuilder();
                for (char myLetter : myCharacter) {
                    empties.append("%").append(myLetter);
                }
                empties.append("%");
                String empty = new String(empties);
                theQuery.append("_length_ = ").append(letter.length() + blanks).append(" AND _alphagram_ LIKE '").append(empty).append("'");
            }

            if (!extra.isEmpty()) {
                theQuery.append(" AND (").append(autoUnderscore ? db.addUnderscores(extra) : extra).append(")");
            }

            execute(false, new String(theQuery), sortBy(sortIndex));
        }
    }

    public String sortBy(int[] selection) {
        switch (selection[0]) {
            case 0: return (selection[1] == 1 ? "DESC" : "ASC");
            case 1: return " ORDER BY RANDOM()" + (selection[1] == 1 ? " DESC" : "");
            default: return " ORDER BY _" + allColumns.get(selection[0]) + "_" + (selection[1] == 1 ? " DESC" : "");
        }
    }
}