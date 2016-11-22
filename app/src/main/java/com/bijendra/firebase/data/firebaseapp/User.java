package com.bijendra.firebase.data.firebaseapp;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Newdream on 20-Nov-16.
 */
@IgnoreExtraProperties
public class User {
    public  String userId;
    public  String imageUrl;
    public  String age;
    public String username;
    public String email;
    public String profession;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String userId,String email,String username, String age,String profession,String imageUrl) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.age=age;
        this.profession=profession;
        this.imageUrl=imageUrl;
    }
}
