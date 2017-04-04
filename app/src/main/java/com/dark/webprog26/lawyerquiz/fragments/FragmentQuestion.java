package com.dark.webprog26.lawyerquiz.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.dark.webprog26.lawyerquiz.R;
import com.dark.webprog26.lawyerquiz.engine.events.AnswerRadioButtonPressedEvent;
import com.dark.webprog26.lawyerquiz.engine.listeners.AnswersListener;
import com.dark.webprog26.lawyerquiz.engine.listeners.OnAnswerApprovedListener;
import com.dark.webprog26.lawyerquiz.engine.models.Answer;
import com.dark.webprog26.lawyerquiz.engine.models.Question;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;

/**
 * Created by webpr on 04.04.2017.
 */

public class FragmentQuestion extends Fragment {

    private static final String FR_QUE_TAG = "fr_que_tag";

    public static final String CURRENT_QUESTION = "current_question";
    public static final String ANSWERS_GIVEN_COUNT = "answers_given_count";
    public static final String SCORED_POINTS_COUNT = "scored_points_count";

    //Initializing GUI
    @BindViews({R.id.btnFirst, R.id.btnSecond, R.id.btnThird, R.id.btnFourth})
    RadioButton[] mAnswersRadioButtons;
    @BindView(R.id.rgAnswers)
    RadioGroup mRgAnswers;
    @BindView(R.id.tvQuestion)
    TextView mTvQuestion;
    @BindView(R.id.tvAnswersGiven)
    TextView mTvAnswersGiven;
    @BindView(R.id.tvPoints)
    TextView mTvPoints;
    @BindView(R.id.btnSkipQuestion)
    Button mBtnSkipQuestion;
    @BindView(R.id.btnResumeQuiz)
    Button mBtnResumeQuestion;
    @BindView(R.id.btnGoToSkippedQuestions)
    Button mBtnGoToSkippedQuestions;


    private OnAnswerApprovedListener mOnAnswerApprovedListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof OnAnswerApprovedListener){
            mOnAnswerApprovedListener = (OnAnswerApprovedListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mOnAnswerApprovedListener = null;
    }

    public static FragmentQuestion newInstance(Question question, int answersGiven, double scoredPoints){
        Bundle args = new Bundle();
        args.putSerializable(CURRENT_QUESTION, question);
        args.putInt(ANSWERS_GIVEN_COUNT, answersGiven);
        args.putDouble(SCORED_POINTS_COUNT, scoredPoints);
        FragmentQuestion fragmentQuestion = new FragmentQuestion();
        fragmentQuestion.setArguments(args);
        return fragmentQuestion;
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_question, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBtnSkipQuestion.setEnabled(false);
        mBtnResumeQuestion.setEnabled(false);
        Bundle args = getArguments();
        if(args != null){
            Question question = (Question) args.getSerializable(CURRENT_QUESTION);
            if(question != null){
                mTvQuestion.setText(question.getQuestionString());
                mTvAnswersGiven.setText(getString(R.string.answer_given, args.getInt(ANSWERS_GIVEN_COUNT)));
                mTvPoints.setText(getString(R.string.points_scored, args.getDouble(SCORED_POINTS_COUNT)));
                List<Answer> answers = question.getAnswers();

                for(int i = 0; i < answers.size(); i++){
                    if(mAnswersRadioButtons[i].getVisibility() == View.GONE){
                        mAnswersRadioButtons[i].setVisibility(View.VISIBLE);
                        mAnswersRadioButtons[i].setText(answers.get(i).getAnswerText());
                    }
                }

                mRgAnswers.setOnCheckedChangeListener(new AnswersListener(answers));
            } else {
                Log.i(FR_QUE_TAG, "question is null");
            }
        } else {
            Log.i(FR_QUE_TAG, "args are null");
        }
    }

    @Subscribe (threadMode = ThreadMode.MAIN)
    public void onAnswerRadioButtonPressedEvent(final AnswerRadioButtonPressedEvent answerRadioButtonPressedEvent){
        mBtnResumeQuestion.setEnabled(true);
        mBtnResumeQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnAnswerApprovedListener.onAnswerApproved(answerRadioButtonPressedEvent.getCheckedAnswer());
                mBtnResumeQuestion.setEnabled(false);
            }
        });
    }
}