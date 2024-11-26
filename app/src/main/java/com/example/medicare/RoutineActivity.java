package com.example.medicare;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class RoutineActivity extends AppCompatActivity {

    private TextView selectedDateTextView;

    EditText edName,edAmount,edNumberOfTimesADay,startTime;
    Button  btnsubmit;
    private static final String PREFS_NAME = "StreakPreferences";
    private static final String LAST_OPEN_DATE = "last_open_date";
    private static final String STREAK_COUNT = "streak_count";
    private TextView streakTextView;


    @SuppressLint({"DefaultLocale", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_routine);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        edName = findViewById(R.id.medication_name);
        edAmount = findViewById(R.id.medication_amount);
        edNumberOfTimesADay = findViewById(R.id.medication_frequency);
        startTime = findViewById(R.id.startTime);
        btnsubmit = findViewById(R.id.submitButton);

        MedDatabase medDatabase = new MedDatabase(getApplicationContext(),"Medicare",null,1);

        btnsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edName.getText().toString();
                String amountToTake = edAmount.getText().toString();
                String times = edNumberOfTimesADay.getText().toString();
                String start = startTime.getText().toString();

                if (name.isEmpty() || amountToTake.isEmpty() || times.isEmpty() || start.isEmpty()) {
                    Toast.makeText(RoutineActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                long result = medDatabase.Routine(name, amountToTake, Integer.parseInt(times), start);

                if (result != -1) {
                    Toast.makeText(RoutineActivity.this, "Medication added successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(RoutineActivity.this, "Failed to add medication", Toast.LENGTH_SHORT).show();
                }

                int timesADay = Integer.parseInt(times);
                scheduleAlarms(timesADay, start);

                startActivity(new Intent(RoutineActivity.this, HomeActivity.class));

                //Intent intent = new Intent(RoutineActivity.this, HomeActivity.class);
                //intent.putExtra("medication_name", name);
                //intent.putExtra("medication_amount", amountToTake);
                //intent.putExtra("medication_frequency", times);
                //intent.putExtra("startTime", start);
                //startActivity(intent);
            }
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
            @SuppressLint("SetTextI18n")
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

        // Set OnClickListener for icon1
        icon1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RoutineActivity.this, HomeActivity.class));
            }
        });

        // Set OnClickListener for icon2
        icon2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RoutineActivity.this, DiscoverActivity.class);
                startActivity(intent);
            }
        });

        // Set OnClickListener for icon3
        icon3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RoutineActivity.this, ProfileActivity.class);
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
        @SuppressLint("SetTextI18n") DatePickerDialog datePickerDialog = new DatePickerDialog(RoutineActivity.this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Update the TextView with the selected date
                    String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    selectedDateTextView.setText("Selected Date: " + selectedDate);
                    selectedDateTextView.setVisibility(View.VISIBLE); // Make the TextView visible
                }, year, month, day);

        // Show the DatePickerDialog
        datePickerDialog.show();
    }



    @SuppressLint({"MissingPermission", "ScheduleExactAlarm"})
    private void scheduleAlarms(int timesPerDay, String startTime) {
        try {
            // Parse start time
            String[] timeParts = startTime.split(":");
            if (timeParts.length != 2) throw new IllegalArgumentException("Invalid time format.");
            int startHour = Integer.parseInt(timeParts[0]);
            int startMinute = Integer.parseInt(timeParts[1]);

            if (timesPerDay <= 0) throw new IllegalArgumentException("Times per day must be greater than 0");

            // Calculate interval in milliseconds
            long intervalMillis = (24 * 60 * 60 * 1000L) / timesPerDay;

            // Initialize AlarmManager
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, startHour);
            calendar.set(Calendar.MINUTE, startMinute);
            calendar.set(Calendar.SECOND, 0);

            long startAlarmMillis = calendar.getTimeInMillis();
            if (startAlarmMillis < System.currentTimeMillis()) {
                startAlarmMillis += 24 * 60 * 60 * 1000L; // Adjust to the next day
            }

            // Schedule alarms for the day
            for (int i = 0; i < timesPerDay; i++) {
                long triggerTime = startAlarmMillis + (i * intervalMillis);

                Intent intent = new Intent(this, AlarmReceiver.class);
                intent.putExtra("medication_name", edName.getText().toString());
                intent.putExtra("medication_amount", edAmount.getText().toString());

                PendingIntent pendingIntent = PendingIntent.getBroadcast(
                        this,
                        i, // Unique request code for each alarm
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                );

                if (alarmManager != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
                    } else {
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
                    }
                }
            }

            Toast.makeText(this, "Alarms scheduled successfully!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("ScheduleAlarmsError", "Failed to schedule alarms", e);
            Toast.makeText(this, "Error scheduling alarms: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
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