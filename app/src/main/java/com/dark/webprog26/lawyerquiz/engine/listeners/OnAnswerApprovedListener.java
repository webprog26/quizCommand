package com.dark.webprog26.lawyerquiz.engine.listeners;

import com.dark.webprog26.lawyerquiz.engine.models.Answer;

/**
 * Implemented by {@link com.dark.webprog26.lawyerquiz.QuizActivity} provides quiz resuming by
 * transmiting next question id to {@link com.dark.webprog26.lawyerquiz.engine.Quiz} instance
 */

public interface OnAnswerApprovedListener {

    /**
     * Handles answer approving
     * @param answer {@link Answer}
     */
    public void onAnswerApproved(Answer answer);
}
