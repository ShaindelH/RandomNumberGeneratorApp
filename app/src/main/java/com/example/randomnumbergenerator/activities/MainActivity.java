package com.example.randomnumbergenerator.activities;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;


import static com.example.randomnumbergenerator.libs.Utils.getJSONStringFromNumberList;
import static com.example.randomnumbergenerator.libs.Utils.getNumberListFromJSONString;

import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.randomnumbergenerator.R;
import com.example.randomnumbergenerator.libs.Utils;
import com.example.randomnumbergenerator.model.RandomNumber;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.appcompat.widget.Toolbar;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.preference.PreferenceManager;

import com.example.randomnumbergenerator.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private RandomNumber mRandomNumber;
    private ArrayList<Integer> mNumberHistory;
    private Snackbar mSnackBar;
    private TextView mTextViewResults;
    private String mKey = getString(R.string.saving_key);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ExtendedFloatingActionButton fab = findViewById(R.id.generateBtn);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generateNumber();

            }
        });
        mTextViewResults = findViewById(R.id.random_number_text_view);
        mRandomNumber = new RandomNumber();
        initializeHistoryList(savedInstanceState, mKey);


    }

    private void initializeHistoryList(Bundle savedInstanceState, String key) {
        if (savedInstanceState != null) {
            mNumberHistory = savedInstanceState.getIntegerArrayList(key);
        } else {
            String history = getDefaultSharedPreferences(this).getString(key, null);
            mNumberHistory = history == null ?
                    new ArrayList<>() : Utils.getNumberListFromJSONString(history);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(mKey, getJSONStringFromNumberList(mNumberHistory));
    }

    @Override
    protected void onStop(){
        super.onStop();
        saveListInSharedPrefs();
    }
    private void saveListInSharedPrefs() {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = defaultSharedPreferences.edit();

        editor.putString(mKey, getJSONStringFromNumberList(mNumberHistory));
        editor.apply();
    }

    @Override
    protected void onStart() {
        super.onStart();
        restoreFromPreferences();
    }
    private void restoreFromPreferences() {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String listString = defaultSharedPreferences.getString(mKey, null);
        if (listString!=null) {
            mNumberHistory = getNumberListFromJSONString(listString);
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_history: {
                showHistory();
                return true;
            }
            case R.id.action_clear_history: {
                clearHistory();
                return true;
            }
            case R.id.action_about: {
                showAbout();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void clearHistory() {
        mNumberHistory.clear();
        mSnackBar.setText("History Cleared");
        mSnackBar.show();
    }

    private void showHistory() {
        Utils.showInfoDialog (MainActivity.this,
                "History", mNumberHistory.toString());
    }

    private void showAbout() {

        mSnackBar =
                Snackbar.make(findViewById(android.R.id.content), getString(R.string.about_content),
                        Snackbar.LENGTH_LONG);
        mSnackBar.show();

    }

    public void generateNumber() {

        EditText fromEditText = findViewById(R.id.from_number);
        int fromNumber = Integer.parseInt(fromEditText.getText().toString());
        EditText toEditText = findViewById(R.id.to_number);
        int toNumber = Integer.parseInt(toEditText.getText().toString());

        mRandomNumber.setFromTo(fromNumber, toNumber);
        int rand = mRandomNumber.getCurrentRandomNumber();

        mNumberHistory.add(rand);
        mTextViewResults.setText(String.format("%s %d", getString(R.string.random_number_title), rand));
    }
}