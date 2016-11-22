package com.bijendra.firebase.data.firebaseapp;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Newdream on 20-Nov-16.
 */
@IgnoreExtraProperties
public class Friend {
    public  String userId;
    public  String age;
    public String username;
    public String address;


    public Friend() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Friend(String userId,String username, String age, String address) {
        this.userId = userId;
        this.address = address;
        this.username = username;
        this.age=age;
    }
}
