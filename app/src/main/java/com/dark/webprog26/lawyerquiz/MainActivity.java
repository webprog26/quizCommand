package com.dark.webprog26.lawyerquiz;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.dark.webprog26.lawyerquiz.fragments.FragmentGreetings;

public class MainActivity extends AppCompatActivity {

    private static final String GREETINGS_FRAGMENT_TAG = "greetings_fragment_tag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentGreetings fragmentGreetings = (FragmentGreetings) fragmentManager.findFragmentByTag(GREETINGS_FRAGMENT_TAG);
        if(fragmentGreetings == null){
            fragmentGreetings = new FragmentGreetings();
            fragmentManager.beginTransaction().add(R.id.activity_main, fragmentGreetings, GREETINGS_FRAGMENT_TAG).commit();
        }
    }
}
