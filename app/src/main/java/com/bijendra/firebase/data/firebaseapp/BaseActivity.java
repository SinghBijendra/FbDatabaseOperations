package com.bijendra.firebase.data.firebaseapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by Newdream on 21-Nov-16.
 */

public class BaseActivity extends AppCompatActivity {
    public static String USER_ID="";
    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        if (!MyApplication.isUserLoggedIn())
        {
            startActivity(new Intent(BaseActivity.this,MainActivity.class));
            this.finish();
        }

    }
    public  void logout()
    {
        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();
        this.finish();
    }
}
