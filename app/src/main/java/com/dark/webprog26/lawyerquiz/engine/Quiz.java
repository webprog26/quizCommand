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
import com.dark.webprog26.lawyerquiz.engine.interfaces.Command;
import com.dark.webprog26.lawyerquiz.engine.modes.ArcadeMode;
import com.dark.webprog26.lawyerquiz.engine.modes.QuizMode;
import com.dark.webprog26.lawyerquiz.engine.modes.SkippedQuestionsMode;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import static com.dark.webprog26.lawyerquiz.QuizActivity.FIREBASE__DB_HAS_DATA;

/**
 * Main class of the app engine, which manages quiz modes, preparing, resuming and pausing. In Command design-pattern it plays sender role
 * and forms concrete {@link Command} instance to perform necessary operation such as load next question, reset quiz, show useful tips etc
 */

public class Quiz {

    private static final String QUIZ_TAG = "Quiz_TAG";

    //this data will be saved if app interrupts it's work
    public static final String LAST_QUESTION_ID = "last_question_id";
    public static final String ANSWERED_QUESTIONS_COUNT = "answered_questions_count";
    public static final String SCORED_POINTS_COUNT = "scored_points_count";

    //Our quiz could work in one of this modes
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
    private int mQuizModeType;
    private long[] mSkippedQuestionsIDs;
    private int mSkippedQuestionsIndex = 0;

    private QuizMode quizMode;

    public Quiz(SharedPreferences sharedPreferences, FragmentManager fragmentManager, int containerViewId) {
        this.mSharedPreferences = sharedPreferences;
        this.mFragmentManager = fragmentManager;
        this.mContainerViewId = containerViewId;
        setCurrentQuestionId(mSharedPreferences.getLong(LAST_QUESTION_ID, 0));
        setAnsweredQuestionsCount(mSharedPreferences.getInt(ANSWERED_QUESTIONS_COUNT, 0));
        setScoredPointsCount(Double.parseDouble(mSharedPreferences.getString(SCORED_POINTS_COUNT, "0")));
        setQuizMode(mSharedPreferences.getInt(QUIZ_MODE, ARCADE_MODE));
    }

    /**
     * Prepares quiz to be started or resumed.
     */
    public void prepare(){
        //Start uploading data to Firebase database if necessary
        if(!mSharedPreferences.getBoolean(FIREBASE__DB_HAS_DATA, false)) {
            EventBus.getDefault().post(new ReadDataFromJsonEvent());
        }

        //Get array of skipped Question instances IDs from android.database.sqlite.SQLiteDatabase if necessary
        if(mQuizModeType == SKIPPED_QUESTIONS_MODE){
            EventBus.getDefault().post(new GetSkippedQuestionsIDsFromDbEvent());
        }
    }

    /**
     * Starts transforming data from String type to POJOs: com.dark.webprog26.lawyerquiz.engine.models.Question
     * and com.dark.webprog26.lawyerquiz.engine.models.Answer
     * @param data {@link String}
     */
    public void transformDataToPOJOs(String data){
        mCommand = new TransformJsonDataToPOJOsCommand(data);
        mCommand.execute();
    }

    /**
     * Starts uploading data to Firebase database
     * @param questionsList {@link List}
     */
    public void uploadDataToFirebaseDB(List<Question> questionsList){
        Log.i(QUIZ_TAG, "uploadDataToFirebaseDB");
        mCommand = new UploadDataToFirebaseDbCommand(questionsList);
        mCommand.execute();
    }

    /**
     * Resumes quiz. Depending on current quiz mode starts reading next {@link Question} from Firebase database
     */
    public void resume(){
        Log.i(QUIZ_TAG, "resume()");
        quizMode.resume();
    }

    /**
     * Starts saving quiz stats
     */
    public void pause(){
        mCommand = new SaveStatsCommand(mSharedPreferences,
                                        mCurrentQuestionId,
                                        mAnsweredQuestionsCount,
                                        mScoredPointsCount,
                mQuizModeType);
        mCommand.execute();
    }

    /**
     * Resets quiz stats
     */
    public void reset(){
        Log.i(QUIZ_TAG, "reset() quizMode " + getQuizMode());
        if(mQuizModeType == ARCADE_MODE){
            setScoredPointsCount(0);
            setAnsweredQuestionsCount(0);
            setCurrentQuestionId(0);
            setSkippedQuestionsIndex(0);
        }
    }

    /**
     * Starts useful tip showing
     * @param usefulTipText {@link String}
     */
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
        return mQuizModeType;
    }

    public void setQuizMode(int quizMode) {
        switch (quizMode){
            case ARCADE_MODE:
                this.quizMode = new ArcadeMode(this);
                Log.i(QUIZ_TAG, "quiz mode is ArcadeMode");
                break;
            case SKIPPED_QUESTIONS_MODE:
                Log.i(QUIZ_TAG, "quiz mode is SkippedQuestionsMode");
                this.quizMode = new SkippedQuestionsMode(this);
                break;
        }
        this.mQuizModeType = quizMode;
    }

    public void setSkippedQuestionsIDs(long[] mSkippedQuestionsIDs) {
        this.mSkippedQuestionsIDs = mSkippedQuestionsIDs;
    }

    private void setSkippedQuestionsIndex(int mSkippedQuestionsIndex) {
        this.mSkippedQuestionsIndex = mSkippedQuestionsIndex;
    }

    public long[] getSkippedQuestionsIDs() {
        return mSkippedQuestionsIDs;
    }

    public FragmentManager getFragmentManager() {
        return mFragmentManager;
    }

    public int getContainerViewId() {
        return mContainerViewId;
    }
}
