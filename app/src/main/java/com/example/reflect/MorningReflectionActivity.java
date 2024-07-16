package com.example.reflect;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
    private TextView questionTitle;
    private TextView answerDescription;
    private TextView timeDescription;
    private TextView sliderText;

    // buttons
    private FloatingActionButton nextButton;
    private ImageView backButton;
    private ImageView closeButton;

    // scoring
    private int sleepScore; // score from 1-5 detailing the level of sleep user got
    private int motivationScore; // score from 1-5 detailing motivation level of user

    // questions
    private int currentQuestion;

    private String[] questions = {"How well did you sleep?", "How motivated are you?"};
    private String[] timesDescriptions = {"Last night", "Currently"};
    private String[] answerBeginning = {"My sleep was ", "My motivation is "};
    private String[] scoreBasedMessages = {"awful.", "bad.", "okay.", "good.", "excellent."};


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
        getSleepValue();

        // handle button presses
        handleNextButton();
        handleBackButton();
        handleCloseButton();


    }

    private void getSleepValue() {
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
                    // TODO: add values to the database

                    Toast.makeText(MorningReflectionActivity.this, "Sleep score: " + sleepScore + "\nMotivation score: " + motivationScore, Toast.LENGTH_SHORT).show();

//                    // navigate back to the homepage
//                    Intent intent = new Intent(MorningReflectionActivity.this, MainActivity.class);
//                    intent.putExtra("date", date); // pass the date so we can move back to that page
//                    startActivity(intent);
                }
            }
        });
    }

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

    private void updateSliders(){
        // update the modifies as necessary
        questionTitle.setText(questions[currentQuestion]);
        answerDescription.setText(answerBeginning[currentQuestion]);
        timeDescription.setText(timesDescriptions[currentQuestion]);

        // reset slider
        sliderText.setText(scoreBasedMessages[2]);
        slider.setValue(2.5f);
    }
}