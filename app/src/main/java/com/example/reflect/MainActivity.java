package com.example.reflect;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private Calendar calendar; // used to store the day the user is currently on
    private CardView morningCardView; // the cardview for morning reflection
    private CardView eveningCardView; // the cardview for evening reflection


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


    }

    /**
     * initialises all component values
     */
    private void initValues(){
        // calendar
        calendar = Calendar.getInstance();

        // different tabs
        morningCardView = findViewById(R.id.morningReflectionCard);
        eveningCardView = findViewById(R.id.eveningReflectionCard);
    }

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
    }

}