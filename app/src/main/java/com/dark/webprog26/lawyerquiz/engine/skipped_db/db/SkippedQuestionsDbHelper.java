package com.dark.webprog26.lawyerquiz.engine.skipped_db.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Creates Skipped Questions {@link SQLiteDatabase}
 */

public class SkippedQuestionsDbHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "skipped_questions_db";
    private static final int DB_VERSION = 1;


    public SkippedQuestionsDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public static final String SKIPPED_QUESTIONS_TABLE = "skipped_questions_table";
    public static final String ID = "_id";
    public static final String SKIPPED_QUESTION_ID = "skipped_question_id";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + SKIPPED_QUESTIONS_TABLE + "("
                + ID + " integer primary key autoincrement, "
                + SKIPPED_QUESTION_ID + " integer)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //
    }
}
