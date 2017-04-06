package com.dark.webprog26.lawyerquiz.engine.events;

import com.dark.webprog26.lawyerquiz.engine.models.Answer;

/**
 * This event takes place when answer-radiobutton has been pressed.
 * Transmits chosen Answer to {@link com.dark.webprog26.lawyerquiz.fragments.FragmentQuestion}
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
