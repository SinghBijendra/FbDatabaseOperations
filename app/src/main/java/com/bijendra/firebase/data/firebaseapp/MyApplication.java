package com.bijendra.firebase.data.firebaseapp;

import android.app.Application;

import com.facebook.FacebookSdk;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Created by Newdream on 20-Nov-16.
 */

public class MyApplication extends Application {

    private static DatabaseReference mRefData=null;
    private static StorageReference mRefStorage=null;

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        Fresco.initialize(this);
        FacebookSdk.sdkInitialize(this);
    }

    public static DatabaseReference getDataBaseRef()
    {
        if(mRefData==null) {
            mRefData = FirebaseDatabase.getInstance().getReference();
            mRefData.keepSynced(true);
        }

        return  mRefData;
    }
    public static StorageReference getStorageRef()
    {
        if(mRefStorage==null)
            mRefStorage= FirebaseStorage.getInstance().getReference();

        return  mRefStorage;
    }

    public static boolean isUserLoggedIn()
    {
       return ( FirebaseAuth.getInstance().getCurrentUser()!=null);
    }
}
