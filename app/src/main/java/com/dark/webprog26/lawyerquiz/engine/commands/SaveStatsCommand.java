package com.dark.webprog26.lawyerquiz.engine.commands;

import android.content.SharedPreferences;

import com.dark.webprog26.lawyerquiz.engine.Quiz;
import com.dark.webprog26.lawyerquiz.interfaces.Command;

/**
 * Created by webpr on 04.04.2017.
 */

public class SaveStatsCommand implements Command{

    private final SharedPreferences mSharedPreferences;
    private final long mLastQuestionId;
    private final int mAnsweredQuestionsCount;
    private final double mScoredPoints;

    public SaveStatsCommand(SharedPreferences sharedPreferences, long lastQuestionId, int answeredQuestionsCount, double scoredPoints) {
        this.mSharedPreferences = sharedPreferences;
        this.mLastQuestionId = lastQuestionId;
        this.mAnsweredQuestionsCount = answeredQuestionsCount;
        this.mScoredPoints = scoredPoints;
    }

    @Override
    public void execute() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putLong(Quiz.LAST_QUESTION_ID, mLastQuestionId).apply();
        editor.putInt(Quiz.ANSWERED_QUESTIONS_COUNT, mAnsweredQuestionsCount).apply();
        editor.putString(Quiz.SCORED_POINTS_COUNT, String.valueOf(mScoredPoints)).apply();
    }
}
