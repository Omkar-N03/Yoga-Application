package com.example.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.model.YogaPose;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";
    private static final String DATABASE_NAME = "people_doing_yoga.db";
    private static final int DATABASE_VERSION = 1;

    // Table: users
    public static final String TABLE_USERS = "users";
    public static final String COL_USER_ID = "id";
    public static final String COL_USER_NAME = "username";
    public static final String COL_USER_EMAIL = "email";
    public static final String COL_USER_PASSWORD = "password";
    public static final String COL_USER_PROFILE_IMAGE = "profile_image_path";

    // Table: yoga_poses
    public static final String TABLE_POSES = "yoga_poses";
    public static final String COL_POSE_ID = "id";
    public static final String COL_POSE_ENG_NAME = "english_name";
    public static final String COL_POSE_SANS_NAME = "sanskrit_name";
    public static final String COL_POSE_CATEGORY = "category";
    public static final String COL_POSE_DIFFICULTY = "difficulty_level";
    public static final String COL_POSE_MUSCLES = "target_muscles";
    public static final String COL_POSE_BENEFITS = "benefits";
    public static final String COL_POSE_CONTRAINDICATIONS = "contraindications";
    public static final String COL_POSE_DURATION = "duration_seconds";
    public static final String COL_POSE_IMAGE = "image_asset_path";
    public static final String COL_POSE_YT_ID = "youtube_video_id";
    public static final String COL_POSE_YT_URL = "youtube_url";

    // Table: weekly_routine
    public static final String TABLE_ROUTINE = "weekly_routine";
    public static final String COL_ROUTINE_ID = "routine_id";
    public static final String COL_ROUTINE_USER_ID = "user_id";
    public static final String COL_ROUTINE_DAY = "day_of_week";
    public static final String COL_ROUTINE_POSE_ID = "pose_id";
    public static final String COL_ROUTINE_DURATION = "target_duration";
    public static final String COL_ROUTINE_COMPLETED = "is_completed";
    public static final String COL_ROUTINE_NOTES = "custom_notes";

    // Table: progress_tracker
    public static final String TABLE_PROGRESS = "progress_tracker";
    public static final String COL_PROGRESS_ID = "log_id";
    public static final String COL_PROGRESS_USER_ID = "user_id";
    public static final String COL_PROGRESS_IMAGE = "image_path";
    public static final String COL_PROGRESS_DATE = "date_logged";
    public static final String COL_PROGRESS_POSE_NAME = "pose_name";

    private static DatabaseHelper instance;
    private final Context context;

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context.getApplicationContext();
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Table: users
        String createUsers = "CREATE TABLE " + TABLE_USERS + " (" +
                COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USER_NAME + " TEXT UNIQUE, " +
                COL_USER_EMAIL + " TEXT UNIQUE, " +
                COL_USER_PASSWORD + " TEXT, " +
                COL_USER_PROFILE_IMAGE + " TEXT);";
        db.execSQL(createUsers);

        // Table: yoga_poses
        String createPoses = "CREATE TABLE " + TABLE_POSES + " (" +
                COL_POSE_ID + " TEXT PRIMARY KEY, " +
                COL_POSE_ENG_NAME + " TEXT, " +
                COL_POSE_SANS_NAME + " TEXT, " +
                COL_POSE_CATEGORY + " TEXT, " +
                COL_POSE_DIFFICULTY + " TEXT, " +
                COL_POSE_MUSCLES + " TEXT, " +
                COL_POSE_BENEFITS + " TEXT, " +
                COL_POSE_CONTRAINDICATIONS + " TEXT, " +
                COL_POSE_DURATION + " INTEGER, " +
                COL_POSE_IMAGE + " TEXT, " +
                COL_POSE_YT_ID + " TEXT, " +
                COL_POSE_YT_URL + " TEXT);";
        db.execSQL(createPoses);

        // Table: weekly_routine
        String createRoutine = "CREATE TABLE " + TABLE_ROUTINE + " (" +
                COL_ROUTINE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_ROUTINE_USER_ID + " INTEGER, " +
                COL_ROUTINE_DAY + " TEXT, " +
                COL_ROUTINE_POSE_ID + " TEXT, " +
                COL_ROUTINE_DURATION + " INTEGER, " +
                COL_ROUTINE_COMPLETED + " INTEGER DEFAULT 0, " +
                COL_ROUTINE_NOTES + " TEXT, " +
                "FOREIGN KEY (" + COL_ROUTINE_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COL_USER_ID + ") ON DELETE CASCADE, " +
                "FOREIGN KEY (" + COL_ROUTINE_POSE_ID + ") REFERENCES " + TABLE_POSES + "(" + COL_POSE_ID + ") ON DELETE CASCADE);";
        db.execSQL(createRoutine);

        // Table: progress_tracker
        String createProgress = "CREATE TABLE " + TABLE_PROGRESS + " (" +
                COL_PROGRESS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_PROGRESS_USER_ID + " INTEGER, " +
                COL_PROGRESS_IMAGE + " TEXT, " +
                COL_PROGRESS_DATE + " TEXT, " +
                COL_PROGRESS_POSE_NAME + " TEXT, " +
                "FOREIGN KEY (" + COL_PROGRESS_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COL_USER_ID + ") ON DELETE CASCADE);";
        db.execSQL(createProgress);

        // Seed 200 poses from CSV immediately
        seedYogaPosesFromAssets(db);

        // Seed default demo user
        seedDemoUser(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROGRESS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ROUTINE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_POSES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    public void seedYogaPosesIfEmpty() {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_POSES, null);
        int count = 0;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
            cursor.close();
        }
        if (count == 0) {
            seedYogaPosesFromAssets(db);
        }
    }

    private void seedYogaPosesFromAssets(SQLiteDatabase db) {
        try {
            InputStream is = context.getAssets().open("yoga_poses_200.csv");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String line = reader.readLine(); // skip header

            db.beginTransaction();
            int inserted = 0;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                List<String> tokens = parseCsvLine(line);
                if (tokens.size() >= 12) {
                    ContentValues cv = new ContentValues();
                    cv.put(COL_POSE_ID, tokens.get(0));
                    cv.put(COL_POSE_ENG_NAME, tokens.get(1));
                    cv.put(COL_POSE_SANS_NAME, tokens.get(2));
                    cv.put(COL_POSE_CATEGORY, tokens.get(3));
                    cv.put(COL_POSE_DIFFICULTY, tokens.get(4));
                    cv.put(COL_POSE_MUSCLES, tokens.get(5));
                    cv.put(COL_POSE_BENEFITS, tokens.get(6));
                    cv.put(COL_POSE_CONTRAINDICATIONS, tokens.get(7));
                    try {
                        cv.put(COL_POSE_DURATION, Integer.parseInt(tokens.get(8)));
                    } catch (NumberFormatException e) {
                        cv.put(COL_POSE_DURATION, 30);
                    }
                    cv.put(COL_POSE_IMAGE, tokens.get(9));
                    cv.put(COL_POSE_YT_ID, tokens.get(10));
                    cv.put(COL_POSE_YT_URL, tokens.get(11));

                    db.insertWithOnConflict(TABLE_POSES, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
                    inserted++;
                }
            }
            db.setTransactionSuccessful();
            db.endTransaction();
            reader.close();
            Log.d(TAG, "Successfully seeded " + inserted + " yoga poses.");
        } catch (Exception e) {
            Log.e(TAG, "Error seeding yoga poses CSV: " + e.getMessage(), e);
        }
    }

    private void seedDemoUser(SQLiteDatabase db) {
        try {
            ContentValues cv = new ContentValues();
            cv.put(COL_USER_NAME, "yogamaster");
            cv.put(COL_USER_EMAIL, "demo@peopledoingyoga.com");
            cv.put(COL_USER_PASSWORD, "yoga123");
            cv.put(COL_USER_PROFILE_IMAGE, "");
            long userId = db.insertWithOnConflict(TABLE_USERS, null, cv, SQLiteDatabase.CONFLICT_IGNORE);

            if (userId > 0) {
                // Add a few sample routine entries for Monday and Wednesday
                addSampleRoutine(db, (int) userId, "Monday", "pose_001", 30, 1, "Warm up flow");
                addSampleRoutine(db, (int) userId, "Monday", "pose_004", 45, 0, "Focus on deep breath");
                addSampleRoutine(db, (int) userId, "Wednesday", "pose_007", 30, 0, "Balance practice");
                addSampleRoutine(db, (int) userId, "Friday", "pose_011", 60, 0, "Restorative evening");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error seeding demo user: " + e.getMessage());
        }
    }

    private void addSampleRoutine(SQLiteDatabase db, int userId, String day, String poseId, int duration, int completed, String notes) {
        ContentValues cv = new ContentValues();
        cv.put(COL_ROUTINE_USER_ID, userId);
        cv.put(COL_ROUTINE_DAY, day);
        cv.put(COL_ROUTINE_POSE_ID, poseId);
        cv.put(COL_ROUTINE_DURATION, duration);
        cv.put(COL_ROUTINE_COMPLETED, completed);
        cv.put(COL_ROUTINE_NOTES, notes);
        db.insert(TABLE_ROUTINE, null, cv);
    }

    public static List<String> parseCsvLine(String line) {
        List<String> tokens = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '\"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                tokens.add(sb.toString().trim());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        tokens.add(sb.toString().trim());
        return tokens;
    }
}
