package com.dark.webprog26.lawyerquiz.engine.events;

import com.dark.webprog26.lawyerquiz.engine.models.Question;

/**
 * This event takes place when {@link Question} was skipped.
 * Transmits skipped {@link Question} instance and next question id to QuizActivity
 */

public class QuestionWasSkippedEvent {

    private final Question mSkippedQuestion;
    private final long mNextQuestionId;

    public QuestionWasSkippedEvent(Question skippedQuestion, long nextQuestionId) {
        this.mSkippedQuestion = skippedQuestion;
        this.mNextQuestionId = nextQuestionId;
    }

    public Question getSkippedQuestion() {
        return mSkippedQuestion;
    }

    public long getNextQuestionId() {
        return mNextQuestionId;
    }
}
