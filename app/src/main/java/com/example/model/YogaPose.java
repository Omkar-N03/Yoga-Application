package com.example.model;

public class YogaPose {
    private String id;
    private String englishName;
    private String sanskritName;
    private String category;
    private String difficultyLevel;
    private String targetMuscles;
    private String benefits;
    private String contraindications;
    private int durationSeconds;
    private String imageAssetPath;
    private String youtubeVideoId;
    private String youtubeUrl;

    public YogaPose() {
    }

    public YogaPose(String id, String englishName, String sanskritName, String category,
                    String difficultyLevel, String targetMuscles, String benefits,
                    String contraindications, int durationSeconds, String imageAssetPath,
                    String youtubeVideoId, String youtubeUrl) {
        this.id = id;
        this.englishName = englishName;
        this.sanskritName = sanskritName;
        this.category = category;
        this.difficultyLevel = difficultyLevel;
        this.targetMuscles = targetMuscles;
        this.benefits = benefits;
        this.contraindications = contraindications;
        this.durationSeconds = durationSeconds;
        this.imageAssetPath = imageAssetPath;
        this.youtubeVideoId = youtubeVideoId;
        this.youtubeUrl = youtubeUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEnglishName() {
        return englishName;
    }

    public void setEnglishName(String englishName) {
        this.englishName = englishName;
    }

    public String getSanskritName() {
        return sanskritName;
    }

    public void setSanskritName(String sanskritName) {
        this.sanskritName = sanskritName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDifficultyLevel() {
        return difficultyLevel;
    }

    public void setDifficultyLevel(String difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }

    public String getTargetMuscles() {
        return targetMuscles;
    }

    public void setTargetMuscles(String targetMuscles) {
        this.targetMuscles = targetMuscles;
    }

    public String getBenefits() {
        return benefits;
    }

    public void setBenefits(String benefits) {
        this.benefits = benefits;
    }

    public String getContraindications() {
        return contraindications;
    }

    public void setContraindications(String contraindications) {
        this.contraindications = contraindications;
    }

    public int getDurationSeconds() {
        return durationSeconds;
    }

    public void setDurationSeconds(int durationSeconds) {
        this.durationSeconds = durationSeconds;
    }

    public String getImageAssetPath() {
        return imageAssetPath;
    }

    public void setImageAssetPath(String imageAssetPath) {
        this.imageAssetPath = imageAssetPath;
    }

    public String getYoutubeVideoId() {
        return youtubeVideoId;
    }

    public void setYoutubeVideoId(String youtubeVideoId) {
        this.youtubeVideoId = youtubeVideoId;
    }

    public String getYoutubeUrl() {
        return youtubeUrl;
    }

    public void setYoutubeUrl(String youtubeUrl) {
        this.youtubeUrl = youtubeUrl;
    }
}
