package com.dark.webprog26.lawyerquiz.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.dark.webprog26.lawyerquiz.QuizActivity;
import com.dark.webprog26.lawyerquiz.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by webpr on 04.04.2017.
 */

public class FragmentGreetings extends Fragment {

    @BindView(R.id.btnBeginTest)
    Button mBtnBeginTest;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_greetings, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBtnBeginTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), QuizActivity.class));
                getActivity().finish();
            }
        });
    }
}
