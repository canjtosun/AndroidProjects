package com.example.finalproject;

import java.io.Serializable;

//User class with constructor and with getters / setters

public class User implements Serializable {

    private String name, email, profilePic;

    public User(String name, String email, String profilePic) {
        this.name = name;
        this.email = email;
        this.profilePic = profilePic;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }
}
