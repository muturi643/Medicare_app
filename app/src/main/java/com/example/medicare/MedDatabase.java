package com.example.medicare;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;


public class MedDatabase extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "medication.db";
    private static final int DATABASE_VERSION = 1;

    // Table and column names
    private static final String TABLE_MEDICATIONS = "medications";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_AMOUNT = "amount";
    private static final String COLUMN_TIMES_PER_DAY = "times_per_day";
    private static final String COLUMN_START_TIME = "start_time";

    public MedDatabase(@Nullable Context context, String medicare, Object o, int i) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL to create medications table
        String createTableQuery = "CREATE TABLE " + TABLE_MEDICATIONS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_NAME + " TEXT, "
                + COLUMN_AMOUNT + " TEXT, "
                + COLUMN_TIMES_PER_DAY + " INTEGER, "
                + COLUMN_START_TIME + " TEXT)";
        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if it exists
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEDICATIONS);
        onCreate(db);
    }

    // Method to insert a new medication
    public long Routine(String name, String amount, int timesPerDay, String startTime) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_AMOUNT, amount);
        values.put(COLUMN_TIMES_PER_DAY, timesPerDay);
        values.put(COLUMN_START_TIME, startTime);
        db.insert(TABLE_MEDICATIONS, null, values);
        db.close();
        return 0;
    }

    // Update a medication
    public int updateMedication(int id, String name, String amount, int timesPerDay, String startTime) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_AMOUNT, amount);
        values.put(COLUMN_TIMES_PER_DAY, timesPerDay);
        values.put(COLUMN_START_TIME, startTime);
        int rowsAffected = db.update(TABLE_MEDICATIONS, values, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
        return rowsAffected;
    }

    // Delete a medication by ID
    public int deleteMedication(int id) {
        SQLiteDatabase db = getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_MEDICATIONS, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
        return rowsDeleted;
    }

    // Fetch all medications
    public Cursor getAllMedications() {
        SQLiteDatabase db = getReadableDatabase();
        return db.query(TABLE_MEDICATIONS, null, null, null, null, null, null);
    }

    public Cursor getMedicationById(int recordId) {
        SQLiteDatabase db = getReadableDatabase();
        // Query the database to get a specific medication by ID
        return db.query(
                TABLE_MEDICATIONS,          // Table name
                null,                       // Select all columns
                COLUMN_ID + "=?",           // WHERE clause
                new String[]{String.valueOf(recordId)}, // WHERE arguments
                null,                       // No GROUP BY clause
                null,                       // No HAVING clause
                null                        // Default sort order
        );
    }
}


