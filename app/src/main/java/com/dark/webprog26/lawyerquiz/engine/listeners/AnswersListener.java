package com.dark.webprog26.lawyerquiz.engine.listeners;

import android.support.annotation.IdRes;
import android.widget.RadioGroup;

import com.dark.webprog26.lawyerquiz.engine.events.AnswerRadioButtonPressedEvent;
import com.dark.webprog26.lawyerquiz.engine.models.Answer;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * This class provides answer-radiobuttons state changes handling.
 * Notifies {@link com.dark.webprog26.lawyerquiz.fragments.FragmentQuestion} via {@link AnswerRadioButtonPressedEvent}
 */

public class AnswersListener implements RadioGroup.OnCheckedChangeListener {

    private final List<Answer> mAnswers;

    public AnswersListener(List<Answer> mAnswers) {
        this.mAnswers = mAnswers;
    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        if(checkedId != -1){//no radiobuttons checked. Could throw NPE
            EventBus.getDefault().post(new AnswerRadioButtonPressedEvent(mAnswers.get(group.indexOfChild(group.findViewById(checkedId)))));
        }
    }
}
