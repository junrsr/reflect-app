package com.example.reflect;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.slider.Slider;

import java.util.Date;

public class MorningReflectionActivity extends AppCompatActivity {
    private Date date; // the date the user has currently selected -- will be stored to database

    // slider content
    private Slider slider;
    private TextView questionTitle; // the ui component which will store the question title
    private TextView answerDescription; // the ui component which will store a text description of the answer the user is providing
    private TextView timeDescription; // the ui component which will store a description of when the reflection is taking place
    private TextView sliderText; // the ui component which will store a text representation of the slider score (awful -> excellent)

    // buttons
    private FloatingActionButton nextButton; // the next button to bring up a new question / insert into database
    private ImageView backButton; // the back button to navigate to a previous question
    private ImageView closeButton; // the close button to close this activity and return to the homepage

    // scoring
    private int sleepScore; // score from 1-5 detailing the level of sleep user got
    private int motivationScore; // score from 1-5 detailing motivation level of user

    // questions
    private int currentQuestion; // the value of the question the user is currently on

    // constant arrays of messages we will display in relevant UI components
    private final String[] questions = {"How well did you sleep?", "How motivated are you?"};
    private final String[] timesDescriptions = {"Last night", "Currently"};
    private final String[] answerBeginning = {"My sleep was ", "My motivation is "};
    private final String[] scoreBasedMessages = {"awful.", "bad.", "okay.", "good.", "excellent."};


    /**
     * standard onCreate method
     * initialises all components
     * updates ui based on slider feedback
     * handles button presses
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
        setContentView(R.layout.activity_morning_reflection);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // initialise all component values
        initValues();

        // checks to see the value of the slider and stores it
        getSliderValue();

        // handle button presses
        handleNextButton();
        handleBackButton();
        handleCloseButton();
    }

    /**
     * modifies slider text based on current slider value
     */
    private void getSliderValue() {
        // listener for when slider value changes
        slider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float v, boolean b) {
                // store the current score
                int curScore = (int) Math.ceil(slider.getValue());

                // set the text to the appropriate message
                sliderText.setText(scoreBasedMessages[curScore - 1]);

            }
        });
    }

    /**
     * initialises all component values and retrieves data passed from the intent
     * starts on the initial question
     */
    private void initValues(){

        // get the intent passed through by other activities
        Intent intent = getIntent();

        // get the date stored within the intent
        if (intent != null){
            // retrieve the date from the intent
            date = (Date) intent.getSerializableExtra("date");
        }


        // initialise necessary components
        // slider + descriptors
        slider = findViewById(R.id.slider);
        sliderText = findViewById(R.id.sliderText);
        questionTitle = findViewById(R.id.title);
        answerDescription = findViewById(R.id.answerDescriptor);
        timeDescription = findViewById(R.id.timeDescription);

        // buttons
        nextButton = findViewById(R.id.nextButton);
        backButton = findViewById(R.id.back);
        closeButton = findViewById(R.id.close);

        // start on question 1
        currentQuestion = 0;
    }

    /**
     * calculates and stores the relevant score
     * moves to the next set of questions if possible
     * adds values to database otherwise
     */
    private void handleNextButton(){
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // store the current score with the appropriate value
                if (currentQuestion == 0){
                    sleepScore = (int) Math.ceil(slider.getValue());
                }
                else if (currentQuestion == 1){
                    motivationScore = (int) Math.ceil(slider.getValue());
                }

                // move to the next question
                currentQuestion++;

                // check if question is in range
                if (currentQuestion < questions.length){
                    // update the sliders and slider text for the next question
                    updateSliders();
                }
                else{
                    insertIntoDatabase();
                }
            }
        });
    }

    /**
     * moves the user back to the previous question
     */
    private void handleBackButton(){
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // move to the previous question
                currentQuestion--;

                // check if question is in range
                if (currentQuestion >= 0){
                    // update the sliders and slider text for the next question
                    updateSliders();
                }
                else{
                    currentQuestion = 0;
                }
            }
        });
    }

    /**
     * redirect the user back to the homepage once the close button is clicked
     */
    private void handleCloseButton(){
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // navigate back to main activity
                Intent intent = new Intent(MorningReflectionActivity.this, MainActivity.class);
                intent.putExtra("date", date); // pass the date so we can move back to that page
                startActivity(intent);
            }
        });

    }

    /**
     * updates the text views to display the next set of questions / prompts
     * resets the slider position back to the center
     */
    private void updateSliders(){
        // update the modifies as necessary
        questionTitle.setText(questions[currentQuestion]);
        answerDescription.setText(answerBeginning[currentQuestion]);
        timeDescription.setText(timesDescriptions[currentQuestion]);

        // reset slider
        sliderText.setText(scoreBasedMessages[2]);
        slider.setValue(2.5f);
    }

    /**
     * takes the input values from the user and stores it in the morning reflection table of the database
     * redirects the user back to the homepage
     */
    private void insertIntoDatabase(){
        // get an instance of the database
        DatabaseHelper databaseHelper = new DatabaseHelper(MorningReflectionActivity.this);
        SQLiteDatabase database = databaseHelper.getWritableDatabase();

        // create a new ContentValues object to store pairs and values
        ContentValues values = new ContentValues();

        // add field names and corresponding values for new record to the ContentValues object
        values.put("sleepScore", sleepScore);
        values.put("motivationScore", motivationScore);
        values.put("date", date.getTime()); // convert the date into long format to be stored -- convert back to date on retrieval

        // insert the values to the database
        database.insert(Utils.MORNING_REFLECTION_TABLE, null, values);

        // redirect to homepage
        Intent intent = new Intent(MorningReflectionActivity.this, MainActivity.class);
        startActivity(intent);

        // close the database
        database.close();
    }
}