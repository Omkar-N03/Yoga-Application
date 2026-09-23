package com.example.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.db.DatabaseHelper;
import com.example.model.ProgressTracker;

import java.util.ArrayList;
import java.util.List;

public class ProgressTrackerDao {
    private final DatabaseHelper dbHelper;

    public ProgressTrackerDao(Context context) {
        this.dbHelper = DatabaseHelper.getInstance(context);
    }

    public long addProgressLog(ProgressTracker log) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_PROGRESS_USER_ID, log.getUserId());
        values.put(DatabaseHelper.COL_PROGRESS_IMAGE, log.getImagePath());
        values.put(DatabaseHelper.COL_PROGRESS_DATE, log.getDateLogged());
        values.put(DatabaseHelper.COL_PROGRESS_POSE_NAME, log.getPoseName());
        return db.insert(DatabaseHelper.TABLE_PROGRESS, null, values);
    }

    public List<ProgressTracker> getLogsForUser(int userId) {
        List<ProgressTracker> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_PROGRESS, null,
                DatabaseHelper.COL_PROGRESS_USER_ID + " = ?",
                new String[]{String.valueOf(userId)}, null, null,
                DatabaseHelper.COL_PROGRESS_ID + " DESC");

        if (cursor != null) {
            while (cursor.moveToNext()) {
                int logId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PROGRESS_ID));
                int uId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PROGRESS_USER_ID));
                String imgPath = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PROGRESS_IMAGE));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PROGRESS_DATE));
                String poseName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PROGRESS_POSE_NAME));
                list.add(new ProgressTracker(logId, uId, imgPath, date, poseName));
            }
            cursor.close();
        }
        return list;
    }

    public boolean deleteProgressLog(int logId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rows = db.delete(DatabaseHelper.TABLE_PROGRESS,
                DatabaseHelper.COL_PROGRESS_ID + " = ?", new String[]{String.valueOf(logId)});
        return rows > 0;
    }
}
