package com.dark.webprog26.lawyerquiz;

import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.dark.webprog26.lawyerquiz.engine.commands.ReadDataFromJsonCommand;
import com.dark.webprog26.lawyerquiz.engine.Quiz;
import com.dark.webprog26.lawyerquiz.engine.events.DataHasBeenTransformedToPOJOsEvent;
import com.dark.webprog26.lawyerquiz.engine.events.DataHasBeenUploadedToFirebaseDbEvent;
import com.dark.webprog26.lawyerquiz.engine.events.JsonDataFileHasBeenReadEvent;
import com.dark.webprog26.lawyerquiz.engine.events.ReadDataFromJsonEvent;
import com.dark.webprog26.lawyerquiz.engine.listeners.OnAnswerApprovedListener;
import com.dark.webprog26.lawyerquiz.engine.models.Answer;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class QuizActivity extends AppCompatActivity implements OnAnswerApprovedListener{

    private static final String TTTAG = "QuizActivity_TAG";

    private Quiz mQuiz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        mQuiz = new Quiz(PreferenceManager.getDefaultSharedPreferences(this), getSupportFragmentManager(), R.id.activity_quiz);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Registering EventBus to handle events
        EventBus.getDefault().register(this);
        mQuiz.prepare();
    }

    @Override
    protected void onStop() {
        //Unregister EventBus to avoid memory leaks
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
            mQuiz.pause();
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onReadDataFromJsonEvent(ReadDataFromJsonEvent readDataFromJsonEvent){
        Log.i(TTTAG, "onReadDataFromJsonEvent");
        new ReadDataFromJsonCommand(getAssets()).execute();
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onJsonDataFileHasBeenReadEvent(JsonDataFileHasBeenReadEvent jsonDataFileHasBeenReadEvent){
        Log.i(TTTAG, "onJsonDataFileHasBeenReadEvent");
       mQuiz.transformDataToPOJOs(jsonDataFileHasBeenReadEvent.getJsonData());
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onDataHasBeenTransformedToPOJOsEvent(DataHasBeenTransformedToPOJOsEvent dataHasBeenTransformedToPOJOsEvent){
        Log.i(TTTAG, "onDataHasBeenTransformedToPOJOsEvent");
        mQuiz.uploadDataToFirebaseDB(dataHasBeenTransformedToPOJOsEvent.getQuestionsList());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDataHasBeenUploadedToFirebaseDbEvent(DataHasBeenUploadedToFirebaseDbEvent dataHasBeenUploadedToFirebaseDbEvent){
        Log.i(TTTAG, "onDataHasBeenUploadedToFirebaseDbEvent");
        mQuiz.resume();
    }

    @Override
    public void onAnswerApproved(Answer answer) {
        mQuiz.setCurrentQuestionId(answer.getNextQuestionId());
        mQuiz.setAnsweredQuestionsCount(mQuiz.getAnsweredQuestionsCount() + 1);
        mQuiz.setScoredPointsCount(mQuiz.getScoredPointsCount() + answer.getPoints());

        String usefulTipText = answer.getReferenceText();

        if(!usefulTipText.equalsIgnoreCase("null")){
            mQuiz.showUsefulTip(usefulTipText);
        }
        mQuiz.resume();
    }
}
