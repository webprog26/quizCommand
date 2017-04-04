package com.dark.webprog26.lawyerquiz.engine.models;

import java.io.Serializable;
import java.util.List;

/**
 * Created by webpr on 15.03.2017.
 */

public class Question implements Serializable{

    /**
     * Question POJO class
     */

    //Questions types
    public static final int REQUIRED_QUESTION = 0;
    public static final int OPTIONAL_QUESTION = 1;
    public static final int LAST_QUESTION_ID = -1;

    //Whatever Question instance is formed from the FirebaseDatabase just like the Answer instance, it has no public fields
    //The reason is it has List of answers, so it couldn't ne constructed automatically
    private long mId;
    private String mQuestionString;
    private List<Answer> mAnswers;
    private long mQuestionType;
    private long mAnswersNum = 0;
    private String mQuestionImageName = null;
    private boolean hasImage;
    private boolean couldBeSkipped;

    public Question(long id,
                    String mQuestionString,
                    List<Answer> answers,
                    long questionType,
                    String questionImageName,
                    boolean couldBeSkipped) {
        this.mId = id;
        this.mQuestionType = questionType;
        this.mQuestionString = mQuestionString;
        this.mAnswers = answers;
        this.mQuestionImageName = questionImageName;
        this.hasImage = (questionImageName != null && !questionImageName.equalsIgnoreCase("null"));
        if(answers != null){
            this.mAnswersNum = answers.size();
        }
        this.couldBeSkipped = couldBeSkipped;
    }

    public long getId() {
        return mId;
    }

    public void setId(long mId) {
        this.mId = mId;
    }

    public String getQuestionString() {
        return mQuestionString;
    }

    public void setQuestionString(String mQuestionString) {
        this.mQuestionString = mQuestionString;
    }

    public List<Answer> getAnswers() {
        return mAnswers;
    }


    public long getQuestionType() {
        return mQuestionType;
    }

    public void setQuestionType(long mQuestionType) {
        this.mQuestionType = mQuestionType;
    }

    public long getAnswersNum() {
        return mAnswersNum;
    }

    public String getQuestionImageName() {
        return mQuestionImageName;
    }


    public boolean isHasImage() {
        return hasImage;
    }

    public boolean isCouldBeSkipped() {
        return couldBeSkipped;
    }

    public void setCouldBeSkipped(boolean couldBeSkipped) {
        this.couldBeSkipped = couldBeSkipped;
    }


    //Just for testing mode
    @Override
    public String toString() {
        return "question with id" + getId() + "\n"
                + "text " + getQuestionString() + "\n"
                + "type " + getQuestionType() + "\n"
                + "has " + getAnswersNum() + " answers" + "\n"
                + "has image " + isHasImage() + "\n"
                + "could be skipped " + isCouldBeSkipped();
    }
}
