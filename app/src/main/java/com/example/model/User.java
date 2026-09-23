package com.example.model;

public class User {
    private int id;
    private String username;
    private String email;
    private String password;
    private String profileImagePath;

    public User() {
    }

    public User(int id, String username, String email, String password, String profileImagePath) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.profileImagePath = profileImagePath;
    }

    public User(String username, String email, String password, String profileImagePath) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.profileImagePath = profileImagePath;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProfileImagePath() {
        return profileImagePath;
    }

    public void setProfileImagePath(String profileImagePath) {
        this.profileImagePath = profileImagePath;
    }
}
