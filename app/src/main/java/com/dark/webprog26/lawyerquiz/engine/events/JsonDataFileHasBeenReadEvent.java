package com.dark.webprog26.lawyerquiz.engine.events;

/**
 * Created by webpr on 04.04.2017.
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
