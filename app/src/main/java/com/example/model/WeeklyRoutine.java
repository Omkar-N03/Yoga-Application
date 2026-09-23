package com.example.model;

public class WeeklyRoutine {
    private int routineId;
    private int userId;
    private String dayOfWeek;
    private String poseId;
    private int targetDuration;
    private int isCompleted; // 0 for Pending, 1 for Completed
    private String customNotes;

    // Joined helper field
    private YogaPose yogaPose;

    public WeeklyRoutine() {
    }

    public WeeklyRoutine(int routineId, int userId, String dayOfWeek, String poseId,
                         int targetDuration, int isCompleted, String customNotes) {
        this.routineId = routineId;
        this.userId = userId;
        this.dayOfWeek = dayOfWeek;
        this.poseId = poseId;
        this.targetDuration = targetDuration;
        this.isCompleted = isCompleted;
        this.customNotes = customNotes;
    }

    public WeeklyRoutine(int userId, String dayOfWeek, String poseId,
                         int targetDuration, int isCompleted, String customNotes) {
        this.userId = userId;
        this.dayOfWeek = dayOfWeek;
        this.poseId = poseId;
        this.targetDuration = targetDuration;
        this.isCompleted = isCompleted;
        this.customNotes = customNotes;
    }

    public int getRoutineId() {
        return routineId;
    }

    public void setRoutineId(int routineId) {
        this.routineId = routineId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public String getPoseId() {
        return poseId;
    }

    public void setPoseId(String poseId) {
        this.poseId = poseId;
    }

    public int getTargetDuration() {
        return targetDuration;
    }

    public void setTargetDuration(int targetDuration) {
        this.targetDuration = targetDuration;
    }

    public int getIsCompleted() {
        return isCompleted;
    }

    public void setIsCompleted(int isCompleted) {
        this.isCompleted = isCompleted;
    }

    public boolean isCompletedBoolean() {
        return isCompleted == 1;
    }

    public String getCustomNotes() {
        return customNotes;
    }

    public void setCustomNotes(String customNotes) {
        this.customNotes = customNotes;
    }

    public YogaPose getYogaPose() {
        return yogaPose;
    }

    public void setYogaPose(YogaPose yogaPose) {
        this.yogaPose = yogaPose;
    }
}
