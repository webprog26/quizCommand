package com.dark.webprog26.lawyerquiz.engine.commands;

import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.dark.webprog26.lawyerquiz.engine.app.FirebaseApplication;
import com.dark.webprog26.lawyerquiz.engine.events.GameOverEvent;
import com.dark.webprog26.lawyerquiz.engine.models.Answer;
import com.dark.webprog26.lawyerquiz.engine.models.Question;
import com.dark.webprog26.lawyerquiz.fragments.FragmentQuestion;
import com.dark.webprog26.lawyerquiz.interfaces.Command;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import static com.dark.webprog26.lawyerquiz.engine.app.FirebaseApplication.FIREBASE_DATABASE_ROOT;

/**
 * Loads next quiz question from Firebase database
 */

public class LoadNextQuestionCommand implements Command {

    private static final String LOAD_TAG = "load_tag";

    private static final String FRAGMENT_QUESTION_TAG = "fragment_question_tag";

    //Question fields to read'em from database
    private static final String QUESTION_ID  ="id";
    private static final String QUESTION_ANSWERS_NUM = "answersNum";
    private static final String QUESTION_ANSWERS = "answers";
    private static final String QUESTION_STRING = "questionString";
    private static final String QUESTION_TYPE = "questionType";
    private static final String QUESTION_IMAGE_NAME = "questionImageName";
    private static final String QUESTION_COULD_BE_SKIPPED = "couldBeSkipped";

    private final long mNextQuestionId;
    private final FragmentManager mFragmentManager;
    private final int mContainerViewId;
    private final int mAnswersGivenCount;
    private final double mScoredPointsCount;

    //Firebase references
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;

    public LoadNextQuestionCommand(long nextQuestionId,
                                   int answersGivenCount,
                                   double scoredPointsCount,
                                   FragmentManager fragmentManager,
                                   int containerViewId) {
        this.mNextQuestionId = nextQuestionId;
        this.mAnswersGivenCount = answersGivenCount;
        this.mScoredPointsCount = scoredPointsCount;
        this.mFragmentManager = fragmentManager;
        this.mContainerViewId = containerViewId;
        mDatabase = FirebaseApplication.getFirebaseDatabase();
        //DatabaseReference instance to perform requests to database
        mReference = mDatabase.getReference(FIREBASE_DATABASE_ROOT);
    }

    @Override
    public void execute() {
        getQuestionFromFirebaseDb();
    }

    private void getQuestionFromFirebaseDb(){
        if(mNextQuestionId == Question.LAST_QUESTION_ID){
            //We've reached the last question which answers has nextQuestionId = -1
            //Run new GameOverEvent()
            Log.i(LOAD_TAG, "gameOver");
            EventBus.getDefault().post(new GameOverEvent());
            return;
        }
        mReference.child(String.valueOf(mNextQuestionId)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long answersNum = (long) dataSnapshot.child(QUESTION_ANSWERS_NUM).getValue();
                List<Answer> answers  = new ArrayList<Answer>();
                for(int i = 0; i < answersNum; i++){
                    Answer answer = dataSnapshot.child(QUESTION_ANSWERS).child(String.valueOf(i)).getValue(Answer.class);
                    answers.add(answer);
                }
                Question question = new Question(
                        (long) dataSnapshot.child(QUESTION_ID).getValue(),
                        String.valueOf(dataSnapshot.child(QUESTION_STRING).getValue()),
                        answers,
                        (long) dataSnapshot.child(QUESTION_TYPE).getValue(),
                        String.valueOf(dataSnapshot.child(QUESTION_IMAGE_NAME).getValue()),
                        (boolean) dataSnapshot.child(QUESTION_COULD_BE_SKIPPED).getValue()
                );
                Log.i(LOAD_TAG, question.toString());

                FragmentQuestion fragmentQuestion = (FragmentQuestion) mFragmentManager.findFragmentByTag(FRAGMENT_QUESTION_TAG);
                if(fragmentQuestion != null){
                    mFragmentManager.beginTransaction().replace(mContainerViewId, FragmentQuestion.newInstance(question, mAnswersGivenCount, mScoredPointsCount), FRAGMENT_QUESTION_TAG).commit();
                } else {
                    fragmentQuestion = FragmentQuestion.newInstance(question, mAnswersGivenCount, mScoredPointsCount);
                    mFragmentManager.beginTransaction().add(mContainerViewId, fragmentQuestion, FRAGMENT_QUESTION_TAG).commit();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //
            }
        });
    }
}
