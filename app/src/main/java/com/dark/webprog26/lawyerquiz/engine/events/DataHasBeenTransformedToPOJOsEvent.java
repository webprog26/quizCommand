package com.dark.webprog26.lawyerquiz.engine.events;

import com.dark.webprog26.lawyerquiz.engine.models.Question;

import java.util.List;

/**
 * Created by webpr on 04.04.2017.
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
