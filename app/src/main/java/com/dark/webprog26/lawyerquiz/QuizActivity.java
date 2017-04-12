package com.dark.webprog26.lawyerquiz;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

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

import butterknife.BindView;
import butterknife.ButterKnife;

public class QuizActivity extends AppCompatActivity implements OnAnswerApprovedListener{

    private static final String QUIZ_ACTIVITY_TAG = "QuizActivity_TAG";

    public static final String FIREBASE__DB_HAS_DATA = "firebase_db_has_data";

    @BindView(R.id.pbQuizIsLoading)
    ProgressBar mPbQuizIsLoading;

    private Quiz mQuiz;
    private SkippedQuestionsDbProvider mSkippedQuestionsDbProvider;
    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        ButterKnife.bind(this);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mSkippedQuestionsDbProvider = new SkippedQuestionsDbProvider(this);
        mQuiz = new Quiz(mSharedPreferences,
                            getSupportFragmentManager(),
                            R.id.activity_quiz);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //setting default quiz background instead of logo
        getWindow().setBackgroundDrawableResource(R.drawable.screen_bg);
        //Registering EventBus to handle events
        EventBus.getDefault().register(this);
        //Prepare the quiz to resume
        mQuiz.prepare();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(QUIZ_ACTIVITY_TAG, "onResume");
        if(mSharedPreferences.getBoolean(BuyFullVersionActivity.GAME_IS_OVER, false)){
            //quiz has been resumed afret buy-full-version offer was shown, so the quiz must reset itself
            mSharedPreferences.edit().putBoolean(BuyFullVersionActivity.GAME_IS_OVER, false).apply();
            Log.i(QUIZ_ACTIVITY_TAG, "onResume. question id is " + mQuiz.getCurrentQuestionId());
            mQuiz.reset();
        }
        //hide progress bar and resume the quiz
        hideProgressBar();
        if((mQuiz.getQuizMode() == Quiz.ARCADE_MODE && mSharedPreferences.getBoolean(FIREBASE__DB_HAS_DATA, false)) || mQuiz.getSkippedQuestionsIDs() != null) {
            Log.i(QUIZ_ACTIVITY_TAG, "Resume in onResume()");
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
        //saving quiz stats
        mQuiz.pause();
    }

    /**
     * Handles {@link ReadDataFromJsonEvent}. Reads data from assets in a separate thread
     * @param readDataFromJsonEvent {@link ReadDataFromJsonEvent}
     */
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onReadDataFromJsonEvent(ReadDataFromJsonEvent readDataFromJsonEvent){
        Log.i(QUIZ_ACTIVITY_TAG, "onReadDataFromJsonEvent");
        new ReadDataFromJsonCommand(getAssets()).execute();
    }

    /**
     * Handles {@link JsonDataFileHasBeenReadEvent}. Notifies {@link QuizActivity} that data
     * has been read from .jsom and could be transformed into POJOs. Works in a separate thread
     * @param jsonDataFileHasBeenReadEvent {@link JsonDataFileHasBeenReadEvent}
     */
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onJsonDataFileHasBeenReadEvent(JsonDataFileHasBeenReadEvent jsonDataFileHasBeenReadEvent){
        Log.i(QUIZ_ACTIVITY_TAG, "onJsonDataFileHasBeenReadEvent");
       mQuiz.transformDataToPOJOs(jsonDataFileHasBeenReadEvent.getJsonData());
    }

    /**
     * Handles {@link DataHasBeenTransformedToPOJOsEvent}. Provides {@link Quiz} to upload read data
     * to Firebase database. Works in a separate thread
     * @param dataHasBeenTransformedToPOJOsEvent {@link DataHasBeenTransformedToPOJOsEvent}
     */
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onDataHasBeenTransformedToPOJOsEvent(DataHasBeenTransformedToPOJOsEvent dataHasBeenTransformedToPOJOsEvent){
        Log.i(QUIZ_ACTIVITY_TAG, "onDataHasBeenTransformedToPOJOsEvent");
        mQuiz.uploadDataToFirebaseDB(dataHasBeenTransformedToPOJOsEvent.getQuestionsList());
    }

    /**
     * Handles {@link DataHasBeenUploadedToFirebaseDbEvent}. Provides {@link Quiz} to resume it's work from the first Question
     * @param dataHasBeenUploadedToFirebaseDbEvent {@link DataHasBeenUploadedToFirebaseDbEvent}
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDataHasBeenUploadedToFirebaseDbEvent(DataHasBeenUploadedToFirebaseDbEvent dataHasBeenUploadedToFirebaseDbEvent){
        Log.i(QUIZ_ACTIVITY_TAG, "onDataHasBeenUploadedToFirebaseDbEvent");
        mSharedPreferences.edit().putBoolean(FIREBASE__DB_HAS_DATA, true).apply();
        //hide progress bar and resume the quiz
        mQuiz.resume();
    }

    @Override
    public void onAnswerApproved(Answer answer) {
        //update quiz stats
        mQuiz.setAnsweredQuestionsCount(mQuiz.getAnsweredQuestionsCount() + 1);
        mQuiz.setScoredPointsCount(mQuiz.getScoredPointsCount() + answer.getPoints());

        //getting useful tip text
        String usefulTipText = answer.getReferenceText();

        if(!usefulTipText.equalsIgnoreCase("null")){
            //useful exists and should be shown
            mQuiz.showUsefulTip(usefulTipText);
        }

        if(mQuiz.getQuizMode() == Quiz.ARCADE_MODE){
            //in arcade mode we should set next question id depending on the Answer user chose
            mQuiz.setCurrentQuestionId(answer.getNextQuestionId());
        } else {
            Log.i(QUIZ_ACTIVITY_TAG, "deleting from SQLiteDb question with id  " + mQuiz.getCurrentQuestionId());
            //in skipped questions mode we should delete passed question id from skipped questions IDs database
            EventBus.getDefault().post(new DeleteSkippedQuestionIdFromDbEvent(mQuiz.getCurrentQuestionId()));
        }

        Log.i(QUIZ_ACTIVITY_TAG, "in quiz next id is " + mQuiz.getCurrentQuestionId());
        //resume the quiz
            mQuiz.resume();
    }

    /**
     * Handles {@link UsefulTipDialogDismissedEvent}. Works in a separate thread
     * @param usefulTipDialogDismissedEvent {@link UsefulTipDialogDismissedEvent}
     */
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onUsefulTipDialogDismissedEvent(UsefulTipDialogDismissedEvent usefulTipDialogDismissedEvent){
        //Notify Quiz that no UsefulTipDialog is visible
        mQuiz.setUsefulTipShown(false);
        if(mQuiz.getCurrentQuestionId() == Question.LAST_QUESTION_ID
                && mSkippedQuestionsDbProvider.getSkippedQuestionsCount() == 0){
            //We've reached the end of the quiz and there are no skipped questions.
            //Show buy-full-version-offer
            EventBus.getDefault().post(new ShowBuyFullVersionOfferEvent());
        }
    }

    /**
     * Handles {@link GameOverEvent}. Starts skipped questions counting
     * @param gameOverEvent {@link GameOverEvent}
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGameOverEvent(GameOverEvent gameOverEvent){
        Log.i(QUIZ_ACTIVITY_TAG, "onGameOverEvent()");
        EventBus.getDefault().post(new CountSkippedQuestionsEvent());
    }

    /**
     * Handles {@link CountSkippedQuestionsEvent}. Works in a separate thread
     * @param countSkippedQuestionsEvent {@link CountSkippedQuestionsEvent}
     */
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onCountSkippedQuestionsEvent (CountSkippedQuestionsEvent countSkippedQuestionsEvent){
        int skippedQuestionsCount = mSkippedQuestionsDbProvider.getSkippedQuestionsCount();
        if(skippedQuestionsCount > 0){
            //we have skipped questions
            //set Quiz mode to skipped qusetions mode
            Log.i(QUIZ_ACTIVITY_TAG, skippedQuestionsCount + " questions were skipped");
            mQuiz.setQuizMode(Quiz.SKIPPED_QUESTIONS_MODE);
            if(mQuiz.getQuizMode() == Quiz.SKIPPED_QUESTIONS_MODE){
                //give the quiz array of skipped questions IDs
                mQuiz.setSkippedQuestionsIDs(mSkippedQuestionsDbProvider.getSkippedQuestionsIDs());
                //resume the quiz
                mQuiz.resume();
            }
        } else {
            //We've reached the end of the quiz and there are no skipped questions.
            //Show buy-full-version-offer
            EventBus.getDefault().post(new ShowBuyFullVersionOfferEvent());
        }
    }

    /**
     * Handles {@link ShowBuyFullVersionOfferEvent}.
     * @param showBuyFullVersionOfferEvent {@link ShowBuyFullVersionOfferEvent}
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShowBuyFullVersionOfferEvent(ShowBuyFullVersionOfferEvent showBuyFullVersionOfferEvent){
        Log.i(QUIZ_ACTIVITY_TAG, "onShowBuyFullVersionOfferEvent is usefulTip shown " + mQuiz.isUsefulTipShown());
        if(!mQuiz.isUsefulTipShown()){
            //No UsefulTipDialog is visible right now, show buy-full-version-offer
            startActivity(new Intent(getApplicationContext(), BuyFullVersionActivity.class));
        }
    }

    /**
     * Handles {@link QuestionWasSkippedEvent}. Writes skipped Question id to database,
     * gives the quiz next question id after previous one was skipped
     * @param questionWasSkippedEvent {@link QuestionWasSkippedEvent}
     */
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onQuestionWasSkippedEvent(QuestionWasSkippedEvent questionWasSkippedEvent){
        Log.i(QUIZ_ACTIVITY_TAG, "onQuestionWasSkippedEvent");
        //Write skipped question id to database
        mSkippedQuestionsDbProvider.insertSkippedQuestionIdToDb(questionWasSkippedEvent.getSkippedQuestion().getId());
        long nextQuestionId = questionWasSkippedEvent.getNextQuestionId();
        Log.i(QUIZ_ACTIVITY_TAG, "onQuestionWasSkippedEvent. nextQuestionId = " + nextQuestionId);
        if(nextQuestionId != Question.LAST_QUESTION_ID){
            //that wasn't the last question, resume the quiz
            mQuiz.setCurrentQuestionId(nextQuestionId);
            mQuiz.resume();
        } else {
            //that was the last question. Game is over
            EventBus.getDefault().post(new GameOverEvent());
        }
    }

    /**
     * Handles {@link GetSkippedQuestionsIDsFromDbEvent}. Loads array of skipped questions IDs from database
     * in a separate thread
     * @param getSkippedQuestionsIDsFromDbEvent {@link GetSkippedQuestionsIDsFromDbEvent}
     */
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onGetSkippedQuestionsIDsFromDbEvent(GetSkippedQuestionsIDsFromDbEvent getSkippedQuestionsIDsFromDbEvent){
        long[] skippedQuestionsIDs = mSkippedQuestionsDbProvider.getSkippedQuestionsIDs();
            mQuiz.setSkippedQuestionsIDs(skippedQuestionsIDs);
            mQuiz.resume();
    }

    /**
     * Handles {@link DeleteSkippedQuestionIdFromDbEvent}. Deletes already passed previously skipped question id from database.
     * Works in a separate thread
     * @param deleteSkippedQuestionIdFromDbEvent {@link DeleteSkippedQuestionIdFromDbEvent}
     */
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onDeleteSkippedQuestionIdFromDbEvent(DeleteSkippedQuestionIdFromDbEvent deleteSkippedQuestionIdFromDbEvent){
        mSkippedQuestionsDbProvider.deleteSkippedQuestionIdFromDb(deleteSkippedQuestionIdFromDbEvent.getId());
    }

    /**
     * Hides quiz loading progress bar
     */
    private void hideProgressBar(){
        if(mPbQuizIsLoading.getVisibility() == View.VISIBLE){
            mPbQuizIsLoading.setVisibility(View.GONE);
        }
    }
}
