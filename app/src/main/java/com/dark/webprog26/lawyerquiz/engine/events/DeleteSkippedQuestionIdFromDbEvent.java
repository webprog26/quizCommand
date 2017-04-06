package com.dark.webprog26.lawyerquiz.engine.events;

/**
 * This event takes place when previously skipped {@link com.dark.webprog26.lawyerquiz.engine.models.Question}
 * has been passed. Notifies QuizActivity to delete it from {@link android.database.sqlite.SQLiteDatabase}
 */

public class DeleteSkippedQuestionIdFromDbEvent {

    private final long id;

    public DeleteSkippedQuestionIdFromDbEvent(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }
}
