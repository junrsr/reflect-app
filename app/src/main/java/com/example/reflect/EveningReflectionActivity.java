package com.example.reflect;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Date;

public class EveningReflectionActivity extends AppCompatActivity {

    private Intent intent; // the intent which holds all values passed in
    private Date date; // the date the user has currently selected -- will be stored to database

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // standard onCreate functionality
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_evening_reflection);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // initialise all component values
        initValues();

        showDatabase();

    }

    /**
     * initialises all component values and retrieves data passed from the intent
     */
    private void initValues(){
        // get the intent passed through by other activities
        intent = getIntent();

        // get the date stored within the intent
        if (intent != null){
            // retrieve the date from the intent
            date = (Date) intent.getSerializableExtra("date");
        }
    }

    private void showDatabase(){
        DatabaseHelper databaseHelper = new DatabaseHelper(EveningReflectionActivity.this);
        SQLiteDatabase database = databaseHelper.getWritableDatabase();

        Cursor results = database.query(Utils.JOURNAL_TABLE, new String[] {"date", "moodScore", "title", "content"}, null, null, null, null, null);
        results.moveToFirst();

        String str = "";

        for (int i = 0; i < results.getCount(); i++){
            long dateLong = results.getLong(0);
            Date date1 = new Date(dateLong);
            str += "date: " + date1.toString() + "| moodScore: " + results.getInt(1) + "\n" + results.getString(2) + "\n" + results.getString(3);

            results.moveToNext();
        }

        TextView tv = findViewById(R.id.textView2);
        tv.setText(str);

    }
}