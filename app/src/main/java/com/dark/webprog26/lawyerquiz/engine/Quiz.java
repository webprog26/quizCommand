package com.dark.webprog26.lawyerquiz.engine;

import android.content.SharedPreferences;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.dark.webprog26.lawyerquiz.engine.commands.LoadNextQuestionCommand;
import com.dark.webprog26.lawyerquiz.engine.commands.SaveStatsCommand;
import com.dark.webprog26.lawyerquiz.engine.commands.ShowUsefulTipCommand;
import com.dark.webprog26.lawyerquiz.engine.commands.TransformJsonDataToPOJOsCommand;
import com.dark.webprog26.lawyerquiz.engine.commands.UploadDataToFirebaseDbCommand;
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

    private long mCurrentQuestionId;
    private int mAnsweredQuestionsCount;
    private double mScoredPointsCount;


    private SharedPreferences mSharedPreferences;
    private FragmentManager mFragmentManager;
    private int mContainerViewId;
    private Command mCommand;

    public Quiz(SharedPreferences sharedPreferences, FragmentManager fragmentManager, int containerViewId) {
        this.mSharedPreferences = sharedPreferences;
        this.mFragmentManager = fragmentManager;
        this.mContainerViewId = containerViewId;
        setCurrentQuestionId(mSharedPreferences.getLong(LAST_QUESTION_ID, 0));
        setAnsweredQuestionsCount(mSharedPreferences.getInt(ANSWERED_QUESTIONS_COUNT, 0));
        setScoredPointsCount(Double.parseDouble(mSharedPreferences.getString(SCORED_POINTS_COUNT, "0")));
    }

    public void prepare(){
        EventBus.getDefault().post(new ReadDataFromJsonEvent());
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
        Log.i(QUIZ_TAG, "resume()");

        mCommand = new LoadNextQuestionCommand(mCurrentQuestionId, mAnsweredQuestionsCount, mScoredPointsCount, mFragmentManager, mContainerViewId);
        mCommand.execute();
    }

    public void pause(){
        mCommand = new SaveStatsCommand(mSharedPreferences,
                                        mCurrentQuestionId,
                                        mAnsweredQuestionsCount,
                                        mScoredPointsCount);
        mCommand.execute();
    }

    public void showUsefulTip(String usefulTipText){
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

    public int getAnsweredQuestionsCount() {
        return mAnsweredQuestionsCount;
    }

    public double getScoredPointsCount() {
        return mScoredPointsCount;
    }
}
