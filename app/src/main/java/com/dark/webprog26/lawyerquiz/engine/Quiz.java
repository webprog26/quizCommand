package com.dark.webprog26.lawyerquiz.engine;

import android.content.SharedPreferences;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.dark.webprog26.lawyerquiz.engine.commands.LoadNextQuestionCommand;
import com.dark.webprog26.lawyerquiz.engine.commands.SaveStatsCommand;
import com.dark.webprog26.lawyerquiz.engine.commands.ShowUsefulTipCommand;
import com.dark.webprog26.lawyerquiz.engine.commands.TransformJsonDataToPOJOsCommand;
import com.dark.webprog26.lawyerquiz.engine.commands.UploadDataToFirebaseDbCommand;
import com.dark.webprog26.lawyerquiz.engine.events.GetSkippedQuestionsIDsFromDbEvent;
import com.dark.webprog26.lawyerquiz.engine.events.ReadDataFromJsonEvent;
import com.dark.webprog26.lawyerquiz.engine.models.Question;
import com.dark.webprog26.lawyerquiz.interfaces.Command;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by webpr on 04.04.2017.
 */

public class Quiz {

    private static final String QUIZ_TAG = "Quiz_TAG";

    public static final String LAST_QUESTION_ID = "last_question_id";
    public static final String ANSWERED_QUESTIONS_COUNT = "answered_questions_count";
    public static final String SCORED_POINTS_COUNT = "scored_points_count";

    public static final String QUIZ_MODE = "current_quiz_mode";
    public static final int ARCADE_MODE = 0;
    public static final int SKIPPED_QUESTIONS_MODE = 1;

    private long mCurrentQuestionId;
    private int mAnsweredQuestionsCount;
    private double mScoredPointsCount;


    private SharedPreferences mSharedPreferences;
    private final FragmentManager mFragmentManager;
    private int mContainerViewId;
    private Command mCommand;
    private boolean isUsefulTipShown = false;
    private int mQuizMode;
    private long[] mSkippedQuestionsIDs;
    private int mSkippedQuestionsIndex = 0;

    public Quiz(SharedPreferences sharedPreferences, FragmentManager fragmentManager, int containerViewId) {
        this.mSharedPreferences = sharedPreferences;
        this.mFragmentManager = fragmentManager;
        this.mContainerViewId = containerViewId;
        setCurrentQuestionId(mSharedPreferences.getLong(LAST_QUESTION_ID, 0));
        setAnsweredQuestionsCount(mSharedPreferences.getInt(ANSWERED_QUESTIONS_COUNT, 0));
        setScoredPointsCount(Double.parseDouble(mSharedPreferences.getString(SCORED_POINTS_COUNT, "0")));
        setQuizMode(mSharedPreferences.getInt(QUIZ_MODE, ARCADE_MODE));
    }

    public void prepare(){
        EventBus.getDefault().post(new ReadDataFromJsonEvent());
        if(mQuizMode == SKIPPED_QUESTIONS_MODE){
            EventBus.getDefault().post(new GetSkippedQuestionsIDsFromDbEvent());
        }
    }

    public void transformDataToPOJOs(String data){
        mCommand = new TransformJsonDataToPOJOsCommand(data);
        mCommand.execute();
    }

    public void uploadDataToFirebaseDB(List<Question> questionsList){
        Log.i(QUIZ_TAG, "uploadDataToFirebaseDB");
        mCommand = new UploadDataToFirebaseDbCommand(questionsList);
        mCommand.execute();
    }

    public void resume(){
        switch (mQuizMode){
            case ARCADE_MODE:
                mCommand = new LoadNextQuestionCommand(mCurrentQuestionId,
                        mAnsweredQuestionsCount,
                        mScoredPointsCount,
                        mFragmentManager,
                        mContainerViewId);
                break;
            case SKIPPED_QUESTIONS_MODE:
                Log.i(QUIZ_TAG, "mSkippedQuestionsIndex < mSkippedQuestionsIDs.length " + String.valueOf(mSkippedQuestionsIndex < mSkippedQuestionsIDs.length));
                if(mSkippedQuestionsIndex < mSkippedQuestionsIDs.length){
                    Log.i(QUIZ_TAG, "next index is " + mSkippedQuestionsIndex);
                    Log.i(QUIZ_TAG, "mSkippedQuestionsIDs.length " + mSkippedQuestionsIDs.length);
                    setCurrentQuestionId(mSkippedQuestionsIDs[mSkippedQuestionsIndex]);
                    mSkippedQuestionsIndex++;
                } else {
                    setCurrentQuestionId(Question.LAST_QUESTION_ID);
                    setQuizMode(ARCADE_MODE);
                }
                Log.i(QUIZ_TAG, "CurrentQuestionId is " + mCurrentQuestionId);
                mCommand = new LoadNextQuestionCommand(mCurrentQuestionId,
                        mAnsweredQuestionsCount,
                        mScoredPointsCount,
                        mFragmentManager,
                        mContainerViewId);
                mSkippedQuestionsIndex++;
        }
        mCommand.execute();
    }

    public void pause(){
        mCommand = new SaveStatsCommand(mSharedPreferences,
                                        mCurrentQuestionId,
                                        mAnsweredQuestionsCount,
                                        mScoredPointsCount,
                                        mQuizMode);
        mCommand.execute();
    }

    public void reset(){
        if(mQuizMode == ARCADE_MODE){
            setScoredPointsCount(0);
            setAnsweredQuestionsCount(0);
            setCurrentQuestionId(0);
        } else {
            Log.i(QUIZ_TAG, "Got " + mSkippedQuestionsIDs.length + " skipped questions");
        }
    }

    public void showUsefulTip(String usefulTipText){
        setUsefulTipShown(true);
        mCommand = new ShowUsefulTipCommand(usefulTipText, mFragmentManager);
        mCommand.execute();
    }

    public void setCurrentQuestionId(long mCurrentQuestionId) {
        this.mCurrentQuestionId = mCurrentQuestionId;
    }

    public void setAnsweredQuestionsCount(int mAnsweredQuestionsCount) {
        this.mAnsweredQuestionsCount = mAnsweredQuestionsCount;
    }

    public void setScoredPointsCount(double mScoredPointsCount) {
        this.mScoredPointsCount = mScoredPointsCount;
    }

    public long getCurrentQuestionId() {
        return mCurrentQuestionId;
    }

    public int getAnsweredQuestionsCount() {
        return mAnsweredQuestionsCount;
    }

    public double getScoredPointsCount() {
        return mScoredPointsCount;
    }

    public boolean isUsefulTipShown() {
        return isUsefulTipShown;
    }

    public void setUsefulTipShown(boolean usefulTipShown) {
        isUsefulTipShown = usefulTipShown;
    }

    public int getQuizMode() {
        return mQuizMode;
    }

    public void setQuizMode(int mQuizMode) {
        this.mQuizMode = mQuizMode;
    }

    public long[] getSkippedQuestionsIDs() {
        return mSkippedQuestionsIDs;
    }

    public void setSkippedQuestionsIDs(long[] mSkippedQuestionsIDs) {
        this.mSkippedQuestionsIDs = mSkippedQuestionsIDs;
    }

    private boolean hasNextSkippedQuestion(){
        return mSkippedQuestionsIDs != null && mSkippedQuestionsIndex < (mSkippedQuestionsIDs.length);
    }
}
