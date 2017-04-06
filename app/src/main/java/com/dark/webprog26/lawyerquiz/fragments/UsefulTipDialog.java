package com.dark.webprog26.lawyerquiz.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.dark.webprog26.lawyerquiz.R;
import com.dark.webprog26.lawyerquiz.engine.events.GameOverEvent;
import com.dark.webprog26.lawyerquiz.engine.events.UsefulTipDialogDismissedEvent;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * This class shows some useful tips, if such action is envisaged by the quiz logic
 */

public class UsefulTipDialog extends DialogFragment {

    private static final String TAG = "UsefulTipDialog";
    public static final String USEFUL_TIP_TEXT = "useful_tip_text";

    //Views initialization
    @BindView(R.id.tvUsefulTipText)
    TextView mTvUsefulTipText;
    @BindView(R.id.btnBackToQuiz)
    Button mBtnBackToQuiz;

    /**
     * Takes String as a parameter and returns {@link UsefulTipDialog} instance
     * which already contains given {@link String} as an argument
     * @param usefulTipText {@link String}
     * @return UsefulTipDialog
     */
    public static UsefulTipDialog newInstance(String usefulTipText){
        Bundle args = new Bundle();
        args.putString(USEFUL_TIP_TEXT, usefulTipText);
        UsefulTipDialog dialog = new UsefulTipDialog();
        dialog.setArguments(args);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.useful_tip, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Setting dialog title from resources values
        getDialog().setTitle(getString(R.string.useful_tip));
        getDialog().setCancelable(false);
        //Reading String given in UsefulTipDialog newInstance() method
        Bundle args = getArguments();
        if(args != null){
            String usefulTipText = getArguments().getString(USEFUL_TIP_TEXT);
            if(usefulTipText != null){
                //if String exists setting it as a mTvUsefulTipText text
                mTvUsefulTipText.setText(usefulTipText);
            }
        }

        mBtnBackToQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //getting back to the quiz
                dismiss();
            }
        });
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        EventBus.getDefault().post(new UsefulTipDialogDismissedEvent());
    }
}
