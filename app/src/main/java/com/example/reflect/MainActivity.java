package com.example.reflect;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private Calendar calendar; // used to store the day the user is currently on
    private CardView morningCardView; // the cardview for morning reflection
    private CardView eveningCardView; // the cardview for evening reflection
    private CardView journalCardView; // the cardview for journaling

    private DatabaseHelper databaseHelper; // the database used to store reflection and journal details


    /**
     * initialise all component values
     * set the card listeners for relevant components
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // standard onCreate functionality
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // initialise all component values
        initValues();

        // handles morning, evening and reflection tab functionality when pressed
        setCardViewListeners();

        // display recorded reflection / journal logs from previous entries if it already exists
        setUICards();
    }

    /**
     * initialises all component values
     */
    private void initValues(){
        // initialise the calendar to current day
        calendar = Calendar.getInstance();

        // set time to 00:00:00 for easier comparison
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        // initialise the different card views within the project
        morningCardView = findViewById(R.id.morningReflectionCard);
        eveningCardView = findViewById(R.id.eveningReflectionCard);
        journalCardView = findViewById(R.id.journalCardView);

        // initialise the database
        databaseHelper = new DatabaseHelper(MainActivity.this);
    }

    /**
     * handles card view activity once pressed
     * brings up the morning / evening / journal activities
     */
    private void setCardViewListeners(){
        // morning card view
        morningCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // navigate to the morning reflection page
                Intent intent = new Intent(MainActivity.this, MorningReflectionActivity.class);
                intent.putExtra("date", calendar.getTime()); // passes the current date through to the next page
                startActivity(intent); // move to the new activity
            }
        });

        // evening card view
        eveningCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // navigate to the evening reflection page
                Intent intent = new Intent(MainActivity.this, EveningReflectionActivity.class);
                intent.putExtra("date", calendar.getTime()); // passes the current date through to the next page
                startActivity(intent); // move to the new activity
            }
        });


        // journal card view
        journalCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // navigate to the morning reflection page
                Intent intent = new Intent(MainActivity.this, JournalActivity.class);
                intent.putExtra("date", calendar.getTime()); // passes the current date through to the next page
                startActivity(intent); // move to the new activity
            }
        });
    }

    private void setUICards(){
        // get a writable instance of the database so we can read / write data
        SQLiteDatabase database = databaseHelper.getReadableDatabase();

        // queries the morning entry table to collect the sleep and motivation scores for entries with matching dates
        Cursor morningResults = database.query(Utils.MORNING_REFLECTION_TABLE, new String[] {"sleepScore", "motivationScore"}, "date = ?", new String[] {"" + calendar.getTime().getTime()}, null, null, null);

        // if we have existing entries
        if (morningResults.getCount() != 0){
            // navigate to the first element
            morningResults.moveToFirst();

            // get the column indexes for the entry
            int sleepColumn = morningResults.getColumnIndex("sleepScore");
            int motivationColumn = morningResults.getColumnIndex("motivationScore");

            // get the relevant scores
            int sleepScore = morningResults.getInt(sleepColumn);
            int motivationScore = morningResults.getInt(motivationColumn);

            // update the morning card
            updateMorningCard(sleepScore, motivationScore);
        }

        // close the cursor
        morningResults.close();


        // close the database
        database.close();

    }

    /**
     * displays the appropriate layout depending on if an entry has been logged with correct values
     * @param sleepScore the level of sleep the user got (1 - 5)
     * @param motivationScore how motivated the user is feeling (1 - 5)
     */
    private void updateMorningCard(int sleepScore, int motivationScore){
        // hide the layout components for the unfilled morning card
        ConstraintLayout morningLayoutUnfilled = findViewById(R.id.morningCardLayoutUnfilled);
        morningLayoutUnfilled.setVisibility(View.INVISIBLE);

        // show the layout component for the completed morning card
        ConstraintLayout morningLayoutCompleted = findViewById(R.id.morningCardLayoutCompleted);
        morningLayoutCompleted.setVisibility(View.VISIBLE);

        // find the text view used to store all text
        TextView scoreText = findViewById(R.id.morningCardScoreText);

        // display the correct motivation and sleep score
        String displayText = "Motivation: " + sleepScore + "/5\n Sleep: " + motivationScore + "/5";
        scoreText.setText(displayText);

    }

}