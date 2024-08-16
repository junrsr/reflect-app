package com.example.reflect;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Date;

public class CompletedJournalActivity extends AppCompatActivity {
    private Date date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_completed_journal);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initData();

        // connect to the database
        DatabaseHelper databaseHelper = new DatabaseHelper(CompletedJournalActivity.this);
        SQLiteDatabase database = databaseHelper.getWritableDatabase();

        // queries the journal table to collect the content and mood for entries with matching dates
        Cursor journalResults = database.query(Utils.JOURNAL_TABLE, new String[] {"moodScore", "content", "title"}, "date = ?", new String[] {"" + date.getTime()}, null, null, null);
        journalResults.moveToFirst();

        // get the column indexes for the entry
        int moodColumn = journalResults.getColumnIndex("moodScore");
        int contentColumn = journalResults.getColumnIndex("content");
        int titleColumn = journalResults.getColumnIndex("title");

        // get the relevant scores
        int moodScore = journalResults.getInt(moodColumn);
        String title = journalResults.getString(titleColumn);
        String content = journalResults.getString(contentColumn);

        // TODO change -- display the appropriate fields
        TextView textView = findViewById(R.id.textView);

        String text = (moodScore + " / 5\n\n" ) + title + "\n\n" + content;
        textView.setText(text);

    }

    private void initData(){
        // get the intent passed through by other activities
        Intent intent = getIntent();

        // get the date stored within the intent
        if (intent != null){
            // retrieve the date from the intent
            date = (Date) intent.getSerializableExtra("date");
        }
    }
}