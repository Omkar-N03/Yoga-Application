package com.example.model;

public class ProgressTracker {
    private int logId;
    private int userId;
    private String imagePath;
    private String dateLogged;
    private String poseName;

    public ProgressTracker() {
    }

    public ProgressTracker(int logId, int userId, String imagePath, String dateLogged, String poseName) {
        this.logId = logId;
        this.userId = userId;
        this.imagePath = imagePath;
        this.dateLogged = dateLogged;
        this.poseName = poseName;
    }

    public ProgressTracker(int userId, String imagePath, String dateLogged, String poseName) {
        this.userId = userId;
        this.imagePath = imagePath;
        this.dateLogged = dateLogged;
        this.poseName = poseName;
    }

    public int getLogId() {
        return logId;
    }

    public void setLogId(int logId) {
        this.logId = logId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getDateLogged() {
        return dateLogged;
    }

    public void setDateLogged(String dateLogged) {
        this.dateLogged = dateLogged;
    }

    public String getPoseName() {
        return poseName;
    }

    public void setPoseName(String poseName) {
        this.poseName = poseName;
    }
}
