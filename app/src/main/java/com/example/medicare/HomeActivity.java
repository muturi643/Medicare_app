package com.example.medicare;


import android.content.SharedPreferences;
import android.database.AbstractCursor;
import android.database.Cursor;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
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

public class HomeActivity extends AppCompatActivity {
    private MedDatabase medDatabase;
    private final int currentMedicationId = 1;

    private TextView selectedDateTextView;

    TextView tvName, tvAmount, tvFrequency, tvTime;
    Button editButton, saveButton, deleteButton;
    EditText editMedName, editAmount, editTimesPerDay, editStartTime;

    private String name, amountToTake, start;
    private AbstractCursor cursor;
    private int  times;

    private static final String PREFS_NAME = "StreakPreferences";
    private static final String LAST_OPEN_DATE = "last_open_date";
    private static final String STREAK_COUNT = "streak_count";

    private TextView streakTextView;


    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        medDatabase = new MedDatabase(getApplicationContext(),"Medicare",null,1);
        //db = medDatabase.getReadableDatabase();


         tvName = findViewById(R.id.medication_name_text);
         tvAmount =findViewById(R.id.medication_amount_text);
         tvFrequency =findViewById(R.id.medication_frequency_text);
         tvTime =findViewById(R.id.medication_time_text);

        editMedName = findViewById(R.id.editMedName);
        editAmount = findViewById(R.id.editAmount);
        editTimesPerDay = findViewById(R.id.editTimesPerDay);
        editStartTime = findViewById(R.id.editStartTime);

        editButton =findViewById(R.id.edit_button);
        saveButton = findViewById(R.id.saveButton);
        deleteButton = findViewById(R.id.deleteButton);

        //recordId = getIntent().getIntExtra("RECORD_ID", -1);
        loadMedicationDetails(currentMedicationId);


        // Set up the edit button
        editButton.setOnClickListener(v -> {
            tvName.setVisibility(View.GONE);
            tvAmount.setVisibility(View.GONE);
            tvFrequency.setVisibility(View.GONE);
            tvTime.setVisibility(View.GONE);

            editMedName.setVisibility(View.VISIBLE);
            editAmount.setVisibility(View.VISIBLE);
            editTimesPerDay.setVisibility(View.VISIBLE);
            editStartTime.setVisibility(View.VISIBLE);

            editMedName.setText(tvName.getText().toString().replace("Medication: ", ""));
            editAmount.setText(tvAmount.getText().toString().replace("Amount: ", ""));
            editTimesPerDay.setText(tvFrequency.getText().toString().replace("Frequency: ", "").replace(" times a day", ""));
            editStartTime.setText(tvTime.getText().toString().replace("Start Time: ", ""));

            // Populate edit fields with current values
            //editMedName.setText(name);
            //editAmount.setText(amountToTake);
            //editTimesPerDay.setText(times);
            //editStartTime.setText(start);

            saveButton.setVisibility(View.VISIBLE);

        });

        saveButton.setOnClickListener(v -> {
            String updatedName = editMedName.getText().toString();
            String updatedAmount = editAmount.getText().toString();
            int updatedFrequency = Integer.parseInt(editTimesPerDay.getText().toString());
            String updatedStartTime = editStartTime.getText().toString();

            int rowsUpdated = medDatabase.updateMedication(currentMedicationId, updatedName, updatedAmount, updatedFrequency, updatedStartTime);

            if (rowsUpdated > 0) {
                Toast.makeText(this, "Record updated successfully!", Toast.LENGTH_SHORT).show();


                editMedName.setVisibility(View.GONE);
                editAmount.setVisibility(View.GONE);
                editTimesPerDay.setVisibility(View.GONE);
                editStartTime.setVisibility(View.GONE);

                saveButton.setVisibility(View.GONE);
                startActivity(new Intent(HomeActivity.this, HomeActivity.class));
                loadMedicationDetails(currentMedicationId);
            } else {
                Toast.makeText(this, "Failed to update record.", Toast.LENGTH_SHORT).show();
            }

        });

        deleteButton.setOnClickListener(v -> {
            int rowsDeleted = medDatabase.deleteMedication(currentMedicationId );

            if (rowsDeleted > 0) {
                Toast.makeText(this, "Record deleted successfully!", Toast.LENGTH_SHORT).show();
                tvName.setVisibility(View.VISIBLE);
                tvAmount.setVisibility(View.VISIBLE);
                tvFrequency.setVisibility(View.VISIBLE);
                tvTime.setVisibility(View.VISIBLE);

                editMedName.setVisibility(View.GONE);
                editAmount.setVisibility(View.GONE);
                editTimesPerDay.setVisibility(View.GONE);
                editStartTime.setVisibility(View.GONE);
                saveButton.setVisibility(View.GONE);
                //finish(); // Close the activity
                startActivity(new Intent(HomeActivity.this, HomeActivity.class));
            } else {
                Toast.makeText(this, "Failed to delete record.", Toast.LENGTH_SHORT).show();
            }
        });

        ImageView icon1 = findViewById(R.id.icon1);
        ImageView icon2 = findViewById(R.id.icon2);
        ImageView icon3 = findViewById(R.id.icon3);
        ImageView icon4 = findViewById(R.id.add);


        // Set OnClickListener for icon1
        icon1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, HomeActivity.class));
            }
        });

        // Set OnClickListener for icon2
        icon2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, DiscoverActivity.class);
                startActivity(intent);
            }
        });

        // Set OnClickListener for icon3
        icon3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        icon4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, RoutineActivity.class);
                startActivity(intent);
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
            @Override
            public void onClick(View v) {
                int currentStreak = calculateAndUpdateStreak();
                streakTextView.setVisibility(View.VISIBLE);
                streakTextView.setText("Current Streak: " + currentStreak + " days");
            }
        });

    }

    @SuppressLint("SetTextI18n")
    private void loadMedicationDetails(int medicationId) {
        Cursor cursor = medDatabase.getMedicationById(medicationId);
        //@SuppressLint("Recycle") Cursor cursor = db.query("medications", null, "id = ?", new String[]{String.valueOf(recordId)}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            // Assuming the table has these columns
            name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            amountToTake = cursor.getString(cursor.getColumnIndexOrThrow("amount"));
            times = cursor.getInt(cursor.getColumnIndexOrThrow("times_per_day"));
            start = cursor.getString(cursor.getColumnIndexOrThrow("start_time"));


            // Get the data from the intent
            //Intent intent = getIntent();
            //if (intent.hasExtra("medication_name") && intent.hasExtra("medication_amount") && intent.hasExtra("medication_frequency") && intent.hasExtra("startTime")) {
            //name = intent.getStringExtra("medication_name");
            //amountToTake = intent.getStringExtra("medication_amount");
            //times = intent.getStringExtra("medication_frequency");
            //start = intent.getStringExtra("startTime");


            // Set the text views with the medication details
            tvName.setText("Medication: " + name);
            tvAmount.setText("Amount: " + amountToTake);
            tvFrequency.setText("Frequency: " + times + " times a day");
            tvTime.setText("Time: " + start);

            // Make TextViews and Buttons Visible
            tvName.setVisibility(View.VISIBLE);
            tvAmount.setVisibility(View.VISIBLE);
            tvFrequency.setVisibility(View.VISIBLE);
            tvTime.setVisibility(View.VISIBLE);

            editButton.setVisibility(View.VISIBLE);
            deleteButton.setVisibility(View.VISIBLE);
            //saveButton.setVisibility(View.VISIBLE);
        } else {
            // If no data, keep everything hidden
            tvName.setVisibility(View.GONE);
            tvAmount.setVisibility(View.GONE);
            tvFrequency.setVisibility(View.GONE);
            tvTime.setVisibility(View.GONE);

            editButton.setVisibility(View.GONE);
            saveButton.setVisibility(View.GONE);
            deleteButton.setVisibility(View.GONE);
        }
    }


    private void showDatePickerDialog() {
        // Get the current date
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Create a DatePickerDialog
        @SuppressLint("SetTextI18n") DatePickerDialog datePickerDialog = new DatePickerDialog(HomeActivity.this,
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