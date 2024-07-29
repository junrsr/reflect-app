package com.example.reflect;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.slider.Slider;

import java.util.Date;

public class JournalActivity extends AppCompatActivity {
    private Date date;

    // slider content
    private Slider slider;
    private TextView sliderText; // the ui component which will store a text representation of the slider score (awful -> excellent)

    // buttons
    private FloatingActionButton nextButton; // the next button to bring up a new question / insert into database
    private ImageView closeButton;
    private ImageView backButton;

    // input fields
    private EditText journalTitleInput;
    private EditText journalContentInput;

    // scoring
    private int moodScore;
    private String journalTitle;
    private String journalContent;

    // questions
    private int currentQuestion = 0;
    private ConstraintLayout moodLayout;
    private ScrollView journalLogLayout;

    // constant arrays of messages we will display in relevant UI components
    private final String[] scoreBasedMessages = {"awful.", "bad.", "okay.", "good.", "amazing."};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_journal);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // initialises all ui components
        initValues();

        // checks to see the value of the slider -- adjusts text manually
        getSliderValue();

        // handle button presses
        handleNextButton();
        handleBackButton();
        handleCloseButton();
    }

    private void initValues(){

        // get the intent passed through by other activities
        Intent intent = getIntent();

        // get the date stored within the intent
        if (intent != null){
            // retrieve the date from the intent
            date = (Date) intent.getSerializableExtra("date");
        }

        // slider
        slider = findViewById(R.id.slider);
        sliderText = findViewById(R.id.sliderText);

        // input fields
        journalTitleInput = findViewById(R.id.title);
        journalContentInput = findViewById(R.id.bodyContent);

        // buttons
        nextButton = findViewById(R.id.nextButton);
        closeButton = findViewById(R.id.close);
        backButton = findViewById(R.id.back);

        // layouts
        journalLogLayout = findViewById(R.id.journalLogLayout);
        moodLayout = findViewById(R.id.moodLayout);

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
                    // store the mood score
                    moodScore = (int) Math.ceil(slider.getValue());

                    // display the next question
                    moodLayout.setVisibility(View.INVISIBLE);
                    journalLogLayout.setVisibility(View.VISIBLE);

                    currentQuestion++; // increment to the next question
                }
                else if (currentQuestion == 1) {
                    // store the input values
                    journalTitle = journalTitleInput.getText().toString();
                    journalContent = journalContentInput.getText().toString();

                    if (validateInput()){
                        // TODO:  add to database
                        Toast.makeText(JournalActivity.this, moodScore + "\n" + journalTitle + "\n" + journalContent, Toast.LENGTH_SHORT).show();
                    }

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
                Intent intent = new Intent(JournalActivity.this, MainActivity.class);
                intent.putExtra("date", date); // pass the date so we can move back to that page
                startActivity(intent);
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

                // if its the first question
                if (currentQuestion == 0){
                    // show the slider, hide the journal log
                    moodLayout.setVisibility(View.VISIBLE);
                    journalLogLayout.setVisibility(View.INVISIBLE);
                }
                // TODO:  update with additional questions
                else{
                    currentQuestion = 0;
                }
            }
        });
    }


    /**
     * validates the journal text input to ensure that its valid
     *
     * @return whether or not the input is valid
     */
    private boolean validateInput(){
        // if there is no content
        if (journalContent.isEmpty()){
            // display appropriate error message
            Toast.makeText(JournalActivity.this, "Please make sure to write a journal entry", Toast.LENGTH_SHORT).show();
            return false;
        }

        else if (journalTitle.length() < 3 || journalTitle.length() > 30){
            Toast.makeText(this, "Please ensure the title is between 3 and 30 characters long", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}