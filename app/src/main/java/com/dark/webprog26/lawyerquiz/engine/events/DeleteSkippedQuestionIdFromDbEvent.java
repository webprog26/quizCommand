package com.dark.webprog26.lawyerquiz.engine.events;

/**
 * Created by webpr on 05.04.2017.
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
