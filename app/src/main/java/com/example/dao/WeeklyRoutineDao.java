package com.example.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.db.DatabaseHelper;
import com.example.model.WeeklyRoutine;
import com.example.model.YogaPose;

import java.util.ArrayList;
import java.util.List;

public class WeeklyRoutineDao {
    private final DatabaseHelper dbHelper;

    public WeeklyRoutineDao(Context context) {
        this.dbHelper = DatabaseHelper.getInstance(context);
    }

    public long addRoutine(WeeklyRoutine routine) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_ROUTINE_USER_ID, routine.getUserId());
        values.put(DatabaseHelper.COL_ROUTINE_DAY, routine.getDayOfWeek());
        values.put(DatabaseHelper.COL_ROUTINE_POSE_ID, routine.getPoseId());
        values.put(DatabaseHelper.COL_ROUTINE_DURATION, routine.getTargetDuration());
        values.put(DatabaseHelper.COL_ROUTINE_COMPLETED, routine.getIsCompleted());
        values.put(DatabaseHelper.COL_ROUTINE_NOTES, routine.getCustomNotes() != null ? routine.getCustomNotes() : "");
        return db.insert(DatabaseHelper.TABLE_ROUTINE, null, values);
    }

    public List<WeeklyRoutine> getRoutinesForDay(int userId, String dayOfWeek) {
        List<WeeklyRoutine> routines = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT r.*, p." + DatabaseHelper.COL_POSE_ENG_NAME + ", p." + DatabaseHelper.COL_POSE_SANS_NAME +
                ", p." + DatabaseHelper.COL_POSE_CATEGORY + ", p." + DatabaseHelper.COL_POSE_DIFFICULTY +
                ", p." + DatabaseHelper.COL_POSE_MUSCLES + ", p." + DatabaseHelper.COL_POSE_BENEFITS +
                ", p." + DatabaseHelper.COL_POSE_CONTRAINDICATIONS + ", p." + DatabaseHelper.COL_POSE_IMAGE +
                ", p." + DatabaseHelper.COL_POSE_YT_ID + ", p." + DatabaseHelper.COL_POSE_YT_URL +
                " FROM " + DatabaseHelper.TABLE_ROUTINE + " r " +
                " LEFT JOIN " + DatabaseHelper.TABLE_POSES + " p ON r." + DatabaseHelper.COL_ROUTINE_POSE_ID + " = p." + DatabaseHelper.COL_POSE_ID +
                " WHERE r." + DatabaseHelper.COL_ROUTINE_USER_ID + " = ? AND r." + DatabaseHelper.COL_ROUTINE_DAY + " = ? " +
                " ORDER BY r." + DatabaseHelper.COL_ROUTINE_ID + " ASC";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId), dayOfWeek});
        if (cursor != null) {
            while (cursor.moveToNext()) {
                WeeklyRoutine r = new WeeklyRoutine();
                r.setRoutineId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ROUTINE_ID)));
                r.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ROUTINE_USER_ID)));
                r.setDayOfWeek(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ROUTINE_DAY)));
                r.setPoseId(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ROUTINE_POSE_ID)));
                r.setTargetDuration(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ROUTINE_DURATION)));
                r.setIsCompleted(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ROUTINE_COMPLETED)));
                r.setCustomNotes(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ROUTINE_NOTES)));

                // Joined pose details
                if (!cursor.isNull(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_POSE_ENG_NAME))) {
                    YogaPose pose = new YogaPose();
                    pose.setId(r.getPoseId());
                    pose.setEnglishName(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_POSE_ENG_NAME)));
                    pose.setSanskritName(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_POSE_SANS_NAME)));
                    pose.setCategory(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_POSE_CATEGORY)));
                    pose.setDifficultyLevel(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_POSE_DIFFICULTY)));
                    pose.setTargetMuscles(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_POSE_MUSCLES)));
                    pose.setBenefits(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_POSE_BENEFITS)));
                    pose.setContraindications(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_POSE_CONTRAINDICATIONS)));
                    pose.setDurationSeconds(r.getTargetDuration());
                    pose.setImageAssetPath(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_POSE_IMAGE)));
                    pose.setYoutubeVideoId(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_POSE_YT_ID)));
                    pose.setYoutubeUrl(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_POSE_YT_URL)));
                    r.setYogaPose(pose);
                }

                routines.add(r);
            }
            cursor.close();
        }
        return routines;
    }

    public boolean updateRoutineCompletion(int routineId, int isCompleted) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.COL_ROUTINE_COMPLETED, isCompleted);
        int rows = db.update(DatabaseHelper.TABLE_ROUTINE, cv,
                DatabaseHelper.COL_ROUTINE_ID + " = ?", new String[]{String.valueOf(routineId)});
        return rows > 0;
    }

    public boolean updateRoutine(int routineId, int targetDuration, String customNotes) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.COL_ROUTINE_DURATION, targetDuration);
        cv.put(DatabaseHelper.COL_ROUTINE_NOTES, customNotes);
        int rows = db.update(DatabaseHelper.TABLE_ROUTINE, cv,
                DatabaseHelper.COL_ROUTINE_ID + " = ?", new String[]{String.valueOf(routineId)});
        return rows > 0;
    }

    public boolean deleteRoutine(int routineId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rows = db.delete(DatabaseHelper.TABLE_ROUTINE,
                DatabaseHelper.COL_ROUTINE_ID + " = ?", new String[]{String.valueOf(routineId)});
        return rows > 0;
    }

    public int getCompletedCount(int userId, String dayOfWeek) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_ROUTINE +
                " WHERE " + DatabaseHelper.COL_ROUTINE_USER_ID + " = ? AND " +
                DatabaseHelper.COL_ROUTINE_DAY + " = ? AND " +
                DatabaseHelper.COL_ROUTINE_COMPLETED + " = 1", new String[]{String.valueOf(userId), dayOfWeek});
        int count = 0;
        if (cursor != null) {
            if (cursor.moveToFirst()) count = cursor.getInt(0);
            cursor.close();
        }
        return count;
    }

    public int getTotalCount(int userId, String dayOfWeek) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_ROUTINE +
                " WHERE " + DatabaseHelper.COL_ROUTINE_USER_ID + " = ? AND " +
                DatabaseHelper.COL_ROUTINE_DAY + " = ?", new String[]{String.valueOf(userId), dayOfWeek});
        int count = 0;
        if (cursor != null) {
            if (cursor.moveToFirst()) count = cursor.getInt(0);
            cursor.close();
        }
        return count;
    }

    public int getTotalCompletedAllTime(int userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_ROUTINE +
                " WHERE " + DatabaseHelper.COL_ROUTINE_USER_ID + " = ? AND " +
                DatabaseHelper.COL_ROUTINE_COMPLETED + " = 1", new String[]{String.valueOf(userId)});
        int count = 0;
        if (cursor != null) {
            if (cursor.moveToFirst()) count = cursor.getInt(0);
            cursor.close();
        }
        return count;
    }
}
