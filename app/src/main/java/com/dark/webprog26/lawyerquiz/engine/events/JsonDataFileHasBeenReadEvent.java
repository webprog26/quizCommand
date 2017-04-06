package com.dark.webprog26.lawyerquiz.engine.events;

/**
 * This event takes place when data from .json file has been read.
 * Transmits read {@link String} of data to QuizActivity
 */

public class JsonDataFileHasBeenReadEvent {

    private final String mJsonData;

    public JsonDataFileHasBeenReadEvent(String mJsonData) {
        this.mJsonData = mJsonData;
    }

    public String getJsonData() {
        return mJsonData;
    }
}
