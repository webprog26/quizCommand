package com.dark.webprog26.lawyerquiz.engine.app;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by webpr on 04.04.2017.
 */

public class FirebaseApplication extends Application {

    private static FirebaseDatabase mFirebaseDatabase;
    public static final String FIREBASE_DATABASE_ROOT = "questions";

    @Override
    public void onCreate() {
        super.onCreate();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        //Enable FirebaseDatabase to work offline
        mFirebaseDatabase.setPersistenceEnabled(true);
    }

    /**
     * Returns initialized {@link FirebaseDatabase} instance to read questions from it.
     * @return FirebaseDatabase
     */
    public static FirebaseDatabase getFirebaseDatabase() {
        return mFirebaseDatabase;
    }
}
