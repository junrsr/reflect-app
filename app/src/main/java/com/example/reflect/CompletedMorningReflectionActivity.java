package com.example.reflect;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.slider.Slider;

import java.util.Date;

public class CompletedMorningReflectionActivity extends AppCompatActivity {
    private Date date;

    private TextView sleepText;
    private TextView motivationText;

    private MaterialButton submitButton;
    private ImageView closeButton;

    private Slider sleepSlider;
    private Slider motivationSlider;

    private String[] descriptions = {"awful", "bad", "ok", "good", "excellent"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_completed_morning_reflection);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initData();

        // connect to the database
        DatabaseHelper databaseHelper = new DatabaseHelper(CompletedMorningReflectionActivity.this);
        SQLiteDatabase database = databaseHelper.getWritableDatabase();

        // queries the journal table to collect the content and mood for entries with matching dates
        Cursor morningResults = database.query(Utils.MORNING_REFLECTION_TABLE, new String[] {"sleepScore", "motivationScore"}, "date = ?", new String[] {"" + date.getTime()}, null, null, null);
        morningResults.moveToFirst();

        // get the column indexes for the entry
        int sleepColumn = morningResults.getColumnIndex("sleepScore");
        int motivationColumn = morningResults.getColumnIndex("motivationScore");

        // get the relevant scores
        int sleepScore = morningResults.getInt(sleepColumn);
        int motivationScore = morningResults.getInt(motivationColumn);

        // set the slider and text values
        sleepSlider.setValue(sleepScore);
        sleepText.setText(descriptions[sleepScore - 1]);
        motivationSlider.setValue(motivationScore);
        motivationText.setText(descriptions[motivationScore - 1]);

        getSliderValue();

        checkSubmit();
        checkClose();

    }

    private void initData(){
        // get the intent passed through by other activities
        Intent intent = getIntent();

        // get the date stored within the intent
        if (intent != null){
            // retrieve the date from the intent
            date = (Date) intent.getSerializableExtra("date");
        }

        // find the relevant sliders
        sleepSlider = findViewById(R.id.sleepSlider);
        motivationSlider = findViewById(R.id.motivationSlider);

        motivationText = findViewById(R.id.motivationText);
        sleepText = findViewById(R.id.sleepText);

        submitButton = findViewById(R.id.submitButton);
        closeButton = findViewById(R.id.close);
    }

    /**
     * modifies slider text based on current slider value
     */
    private void getSliderValue() {
        // listener for when slider value changes
        motivationSlider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float v, boolean b) {
                // store the current score
                int score = (int) Math.ceil(slider.getValue());
                
                // set the text to the appropriate message
                motivationText.setText(descriptions[score - 1]);

            }
        });

        sleepSlider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float v, boolean b) {
                // store the current score
                int score = (int) Math.ceil(slider.getValue());

                // set the text to the appropriate message
                sleepText.setText(descriptions[score - 1]);

            }
        });
    }

    private void checkSubmit(){
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get the sleep score
                int sleepScore = (int) Math.ceil(sleepSlider.getValue());
                int motivationScore = (int) Math.ceil(motivationSlider.getValue());

                // get reference to the database
                DatabaseHelper databaseHelper = new DatabaseHelper(CompletedMorningReflectionActivity.this);
                SQLiteDatabase database = databaseHelper.getWritableDatabase();

                // updated values
                ContentValues values = new ContentValues();
                values.put("sleepScore", sleepScore);
                values.put("motivationScore", motivationScore);

                // update the database
                database.update(Utils.MORNING_REFLECTION_TABLE, values, "date = ?", new String[] {"" + date.getTime()});

                // close the database
                database.close();

                // redirect to homepage
                Intent intent = new Intent(CompletedMorningReflectionActivity.this, MainActivity.class);
                intent.putExtra("date", date);
                startActivity(intent);


            }
        });
    }

    /**
     * closes the current activity and navigates back to the homepage
     */
    private void checkClose(){
        // add an onclick listener to the close button
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // navigate back to the homepage
                Intent intent = new Intent(CompletedMorningReflectionActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}