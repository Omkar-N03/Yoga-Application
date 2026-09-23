package com.example.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.db.DatabaseHelper;
import com.example.model.User;

public class UserDao {
    private final DatabaseHelper dbHelper;

    public UserDao(Context context) {
        this.dbHelper = DatabaseHelper.getInstance(context);
    }

    public long registerUser(User user) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_USER_NAME, user.getUsername());
        values.put(DatabaseHelper.COL_USER_EMAIL, user.getEmail());
        values.put(DatabaseHelper.COL_USER_PASSWORD, user.getPassword());
        values.put(DatabaseHelper.COL_USER_PROFILE_IMAGE, user.getProfileImagePath() != null ? user.getProfileImagePath() : "");
        return db.insert(DatabaseHelper.TABLE_USERS, null, values);
    }

    public User login(String identifier, String password) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selection = "(" + DatabaseHelper.COL_USER_NAME + " = ? OR " + DatabaseHelper.COL_USER_EMAIL + " = ?) AND " +
                DatabaseHelper.COL_USER_PASSWORD + " = ?";
        String[] selectionArgs = new String[]{identifier, identifier, password};

        Cursor cursor = db.query(DatabaseHelper.TABLE_USERS, null, selection, selectionArgs, null, null, null);
        User user = null;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                user = cursorToUser(cursor);
            }
            cursor.close();
        }
        return user;
    }

    public User getUserById(int userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selection = DatabaseHelper.COL_USER_ID + " = ?";
        String[] selectionArgs = new String[]{String.valueOf(userId)};

        Cursor cursor = db.query(DatabaseHelper.TABLE_USERS, null, selection, selectionArgs, null, null, null);
        User user = null;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                user = cursorToUser(cursor);
            }
            cursor.close();
        }
        return user;
    }

    public boolean isUsernameTaken(String username) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_USERS, new String[]{DatabaseHelper.COL_USER_ID},
                DatabaseHelper.COL_USER_NAME + " = ?", new String[]{username}, null, null, null);
        boolean exists = (cursor != null && cursor.getCount() > 0);
        if (cursor != null) cursor.close();
        return exists;
    }

    public boolean isEmailTaken(String email) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_USERS, new String[]{DatabaseHelper.COL_USER_ID},
                DatabaseHelper.COL_USER_EMAIL + " = ?", new String[]{email}, null, null, null);
        boolean exists = (cursor != null && cursor.getCount() > 0);
        if (cursor != null) cursor.close();
        return exists;
    }

    public boolean updateProfileImage(int userId, String imagePath) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.COL_USER_PROFILE_IMAGE, imagePath);
        int rows = db.update(DatabaseHelper.TABLE_USERS, cv, DatabaseHelper.COL_USER_ID + " = ?",
                new String[]{String.valueOf(userId)});
        return rows > 0;
    }

    private User cursorToUser(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_USER_ID));
        String username = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_USER_NAME));
        String email = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_USER_EMAIL));
        String password = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_USER_PASSWORD));
        String profileImage = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_USER_PROFILE_IMAGE));
        return new User(id, username, email, password, profileImage);
    }
}
