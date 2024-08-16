package com.example.reflect;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

    private TextView dateView; // ui component which displays currently selected date

    private DatabaseHelper databaseHelper; // the database used to store reflection and journal details
    private SQLiteDatabase database;


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

        // handles date, morning, evening and reflection tab functionality when pressed
        setCardViewListeners();
        setDateListener();

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

        // find the UI component currently storing the date
        dateView = findViewById(R.id.dateView);
        dateView.setText(Utils.getDateString(calendar.getTime()));

        // initialise the database
        databaseHelper = new DatabaseHelper(MainActivity.this);
        database = databaseHelper.getWritableDatabase();
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
                // queries the morning entry table to collect the sleep and motivation scores for entries with matching dates
                SQLiteDatabase database = databaseHelper.getReadableDatabase();
                Cursor morningResults = database.query(Utils.MORNING_REFLECTION_TABLE, null, "date = ?", new String[] {"" + calendar.getTime().getTime()}, null, null, null);

                // if the entry hasn't been completed yet
                if (morningResults.getCount() == 0){
                    // navigate to the morning reflection page
                    Intent intent = new Intent(MainActivity.this, MorningReflectionActivity.class);
                    intent.putExtra("date", calendar.getTime()); // passes the current date through to the next page
                    startActivity(intent); // move to the new activity
                }
                // otherwise if it has been completed
                else{
                    // navigate to the completed journal entry page
                    Intent intent = new Intent(MainActivity.this, CompletedMorningReflectionActivity.class);
                    intent.putExtra("date", calendar.getTime()); // passes the current date through to the next page
                    startActivity(intent); // move to the new activity
                }

                // close the database
                database.close();

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
                // queries the journal entry table to see if any entries exist
                SQLiteDatabase database = databaseHelper.getReadableDatabase();
                Cursor journalResults = database.query(Utils.JOURNAL_TABLE, null, "date = ?", new String[] {"" + calendar.getTime().getTime()}, null, null, null);

                // if the entry hasn't been completed yet
                if (journalResults.getCount() == 0){
                    // navigate to the journal entry page
                    Intent intent = new Intent(MainActivity.this, JournalActivity.class);
                    intent.putExtra("date", calendar.getTime()); // passes the current date through to the next page
                    startActivity(intent); // move to the new activity
                }
                // otherwise if it has been completed
                else{
                    // navigate to the completed journal entry page
                    Intent intent = new Intent(MainActivity.this, CompletedJournalActivity.class);
                    intent.putExtra("date", calendar.getTime()); // passes the current date through to the next page
                    startActivity(intent); // move to the new activity
                }
            }
        });
    }


    /**
     * allows the user to modify the date they are currently viewing
     */
    private void setDateListener(){
        // add an event listener to the date view
        dateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // bring up a new event dialog
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        MainActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int day) {

                                // adjust the day, month and year
                                calendar.set(Calendar.DAY_OF_MONTH, day);
                                calendar.set(Calendar.MONTH, month);
                                calendar.set(Calendar.YEAR, year);

                                // adjust the UI text
                                String date = Utils.getDateString(calendar.getTime());
                                dateView.setText(date);

                                // reset the ui cards for this new date
                                setUICards();
                            }
                        },
                        calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
                );
                datePickerDialog.show();
            }
        });
    }

    /**
     * helper method which updates the UI components if an entry has been made
     */
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
        // otherwise if there are no entries for that date
        else{
            // store references to ui layouts
            ConstraintLayout morningLayoutUnfilled = findViewById(R.id.morningCardLayoutUnfilled);
            ConstraintLayout morningLayoutCompleted = findViewById(R.id.morningCardLayoutCompleted);

            // hide the layout component for the completed morning card
            morningLayoutUnfilled.setVisibility(View.VISIBLE);
            morningLayoutCompleted.setVisibility(View.INVISIBLE);
        }

        // close the cursor
        morningResults.close();


        // queries the journal table to collect the content and mood for entries with matching dates
        Cursor journalResults = database.query(Utils.JOURNAL_TABLE, new String[] {"moodScore", "content"}, "date = ?", new String[] {"" + calendar.getTime().getTime()}, null, null, null);

        // if we have existing entries
        if (journalResults.getCount() != 0){
            // navigate to the first element
            journalResults.moveToFirst();

            // get the column indexes for the entry
            int moodColumn = journalResults.getColumnIndex("moodScore");
            int contentColumn = journalResults.getColumnIndex("content");

            // get the relevant scores
            int moodScore = journalResults.getInt(moodColumn);
            String content = journalResults.getString(contentColumn);

            // update the morning card
            updateJournal(moodScore, content);
        }
        // otherwise if there are no entries for that date
        else{
            // show the layout component for an uncompleted journal entry
            ConstraintLayout journalLayoutUnfilled = findViewById(R.id.journalLayoutUnfilled);
            journalLayoutUnfilled.setVisibility(View.VISIBLE);

            // hide the layout component for a completed journal entry
            ConstraintLayout journalLayoutFilled = findViewById(R.id.journalLayoutFilled);
            journalLayoutFilled.setVisibility(View.INVISIBLE);
        }

        // close the cursor
        journalResults.close();

        // close the database
        database.close();

    }

    /**
     * displays the appropriate layout depending on if an entry has been logged with correct values
     * @param sleepScore the level of sleep the user got (1 - 5)
     * @param motivationScore how motivated the user is feeling (1 - 5)
     */
    private void updateMorningCard(int sleepScore, int motivationScore){
        // store references to ui layouts
        ConstraintLayout morningLayoutUnfilled = findViewById(R.id.morningCardLayoutUnfilled);
        ConstraintLayout morningLayoutCompleted = findViewById(R.id.morningCardLayoutCompleted);

        // show the layout component for the completed morning card
        morningLayoutUnfilled.setVisibility(View.INVISIBLE);
        morningLayoutCompleted.setVisibility(View.VISIBLE);

        // find the text view used to store all text
        TextView scoreText = findViewById(R.id.morningCardScoreText);

        // display the correct motivation and sleep score
        String displayText = "Motivation: " + sleepScore + "/5\n Sleep: " + motivationScore + "/5";
        scoreText.setText(displayText);

    }

    /**
     * appropriately displays and fills the journal content if the user has already made an entry for the specified day
     *
     * @param moodScore a constant integer which denotes how the user felt on a given day
     * @param content a string value which holds the journal content
     */
    private void updateJournal(final int moodScore, String content){
        // hide the layout component for an uncompleted journal entry
        ConstraintLayout journalLayoutUnfilled = findViewById(R.id.journalLayoutUnfilled);
        journalLayoutUnfilled.setVisibility(View.INVISIBLE);

        // show the layout component for a completed journal entry
        ConstraintLayout journalLayoutFilled = findViewById(R.id.journalLayoutFilled);
        journalLayoutFilled.setVisibility(View.VISIBLE);

        // find the relevant UI components
        TextView contentTextView = findViewById(R.id.journalContent);
        TextView moodTextView = findViewById(R.id.moodText);
        TextView journalDateTextView = findViewById(R.id.journalDateText);
        ImageView journalImageView = findViewById(R.id.journalMoodImage);

        // adjust the mood image and text based on the given mood score
        switch (moodScore){
            case 1:
                // adjust the text and color of the mood text
                moodTextView.setText("awful");
                moodTextView.setTextColor(getResources().getColor(R.color.emotionAwful));

                // display the very upset icon
                journalImageView.setImageResource(R.drawable.ic_very_sad);
                break;
            case 2:
                // adjust the text and color of the mood text
                moodTextView.setText("bad");
                moodTextView.setTextColor(getResources().getColor(R.color.emotionBad));

                // display the sad icon
                journalImageView.setImageResource(R.drawable.ic_sad);
                break;
            case 3:
                // adjust the text and color of the mood text
                moodTextView.setText("meh");
                moodTextView.setTextColor(getResources().getColor(R.color.emotionNeutral));

                // display the neutral face icon
                journalImageView.setImageResource(R.drawable.ic_neutral);
                break;
            case 4:
                // adjust the text and color of the mood text
                moodTextView.setText("good");
                moodTextView.setTextColor(getResources().getColor(R.color.emotionGood));

                // display the happy icon
                journalImageView.setImageResource(R.drawable.ic_happy);
                break;
            case 5:
                // adjust the text and color of the mood text
                moodTextView.setText("amazing");
                moodTextView.setTextColor(getResources().getColor(R.color.emotionGreat));

                // display the very happy icon
                journalImageView.setImageResource(R.drawable.ic_very_happy);
                break;
        }

        // display the journal content
        contentTextView.setText(content);

        // convert the current date into ideal string format
        String dateText = Utils.getDateString(calendar.getTime());

        // display the date
        journalDateTextView.setText(dateText);

    }




}