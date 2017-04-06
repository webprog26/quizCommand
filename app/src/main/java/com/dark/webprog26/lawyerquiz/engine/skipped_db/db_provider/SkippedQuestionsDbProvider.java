package com.dark.webprog26.lawyerquiz.engine.skipped_db.db_provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.dark.webprog26.lawyerquiz.engine.skipped_db.db.SkippedQuestionsDbHelper;

import java.lang.ref.WeakReference;

/**
 * Created by webpr on 05.04.2017.
 */

public class SkippedQuestionsDbProvider {

    /**
     * Manages {@link android.database.sqlite.SQLiteDatabase} CRUD operations
     */

    private static final String TAG = "DbProvider";

    private final WeakReference<Context> mContextWeakReference;
    private final SkippedQuestionsDbHelper mDbHelper;

    public SkippedQuestionsDbProvider(Context context) {
        this.mContextWeakReference = new WeakReference<Context>(context);
        this.mDbHelper = new SkippedQuestionsDbHelper(mContextWeakReference.get());
    }

    /**
     * Inserts skipped question ID in {@link android.database.sqlite.SQLiteDatabase}.
     * Returns true if operation was successful and otherwise false
     * @param skippedQuestionId long
     * @return boolean
     */
    public boolean insertSkippedQuestionIdToDb(long skippedQuestionId){
        ContentValues contentValues = new ContentValues();
        contentValues.put(SkippedQuestionsDbHelper.SKIPPED_QUESTION_ID, skippedQuestionId);

        return mDbHelper.getWritableDatabase()
                .insert(SkippedQuestionsDbHelper.SKIPPED_QUESTIONS_TABLE,
                        null,
                        contentValues) != 0;
    }

    /**
     * Returns number of skipped questions IDs stored in {@link android.database.sqlite.SQLiteDatabase}
     * @return int
     */
    public int getSkippedQuestionsCount(){
        int skippedQuestionsCount = 0;
        Cursor cursor = mDbHelper.getReadableDatabase().query(SkippedQuestionsDbHelper.SKIPPED_QUESTIONS_TABLE,
                null,
                null,
                null,
                null,
                null,
                SkippedQuestionsDbHelper.ID);

        try{
            skippedQuestionsCount = cursor.getCount();
        } finally {
            cursor.close();
        }
        return skippedQuestionsCount;
    }

    /**
     * Returns array of skipped questions IDs stored in {@link android.database.sqlite.SQLiteDatabase}
     * null if there is no data
     * @return long[]
     */
    public long[] getSkippedQuestionsIDs(){
        int skippedQuestionsCount = getSkippedQuestionsCount();
        Log.i(TAG, "count is " + skippedQuestionsCount);
        if(skippedQuestionsCount == 0){
            return null;
        }
        long[] skippedQuestionsIDs = new long[skippedQuestionsCount];

        Cursor cursor = mDbHelper.getReadableDatabase().query(SkippedQuestionsDbHelper.SKIPPED_QUESTIONS_TABLE,
                null,
                null,
                null,
                null,
                null,
                SkippedQuestionsDbHelper.ID);

        int index = 0;

        try {
            while (cursor.moveToNext()){
                skippedQuestionsIDs[index] = cursor.getLong(cursor.getColumnIndex(SkippedQuestionsDbHelper.SKIPPED_QUESTION_ID));
                index++;
            }
        } finally {
            cursor.close();
        }

        return skippedQuestionsIDs;
    }

    /**
     * Deletes single skipped question ID from {@link android.database.sqlite.SQLiteDatabase}
     * Returns true if operation was successful and otherwise false
     * @param skippedQuestionId long
     * @return boolean
     */
    public boolean deleteSkippedQuestionIdFromDb(long skippedQuestionId){
        String whereClause = SkippedQuestionsDbHelper.SKIPPED_QUESTION_ID + " = ?";
        String[] whereArgs = new String[]{String.valueOf(skippedQuestionId)};

        return mDbHelper.getWritableDatabase().delete(SkippedQuestionsDbHelper.SKIPPED_QUESTIONS_TABLE, whereClause, whereArgs) != 0;
    }

    /**
     * Deletes all the rows in skipped questions table
     * Returns true if operation was successful and otherwise false
     * @return boolean
     */
    public boolean deleteAll(){
        return mDbHelper.getWritableDatabase().delete(SkippedQuestionsDbHelper.SKIPPED_QUESTIONS_TABLE, null, null) != 0;
    }
}

