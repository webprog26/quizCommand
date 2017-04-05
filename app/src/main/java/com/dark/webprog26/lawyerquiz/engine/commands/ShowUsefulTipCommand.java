package com.dark.webprog26.lawyerquiz.engine.commands;

import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.dark.webprog26.lawyerquiz.fragments.UsefulTipDialog;
import com.dark.webprog26.lawyerquiz.interfaces.Command;

/**
 * Created by webpr on 05.04.2017.
 */

public class ShowUsefulTipCommand implements Command {

    private static final String TIP_TAG = "tip_tag";

    private final String mUsefulTipText;
    private final FragmentManager mFragmentManager;

    public ShowUsefulTipCommand(String mUsefulTipText, FragmentManager fragmentManager) {
        this.mUsefulTipText = mUsefulTipText;
        this.mFragmentManager = fragmentManager;
    }

    @Override
    public void execute() {
        showUsefulTipDialog();
    }

    private void showUsefulTipDialog(){
        UsefulTipDialog usefulTipDialog = UsefulTipDialog.newInstance(mUsefulTipText);
        usefulTipDialog.show(mFragmentManager, null);
    }
}
