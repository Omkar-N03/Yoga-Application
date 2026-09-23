package com.example.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.db.DatabaseHelper;
import com.example.model.YogaPose;

import java.util.ArrayList;
import java.util.List;

public class YogaPoseDao {
    private final DatabaseHelper dbHelper;

    public YogaPoseDao(Context context) {
        this.dbHelper = DatabaseHelper.getInstance(context);
    }

    public List<YogaPose> getAllPoses() {
        List<YogaPose> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_POSES, null, null, null, null, null,
                DatabaseHelper.COL_POSE_ENG_NAME + " ASC");
        if (cursor != null) {
            while (cursor.moveToNext()) {
                list.add(cursorToPose(cursor));
            }
            cursor.close();
        }
        return list;
    }

    public YogaPose getPoseById(String id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_POSES, null,
                DatabaseHelper.COL_POSE_ID + " = ?", new String[]{id}, null, null, null);
        YogaPose pose = null;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                pose = cursorToPose(cursor);
            }
            cursor.close();
        }
        return pose;
    }

    public List<YogaPose> searchAndFilter(String query, String category, String difficulty) {
        List<YogaPose> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        StringBuilder selection = new StringBuilder("1=1");
        List<String> args = new ArrayList<>();

        if (query != null && !query.trim().isEmpty()) {
            selection.append(" AND (")
                    .append(DatabaseHelper.COL_POSE_ENG_NAME).append(" LIKE ? OR ")
                    .append(DatabaseHelper.COL_POSE_SANS_NAME).append(" LIKE ? OR ")
                    .append(DatabaseHelper.COL_POSE_MUSCLES).append(" LIKE ?)");
            String pattern = "%" + query.trim() + "%";
            args.add(pattern);
            args.add(pattern);
            args.add(pattern);
        }

        if (category != null && !category.equalsIgnoreCase("All")) {
            selection.append(" AND ").append(DatabaseHelper.COL_POSE_CATEGORY).append(" = ?");
            args.add(category);
        }

        if (difficulty != null && !difficulty.equalsIgnoreCase("All")) {
            selection.append(" AND ").append(DatabaseHelper.COL_POSE_DIFFICULTY).append(" = ?");
            args.add(difficulty);
        }

        String[] selectionArgs = args.isEmpty() ? null : args.toArray(new String[0]);
        Cursor cursor = db.query(DatabaseHelper.TABLE_POSES, null, selection.toString(),
                selectionArgs, null, null, DatabaseHelper.COL_POSE_ENG_NAME + " ASC");

        if (cursor != null) {
            while (cursor.moveToNext()) {
                list.add(cursorToPose(cursor));
            }
            cursor.close();
        }
        return list;
    }

    public List<String> getAllCategories() {
        List<String> categories = new ArrayList<>();
        categories.add("All");
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT DISTINCT " + DatabaseHelper.COL_POSE_CATEGORY +
                " FROM " + DatabaseHelper.TABLE_POSES + " ORDER BY " + DatabaseHelper.COL_POSE_CATEGORY + " ASC", null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                categories.add(cursor.getString(0));
            }
            cursor.close();
        }
        return categories;
    }

    public int getTotalPoseCount() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_POSES, null);
        int count = 0;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
            cursor.close();
        }
        return count;
    }

    private YogaPose cursorToPose(Cursor cursor) {
        String id = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_POSE_ID));
        String eng = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_POSE_ENG_NAME));
        String sans = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_POSE_SANS_NAME));
        String cat = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_POSE_CATEGORY));
        String diff = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_POSE_DIFFICULTY));
        String muscles = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_POSE_MUSCLES));
        String benefits = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_POSE_BENEFITS));
        String contra = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_POSE_CONTRAINDICATIONS));
        int duration = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_POSE_DURATION));
        String img = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_POSE_IMAGE));
        String ytId = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_POSE_YT_ID));
        String ytUrl = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_POSE_YT_URL));

        return new YogaPose(id, eng, sans, cat, diff, muscles, benefits, contra, duration, img, ytId, ytUrl);
    }
}
