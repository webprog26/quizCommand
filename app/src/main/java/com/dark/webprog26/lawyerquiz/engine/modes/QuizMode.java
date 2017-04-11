package com.dark.webprog26.lawyerquiz.engine.modes;

import com.dark.webprog26.lawyerquiz.engine.Quiz;


/**
 * Created by webpr on 11.04.2017.
 */

public abstract class QuizMode {

    private Quiz mQuiz;

    public QuizMode(Quiz quiz) {
        this.mQuiz = quiz;
    }

    public abstract void resume();

    public Quiz getQuiz() {
        return mQuiz;
    }
}
