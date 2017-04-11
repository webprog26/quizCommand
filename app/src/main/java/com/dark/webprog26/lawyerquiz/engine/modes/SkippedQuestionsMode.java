package com.dark.webprog26.lawyerquiz.engine.modes;

import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.dark.webprog26.lawyerquiz.engine.Quiz;
import com.dark.webprog26.lawyerquiz.engine.commands.LoadNextQuestionCommand;
import com.dark.webprog26.lawyerquiz.engine.interfaces.Command;
import com.dark.webprog26.lawyerquiz.engine.models.Question;

import static com.dark.webprog26.lawyerquiz.engine.Quiz.ARCADE_MODE;

/**
 * Created by webpr on 11.04.2017.
 */

public class SkippedQuestionsMode extends QuizMode {

    private static final String QUIZ_MODE_TAG = "SkippedQuestionsMode";
    private int mSkippedQuestionsIndex = 0;

    public SkippedQuestionsMode(Quiz quiz) {
        super(quiz);
    }

    @Override
    public void resume() {
        long[] mSkippedQuestionsIDs = getQuiz().getSkippedQuestionsIDs();
        if(mSkippedQuestionsIndex < mSkippedQuestionsIDs.length){
            getQuiz().setCurrentQuestionId(mSkippedQuestionsIDs[mSkippedQuestionsIndex]);
            mSkippedQuestionsIndex++;
        } else {
            getQuiz().setQuizMode(ARCADE_MODE);
            getQuiz().setCurrentQuestionId(Question.LAST_QUESTION_ID);
        }
        new LoadNextQuestionCommand(getQuiz().getCurrentQuestionId(),
                getQuiz().getAnsweredQuestionsCount(),
                getQuiz().getScoredPointsCount(),
                getQuiz().getFragmentManager(),
                getQuiz().getContainerViewId()).execute();
    }
}
