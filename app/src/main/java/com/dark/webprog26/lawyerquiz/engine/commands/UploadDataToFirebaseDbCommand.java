package com.dark.webprog26.lawyerquiz.engine.commands;

import android.content.SharedPreferences;
import android.util.Log;

import com.dark.webprog26.lawyerquiz.engine.app.FirebaseApplication;
import com.dark.webprog26.lawyerquiz.engine.events.DataHasBeenUploadedToFirebaseDbEvent;
import com.dark.webprog26.lawyerquiz.engine.models.Question;
import com.dark.webprog26.lawyerquiz.interfaces.Command;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import static com.dark.webprog26.lawyerquiz.engine.app.FirebaseApplication.FIREBASE_DATABASE_ROOT;

/**
 * Created by webpr on 04.04.2017.
 */

public class UploadDataToFirebaseDbCommand implements Command {

    private static final String UPLOAD_TAG = "upload_tag";

    //Firebase database root name

    //JSON file name in assets dir
    private static final String JSON_FILE_NAME = "data_demo.json";

    private SharedPreferences mSharedPreferences;



    //Firebase references
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    private final List<Question> mQuestionsList;

    public UploadDataToFirebaseDbCommand(List<Question> questionsList) {
        this.mQuestionsList = questionsList;
        mDatabase = FirebaseApplication.getFirebaseDatabase();
        //DatabaseReference instance to perform requests to database
        mReference = mDatabase.getReference(FIREBASE_DATABASE_ROOT);
    }

    @Override
    public void execute() {
        uploadDataToFirebaseDb();
    }

    private void uploadDataToFirebaseDb(){
        for(Question question: mQuestionsList){
            Log.i(UPLOAD_TAG, question.toString());
            //Upload values to database
            mReference.child(String.valueOf(question.getId())).setValue(question);
        }
        EventBus.getDefault().post(new DataHasBeenUploadedToFirebaseDbEvent());
    }
}
