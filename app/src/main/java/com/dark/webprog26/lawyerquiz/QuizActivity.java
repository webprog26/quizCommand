package com.dark.webprog26.lawyerquiz;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.dark.webprog26.lawyerquiz.engine.commands.ReadDataFromJsonCommand;
import com.dark.webprog26.lawyerquiz.engine.Quiz;
import com.dark.webprog26.lawyerquiz.engine.events.CountSkippedQuestionsEvent;
import com.dark.webprog26.lawyerquiz.engine.events.DataHasBeenTransformedToPOJOsEvent;
import com.dark.webprog26.lawyerquiz.engine.events.DataHasBeenUploadedToFirebaseDbEvent;
import com.dark.webprog26.lawyerquiz.engine.events.DeleteSkippedQuestionIdFromDbEvent;
import com.dark.webprog26.lawyerquiz.engine.events.GameOverEvent;
import com.dark.webprog26.lawyerquiz.engine.events.GetSkippedQuestionsIDsFromDbEvent;
import com.dark.webprog26.lawyerquiz.engine.events.JsonDataFileHasBeenReadEvent;
import com.dark.webprog26.lawyerquiz.engine.events.QuestionWasSkippedEvent;
import com.dark.webprog26.lawyerquiz.engine.events.ReadDataFromJsonEvent;
import com.dark.webprog26.lawyerquiz.engine.events.ShowBuyFullVersionOfferEvent;
import com.dark.webprog26.lawyerquiz.engine.events.UsefulTipDialogDismissedEvent;
import com.dark.webprog26.lawyerquiz.engine.listeners.OnAnswerApprovedListener;
import com.dark.webprog26.lawyerquiz.engine.models.Answer;
import com.dark.webprog26.lawyerquiz.engine.models.Question;
import com.dark.webprog26.lawyerquiz.engine.skipped_db.db_provider.SkippedQuestionsDbProvider;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class QuizActivity extends AppCompatActivity implements OnAnswerApprovedListener{

    private static final String QUIZ_ACTIVITY_TAG = "QuizActivity_TAG";

    private Quiz mQuiz;
    private SkippedQuestionsDbProvider mSkippedQuestionsDbProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        mSkippedQuestionsDbProvider = new SkippedQuestionsDbProvider(this);
        mQuiz = new Quiz(PreferenceManager.getDefaultSharedPreferences(this),
                            getSupportFragmentManager(),
                            R.id.activity_quiz);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Registering EventBus to handle events
        EventBus.getDefault().register(this);
        mQuiz.prepare();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if(sharedPreferences.getBoolean(BuyFullVersionActivity.GAME_IS_OVER, false)){
            sharedPreferences.edit().putBoolean(BuyFullVersionActivity.GAME_IS_OVER, false).apply();
            mQuiz.reset();
            mQuiz.resume();
        }
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
//        Log.i(QUIZ_ACTIVITY_TAG, "onReadDataFromJsonEvent");
        new ReadDataFromJsonCommand(getAssets()).execute();
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onJsonDataFileHasBeenReadEvent(JsonDataFileHasBeenReadEvent jsonDataFileHasBeenReadEvent){
//        Log.i(QUIZ_ACTIVITY_TAG, "onJsonDataFileHasBeenReadEvent");
       mQuiz.transformDataToPOJOs(jsonDataFileHasBeenReadEvent.getJsonData());
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onDataHasBeenTransformedToPOJOsEvent(DataHasBeenTransformedToPOJOsEvent dataHasBeenTransformedToPOJOsEvent){
//        Log.i(QUIZ_ACTIVITY_TAG, "onDataHasBeenTransformedToPOJOsEvent");
        mQuiz.uploadDataToFirebaseDB(dataHasBeenTransformedToPOJOsEvent.getQuestionsList());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDataHasBeenUploadedToFirebaseDbEvent(DataHasBeenUploadedToFirebaseDbEvent dataHasBeenUploadedToFirebaseDbEvent){
//        Log.i(QUIZ_ACTIVITY_TAG, "onDataHasBeenUploadedToFirebaseDbEvent");
        mQuiz.resume();
    }

    @Override
    public void onAnswerApproved(Answer answer) {
        mQuiz.setAnsweredQuestionsCount(mQuiz.getAnsweredQuestionsCount() + 1);
        mQuiz.setScoredPointsCount(mQuiz.getScoredPointsCount() + answer.getPoints());

        String usefulTipText = answer.getReferenceText();

        if(!usefulTipText.equalsIgnoreCase("null")){
            mQuiz.showUsefulTip(usefulTipText);
        }

        if(mQuiz.getQuizMode() == Quiz.ARCADE_MODE){
            mQuiz.setCurrentQuestionId(answer.getNextQuestionId());
        } else {
            EventBus.getDefault().post(new DeleteSkippedQuestionIdFromDbEvent(mQuiz.getCurrentQuestionId()));
        }

        Log.i(QUIZ_ACTIVITY_TAG, "in quiz next id is " + mQuiz.getCurrentQuestionId());

        mQuiz.resume();
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onUsefulTipDialogDismissedEvent(UsefulTipDialogDismissedEvent usefulTipDialogDismissedEvent){
        mQuiz.setUsefulTipShown(false);
        if(mQuiz.getCurrentQuestionId() == Question.LAST_QUESTION_ID
                && mSkippedQuestionsDbProvider.getSkippedQuestionsCount() == 0){
            EventBus.getDefault().post(new ShowBuyFullVersionOfferEvent());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGameOverEvent(GameOverEvent gameOverEvent){
        Log.i(QUIZ_ACTIVITY_TAG, "onGameOverEvent()");
        EventBus.getDefault().post(new CountSkippedQuestionsEvent());
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onCountSkippedQuestionsEvent (CountSkippedQuestionsEvent countSkippedQuestionsEvent){
        int skippedQuestionsCount = mSkippedQuestionsDbProvider.getSkippedQuestionsCount();
        if(skippedQuestionsCount > 0){
            Log.i(QUIZ_ACTIVITY_TAG, skippedQuestionsCount + " questions were skipped");
            mQuiz.setQuizMode(Quiz.SKIPPED_QUESTIONS_MODE);
            if(mQuiz.getQuizMode() == Quiz.SKIPPED_QUESTIONS_MODE){
                mQuiz.setSkippedQuestionsIDs(mSkippedQuestionsDbProvider.getSkippedQuestionsIDs());
                mQuiz.resume();
            }
        } else {
            EventBus.getDefault().post(new ShowBuyFullVersionOfferEvent());
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShowBuyFullVersionOfferEvent(ShowBuyFullVersionOfferEvent showBuyFullVersionOfferEvent){
        Log.i(QUIZ_ACTIVITY_TAG, "onShowBuyFullVersionOfferEvent is usefulTip shown " + mQuiz.isUsefulTipShown());
        if(!mQuiz.isUsefulTipShown()){
            startActivity(new Intent(getApplicationContext(), BuyFullVersionActivity.class));
        }
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onQuestionWasSkippedEvent(QuestionWasSkippedEvent questionWasSkippedEvent){
        Log.i(QUIZ_ACTIVITY_TAG, "onQuestionWasSkippedEvent");
        mSkippedQuestionsDbProvider.insertSkippedQuestionIdToDb(questionWasSkippedEvent.getSkippedQuestion().getId());
        long nextQuestionId = questionWasSkippedEvent.getNextQuestionId();
        Log.i(QUIZ_ACTIVITY_TAG, "onQuestionWasSkippedEvent. nextQuestionId = " + nextQuestionId);
        if(nextQuestionId != Question.LAST_QUESTION_ID){
            mQuiz.setCurrentQuestionId(nextQuestionId);
            mQuiz.resume();
        } else {
            EventBus.getDefault().post(new GameOverEvent());
        }
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onGetSkippedQuestionsIDsFromDbEvent(GetSkippedQuestionsIDsFromDbEvent getSkippedQuestionsIDsFromDbEvent){
        long[] skippedQuestionsIDs = mSkippedQuestionsDbProvider.getSkippedQuestionsIDs();
            mQuiz.setSkippedQuestionsIDs(skippedQuestionsIDs);
            mQuiz.resume();
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onDeleteSkippedQuestionIdFromDbEvent(DeleteSkippedQuestionIdFromDbEvent deleteSkippedQuestionIdFromDbEvent){
        mSkippedQuestionsDbProvider.deleteSkippedQuestionIdFromDb(deleteSkippedQuestionIdFromDbEvent.getId());
    }
}
