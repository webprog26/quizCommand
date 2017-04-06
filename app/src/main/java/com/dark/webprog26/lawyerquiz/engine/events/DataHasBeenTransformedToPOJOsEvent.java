package com.dark.webprog26.lawyerquiz.engine.events;

import com.dark.webprog26.lawyerquiz.engine.models.Question;

import java.util.List;

/**
 * This event takes place when data from .json file has been transformed into POJOs.
 * Transmits {@link List} of {@link Question} instances to QuizActivity
 */

public class DataHasBeenTransformedToPOJOsEvent {

    private final List<Question> mQuestionsList;

    public DataHasBeenTransformedToPOJOsEvent(List<Question> questionsList) {
        this.mQuestionsList = questionsList;
    }

    public List<Question> getQuestionsList() {
        return mQuestionsList;
    }
}
