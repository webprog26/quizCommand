package com.dark.webprog26.lawyerquiz.engine.modes;

import com.dark.webprog26.lawyerquiz.engine.Quiz;
import com.dark.webprog26.lawyerquiz.engine.commands.LoadNextQuestionCommand;

/**
 * Created by webpr on 11.04.2017.
 */

public class ArcadeMode extends QuizMode {

    private static final String QUIZ_MODE_TAG = "ArcadeMode";

    public ArcadeMode(Quiz quiz) {
        super(quiz);
    }

    @Override
    public void resume() {
        new LoadNextQuestionCommand(getQuiz().getCurrentQuestionId(),
                getQuiz().getAnsweredQuestionsCount(),
                getQuiz().getScoredPointsCount(),
                getQuiz().getFragmentManager(),
                getQuiz().getContainerViewId()).execute();
    }
}
