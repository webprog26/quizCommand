package com.dark.webprog26.lawyerquiz.engine.events;

import com.dark.webprog26.lawyerquiz.engine.models.Answer;

/**
 * Created by webpr on 04.04.2017.
 */

public class AnswerRadioButtonPressedEvent {

    private final Answer mCheckedAnswer;

    public AnswerRadioButtonPressedEvent(Answer checkedAnswer) {
        this.mCheckedAnswer = checkedAnswer;
    }

    public Answer getCheckedAnswer() {
        return mCheckedAnswer;
    }
}
