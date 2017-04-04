package com.dark.webprog26.lawyerquiz.engine.commands;

import android.content.res.AssetManager;

import com.dark.webprog26.lawyerquiz.engine.events.JsonDataFileHasBeenReadEvent;
import com.dark.webprog26.lawyerquiz.interfaces.Command;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by webpr on 04.04.2017.
 */

public class ReadDataFromJsonCommand implements Command {

    /**
     * Reads data from .json file
     */

    private static final String PROPER_UTF_8_ENCODING = "UTF-8";

    private final AssetManager mAssetManager;
    //JSON file name in assets dir
    private static final String JSON_FILE_NAME = "data_demo.json";

    public ReadDataFromJsonCommand(AssetManager assetManager) {
        this.mAssetManager = assetManager;
    }

    @Override
    public void execute() {
        EventBus.getDefault().post(new JsonDataFileHasBeenReadEvent(loadJSONFromAsset(mAssetManager, JSON_FILE_NAME)));
    }

    private String loadJSONFromAsset(AssetManager assetManager, String jsonFilename) {
        String json;
        try {
            InputStream is = assetManager.open(jsonFilename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, PROPER_UTF_8_ENCODING);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}
