package com.example.medicare;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ProfileActivity extends AppCompatActivity {

    private TextView selectedDateTextView;
    private static final String PREFS_NAME = "StreakPreferences";
    private static final String LAST_OPEN_DATE = "last_open_date";
    private static final String STREAK_COUNT = "streak_count";
    private TextView streakTextView;

    @SuppressLint({"SetTextI18n", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        selectedDateTextView = findViewById(R.id.selected_date_text_view);
        ImageView calendarImageView = findViewById(R.id.calender);

        calendarImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        streakTextView = findViewById(R.id.streak_text_view);
        ImageView strImageView = findViewById(R.id.str);

        strImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentStreak = calculateAndUpdateStreak();
                streakTextView.setVisibility(View.VISIBLE);
                streakTextView.setText("Current Streak: " + currentStreak + " days");
            }
        });

        ImageView icon1 = findViewById(R.id.icon1);
        ImageView icon2 = findViewById(R.id.icon2);
        ImageView icon3 = findViewById(R.id.icon3);
        Button   btn = findViewById(R.id.btnlogout);
        TextView tv = findViewById(R.id.username);

        // Retrieve the user's name from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("shared_prefs", Context.MODE_PRIVATE);
        String userName = sharedPreferences.getString("username", "User "); // Default to "User " if not found
        tv.setText(userName); // Display the user's name


        // Set OnClickListener for icon1
        icon1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, HomeActivity.class));
            }
        });

        // Set OnClickListener for icon2
        icon2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, DiscoverActivity.class);
                startActivity(intent);
            }
        });

        // Set OnClickListener for icon3
        icon3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private void showDatePickerDialog() {
        // Get the current date
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Create a DatePickerDialog
        @SuppressLint("SetTextI18n") DatePickerDialog datePickerDialog = new DatePickerDialog(ProfileActivity.this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Update the TextView with the selected date
                    String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    selectedDateTextView.setText("Selected Date: " + selectedDate);
                    selectedDateTextView.setVisibility(View.VISIBLE); // Make the TextView visible
                }, year, month, day);

        // Show the DatePickerDialog
        datePickerDialog.show();
    }

    private int calculateAndUpdateStreak() {
        // Get current date in "yyyy-MM-dd" format
        String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .format(Calendar.getInstance().getTime());

        // Access SharedPreferences
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        // Retrieve the last open date and streak count from SharedPreferences
        String lastOpenDate = preferences.getString(LAST_OPEN_DATE, null);
        int streakCount = preferences.getInt(STREAK_COUNT, 0);

        try {
            if (lastOpenDate != null) {
                // Parse last open date
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Calendar lastOpen = Calendar.getInstance();
                lastOpen.setTime(sdf.parse(lastOpenDate));

                // Get today's date
                Calendar today = Calendar.getInstance();

                // Calculate the difference in days
                long daysDifference = (today.getTimeInMillis() - lastOpen.getTimeInMillis())
                        / (24 * 60 * 60 * 1000);

                if (daysDifference == 1) {
                    // Continue the streak
                    streakCount++;
                } else if (daysDifference > 1) {
                    // Reset the streak
                    streakCount = 1;
                }
                // If daysDifference == 0, streakCount remains unchanged
            } else {
                // First-time app use, start a new streak
                streakCount = 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Reset streak if parsing fails
            streakCount = 1;
        }

        // Save the updated date and streak count
        editor.putString(LAST_OPEN_DATE, todayDate);
        editor.putInt(STREAK_COUNT, streakCount);
        editor.apply();

        return streakCount;
    }
}